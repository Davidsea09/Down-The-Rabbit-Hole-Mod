package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.entity.ai.goals.RunToRabbitHoleGoal;
import net.emanueljdf09.dtrhmod.entity.ai.goals.WanderAroundHoleGoal;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.*;
import software.bernie.geckolib.util.GeckoLibUtil;
import java.util.Optional;

public class WhiteRabbitEntity extends PathfinderMob implements GeoEntity {
    protected static final RawAnimation IDLE = RawAnimation.begin().thenLoop("animation.white_rabbit.idle");
    protected static final RawAnimation IDLEBREAK = RawAnimation.begin().thenLoop("animation.white_rabbit.idle2");
    protected static final RawAnimation RUN = RawAnimation.begin().thenLoop("animation.white_rabbit.run");
    protected static final RawAnimation GREET = RawAnimation.begin().thenLoop("animation.white_rabbit.greet");

    private static final EntityDataAccessor<Optional<BlockPos>> RABBIT_HOLE_POS = SynchedEntityData.defineId(WhiteRabbitEntity.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private static final EntityDataAccessor<Boolean> IS_RETURNING = SynchedEntityData.defineId(WhiteRabbitEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_GREETING = SynchedEntityData.defineId(WhiteRabbitEntity.class, EntityDataSerializers.BOOLEAN);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private int greetTicks = 0;

    public WhiteRabbitEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.3f)
                .add(Attributes.MAX_HEALTH, 20.0f)
                .add(Attributes.FOLLOW_RANGE, 40.0f)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.7f);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(RABBIT_HOLE_POS, Optional.empty());
        builder.define(IS_RETURNING, false);
        builder.define(IS_GREETING, false);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new RunToRabbitHoleGoal(this));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 2.0f, 1.0f));
        this.goalSelector.addGoal(3, new WanderAroundHoleGoal(this, 30));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            ResourceKey<Level> currentWorldKey = this.level().dimension();

            if (currentWorldKey == Level.OVERWORLD) {
                if (getRabbitHolePos().isEmpty()) {
                    BlockPos foundHole = findNearbyRabbitHole();
                    if (foundHole != null) {
                        setRabbitHolePos(foundHole);
                    } else {
                        setRabbitHolePos(this.blockPosition());
                    }
                }

                if (isGreeting()) {
                    greetTicks++;

                    Player closestPlayer = this.level().getNearestPlayer(this, 5.0D);
                    if (closestPlayer != null) {
                        this.getLookControl().setLookAt(closestPlayer, 30.0F, 30.0F);
                    }

                    if (greetTicks >= 40) {
                        setGreeting(false);
                        greetTicks = 0;
                        setReturning(true);
                    }
                }

                if (isReturning() && getRabbitHolePos().isPresent()) {
                    if (this.blockPosition().closerThan(getRabbitHolePos().get(), 4)) {
                        if (this.level() instanceof ServerLevel serverWorld) {
                            serverWorld.sendParticles(
                                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                                    this.getX(), this.getY() + 0.5D, this.getZ(),
                                    15,
                                    0.2D, 0.2D, 0.2D,
                                    0.05D
                            );

                            serverWorld.playSound(
                                    null, this.getX(), this.getY(), this.getZ(),
                                    SoundEvents.CHICKEN_EGG,
                                    SoundSource.NEUTRAL,
                                    1.0F, 1.5F
                            );
                        }

                        this.discard();
                    }
                }
            }
        }
    }

    private BlockPos findNearbyRabbitHole() {
        BlockPos entityPos = this.blockPosition();
        int radius = 50;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos checkPos = entityPos.offset(x, y, z);
                    if (this.level().getBlockState(checkPos).is(ModBlocks.RABBIT_HOLE.get())) {
                        return checkPos;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.level().isClientSide()) {
            if (this.isReturning()) {
                this.level().addParticle(
                        ParticleTypes.POOF,
                        this.getX(), this.getY(), this.getZ(),
                        0.0D, 0.0D, 0.0D
                );

                if (this.random.nextFloat() < 0.4f) {
                    this.level().addParticle(
                            ParticleTypes.SPLASH,
                            this.getX() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            this.getEyeY() + 0.2D,
                            this.getZ() + (this.random.nextDouble() - 0.5D) * this.getBbWidth(),
                            0.0D, 0.1D, 0.0D
                    );
                }
            }
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 hitPos, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND || this.level().isClientSide) {
            return InteractionResult.PASS;
        }

        ResourceKey<Level> currentWorldKey = this.level().dimension();

        if (player instanceof ServerPlayer serverPlayer) {
            MinecraftServer server = serverPlayer.getServer();
            if (server == null) return InteractionResult.PASS;

            ProgressionComponent component = PlayerProgressionManager.get(server).get(serverPlayer.getUUID());

            if (currentWorldKey == Level.OVERWORLD) {
                if (isReturning() || isGreeting()) return InteractionResult.PASS;

                if (!component.hasMetWhiteRabbit()) {
                    player.displayClientMessage(Component.translatable("entity.dtrhmod.white_rabbit.greet_msg"), false);

                    component.setMetWhiteRabbit(true);
                    component.setMetInOverworld(true);

                    setGreeting(true);
                    this.getNavigation().stop();
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(Component.translatable("entity.dtrhmod.white_rabbit.busy_msg"), false);
                    return InteractionResult.SUCCESS;
                }
            } else {
                if (component.hasMetInOverworld()) {
                    player.displayClientMessage(Component.translatable("entity.dtrhmod.white_rabbit.wonderland_remember_msg"), false);
                } else {
                    player.displayClientMessage(Component.translatable("entity.dtrhmod.white_rabbit.wonderland_stranger_msg"), false);
                    component.setMetWhiteRabbit(true);
                }
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 4, this::predicate));
    }

    protected <E extends WhiteRabbitEntity> PlayState predicate(final AnimationState<E> event) {
        if (this.isGreeting()) {
            event.getController().setAnimationSpeed(1.0D);
            return event.setAndContinue(GREET);
        }

        if (this.isReturning()) {
            event.getController().setAnimationSpeed(1.5D);
            return event.setAndContinue(RUN);
        }

        if (event.isMoving() || this.getNavigation().isInProgress() || this.getDeltaMovement().horizontalDistanceSqr() > 0.002) {
            event.getController().setAnimationSpeed(1.0D);
            return event.setAndContinue(RUN);
        }
        event.getController().setAnimationSpeed(1.0D);

        return event.setAndContinue(this.random.nextFloat() < 0.3f ? IDLEBREAK : IDLE);
    }

    public Optional<BlockPos> getRabbitHolePos() { return this.entityData.get(RABBIT_HOLE_POS); }
    public void setRabbitHolePos(BlockPos pos) { this.entityData.set(RABBIT_HOLE_POS, Optional.ofNullable(pos)); }
    public boolean isReturning() { return this.entityData.get(IS_RETURNING); }
    public void setReturning(boolean returning) { this.entityData.set(IS_RETURNING, returning); }
    public boolean isGreeting() { return this.entityData.get(IS_GREETING); }
    public void setGreeting(boolean greeting) { this.entityData.set(IS_GREETING, greeting); }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        getRabbitHolePos().ifPresent(blockPos -> nbt.put("RabbitHolePos", NbtUtils.writeBlockPos(blockPos)));
        nbt.putBoolean("IsReturning", isReturning());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if (nbt.contains("RabbitHolePos")) {
            NbtUtils.readBlockPos(nbt, "RabbitHolePos").ifPresent(this::setRabbitHolePos);
        }
        setReturning(nbt.getBoolean("IsReturning"));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { return this.cache; }
}