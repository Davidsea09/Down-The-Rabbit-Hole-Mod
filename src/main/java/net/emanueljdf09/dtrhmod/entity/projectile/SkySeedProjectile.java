package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.entity.custom.VineAnchorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

public class SkySeedProjectile extends ThrownItemEntity {
    public static final float BLAST_RADIUS = 2.5f;
    private static final float DAMAGE = 6.0f;
    public SkySeedProjectile(EntityType type, World world) {
        super(type, world);
    }

    public SkySeedProjectile(World world, LivingEntity owner) {
        super(ModEntities.SKY_SEED, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.SPORE_BLOSSOM;
    }

    @Override
    protected float getGravity() {
        return 0.06F;
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof SingingFlowerEntity || entity instanceof VineAnchorEntity) return false;
        return super.canHit(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) {
            this.getWorld().addParticle(ParticleTypes.SPORE_BLOSSOM_AIR, this.getX(), this.getY(), this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient()) {
            burst();
            this.discard();
        }
    }

    private void burst() {
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        LivingEntity owner = this.getOwner() instanceof LivingEntity living ? living : null;
        Box area = this.getBoundingBox().expand(BLAST_RADIUS);

        for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area,
                e -> e.isAlive() && !(e instanceof SingingFlowerEntity))) {

            double dx = target.getX() - this.getX();
            double dz = target.getZ() - this.getZ();
            double reach = BLAST_RADIUS + target.getWidth() / 2.0;
            if (dx * dx + dz * dz > reach * reach) continue;

            target.damage(world.getDamageSources().mobProjectile(this, owner), DAMAGE);
        }

        world.spawnParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 0.2, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
        world.spawnParticles(ParticleTypes.CHERRY_LEAVES, this.getX(), this.getY() + 0.3, this.getZ(),
                25, BLAST_RADIUS * 0.4, 0.3, BLAST_RADIUS * 0.4, 0.05);
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.HOSTILE, 0.6F, 1.4F);
    }
}