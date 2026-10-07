package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.item.custom.DynamicTeaCupItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeapotProjectile extends ThrownItemEntity {
    private static final double SPLASH_RADIUS = 3.0D;
    private static final float DIRECT_DAMAGE = 6.0F;
    private static final float SPLASH_DAMAGE = 6.0F;

    @Nullable
    private Entity directHit = null;

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
        return createPoisonedTeaStack(0, 200);
    }

    public static ItemStack createPoisonedTeaStack(int amplifier, int durationTicks) {
        ItemStack stack = new ItemStack(ModItems.FILLED_TEA_CUP);
        NbtCompound nbt = stack.getOrCreateNbt();
        NbtList effectsList = new NbtList();

        StatusEffectInstance poisonEffect = new StatusEffectInstance(StatusEffects.POISON, durationTicks, amplifier);
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
        if (isFriendly(entity)) return;

        this.directHit = entity;
        entity.damage(this.getDamageSources().thrown(this, this.getOwner()), DIRECT_DAMAGE);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        if (!(this.getWorld() instanceof ServerWorld world)) return;

        world.spawnParticles(ParticleTypes.SPLASH,
                this.getX(), this.getY(), this.getZ(), 30, 0.5, 0.3, 0.5, 0.15);
        world.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.ENTITY_SPLASH_POTION_BREAK, SoundCategory.NEUTRAL, 1.0F, 1.0F);

        List<StatusEffectInstance> effects = DynamicTeaCupItem.getEffectsFromStack(this.getStack());
        Entity owner = this.getOwner();

        for (LivingEntity entity : world.getNonSpectatingEntities(LivingEntity.class, this.getBoundingBox().expand(SPLASH_RADIUS + 1.0D))) {
            if (isFriendly(entity)) continue;

            double reach = SPLASH_RADIUS + entity.getWidth() / 2.0D;
            if (entity.squaredDistanceTo(this) > reach * reach) continue;

            if (entity != this.directHit) {
                entity.damage(this.getDamageSources().indirectMagic(this, owner), SPLASH_DAMAGE);
            }

            for (StatusEffectInstance effect : effects) {
                entity.addStatusEffect(new StatusEffectInstance(effect), owner);
            }
        }

        this.discard();
    }

    private boolean isFriendly(Entity entity) {
        Entity owner = this.getOwner();
        if (entity == owner) return true;
        return owner instanceof MobEntity && entity instanceof Monster;
    }
}