package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.SingingFlowerEntity;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.VineAnchorEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class VineLassoProjectileEntity extends ThrowableItemProjectile {

    public VineLassoProjectileEntity(EntityType<? extends VineLassoProjectileEntity> type, Level world) {
        super(type, world);
    }

    public VineLassoProjectileEntity(Level world, LivingEntity owner) {
        super(ModEntities.VINE_LASSO_PROJECTILE, owner, world);
    }

    @Override
    protected Item getDefaultItem() {
        return Items.VINE;
    }

    @Override
    protected double getDefaultGravity() {
        return 0.015F;
    }

    @Override
    protected boolean canHitEntity(Entity entity) {
        if (entity instanceof VineAnchorEntity) return false;
        if (!(entity instanceof LivingEntity) || entity instanceof Player) return false;
        return super.canHitEntity(entity);
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        if (this.level().isClientSide()) return;
        if (!(hitResult.getEntity() instanceof LivingEntity target)) return;
        if (!(this.getOwner() instanceof Player player)) return;
        if (!(this.level() instanceof ServerLevel serverWorld)) return;

        if (target instanceof SingingFlowerEntity || target instanceof MadHatterEntity) {
            serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ITEM_BREAK, SoundSource.PLAYERS, 0.8f, 0.8f);
            return;
        }

        VineAnchorEntity anchor = ModEntities.VINE_ANCHOR.create(serverWorld);
        if (anchor != null) {
            anchor.setPosition(target.getX(), target.getY(), target.getZ());
            anchor.bind(player, target);
            serverWorld.addFreshEntity(anchor);
        }

        serverWorld.playSound(null, target.getX(), target.getY(), target.getZ(),
                SoundEvents.VEX_CHARGE, SoundSource.PLAYERS, 1.0f, 1.2f);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!this.level().isClientSide()) {
            this.discard();
        }
    }
}