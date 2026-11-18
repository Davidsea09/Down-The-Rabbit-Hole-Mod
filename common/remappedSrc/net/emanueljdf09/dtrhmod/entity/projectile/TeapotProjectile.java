package net.emanueljdf09.dtrhmod.entity.projectile;

import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.item.custom.DynamicTeaCupItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeapotProjectile extends ThrowableItemProjectile {
    private static final double SPLASH_RADIUS = 3.0D;
    private static final float DIRECT_DAMAGE = 6.0F;
    private static final float SPLASH_DAMAGE = 6.0F;

    @Nullable
    private Entity directHit = null;

    public TeapotProjectile(EntityType<? extends ThrowableItemProjectile> entityType, Level world) {
        super(entityType, world);
    }

    public TeapotProjectile(Level world, LivingEntity owner) {
        super(ModEntities.TEAPOT_PROJECTILE, owner, world);
        this.setItem(createPoisonedTeaStack());
    }

    public TeapotProjectile(Level world, LivingEntity owner, ItemStack stack) {
        super(ModEntities.TEAPOT_PROJECTILE, owner, world);
        this.setItem(stack);
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.EMPTY_CUP;
    }

    public static ItemStack createPoisonedTeaStack() {
        return createPoisonedTeaStack(0, 200);
    }

    public static ItemStack createPoisonedTeaStack(int amplifier, int durationTicks) {
        ItemStack stack = new ItemStack(ModItems.FILLED_TEA_CUP);
        CompoundTag nbt = new CompoundTag();
        ListTag effectsList = new ListTag();

        MobEffectInstance poisonEffect = new MobEffectInstance(MobEffects.POISON, durationTicks, amplifier);
        MobEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, poisonEffect)
                .result()
                .ifPresent(element -> effectsList.add((CompoundTag) element));

        nbt.put("Effects", effectsList);
        nbt.putString("FluidUsed", "minecraft:water_bucket");

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return stack;
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        Entity entity = entityHitResult.getEntity();
        if (isFriendly(entity)) return;

        this.directHit = entity;
        if (this.level() instanceof ServerLevel serverWorld) {
            entity.hurt(this.damageSources().thrown(this, this.getOwner()), DIRECT_DAMAGE);
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (!(this.level() instanceof ServerLevel serverWorld)) return;

        serverWorld.sendParticles(ParticleTypes.SPLASH,
                this.getX(), this.getY(), this.getZ(), 30, 0.5, 0.3, 0.5, 0.15);
        serverWorld.playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.SPLASH_POTION_BREAK, SoundSource.NEUTRAL, 1.0F, 1.0F);

        List<MobEffectInstance> effects = DynamicTeaCupItem.getEffectsFromStack(this.getItem(), serverWorld.registryAccess());
        Entity owner = this.getOwner();

        for (LivingEntity entity : serverWorld.getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(SPLASH_RADIUS + 1.0D))) {
            if (isFriendly(entity)) continue;

            double reach = SPLASH_RADIUS + entity.getBbWidth() / 2.0D;
            if (entity.distanceToSqr(this) > reach * reach) continue;

            if (entity != this.directHit) {
                entity.hurt(this.damageSources().indirectMagic(this, owner), SPLASH_DAMAGE);
            }

            for (MobEffectInstance effect : effects) {
                entity.addEffect(new MobEffectInstance(effect), owner);
            }
        }

        this.discard();
    }

    private boolean isFriendly(Entity entity) {
        Entity owner = this.getOwner();
        if (entity == owner) return true;
        return owner instanceof Mob && entity instanceof Enemy;
    }
}