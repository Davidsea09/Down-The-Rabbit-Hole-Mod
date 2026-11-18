package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.VineAnchorEntity;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

public class SkySeedProjectile extends ThrowableItemProjectile {
    public static final float BLAST_RADIUS = 2.5f;
    private static final float DAMAGE = 6.0f;
    public SkySeedProjectile(EntityType type, Level world) {
        super(type, world);
    }

    public SkySeedProjectile(Level world, LivingEntity owner) {
        super(ModEntities.SKY_SEED, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPORE_BLOSSOM;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.06F;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof SingingFlowerEntity || entity instanceof VineAnchorEntity) return false;
        return super.canHitEntity(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            this.level().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            burst();
            this.discard();
        }
    }

    private void burst() {
        if (!(this.level() instanceof ServerLevel world)) return;

        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        AABB area = this.getBoundingBox().inflate(BLAST_RADIUS);

        for (LivingEntity target : world.getEntitiesOfClass(LivingEntity.class, area,
                e -> e.isAlive() && !(e instanceof SingingFlowerEntity))) {

            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double reach = BLAST_RADIUS + target.getBbWidth() / 2.0;
            if (dx * dx + dz * dz > reach * reach) continue;

            target.hurt(world.damageSources().mobProjectile(this, owner), DAMAGE);
        }

        world.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        world.sendParticles(ParticleTypes.CHERRY_LEAVES, this.getX(), this.getY() + 0.3, this.getZ(),
                25, BLAST_RADIUS * 0.4, 0.3, BLAST_RADIUS * 0.4, 0.05);
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 0.6F, 1.4F);
    }
}