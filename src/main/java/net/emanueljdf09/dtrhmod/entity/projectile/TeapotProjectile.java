package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.item.custom.DynamicTeaCupItem;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

import java.util.List;

public class TeapotProjectile extends ThrownItemEntity {
    public TeapotProjectile(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public TeapotProjectile(World world, LivingEntity owner) {
        super(ModEntities.TEAPOT_PROJECTILE, owner, world);

        this.setItem(createPoisonedTeaStack());
    }

    public TeapotProjectile(World world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.TEAPOT_PROJECTILE, owner, world);
        this.setItem(stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.FILLED_TEA_CUP;
    }

    public static ItemStack createPoisonedTeaStack() {
        ItemStack stack = new ItemStack(ModItems.FILLED_TEA_CUP);
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList effectsList = new NbtList();

        StatusEffectInstance poisonEffect = new StatusEffectInstance(StatusEffects.POISON, 200, 0);
        NbtCompound effectNbt = new NbtCompound();
        poisonEffect.writeNbt(effectNbt);
        effectsList.add(effectNbt);

        nbt.put("Effects", effectsList);
        nbt.putString("FluidUsed", "minecraft:water_bucket");
        return stack;
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), 6.0F);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!this.getWorld().isClient()) {

            for (int i = 0; i < 20; i++) {
                this.getWorld().addParticle(
                        ParticleTypes.SPLASH,
                        this.getX(), this.getY(), this.getZ(),
                        this.random.nextGaussian() * 0.2D,
                        this.random.nextGaussian() * 0.2D,
                        this.random.nextGaussian() * 0.2D
                );
            }

            ItemStack projectileStack = this.getStack();
            List<StatusEffectInstance> effects = DynamicTeaCupItem.getEffectsFromStack(projectileStack);

            for (LivingEntity entity : this.getWorld().getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(3.0D))) {
                if (entity != this.getOwner()) {
                    entity.damage(this.getDamageSources().magic(), 6.0f);

                    for (StatusEffectInstance effect : effects) {
                        entity.addStatusEffect(new StatusEffectInstance(effect));
                    }
                }
            }

            this.discard();
        }
    }
}
