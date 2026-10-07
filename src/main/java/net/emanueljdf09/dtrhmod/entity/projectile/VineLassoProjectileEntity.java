package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.entity.custom.VineAnchorEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class VineLassoProjectileEntity extends ThrownItemEntity {

    public VineLassoProjectileEntity(EntityType<? extends VineLassoProjectileEntity> type, World world) {
        super(type, world);
    }

    public VineLassoProjectileEntity(World world, LivingEntity owner) {
        super(ModEntities.VINE_LASSO_PROJECTILE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.VINE;
    }

    @Override
    protected float getGravity() {
        return 0.015F;
    }

    @Override
    protected boolean canHit(Entity entity) {
        if (entity instanceof VineAnchorEntity) return false;
        if (!(entity instanceof LivingEntity) || entity instanceof PlayerEntity) return false;
        return super.canHit(entity);
    }

    @Override
    protected void onEntityHit(EntityHitResult hitResult) {
        if (this.getWorld().isClient()) return;
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        if (!(this.getOwner() instanceof PlayerEntity player)) return;
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        if (target instanceof SingingFlowerEntity || target instanceof MadHatterEntity) {
            serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.PLAYERS, 0.8f, 0.8f);
            return;
        }

        VineAnchorEntity anchor = ModEntities.VINE_ANCHOR.create(serverWorld);
        if (anchor != null) {
            anchor.setPosition(target.getX(), target.getY(), target.getZ());
            anchor.bind(player, target);
            serverWorld.spawnEntity(anchor);
        }

        serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.ENTITY_VEX_CHARGE, SoundCategory.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient()) {
            this.discard();
        }
    }
}