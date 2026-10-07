package net.emanueljdf09.dtrhmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VineAnchorEntity extends Entity {
    private static final TrackedData<Integer> OWNER_ID = DataTracker.registerData(VineAnchorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VICTIM_ID = DataTracker.registerData(VineAnchorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final float VINE_HEALTH = 4.0f;
    private static final int MAX_LIFETIME = 240;

    private static final int PLAYER_MAX_LIFETIME = 200;
    private static final int FLOWER_MAX_LIFETIME = 240;

    private static final int DRAIN_INTERVAL = 20;
    private static final float DRAIN_DAMAGE = 2.0f;
    private static final double LEASH_RADIUS = 2.5;
    private static final int SLOW_AMPLIFIER = 3;

    @Nullable
    private Entity owner;
    @Nullable
    private LivingEntity victim;
    private double holdX, holdZ;
    private float vineHealth = VINE_HEALTH;
    private int ticksAlive = 0;

    public VineAnchorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(OWNER_ID, -1);
        this.dataTracker.startTracking(VICTIM_ID, -1);
    }

    public int getOwnerId() { return this.dataTracker.get(OWNER_ID); }
    public int getVictimId() { return this.dataTracker.get(VICTIM_ID); }

    public void bind(Entity owner, LivingEntity victim) {
        this.owner = owner;
        this.victim = victim;
        this.holdX = victim.getX();
        this.holdZ = victim.getZ();
        this.dataTracker.set(OWNER_ID, owner.getId());
        this.dataTracker.set(VICTIM_ID, victim.getId());
        updateAnchorPosition();

        this.getWorld().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.BLOCK_GRASS_PLACE, SoundCategory.HOSTILE, 1.2F, 0.7F);
        this.getWorld().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.ENTITY_LEASH_KNOT_PLACE, SoundCategory.HOSTILE, 1.0F, 0.8F);
    }


    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;

        ticksAlive++;

        if (owner == null || victim == null
                || owner.isRemoved() || !owner.isAlive()
                || victim.isRemoved() || !victim.isAlive()
                || victim.getWorld() != this.getWorld()) {
            release();
            return;
        }

        if (owner instanceof SingingFlowerEntity flower) {
            if (flower.getBurrowState() != SingingFlowerEntity.BurrowState.SURFACED) {
                release();
                return;
            }
        }


        int maxLifetime = (owner instanceof PlayerEntity) ? PLAYER_MAX_LIFETIME : FLOWER_MAX_LIFETIME;

        if (ticksAlive > maxLifetime) {
            if (owner instanceof SingingFlowerEntity flower) {
                flower.startle(SingingFlowerEntity.STARTLE_VINE_EXPIRE_TICKS);
            }
            release();
            return;
        }

        if (ticksAlive % 5 == 0) {
            victim.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 10, SLOW_AMPLIFIER, false, false, false));
        }

        double dx = victim.getX() - holdX;
        double dz = victim.getZ() - holdZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > LEASH_RADIUS) {
            double k = LEASH_RADIUS / dist;
            victim.requestTeleport(holdX + dx * k, victim.getY(), holdZ + dz * k);
        }

        updateAnchorPosition();

        if (ticksAlive % DRAIN_INTERVAL == 0) {
            drain();
        }
    }

    private void drain() {
        if (owner == null || victim == null) return;

        float before = victim.getHealth();
        DamageSource source = owner instanceof PlayerEntity ? this.getWorld().getDamageSources().playerAttack((PlayerEntity) owner)
                : this.getWorld().getDamageSources().indirectMagic(this, owner);

        victim.damage(source, DRAIN_DAMAGE);
        float dealt = before - victim.getHealth();

        if (dealt > 0 && this.getWorld() instanceof ServerWorld world) {

            if (owner instanceof SingingFlowerEntity flower) {
                flower.heal(dealt);
            } else if (owner instanceof PlayerEntity player) {
                player.heal(dealt);
            }

            world.spawnParticles(ParticleTypes.HEART,
                    victim.getX(), victim.getBodyY(0.7), victim.getZ(), 2, 0.3, 0.3, 0.3, 0.0);
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                    victim.getX(), victim.getBodyY(0.5), victim.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
            world.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.ENTITY_GENERIC_DRINK, SoundCategory.HOSTILE, 0.6F, 0.8F);
        }
    }

    private void updateAnchorPosition() {
        if (owner == null || victim == null) return;
        Vec3d toOwner = owner.getPos().subtract(victim.getPos()).multiply(1, 0, 1);
        double len = toOwner.length();
        if (len < 0.01) return;

        double offset = MathHelper.clamp(len * 0.4, 1.5, 3.0);
        Vec3d dir = toOwner.normalize();
        this.setPosition(victim.getX() + dir.x * offset, victim.getY() + 0.3, victim.getZ() + dir.z * offset);
    }

    private void release() {
        this.discard();
    }

    @Override
    public boolean canHit() {
        return !isPlayerOwned();
    }

    private boolean isPlayerOwned() {
        return this.getWorld().getEntityById(getOwnerId()) instanceof PlayerEntity;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (isPlayerOwned()) return false;

        if (this.getWorld().isClient() || this.isRemoved()) return false;

        vineHealth -= amount;
        if (this.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(ParticleTypes.COMPOSTER, this.getX(), this.getY() + 0.6, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
        this.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.0F, 1.0F);

        if (vineHealth <= 0) {
            if (owner instanceof SingingFlowerEntity flower && !flower.isRemoved()) {
                flower.startle(SingingFlowerEntity.STARTLE_VINE_CUT_TICKS);
            }
            this.playSound(SoundEvents.BLOCK_WEEPING_VINES_BREAK, 1.2F, 0.8F);
            release();
        }
        return true;
    }

    @Override
    public boolean shouldSave() {
        return false;
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) { }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) { }

    @Override
    public Packet<ClientPlayPacketListener> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }
}