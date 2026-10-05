package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.Structure;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

public class CheshireCatEntity extends MobEntity implements GeoEntity {
    private static final TrackedData<Integer> INVISIBILITY_STATE = DataTracker.registerData(CheshireCatEntity.class, TrackedDataHandlerRegistry.INTEGER);

    private static final TrackedData<Boolean> IS_GRINNING = DataTracker.registerData(CheshireCatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_TRANSITIONING = DataTracker.registerData(CheshireCatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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

    public CheshireCatEntity(EntityType<? extends MobEntity> entityType, World world) {
        super(entityType, world);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return MobEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 20.0D)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3D);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(INVISIBILITY_STATE, 0);
        this.dataTracker.startTracking(IS_GRINNING, false);
        this.dataTracker.startTracking(IS_TRANSITIONING, false);
    }

    public int getInvisibilityState() {
        return this.dataTracker.get(INVISIBILITY_STATE);
    }

    public void setInvisibilityState(int state) {
        this.dataTracker.set(INVISIBILITY_STATE, state);
    }

    public boolean isGrinning() {
        return this.dataTracker.get(IS_GRINNING);
    }

    public void setGrinning(boolean grinning) {
        this.dataTracker.set(IS_GRINNING, grinning);
    }

    public boolean isTransitioning() {
        return this.dataTracker.get(IS_TRANSITIONING);
    }

    public void setTransitioning(boolean transitioning) {
        this.dataTracker.set(IS_TRANSITIONING, transitioning);
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

        if (!this.getWorld().isClient()) {
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
        if (this.getWorld() instanceof ServerWorld serverWorld) {

            if (this.homePosition == null) {
                this.homePosition = this.getBlockPos();
            }

            double x = this.homePosition.getX() + (this.random.nextDouble() - 0.5) * 12.0;
            double y = this.homePosition.getY();
            double z = this.homePosition.getZ() + (this.random.nextDouble() - 0.5) * 12.0;

            this.teleport(x, y, z);
            this.getNavigation().stop();
        }
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(1, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
    }

    public void startTransitionTo(int newState) {
        int currentState = getInvisibilityState();
        this.targetInvisState = newState;
        setTransitioning(true);
        this.transitionTicks = 40;

        if (!this.getWorld().isClient()) {
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

        if (!this.getWorld().isClient()) {
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
            boolean isMoving = this.sidewaysSpeed != 0.0f || this.forwardSpeed != 0.0f ||
                    this.getVelocity().horizontalLengthSquared() > 0.00001;

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
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (hand == Hand.MAIN_HAND && !this.getWorld().isClient()) {
            ItemStack stack = player.getStackInHand(hand);

            if (stack.isOf(Items.GOLDEN_APPLE)) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }

                triggerGrin();

                giveStructureClue(player);

                AdvancementUtil.grant((ServerPlayerEntity) player, new Identifier(DownTheRabbitHole.MOD_ID, "cheshire_cat_snitch"));


                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.ENTITY_CAT_PURR,
                        SoundCategory.NEUTRAL, 1.0f, 1.0f);

                return ActionResult.SUCCESS;
            } else {
                String[] confusedQuotes = {
                        "entity.dtrhmod.cheshire_cat.confused1",
                        "entity.dtrhmod.cheshire_cat.confused2",
                        "entity.dtrhmod.cheshire_cat.confused3",
                        "entity.dtrhmod.cheshire_cat.confused4",
                        "entity.dtrhmod.cheshire_cat.confused5"
                };

                String selectedKey = confusedQuotes[this.random.nextInt(confusedQuotes.length)];

                player.sendMessage(Text.translatable(selectedKey), true);
                int nextState = (getInvisibilityState() + 1) % 3;
                startTransitionTo(nextState);
                return ActionResult.SUCCESS;
            }
        }
        return super.interactMob(player, hand);
    }

    private void giveStructureClue(PlayerEntity player) {
        if (!(this.getWorld() instanceof ServerWorld serverWorld)) return;

        DownTheRabbitHole.LOGGER.info("CheshireCat: Attempting to locate 'tulgey_wood/tea_party' structure for player " + player.getName().getString());

        Identifier structureId = new Identifier(DownTheRabbitHole.MOD_ID, "tulgey_wood/tea_party");
        RegistryKey<Structure> structureKey = RegistryKey.of(RegistryKeys.STRUCTURE, structureId);

        RegistryEntry<Structure> structureEntry = serverWorld.getRegistryManager()
                .get(RegistryKeys.STRUCTURE)
                .getEntry(structureKey)
                .orElse(null);

        if (structureEntry != null) {
            net.minecraft.registry.entry.RegistryEntryList<Structure> structureHolderSet =
                    net.minecraft.registry.entry.RegistryEntryList.of(structureEntry);

            com.mojang.datafixers.util.Pair<BlockPos, RegistryEntry<Structure>> structureResult =
                    serverWorld.getChunkManager().getChunkGenerator().locateStructure(
                            serverWorld,
                            structureHolderSet,
                            this.getBlockPos(),
                            100,
                            false
                    );

            if (structureResult != null) {

                BlockPos structurePos = structureResult.getFirst();

                DownTheRabbitHole.LOGGER.info("CheshireCat: Successfully found structure at X: " + structurePos.getX() + ", Z: " + structurePos.getZ());

                int deltaX = structurePos.getX() - this.getBlockPos().getX();
                int deltaZ = structurePos.getZ() - this.getBlockPos().getZ();

                String cardinalDirection = getCardinalDirection(deltaX, deltaZ);
                int estimatedBlocks = (int) Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);

                player.sendMessage(Text.translatable(
                        "entity.dtrhmod.cheshire_cat.tea_party_clue",
                        estimatedBlocks,
                        cardinalDirection
                ), false);

                serverWorld.spawnParticles(ParticleTypes.ENCHANT,
                        this.getX(), this.getY() + 1.0, this.getZ(),
                        20, 0.5, 0.5, 0.5, 0.2);
                return;
            }
        }

        DownTheRabbitHole.LOGGER.warn("CheshireCat: Could not find structure entry or chunk generator location returned null.");
        player.sendMessage(Text.translatable("entity.dtrhmod.cheshire_cat.confused2", false));
    }

    @Override
    public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
        return false;
    }

    @Override
    protected void dropLoot(DamageSource damageSource, boolean causedByPlayer) {
        super.dropLoot(damageSource, causedByPlayer);

        if (this.random.nextFloat() < 0.2f) {
            this.dropItem(ModItems.CAT_FUR);
        }

        if (causedByPlayer) {
            int count = 1 + this.random.nextInt(3);
            for (int i = 0; i < count; i++) {
                this.dropItem(Items.BONE);
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