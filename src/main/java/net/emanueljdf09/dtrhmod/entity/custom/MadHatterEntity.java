package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.entity.ai.goals.mad_hatter.MadHatterBossBehaviorGoal;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.ModPackets;
import net.emanueljdf09.dtrhmod.util.WonderlandProgressionUtil;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.entity.MadHatterTrivia;
import net.minecraft.advancement.Advancement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.BossBar;
import net.minecraft.entity.boss.ServerBossBar;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.ArrayList;
import java.util.List;

public class MadHatterEntity extends PathAwareEntity implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE_SITTING = RawAnimation.begin().thenLoop("sit_idle");
    private static final RawAnimation SIT_GREET = RawAnimation.begin().thenPlay("sit_greet");
    private static final RawAnimation IDLE_STANDING = RawAnimation.begin().thenLoop("standing");
    private static final RawAnimation IDLE_STANDING_WITH_TEAPOT = RawAnimation.begin().thenLoop("standing_with_pot");
    private static final RawAnimation WALK = RawAnimation.begin().thenLoop("walking");
    private static final RawAnimation WALK_WITH_TEAPOT = RawAnimation.begin().thenLoop("walking_with_pot");
    private static final RawAnimation CHARGE_WINDUP = RawAnimation.begin().thenPlayAndHold("charging");
    private static final RawAnimation RUN = RawAnimation.begin().thenLoop("running");
    private static final RawAnimation THROW_HAT = RawAnimation.begin().thenPlay("throwing_hat").thenPlay("hat_spinning");
    private static final RawAnimation ATTACK = RawAnimation.begin().thenPlay("attacking");
    private static final RawAnimation ATTACK_WITH_TEAPOT = RawAnimation.begin().thenPlay("attacking_distance");
    private static final RawAnimation CHARGE_WINDUP_WITH_POT = RawAnimation.begin().thenPlayAndHold("charging_with_pot");
    private static final RawAnimation RUN_WITH_POT = RawAnimation.begin().thenLoop("running_with_pot");
    private static final RawAnimation ATTACK_WITH_POT_MELEE = RawAnimation.begin().thenPlay("attacking_with_pot");

    private static final TrackedData<Boolean> IS_SITTING = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> BOSS_MODE = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Integer> BOSS_PHASE = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> IS_GREETING = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_ATTACKING = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_CHARGING = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_RUNNING = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> IS_THROWING_HAT = DataTracker.registerData(MadHatterEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    private int attackAnimationTicks = 0;
    private int chargeCooldownTicks = 0;
    private int triviaFailures = 0;
    private int greetingTicks = 0;
    private int hatThrowTicks = 0;
    private boolean isPerformingHatRitual = false;
    private LivingEntity designatedHealer = null;
    private int healthThresholdTrigger = 75;

    private final ServerBossBar bossBar = (ServerBossBar) new ServerBossBar(
            this.getDisplayName(),
            BossBar.Color.PURPLE,
            BossBar.Style.PROGRESS
    ).setDarkenSky(true);

    public MadHatterEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(IS_SITTING, true);
        this.dataTracker.startTracking(BOSS_MODE, false);
        this.dataTracker.startTracking(BOSS_PHASE, 0);
        this.dataTracker.startTracking(IS_GREETING, false);
        this.dataTracker.startTracking(IS_ATTACKING, false);
        this.dataTracker.startTracking(IS_CHARGING, false);
        this.dataTracker.startTracking(IS_RUNNING, false);
        this.dataTracker.startTracking(IS_THROWING_HAT, false);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3f)
                .add(EntityAttributes.GENERIC_MAX_HEALTH, 100.0f)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0f)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 0.7f)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 8.0f)
                .add(EntityAttributes.GENERIC_ARMOR, 10.0f)
                .add(EntityAttributes.GENERIC_ARMOR_TOUGHNESS, 4.0f);
    }

    public void setCharging(boolean charging) { this.dataTracker.set(IS_CHARGING, charging); }
    public boolean isCharging() { return this.dataTracker.get(IS_CHARGING); }
    public void setRunning(boolean running) { this.dataTracker.set(IS_RUNNING, running); }
    public boolean isRunning() { return this.dataTracker.get(IS_RUNNING); }
    public int getChargeCooldownTicks() { return chargeCooldownTicks; }
    public void setChargeCooldownTicks(int ticks) { this.chargeCooldownTicks = ticks; }

    @Override
    protected void initGoals() {
        this.goalSelector.add(2, new WanderAroundFarGoal(this, 1.0D) {
            @Override
            public boolean canStart() {
                return !MadHatterEntity.this.dataTracker.get(IS_SITTING) && super.canStart();
            }
        });
        this.goalSelector.add(3, new LookAroundGoal(this));
    }

    public void activateBossAI() {

        this.goalSelector.clear(goal -> true);

        MadHatterBossBehaviorGoal bossGoal = new MadHatterBossBehaviorGoal(this);
        bossGoal.resetTaskState();
        this.goalSelector.add(1, bossGoal);

        this.targetSelector.clear(goal -> true);
        this.targetSelector.add(1, new ActiveTargetGoal<>(this, PlayerEntity.class, true));
    }

    private void triggerHatThrowRitual(int regenCapPercent) {
        this.isPerformingHatRitual = true;
        this.hatThrowTicks = 0;
        this.healthThresholdTrigger = regenCapPercent;
        this.designatedHealer = null;
        this.dataTracker.set(IS_THROWING_HAT, true);

        this.getNavigation().stop();

        PlayerEntity nearest = this.getWorld().getClosestPlayer(this, 40.0D);
        if (nearest != null) {
            this.getLookControl().lookAt(nearest, 30.0F, 30.0F);
            this.setTarget(nearest);
        }

        if (!this.getWorld().isClient()) {
            this.triggerAnim("attack_controller", "throw_hat_anim");
        }
    }

    private void spawnPhase3Horde() {
        if (this.getWorld().isClient()) return;

        List<ZombieEntity> spawnedZombies = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            ZombieEntity zombie = EntityType.ZOMBIE.create(this.getWorld());
            if (zombie != null) {
                double offsetX = (this.random.nextDouble() - 0.5) * 6.0;
                double offsetZ = (this.random.nextDouble() - 0.5) * 6.0;
                zombie.refreshPositionAndAngles(this.getX() + offsetX, this.getY(), this.getZ() + offsetZ, this.random.nextFloat() * 360F, 0.0F);
                this.getWorld().spawnEntity(zombie);
                spawnedZombies.add(zombie);
            }
        }

        if (!spawnedZombies.isEmpty()) {
            this.designatedHealer = spawnedZombies.get(this.random.nextInt(spawnedZombies.size()));
            this.designatedHealer.getCommandTags().add("mad_hatter_healer");

            this.designatedHealer.addStatusEffect(new StatusEffectInstance(
                    StatusEffects.GLOWING, 200, 0, false, false, true));
        }

        this.designatedHealer.addStatusEffect(new StatusEffectInstance(
                StatusEffects.GLOWING, 200, 0, false, false, true));
    }


    @Override
    public boolean canHaveStatusEffect(StatusEffectInstance effect) {
        if (effect.getEffectType() == StatusEffects.POISON) {
            return false;
        }
        return super.canHaveStatusEffect(effect);
    }

    @Override
    public void mobTick() {
        super.mobTick();

        if (this.getWorld().isClient()) return;

        if (isPerformingHatRitual) {
            hatThrowTicks++;

            if (hatThrowTicks == 220) {
                spawnPhase3Horde();
            }

            if (hatThrowTicks >= 460) {
                isPerformingHatRitual = false;
                this.dataTracker.set(IS_THROWING_HAT, false);
                hatThrowTicks = 0;
                setBossPhase(3);

                PlayerEntity nearest = this.getWorld().getClosestPlayer(this, 40.0D);
                if (nearest != null) {
                    this.setTarget(nearest);
                }
            }
            return;
        }

        if (getBossPhase() == 3) {
            if (designatedHealer != null) {
                if (!designatedHealer.isAlive() || designatedHealer.getWorld() != this.getWorld()) {
                    designatedHealer = null;
                } else {
                    float maxAllowedHealth = this.getMaxHealth() * (this.healthThresholdTrigger / 100.0f);
                    if (this.getHealth() < maxAllowedHealth) {
                        this.heal(0.5f);
                        if (this.getWorld() instanceof ServerWorld serverWorld) {
                            serverWorld.spawnParticles(ParticleTypes.HAPPY_VILLAGER,
                                    this.getX(), this.getY() + 1.0, this.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
                        }
                    }
                }
            }
        }

        if (chargeCooldownTicks > 0) chargeCooldownTicks--;

        if (isBossMode() && this.getTarget() == null) {
            PlayerEntity nearestPlayer = this.getWorld().getClosestPlayer(this, 35.0D);
            if (nearestPlayer != null) this.setTarget(nearestPlayer);
        }

        if (this.dataTracker.get(IS_ATTACKING)) {
            attackAnimationTicks--;
            if (attackAnimationTicks <= 0) {
                this.dataTracker.set(IS_ATTACKING, false);
            }
        }
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (!this.getWorld().isClient() && !isBossMode() && hand == Hand.MAIN_HAND) {
            this.dataTracker.set(IS_GREETING, true);
            this.greetingTicks = 0;

            ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(player);

            if (!progression.hasMetMadHatter()) {
                progression.setMetMadHatter(true);

                ModPackets.sendOpenBookPacket((ServerPlayerEntity) player, "mad_hatter_intro");
            }

            MadHatterTrivia.startTrivia(player, this);
            return ActionResult.SUCCESS;
        }
        return super.interactMob(player, hand);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isPerformingHatRitual) return false;

        if (!isBossMode()) {
            triggerBossFight();
        }
        return super.damage(source, amount);
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);

        if (!this.getWorld().isClient()) {
            LivingEntity attacker = this.getPrimeAdversary();
            if (attacker instanceof ServerPlayerEntity serverPlayer) {
                ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(serverPlayer);
                if (!progression.hasKilledMadHatter()) {
                    progression.setKilledMadHatter(true);

                    AdvancementUtil.grant(serverPlayer, new Identifier(DownTheRabbitHole.MOD_ID, "killed_mad_hatter"));
                    dropBossLoot();

                    WonderlandProgressionUtil.triggerMilestone(serverPlayer, 3, null);
                }


            }
        }
    }

    @Override
    public void onStartedTrackingBy(ServerPlayerEntity player) {
        super.onStartedTrackingBy(player);
        if (this.isBossMode()) this.bossBar.addPlayer(player);
    }

    @Override
    public void onStoppedTrackingBy(ServerPlayerEntity player) {
        super.onStoppedTrackingBy(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.getWorld().isClient()) return;

        this.bossBar.setPercent(this.getHealth() / this.getMaxHealth());
        this.bossBar.setName(this.getDisplayName());

        if (this.dataTracker.get(IS_GREETING)) {
            greetingTicks++;
            if (greetingTicks > 120) {
                this.dataTracker.set(IS_GREETING, false);
                greetingTicks = 0;
            }
        }

        float healthPercent = this.getHealth() / this.getMaxHealth();
        if (isBossMode() && !isPerformingHatRitual) {
            if (healthPercent <= 0.75f && getBossPhase() == 1) {
                setBossPhase(2);
            } else if (healthPercent <= 0.50f && getBossPhase() == 2) {
                setBossPhase(3);
                triggerHatThrowRitual(75);
            } else if (healthPercent <= 0.25f && getBossPhase() == 3 && this.healthThresholdTrigger == 75) {
                triggerHatThrowRitual(50);
            }
        }
    }

    public boolean isPerformingHatRitual() {
        return this.isPerformingHatRitual;
    }

    public void rewardTriviaWin(ServerPlayerEntity player) {
        ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(player);
        if (!progression.hasWonMadHatterTrivia()) {
            progression.setWonMadHatterTrivia(true);

            Advancement advancement = player.getServer().getAdvancementLoader()
                    .get(new Identifier(DownTheRabbitHole.MOD_ID, "won_mad_hatter_trivia"));
            if (advancement != null) {
                player.getAdvancementTracker().grantCriterion(advancement, "won_trivia");
            }

            WonderlandProgressionUtil.triggerMilestone(player, 2, null);

            player.giveItemStack(new ItemStack(ModBlocks.TEAPOT_BLOCK));
        }
    }

    public void triggerBossFight() {
        if (!isBossMode()) {
            this.dataTracker.set(BOSS_MODE, true);
            this.dataTracker.set(IS_SITTING, false);
            this.dataTracker.set(IS_GREETING, false);
            this.dataTracker.set(BOSS_PHASE, 1);

            if (!this.getWorld().isClient()) {
                this.getWorld().getOtherEntities(this, this.getBoundingBox().expand(40.0D), entity -> entity instanceof ServerPlayerEntity)
                        .forEach(entity -> this.bossBar.addPlayer((ServerPlayerEntity) entity));
            }

            activateBossAI();
        }
    }

    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossBar.clearPlayers();
    }

    @Override
    public boolean tryAttack(Entity target) {
        boolean succeeded = super.tryAttack(target);
        if (succeeded && !this.getWorld().isClient()) {
            if (isBossMode() && getBossPhase() >= 2) {
                triggerAnim("attack_controller", "attack_with_pot_melee");
            } else {
                triggerAnim("attack_controller", "attack_melee");
            }
        }
        return succeeded;
    }

    public void registerTriviaFailure() {
        triviaFailures++;
        if (triviaFailures >= 3) {
            triggerBossFight();
            if (!this.getWorld().isClient()) {
                this.getWorld().getPlayers().forEach(player ->
                        player.sendMessage(Text.translatable("entity.dtrhmod.mad_hatter.trivia_fail"), false)
                );
            }
        }
    }

    public boolean isBossMode() { return this.dataTracker.get(BOSS_MODE); }
    public int getBossPhase() { return this.dataTracker.get(BOSS_PHASE); }
    public void setBossPhase(int phase) { this.dataTracker.set(BOSS_PHASE, phase); }

    private void dropBossLoot() {
        if (!this.getWorld().isClient()) {
            LivingEntity attacker = this.getPrimeAdversary();
            if (attacker instanceof ServerPlayerEntity serverPlayer) {
                    this.dropItem(ModItems.MAD_HAT_ITEM);
                    this.dropItem(ModItems.MAD_HATTER_HEAD_ITEM);
            }
        }
    }

    @Override
    public int getXpToDrop() {
        return 30;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            boolean sitting = this.dataTracker.get(IS_SITTING);
            boolean greeting = this.dataTracker.get(IS_GREETING);
            boolean boss = isBossMode();
            int phase = getBossPhase();

            if (sitting) {
                return state.setAndContinue(greeting ? SIT_GREET : IDLE_SITTING);
            }

            if (boss) {
                if (phase == 1) {
                    if (isCharging()) return state.setAndContinue(CHARGE_WINDUP);
                    if (isRunning()) return state.setAndContinue(RUN);
                } else if (phase >= 2) {
                    if (isCharging()) return state.setAndContinue(CHARGE_WINDUP_WITH_POT);
                    if (isRunning()) return state.setAndContinue(RUN_WITH_POT);
                }
            }

            if (state.isMoving()) {
                return state.setAndContinue((boss && phase >= 2) ? WALK_WITH_TEAPOT : WALK);
            }

            if (boss && phase >= 2) {
                return state.setAndContinue(IDLE_STANDING_WITH_TEAPOT);
            }

            return state.setAndContinue(IDLE_STANDING);
        }));

        controllers.add(new AnimationController<>(this, "attack_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("attack_melee", ATTACK)
                .triggerableAnim("attack_with_pot_melee", ATTACK_WITH_POT_MELEE)
                .triggerableAnim("attack_ranged", ATTACK_WITH_TEAPOT)
                .triggerableAnim("throw_hat_anim", THROW_HAT));
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}