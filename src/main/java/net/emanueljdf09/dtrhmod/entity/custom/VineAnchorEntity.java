package net.emanueljdf09.dtrhmod.entity.custom;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
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
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class VineAnchorEntity extends Entity {
    private static final TrackedData<Integer> FLOWER_ID = DataTracker.registerData(VineAnchorEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> VICTIM_ID = DataTracker.registerData(VineAnchorEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final float VINE_HEALTH = 4.0f;
    private static final int MAX_LIFETIME = 240;
    private static final int DRAIN_INTERVAL = 20;
    private static final float DRAIN_DAMAGE = 2.0f;
    private static final double LEASH_RADIUS = 2.5;
    private static final int SLOW_AMPLIFIER = 3;

    @Nullable
    private SingingFlowerEntity flower;
    @Nullable
    private ServerPlayerEntity victim;
    private double holdX, holdZ;
    private float vineHealth = VINE_HEALTH;
    private int ticksAlive = 0;

    public VineAnchorEntity(EntityType<?> type, World world) {
        super(type, world);
        this.setNoGravity(true);
    }

    @Override
    protected void initDataTracker() {
        this.dataTracker.startTracking(FLOWER_ID, -1);
        this.dataTracker.startTracking(VICTIM_ID, -1);
    }

    public int getFlowerId() { return this.dataTracker.get(FLOWER_ID); }
    public int getVictimId() { return this.dataTracker.get(VICTIM_ID); }

    public void bind(SingingFlowerEntity flower, ServerPlayerEntity victim) {
        this.flower = flower;
        this.victim = victim;
        this.holdX = victim.getX();
        this.holdZ = victim.getZ();
        this.dataTracker.set(FLOWER_ID, flower.getId());
        this.dataTracker.set(VICTIM_ID, victim.getId());
        updateAnchorPosition();
    }


    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;

        ticksAlive++;

        if (flower == null || victim == null
                || flower.isRemoved() || !flower.isAlive()
                || flower.getBurrowState() != SingingFlowerEntity.BurrowState.SURFACED // flower buried: lets go
                || victim.isRemoved() || !victim.isAlive() || victim.isSpectator()
                || victim.getWorld() != this.getWorld()
                || ticksAlive > MAX_LIFETIME) {
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
        if (flower == null || victim == null) return;

        float before = victim.getHealth();
        victim.damage(this.getWorld().getDamageSources().indirectMagic(this, flower), DRAIN_DAMAGE);
        float dealt = before - victim.getHealth();

        if (dealt > 0 && this.getWorld() instanceof ServerWorld world) {
            flower.heal(dealt);
            world.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                    flower.getX(), flower.getBodyY(0.7), flower.getZ(), 4, 0.3, 0.3, 0.3, 0.0);
            world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR,
                    victim.getX(), victim.getBodyY(0.5), victim.getZ(), 2, 0.2, 0.2, 0.2, 0.0);
        }
    }

    private void updateAnchorPosition() {
        if (flower == null || victim == null) return;
        Vec3d toFlower = flower.getPos().subtract(victim.getPos()).multiply(1, 0, 1);
        double len = toFlower.length();
        if (len < 0.01) return;

        double offset = MathHelper.clamp(len * 0.4, 1.5, 3.0);
        Vec3d dir = toFlower.normalize();
        this.setPosition(victim.getX() + dir.x * offset, victim.getY() + 0.3, victim.getZ() + dir.z * offset);
    }

    private void release() {
        this.discard();
    }

    @Override
    public boolean canHit() {
        return true;
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.getWorld().isClient() || this.isRemoved()) return false;
        // only players (melee or their arrows) can cut the vine
        if (!(source.getAttacker() instanceof PlayerEntity)) return false;

        vineHealth -= amount;
        if (this.getWorld() instanceof ServerWorld world) {
            world.spawnParticles(ParticleTypes.COMPOSTER, this.getX(), this.getY() + 0.6, this.getZ(), 8, 0.3, 0.3, 0.3, 0.05);
        }
        this.playSound(SoundEvents.BLOCK_GRASS_BREAK, 1.0F, 1.0F);

        if (vineHealth <= 0) {
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