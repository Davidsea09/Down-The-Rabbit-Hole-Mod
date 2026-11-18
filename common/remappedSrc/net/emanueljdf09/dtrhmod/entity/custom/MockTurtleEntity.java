package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class MockTurtleEntity extends PathfinderMob implements GeoEntity {
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation SOB = RawAnimation.begin().thenPlay("sob");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");

    private static final EntityDataAccessor<Boolean> IS_SOBBING = SynchedEntityData.defineId(MockTurtleEntity.class, EntityDataSerializers.BOOLEAN);

    private int sobTicks = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public MockTurtleEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_SOBBING, false);
    }

    public boolean isSobbing() {
        return this.entityData.get(IS_SOBBING);
    }


    public void setSobbing(boolean sobbing) {
        this.entityData.set(IS_SOBBING, sobbing);
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.is(Items.BOWL)) {
            this.playSound(SoundEvents.MOOSHROOM_MILK, 1.0f, 1.0f);

            ItemStack soupStack = new ItemStack(ModItems.MOCK_TURTLE_STEW);

            itemStack.shrink(1);
            if (itemStack.isEmpty()) {
                player.setItemInHand(hand, soupStack);
            } else if (!player.getInventory().add(soupStack)) {
                player.drop(soupStack, false);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide());
        }

        return super.mobInteract(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            if (isSobbing()) {
                sobTicks--;
                if (sobTicks <= 0) {
                    setSobbing(false);
                }
            } else {
               if (!this.isMoving() && this.random.nextFloat() < 0.005f) {
                    setSobbing(true);
                    sobTicks = 40;
                }
            }
        }
    }

    private boolean isMoving() {
        return this.getDeltaMovement().horizontalDistanceSqr() > 0.002;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "poseController", 0, event -> {
            if (event.isMoving() || this.getNavigation().isInProgress() || this.isMoving()) {
                event.getController().setAnimationSpeed(1.0D);
                return event.setAndContinue(WALK);
            }

            if (this.isSobbing()) {
                return event.setAndContinue(SOB);
            }

            return event.setAndContinue(IDLE);
        }));
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 2.0f, 1.0f));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 1.0f));}

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}
