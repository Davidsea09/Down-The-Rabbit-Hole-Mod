package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.fabric.entity.custom.SingingFlowerEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class VineAnchorEntity extends Entity {
    private static final EntityDataAccessor<Integer> OWNER_ID = SynchedEntityData.defineId(VineAnchorEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> VICTIM_ID = SynchedEntityData.defineId(VineAnchorEntity.class, EntityDataSerializers.INT);

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

    public VineAnchorEntity(EntityType<?> type, Level world) {
        super(type, world);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(OWNER_ID, -1);
        builder.define(VICTIM_ID, -1);
    }

    public int getOwnerId() { return this.entityData.get(OWNER_ID); }
    public int getVictimId() { return this.entityData.get(VICTIM_ID); }

    public void bind(Entity owner, LivingEntity victim) {
        this.owner = owner;
        this.victim = victim;
        this.holdX = victim.getX();
        this.holdZ = victim.getZ();
        this.entityData.set(OWNER_ID, owner.getId());
        this.entityData.set(VICTIM_ID, victim.getId());
        updateAnchorPosition();

        this.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.GRASS_PLACE, SoundSource.HOSTILE, 1.2F, 0.7F);
        this.level().playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                SoundEvents.LEASH_KNOT_PLACE, SoundSource.HOSTILE, 1.0F, 0.8F);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        ticksAlive++;

        if (owner == null || victim == null
                || owner.isRemoved() || !owner.isAlive()
                || victim.isRemoved() || !victim.isAlive()
                || victim.level() != this.level()) {
            release();
            return;
        }

        if (owner instanceof net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity flower) {
            if (flower.getBurrowState() != net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity.BurrowState.SURFACED) {
                release();
                return;
            }
        }

        int maxLifetime = (owner instanceof Player) ? PLAYER_MAX_LIFETIME : FLOWER_MAX_LIFETIME;

        if (ticksAlive > maxLifetime) {
            if (owner instanceof net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity flower) {
                flower.startle(net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity.STARTLE_VINE_EXPIRE_TICKS);
            }
            release();
            return;
        }

        if (ticksAlive % 5 == 0) {
            victim.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, SLOW_AMPLIFIER, false, false, false));
        }

        double dx = victim.getX() - holdX;
        double dz = victim.getZ() - holdZ;
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > LEASH_RADIUS) {
            double k = LEASH_RADIUS / dist;
            victim.teleportTo(holdX + dx * k, victim.getY(), holdZ + dz * k);
        }

        updateAnchorPosition();

        if (ticksAlive % DRAIN_INTERVAL == 0) {
            drain();
        }
    }

    private void drain() {
        if (owner == null || victim == null) return;

        float before = victim.getHealth();
        DamageSource source = owner instanceof Player ? this.level().damageSources().playerAttack((Player) owner)
                : this.level().damageSources().indirectMagic(this, owner);

        victim.hurt(source, DRAIN_DAMAGE);
        float dealt = before - victim.getHealth();

        if (dealt > 0 && this.level() instanceof ServerLevel world) {
            if (owner instanceof net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity flower) {
                flower.heal(dealt);
            } else if (owner instanceof Player player) {
                player.heal(dealt);
            }

            world.sendParticles(ParticleTypes.HEART,
                    victim.getX(), victim.getY(0.7), victim.getZ(), 2, 0.3, 0.3, 0.3, 0.0);
            world.sendParticles(ParticleTypes.DAMAGE_INDICATOR,
                    victim.getX(), victim.getY(0.5), victim.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
            world.playSound(null, victim.getX(), victim.getY(), victim.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.HOSTILE, 0.6F, 0.8F);
        }
    }

    private void updateAnchorPosition() {
        if (owner == null || victim == null) return;
        Vec3 toOwner = owner.position().subtract(victim.position()).multiply(1, 0, 1);
        double len = toOwner.length();
        if (len < 0.01) return;

        double offset = Mth.clamp(len * 0.4, 1.5, 3.0);
        Vec3 dir = toOwner.normalize();
        this.setPos(victim.getX() + dir.x * offset, victim.getY() + 0.3, victim.getZ() + dir.z * offset);
    }

    private void release() {
        this.discard();
    }

    @Override
    public boolean isPickable() {
        return !isPlayerOwned();
    }

    private boolean isPlayerOwned() {
        return this.level().getEntity(getOwnerId()) instanceof Player;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (isPlayerOwned()) return false;

        if (this.level().isClientSide() || this.isRemoved()) return false;

        vineHealth -= amount;
        if (this.level() instanceof ServerLevel world) {
            world.sendParticles(ParticleTypes.COMPOSTER, this.getX(), this.getY() + 0.6, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
        this.playSound(SoundEvents.GRASS_BREAK, 1.0F, 1.0F);

        if (vineHealth <= 0) {
            if (owner instanceof net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity flower && !flower.isRemoved()) {
                flower.startle(net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity.STARTLE_VINE_CUT_TICKS);
            }
            this.playSound(SoundEvents.WEEPING_VINES_BREAK, 1.2F, 0.8F);
            release();
        }
        return true;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag nbt) { }

    @Override
    protected void addAdditionalSaveData(CompoundTag nbt) { }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket(ServerEntity entityTrackerEntry) {
        return new ClientboundAddEntityPacket(this, entityTrackerEntry);
    }
}