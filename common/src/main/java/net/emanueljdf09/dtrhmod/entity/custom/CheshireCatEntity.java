package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.Structure;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CheshireCatEntity extends Mob implements GeoEntity {
    private static final EntityDataAccessor<Integer> INVISIBILITY_STATE = SynchedEntityData.defineId(CheshireCatEntity.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IS_GRINNING = SynchedEntityData.defineId(CheshireCatEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_TRANSITIONING = SynchedEntityData.defineId(CheshireCatEntity.class, EntityDataSerializers.BOOLEAN);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation IDLE_INVIS1 = RawAnimation.begin().thenLoop("idle_invis1");
    private static final RawAnimation IDLE_INVIS2 = RawAnimation.begin().thenLoop("idle_invis2");

    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walk");
    private static final RawAnimation WALK_INVIS1 = RawAnimation.begin().thenLoop("walk_invis1");
    private static final RawAnimation WALK_INVIS2 = RawAnimation.begin().thenLoop("walk_invis2");

    private static final RawAnimation GRIN = RawAnimation.begin().thenPlay("grin");
    private static final RawAnimation GRIN_INVIS1 = RawAnimation.begin().thenPlay("grin_invis1");
    private static final RawAnimation GRIN_INVIS2 = RawAnimation.begin().thenPlay("grin_invis2");
    private static final RawAnimation TURN_INVIS_1 = RawAnimation.begin().thenPlay("turn_invis1");
    private static final RawAnimation TURN_INVIS_2 = RawAnimation.begin().thenPlay("turn_invis2");
    private static final RawAnimation TURN_INVIS1_FROM_INVIS2 = RawAnimation.begin().thenPlay("turn_invis1_from_invis2");
    private static final RawAnimation TURN_NORMAL_FROM_INVIS1 = RawAnimation.begin().thenPlay("turn_normal_from_invis1");

    private BlockPos homePosition = null;
    private int stateChangeCooldown = 0;
    private int targetInvisState = 0;
    private int transitionTicks = 0;
    private int grinTicks = 0;

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CheshireCatEntity(EntityType<? extends Mob> entityType, Level world) {
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
        builder.define(INVISIBILITY_STATE, 0);
        builder.define(IS_GRINNING, false);
        builder.define(IS_TRANSITIONING, false);
    }


    public int getInvisibilityState() {
        return this.entityData.get(INVISIBILITY_STATE);
    }

    public void setInvisibilityState(int state) {
        this.entityData.set(INVISIBILITY_STATE, state);
    }

    public boolean isGrinning() {
        return this.entityData.get(IS_GRINNING);
    }

    public void setGrinning(boolean grinning) {
        this.entityData.set(IS_GRINNING, grinning);
    }

    public boolean isTransitioning() {
        return this.entityData.get(IS_TRANSITIONING);
    }

    public void setTransitioning(boolean transitioning) {
        this.entityData.set(IS_TRANSITIONING, transitioning);
    }

    @Override
    public void tick() {
        super.tick();

        if (isTransitioning()) {
            transitionTicks--;
            if (transitionTicks <= 0) {
                setTransitioning(false);
                setInvisibilityState(targetInvisState);
            }
        }

        if (isGrinning()) {
            grinTicks--;
            if (grinTicks <= 0) {
                setGrinning(false);
            }
        }

        if (!this.level().isClientSide()) {
            stateChangeCooldown++;
            if (stateChangeCooldown > 120 && !isTransitioning()) {
                stateChangeCooldown = 0;

                if (this.random.nextFloat() < 0.3f) {
                    triggerGrin();
                } else {
                    int currentState = getInvisibilityState();
                    int nextState;

                    if (currentState == 0) {
                        nextState = 1;
                    } else if (currentState == 1) {
                        nextState = this.random.nextBoolean() ? 0 : 2;
                    } else {
                        nextState = 1;
                    }

                    if (nextState != currentState) {
                        startTransitionTo(nextState);
                    }
                }
            }
        }
    }


    private void teleportNearTarget() {
        if (this.level() instanceof ServerLevel serverWorld) {

            if (this.homePosition == null) {
                this.homePosition = this.blockPosition();
            }

            double x = this.homePosition.getX() + (this.random.nextDouble() - 0.5) * 12.0;
            double y = this.homePosition.getY();
            double z = this.homePosition.getZ() + (this.random.nextDouble() - 0.5) * 12.0;

            this.randomTeleport(x,y,z, true);
            this.getNavigation().stop();
        }
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new LookAtPlayerGoal(this, Player.class, 6.0F));
    }

    public void startTransitionTo(int newState) {
        int currentState = getInvisibilityState();
        this.targetInvisState = newState;
        setTransitioning(true);
        this.transitionTicks = 40;

        if (!this.level().isClientSide()) {
            if (currentState == 2 && newState == 1) {
                this.triggerAnim("action_controller", "turn_invis1_from_invis2_anim");
            } else if (currentState == 1 && newState == 0) {
                this.triggerAnim("action_controller", "turn_normal_from_invis1_anim");
            } else if (newState == 2) {
                this.triggerAnim("action_controller", "turn_invis_2_anim");
                teleportNearTarget();
            } else {
                this.triggerAnim("action_controller", "turn_invis_1_anim");
            }
        }
    }

    public void triggerGrin() {
        setGrinning(true);
        this.grinTicks = 30;

        if (!this.level().isClientSide()) {
            int invisState = getInvisibilityState();
            if (invisState == 2) {
                this.triggerAnim("action_controller", "grin_invis2_anim");
            } else if (invisState == 1) {
                this.triggerAnim("action_controller", "grin_invis1_anim");
            } else {
                this.triggerAnim("action_controller", "grin_anim");
            }
        }
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            int invisState = this.getInvisibilityState();
            boolean isMoving = this.xxa != 0.0f || this.zza != 0.0f ||
                    this.getDeltaMovement().horizontalDistanceSqr() > 0.00001;

            if (isMoving) {
                if (invisState == 2) return state.setAndContinue(WALK_INVIS2);
                if (invisState == 1) return state.setAndContinue(WALK_INVIS1);
                return state.setAndContinue(WALK);
            } else {
                if (invisState == 2) return state.setAndContinue(IDLE_INVIS2);
                if (invisState == 1) return state.setAndContinue(IDLE_INVIS1);
                return state.setAndContinue(IDLE);
            }
        }));

        controllers.add(new AnimationController<>(this, "action_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("grin_anim", GRIN)
                .triggerableAnim("grin_invis1_anim", GRIN_INVIS1)
                .triggerableAnim("grin_invis2_anim", GRIN_INVIS2)
                .triggerableAnim("turn_invis_1_anim", TURN_INVIS_1)
                .triggerableAnim("turn_invis_2_anim", TURN_INVIS_2)
                .triggerableAnim("turn_invis1_from_invis2_anim", TURN_INVIS1_FROM_INVIS2)
                .triggerableAnim("turn_normal_from_invis1_anim", TURN_NORMAL_FROM_INVIS1));
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (hand == InteractionHand.MAIN_HAND && !this.level().isClientSide()) {
            ItemStack stack = player.getItemInHand(hand);

            if (stack.is(Items.GOLDEN_APPLE)) {
                if (!player.getAbilities().instabuild) {
                    stack.shrink(1);
                }

                triggerGrin();

                giveStructureClue(player);

                AdvancementUtil.grant((ServerPlayer) player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cheshire_cat_snitch"));


                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.CAT_PURR,
                        SoundSource.NEUTRAL, 1.0f, 1.0f);

                return InteractionResult.SUCCESS;
            } else {
                String[] confusedQuotes = {
                        "entity.dtrhmod.cheshire_cat.confused1",
                        "entity.dtrhmod.cheshire_cat.confused2",
                        "entity.dtrhmod.cheshire_cat.confused3",
                        "entity.dtrhmod.cheshire_cat.confused4",
                        "entity.dtrhmod.cheshire_cat.confused5"
                };

                String selectedKey = confusedQuotes[this.random.nextInt(confusedQuotes.length)];

                player.displayClientMessage(Component.translatable(selectedKey), true);
                int nextState = (getInvisibilityState() + 1) % 3;
                startTransitionTo(nextState);
                return InteractionResult.SUCCESS;
            }
        }
        return super.mobInteract(player, hand);
    }

    private void giveStructureClue(Player player) {
        if (!(this.level() instanceof ServerLevel serverWorld)) return;

        DownTheRabbitHole.LOGGER.info("CheshireCat: Attempting to locate 'tulgey_wood/tea_party' structure for player " + player.getName().getString());

        ResourceLocation structureId = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "tulgey_wood/tea_party");
        ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, structureId);

        Holder<Structure> structureEntry = serverWorld.registryAccess()
                .registryOrThrow(Registries.STRUCTURE)
                .getHolder(structureKey)
                .orElse(null);

        if (structureEntry != null) {
            net.minecraft.core.HolderSet<Structure> structureHolderSet =
                    net.minecraft.core.HolderSet.direct(structureEntry);

            com.mojang.datafixers.util.Pair<BlockPos, Holder<Structure>> structureResult =
                    serverWorld.getChunkSource().getGenerator().findNearestMapStructure(
                            serverWorld,
                            structureHolderSet,
                            this.blockPosition(),
                            100,
                            false
                    );

            if (structureResult != null) {

                BlockPos structurePos = structureResult.getFirst();

                DownTheRabbitHole.LOGGER.info("CheshireCat: Successfully found structure at X: " + structurePos.getX() + ", Z: " + structurePos.getZ());

                int deltaX = structurePos.getX() - this.blockPosition().getX();
                int deltaZ = structurePos.getZ() - this.blockPosition().getZ();

                String cardinalDirection = getCardinalDirection(deltaX, deltaZ);
                int estimatedBlocks = (int) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                player.displayClientMessage(Component.translatable(
                        "entity.dtrhmod.cheshire_cat.tea_party_clue",
                        estimatedBlocks,
                        cardinalDirection
                ), false);

                serverWorld.sendParticles(ParticleTypes.ENCHANT,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.2);
                return;
            }
        }

        DownTheRabbitHole.LOGGER.warn("CheshireCat: Could not find structure entry or chunk generator location returned null.");
        player.sendSystemMessage(Component.translatable("entity.dtrhmod.cheshire_cat.confused2", false));
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void dropFromLootTable(DamageSource damageSource, boolean causedByPlayer) {
        super.dropFromLootTable(damageSource, causedByPlayer);

        if (this.random.nextFloat() < 0.2f) {
            this.spawnAtLocation(ModItems.CAT_FUR.get());
        }

        if (causedByPlayer) {
            int count = 1 + this.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                this.spawnAtLocation(Items.BONE);
            }
        }
    }

    private String getCardinalDirection(int deltaX, int deltaZ) {
        double angle = Math.toDegrees(Math.atan2(deltaZ, deltaX));
        if (angle < 0) angle += 360;

        if (angle >= 22.5 && angle < 67.5) return "southeast";
        if (angle >= 67.5 && angle < 112.5) return "south";
        if (angle >= 112.5 && angle < 157.5) return "southwest";
        if (angle >= 157.5 && angle < 202.5) return "west";
        if (angle >= 202.5 && angle < 247.5) return "northwest";
        if (angle >= 247.5 && angle < 292.5) return "north";
        if (angle >= 292.5 && angle < 337.5) return "northeast";
        return "east";
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}