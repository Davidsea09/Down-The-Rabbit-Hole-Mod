package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.entity.ai.goals.StalkerGoal;
import net.emanueljdf09.dtrhmod.entity.client.models.WeepingPlayerModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class WeepingPlayerEntity extends PathfinderMob implements GeoEntity {

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int poseChangeTimer = 0;

    public WeepingPlayerEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    private static final EntityDataAccessor<Integer> POSE_ID =
            SynchedEntityData.defineId(WeepingPlayerEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IS_SLIM_MODEL =
            SynchedEntityData.defineId(WeepingPlayerEntity.class, EntityDataSerializers.BOOLEAN);

    private static final EntityDataAccessor<Integer> SKIN_POOL_INDEX =
            SynchedEntityData.defineId(WeepingPlayerEntity.class, EntityDataSerializers.INT);

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(POSE_ID, this.random.nextInt(3));
        builder.define(SKIN_POOL_INDEX, this.random.nextInt(4) - 1);
        builder.define(IS_SLIM_MODEL, false);
    }

    public int getSkinPoolIndex() {
        return this.entityData.get(SKIN_POOL_INDEX);
    }

    public int getPoseId() {
        return this.entityData.get(POSE_ID);
    }

    public boolean isSlimModel() {
        return this.entityData.get(IS_SLIM_MODEL);
    }

    private boolean isAiFrozen() {
        if (this.goalSelector == null) {
            return false;
        }

        return this.goalSelector.getAvailableGoals().stream()
                .anyMatch(prioritizedGoal -> prioritizedGoal.isRunning()
                        && prioritizedGoal.getGoal() instanceof StalkerGoal stalker
                        && stalker.isFrozen());
    }

    @Override
    public void setYRot(float yaw) {
        if (this.isAiFrozen()) return;

        float snappedYaw = Math.round(yaw / 45.0f) * 45.0f;
        super.setYRot(snappedYaw);
        this.setYBodyRot(snappedYaw);
    }

    @Override
    public void setYHeadRot(float headYaw) {
        if (this.isAiFrozen()) return;

        float snappedHeadYaw = Math.round(headYaw / 45.0f) * 45.0f;
        super.setYHeadRot(snappedHeadYaw);
    }

    @Override
    public void setXRot(float pitch) {
        if (this.isAiFrozen()) return;
        super.setXRot(pitch);
    }

    @Override
    protected void registerGoals() {
        StalkerGoal stalkerGoal = new StalkerGoal(this);
        this.goalSelector.addGoal(0, stalkerGoal);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.22f)
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.FOLLOW_RANGE, 40.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7f)
                .add(Attributes.ATTACK_DAMAGE, 6.0f);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void push(Entity entity) {
        if (entity instanceof WeepingPlayerEntity) {
            double distanceSq = this.distanceToSqr(entity);
            if (distanceSq < 2.25D) {
                double dx = this.getX() - entity.getX();
                double dz = this.getZ() - entity.getZ();
                double f = Math.sqrt(dx * dx + dz * dz);
                if (f != 0.0) {
                    dx /= f;
                    dz /= f;
                    double pushPower = 0.1D;
                    this.push(dx * pushPower, 0.0D, dz * pushPower);
                }
            }
            return;
        }
        super.push(entity);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide()) {
            int poolIndex = this.getSkinPoolIndex();

            if (poolIndex != -1) {
                boolean shouldBeSlim = WeepingPlayerModel.isPoolSkinSlim(poolIndex);
                if (this.isSlimModel() != shouldBeSlim) {
                    this.entityData.set(IS_SLIM_MODEL, shouldBeSlim);
                }
            }

            if (!this.isAiFrozen()) {
                this.poseChangeTimer++;
                if (this.poseChangeTimer >= 100) {
                    this.poseChangeTimer = 0;
                    int newPose = this.random.nextInt(3);
                    this.entityData.set(POSE_ID, newPose);
                }
            } else {
                this.poseChangeTimer = 0;
            }
        }
    }

    @Override
    public @Nullable SpawnGroupData finalizeSpawn(ServerLevelAccessor world, DifficultyInstance difficulty, MobSpawnType spawnReason, @Nullable SpawnGroupData entityData) {
        SpawnGroupData data = super.finalizeSpawn(world, difficulty, spawnReason, entityData);
        populateDefaultEquipmentSlots(random, difficulty);
        return data;
    }

    protected void populateDefaultEquipmentSlots(RandomSource random, DifficultyInstance localDifficulty) {
        super.populateDefaultEquipmentSlots(random, localDifficulty);
        if (random.nextFloat() < (this.level().getDifficulty() == Difficulty.HARD ? 0.05F : 0.01F)) {
            int i = random.nextInt(3);
            if (i == 0) {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
            } else {
                this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
            }
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "poseController", 0, event -> {
            WeepingPlayerEntity entity = event.getAnimatable();
            int currentPose = entity.getPoseId();
            boolean isSlim = entity.isSlimModel();

            if (entity.level().isClientSide() && entity.getSkinPoolIndex() == -1) {
                Player localPlayer = Minecraft.getInstance().level.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), 24.0, false);
                if (localPlayer instanceof AbstractClientPlayer clientTarget) {
                    isSlim = clientTarget.getSkin().model() == net.minecraft.client.resources.PlayerSkin.Model.SLIM;
                }
            }

            if (isSlim) {
                switch (currentPose) {
                    case 0 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("alex_covering_eyes"));
                    case 1 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("alex_attack"));
                    case 2 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("alex_pointing"));
                    default -> event.getController().setAnimation(RawAnimation.begin().thenLoop("alex_covering_eyes"));
                }
            } else {
                switch (currentPose) {
                    case 0 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("covering_eyes"));
                    case 1 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("attack"));
                    case 2 -> event.getController().setAnimation(RawAnimation.begin().thenLoop("pointing"));
                    default -> event.getController().setAnimation(RawAnimation.begin().thenLoop("covering_eyes"));
                }
            }

            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}