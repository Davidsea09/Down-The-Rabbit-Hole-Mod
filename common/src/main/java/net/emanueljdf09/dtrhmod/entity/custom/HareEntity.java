package net.emanueljdf09.dtrhmod.entity.custom;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.OcelotAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class HareEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("run");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attack");

    public HareEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void registerGoals() {
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(2, new OcelotAttackGoal(this));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 3.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder setAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.MAX_HEALTH, 100.0f)
                .add(Attributes.FOLLOW_RANGE, 40.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7f)
                .add(Attributes.ATTACK_DAMAGE, 8.0f)
                .add(Attributes.ARMOR, 10.0f)
                .add(Attributes.ARMOR_TOUGHNESS, 4.0f);
    }

    @Override
    public int getBaseExperienceReward() {
        if (this.isBaby()) {
            this.xpReward = (int) ((double) this.xpReward * (double) 2.5F);
        }

        return super.getBaseExperienceReward();
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean bl = super.doHurtTarget(target);
        if (bl) {
            if (!this.level().isClientSide()) {
                this.triggerAnim("attack_controller", "attack");
            }

            float f = this.level().getCurrentDifficultyAt(this.blockPosition()).getEffectiveDifficulty();
            if (this.getMainHandItem().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                target.igniteForSeconds(2 * (int) f);
            }
        }

        return bl;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            if (state.isMoving()) {
                return state.setAndContinue(this.isAggressive() ? RUN : WALK);
            }
            return state.setAndContinue(IDLE);
        }));

        controllers.add(new AnimationController<>(this, "attack_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("attack", ATTACK));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}