package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.entity.ai.goals.mad_hatter.MadHatterBossBehaviorGoal;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.HareEntity;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.util.AdvancementUtil;
import net.emanueljdf09.dtrhmod.fabric.util.ModComponents;
import net.emanueljdf09.dtrhmod.fabric.util.ModPackets;
import net.emanueljdf09.dtrhmod.fabric.util.WonderlandProgressionUtil;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.util.entity.MadHatterTrivia;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class MadHatterEntity extends PathfinderMob implements GeoEntity {
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

    private static final EntityDataAccessor<Boolean> IS_SITTING = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> BOSS_MODE = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> BOSS_PHASE = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> IS_GREETING = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_CHARGING = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> IS_RUNNING = SynchedEntityData.defineId(MadHatterEntity.class, EntityDataSerializers.BOOLEAN);

    private static final int RITUAL_HORDE_TICK = 220;
    private static final int RITUAL_END_TICK = 460;
    private static final int HORDE_SIZE = 8;
    private static final int HEAL_INTERVAL_TICKS = 10;
    private static final float HEAL_AMOUNT = 1.0f;
    private static final int HEALER_GLOW_TICKS = 12000;

    private static final int FIGHT_RADIUS = 48;
    private static final int EMPTY_RESET_TICKS = 200;

    private int chargeCooldownTicks = 0;
    private int triviaFailures = 0;
    private int greetingTicks = 0;
    private int hatThrowTicks = 0;
    private boolean isPerformingHatRitual = false;
    private boolean retriggerRitualAnim = false;
    private int healthThresholdTrigger = 75;
    private int ritualsDone = 0;

    private boolean allowDeath = false;
    private int emptyTicks = 0;
    @Nullable
    private BlockPos homePos = null;
    private List<ServerPlayer> fightPlayers = new ArrayList<>();
    @Nullable
    private UUID lastAttackerId = null;
    private int lastAttackAge = 0;

    @Nullable
    private LivingEntity designatedHealer = null;
    @Nullable
    private UUID healerId = null;
    private final List<UUID> hordeIds = new ArrayList<>();
    private final Set<UUID> participants = new HashSet<>();

    private final ServerBossEvent bossBar = (ServerBossEvent) new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    ).setDarkenScreen(true);

    public MadHatterEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(IS_SITTING, true);
        builder.define(BOSS_PHASE, 0);
        builder.define(BOSS_MODE, false);
        builder.define(IS_GREETING, false);
        builder.define(IS_CHARGING, false);
        builder.define(IS_RUNNING, false);
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

    public void setCharging(boolean charging) { this.entityData.set(IS_CHARGING, charging); }
    public boolean isCharging() { return this.entityData.get(IS_CHARGING); }
    public void setRunning(boolean running) { this.entityData.set(IS_RUNNING, running); }
    public boolean isRunning() { return this.entityData.get(IS_RUNNING); }
    public int getChargeCooldownTicks() { return chargeCooldownTicks; }
    public void setChargeCooldownTicks(int ticks) { this.chargeCooldownTicks = ticks; }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(2, new WaterAvoidingRandomStrollGoal(this, 1.0D) {
            @Override
            public boolean canUse() {
                return !MadHatterEntity.this.entityData.get(IS_SITTING) && super.canUse();
            }
        });
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public void activateBossAI() {
        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);

        MadHatterBossBehaviorGoal bossGoal = new MadHatterBossBehaviorGoal(this);
        bossGoal.resetTaskState();
        this.goalSelector.addGoal(1, bossGoal);
    }


    public List<ServerPlayer> getFightPlayers() {
        return fightPlayers;
    }

    @Nullable
    public ServerPlayer getRecentAttacker(int maxAgeTicks) {
        if (lastAttackerId == null || this.tickCount - lastAttackAge > maxAgeTicks) return null;
        if (this.level() instanceof ServerLevel world && world.getPlayerByUUID(lastAttackerId) instanceof ServerPlayer player) {
            return player;
        }
        return null;
    }

    private List<ServerPlayer> collectFightPlayers(ServerLevel world) {
        return world.getEntitiesOfClass(ServerPlayer.class,
                this.getBoundingBox().inflate(FIGHT_RADIUS),
                p -> p.isAlive() && !p.isSpectator());
    }

    private void syncBossBar() {
        for (ServerPlayer p : new ArrayList<>(bossBar.getPlayers())) {
            if (!fightPlayers.contains(p)) bossBar.removePlayer(p);
        }
        for (ServerPlayer p : fightPlayers) {
            bossBar.addPlayer(p);
        }
    }

    private float floorHealth() {
        if (!isBossMode()) return 0.0f;
        return switch (getBossPhase()) {
            case 1 -> getMaxHealth() * 0.75f;
            case 2 -> getMaxHealth() * 0.50f;
            case 3 -> ritualsDone < 2 ? getMaxHealth() * 0.25f : 0.0f;
            default -> 0.0f;
        };
    }

    @Override
    public void setHealth(float health) {
        if (!allowDeath && !this.level().isClientSide()) {
            health = Math.max(health, floorHealth());
        }
        super.setHealth(health);
    }

    private void triggerHatThrowRitual(int regenCapPercent) {
        this.isPerformingHatRitual = true;
        this.hatThrowTicks = 0;
        this.healthThresholdTrigger = regenCapPercent;
        this.designatedHealer = null;
        this.healerId = null;

        this.getNavigation().stop();
        setCharging(false);
        setRunning(false);

        Player nearest = this.level().getNearestPlayer(this, 40.0D);
        if (nearest != null) {
            this.getLookControl().setLookAt(nearest, 30.0F, 30.0F);
            this.setTarget(nearest);
        }

        if (!this.level().isClientSide()) {
            this.triggerAnim("attack_controller", "throw_hat_anim");
        }
    }

    private void discardHorde() {
        if (this.level() instanceof ServerLevel world) {
            for (UUID id : hordeIds) {
                Entity hare = world.getEntity(id);
                if (hare != null) {
                    world.sendParticles(ParticleTypes.POOF, hare.getX(), hare.getY() + 1.0, hare.getZ(), 8, 0.3, 0.5, 0.3, 0.02);
                    hare.discard();
                }
            }
        }
        hordeIds.clear();
        designatedHealer = null;
        healerId = null;
    }

    private void spawnPhase3Horde() {
        if (!(this.level() instanceof ServerLevel world)) return;

        discardHorde();

        List<net.emanueljdf09.dtrhmod.entity.custom.HareEntity> spawned = new ArrayList<>();
        for (int i = 0; i < HORDE_SIZE; i++) {
            net.emanueljdf09.dtrhmod.entity.custom.HareEntity hare = ModEntities.HARE.create(world);
            if (hare == null) continue;

            double offsetX = (this.random.nextDouble() - 0.5) * 6.0;
            double offsetZ = (this.random.nextDouble() - 0.5) * 6.0;
            hare.moveTo(this.getX() + offsetX  - 6, this.getY(), this.getZ() + offsetZ - 6, this.random.nextFloat() * 360F, 0.0F);

            hare.setPersistenceRequired();

            world.addFreshEntity(hare);
            spawned.add(hare);
            hordeIds.add(hare.getUUID());
        }

        if (spawned.isEmpty()) return;

        net.emanueljdf09.dtrhmod.entity.custom.HareEntity healer = spawned.get(this.random.nextInt(spawned.size()));
        healer.addTag("mad_hatter_healer");
        healer.addEffect(new MobEffectInstance(MobEffects.GLOWING, HEALER_GLOW_TICKS, 0, false, false, true));
        this.designatedHealer = healer;
        this.healerId = healer.getUUID();
    }

    public void rallyHorde() {
        if (!(this.level() instanceof ServerLevel world)) return;
        for (UUID id : hordeIds) {
            if (world.getEntity(id) instanceof LivingEntity hare && hare.isAlive()) {
                hare.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60, 0, false, false, false));
                world.sendParticles(ParticleTypes.ANGRY_VILLAGER, hare.getX(), hare.getY() + 2.0, hare.getZ(), 1, 0.2, 0.1, 0.2, 0.0);
            }
        }
    }

    private void tickHealer() {
        if (!(this.level() instanceof ServerLevel world)) return;

        if (designatedHealer == null && healerId != null && this.tickCount % 20 == 0) {
            Entity found = world.getEntity(healerId);
            if (found instanceof LivingEntity living && living.isAlive()) {
                designatedHealer = living;
            } else if (found != null) {
                healerId = null;
            }
        }

        if (designatedHealer != null
                && (!designatedHealer.isAlive() || designatedHealer.level() != this.level())) {
            designatedHealer = null;
            healerId = null;
        }
        if (designatedHealer == null) return;

        float maxAllowedHealth = this.getMaxHealth() * (this.healthThresholdTrigger / 100.0f);
        if (this.tickCount % HEAL_INTERVAL_TICKS == 0 && this.getHealth() < maxAllowedHealth) {
            this.heal(HEAL_AMOUNT);
            world.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                    this.getX(), this.getY() + 1.0, this.getZ(), 3, 0.5, 0.5, 0.5, 0.0);
        }
    }

    @Override
    public boolean canBeAffected(MobEffectInstance effect) {
        if (effect.getEffect() == MobEffects.POISON) {
            return false;
        }
        return super.canBeAffected(effect);
    }

    @Override
    public void customServerAiStep() {
        super.customServerAiStep();

        if (this.level().isClientSide()) return;

        if (isPerformingHatRitual) {
            if (retriggerRitualAnim) {
                retriggerRitualAnim = false;
                this.triggerAnim("attack_controller", "throw_hat_anim");
            }

            hatThrowTicks++;

            if (hatThrowTicks == RITUAL_HORDE_TICK) {
                spawnPhase3Horde();
            }

            if (hatThrowTicks >= RITUAL_END_TICK) {
                isPerformingHatRitual = false;
                hatThrowTicks = 0;
                ritualsDone++;
                setBossPhase(3);
            }
            return;
        }

        if (getBossPhase() == 3) {
            tickHealer();
        }

        if (chargeCooldownTicks > 0) chargeCooldownTicks--;
    }

    @Override
    public InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!this.level().isClientSide() && !isBossMode() && hand == InteractionHand.MAIN_HAND) {
            this.entityData.set(IS_GREETING, true);
            this.greetingTicks = 0;

            ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(player);

            if (!progression.hasMetMadHatter()) {
                progression.setMetMadHatter(true);

                ModPackets.sendOpenBookPacket((ServerPlayer) player, "mad_hatter_intro");
            }

            MadHatterTrivia.startTrivia(player, this);
            return InteractionResult.SUCCESS;
        }
        return super.mobInteract(player, hand);
    }

    public void triggerBossFight() {
        if (!(this.level() instanceof ServerLevel world)) return;
        if (isBossMode()) return;

        this.entityData.set(BOSS_MODE, true);
        this.entityData.set(IS_SITTING, false);
        this.entityData.set(IS_GREETING, false);
        this.entityData.set(BOSS_PHASE, 1);

        this.homePos = this.blockPosition();
        this.emptyTicks = 0;
        this.ritualsDone = 0;
        this.healthThresholdTrigger = 75;

        this.fightPlayers = collectFightPlayers(world);
        syncBossBar();

        activateBossAI();
    }

    private void resetFight() {
        discardHorde();
        bossBar.removeAllPlayers();
        MadHatterTrivia.clearSessionsFor(this);

        isPerformingHatRitual = false;
        retriggerRitualAnim = false;
        hatThrowTicks = 0;
        healthThresholdTrigger = 75;
        ritualsDone = 0;
        triviaFailures = 0;
        chargeCooldownTicks = 0;
        emptyTicks = 0;
        participants.clear();
        lastAttackerId = null;
        fightPlayers = new ArrayList<>();

        setCharging(false);
        setRunning(false);
        this.setTarget(null);
        this.getNavigation().stop();

        this.entityData.set(BOSS_MODE, false);
        this.entityData.set(IS_SITTING, true);
        this.entityData.set(IS_GREETING, false);
        this.entityData.set(BOSS_PHASE, 0);

        AttributeInstance health = this.getAttribute(Attributes.MAX_HEALTH);
        if (health != null) this.setHealth(this.getMaxHealth());

        this.goalSelector.removeAllGoals(goal -> true);
        this.targetSelector.removeAllGoals(goal -> true);
        this.registerGoals();

        if (homePos != null) {
            this.teleportTo(homePos.getX() + 0.5, homePos.getY(), homePos.getZ() + 0.5);
        }
    }

    public boolean isPerformingHatRitual() {
        return this.isPerformingHatRitual;
    }

    public void rewardTriviaWin(ServerPlayer player) {
        ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(player);
        if (!progression.hasWonMadHatterTrivia()) {
            progression.setWonMadHatterTrivia(true);

            AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "won_trivia"));
            player.addItem(new ItemStack(ModBlocks.TEAPOT_BLOCK));
        }
    }

    public void registerTriviaFailure() {
        triviaFailures++;
        if (triviaFailures >= 3) {
            triggerBossFight();
            if (this.level() instanceof ServerLevel world) {
                world.getPlayers(player -> player.distanceToSqr(this) <= 40.0D * 40.0D).forEach(player ->
                        player.displayClientMessage(Component.translatable("entity.dtrhmod.mad_hatter.trivia_fail"), false)
                );
            }
        }
    }

    public boolean isBossMode() { return this.entityData.get(BOSS_MODE); }
    public int getBossPhase() { return this.entityData.get(BOSS_PHASE); }
    public void setBossPhase(int phase) { this.entityData.set(BOSS_PHASE, phase); }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean bypass = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
        if (this.isPerformingHatRitual && !bypass) return false;
        if (this.level().isClientSide()) return super.hurt(source, amount);

        ServerPlayer attacker = source.getEntity() instanceof ServerPlayer p ? p : null;

        if (!isBossMode()) {
            if (attacker == null && !bypass) return false;
            if (attacker != null && amount > 0) triggerBossFight();
        }

        allowDeath = bypass;
        boolean hurt;
        try {
            hurt = super.hurt(source, amount);
        } finally {
            allowDeath = false;
        }

        if (hurt && attacker != null) {
            participants.add(attacker.getUUID());
            lastAttackerId = attacker.getUUID();
            lastAttackAge = this.tickCount;
        }
        return hurt;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (this.level() instanceof ServerLevel world && !world.isClientSide()) {
            Set<UUID> credited = new HashSet<>(participants);
            if (this.getKillCredit() instanceof ServerPlayer killer) {
                credited.add(killer.getUUID());
            }
            for (ServerPlayer present : collectFightPlayers(world)) {
                credited.add(present.getUUID());
            }

            for (UUID id : credited) {
                ServerPlayer player = world.getServer().getPlayerList().getPlayer(id);
                if (player == null) continue;

                ProgressionComponent progression = ModComponents.PROGRESSION_COMPONENT.get(player);
                if (!progression.hasKilledMadHatter()) {
                    progression.setKilledMadHatter(true);


                    AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "boss_root"));
                    AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "killed_mad_hatter"));

                    this.spawnAtLocation(new ItemStack(ModItems.MAD_HAT_ITEM));
                    this.spawnAtLocation(new ItemStack(ModItems.MAD_HATTER_HEAD_ITEM));

                    WonderlandProgressionUtil.triggerMilestone(player, 1, null);
                }
            }


            discardHorde();
            bossBar.removeAllPlayers();
            MadHatterTrivia.clearSessionsFor(this);
        }
    }

    @Override
    public int getBaseExperienceReward() {
        return 30;
    }

    @Override
    public void stopSeenByPlayer(ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossBar.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        if (!(this.level() instanceof ServerLevel world)) return;
        if (!this.isAlive()) return;

        this.bossBar.setProgress(Mth.clamp(this.getHealth() / this.getMaxHealth(), 0.0f, 1.0f));
        this.bossBar.setName(this.getDisplayName());

        if (this.entityData.get(IS_GREETING)) {
            greetingTicks++;
            if (greetingTicks > 120) {
                this.entityData.set(IS_GREETING, false);
                greetingTicks = 0;
            }
        }

        if (!isBossMode()) return;

        if (this.tickCount % 10 == 0) {
            fightPlayers = collectFightPlayers(world);
            syncBossBar();
        }

        if (fightPlayers.isEmpty()) {
            if (++emptyTicks >= EMPTY_RESET_TICKS) {
                resetFight();
                return;
            }
        } else {
            emptyTicks = 0;
        }

        tickPhases(world);
    }

    private void tickPhases(ServerLevel world) {
        if (isPerformingHatRitual) return;

        float hp = this.getHealth() / this.getMaxHealth();
        int phase = getBossPhase();

        if (phase == 1 && hp <= 0.75f + 0.001f) {
            setBossPhase(2);
            world.playSound(null, blockPosition(), SoundEvents.EVOKER_CELEBRATE, SoundSource.HOSTILE, 2.0F, 0.9F);
        } else if (phase == 2 && hp <= 0.50f + 0.001f) {
            setBossPhase(3);
            triggerHatThrowRitual(75);
        } else if (phase == 3 && ritualsDone == 1 && hp <= 0.25f + 0.001f) {
            triggerHatThrowRitual(50);
        }
    }



    @Override
    public void remove(RemovalReason reason) {
        super.remove(reason);
        this.bossBar.removeAllPlayers();
        MadHatterTrivia.clearSessionsFor(this);
    }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) {
        return false;
    }

    @Override
    public boolean doHurtTarget(Entity target) {
        boolean succeeded = super.doHurtTarget(target);
        if (succeeded && !this.level().isClientSide()) {
            if (isBossMode() && getBossPhase() >= 2) {
                triggerAnim("attack_controller", "attack_with_pot_melee");
            } else {
                triggerAnim("attack_controller", "attack_melee");
            }
        }
        return succeeded;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Sitting", this.entityData.get(IS_SITTING));
        nbt.putBoolean("BossMode", isBossMode());
        nbt.putInt("BossPhase", getBossPhase());
        nbt.putInt("TriviaFailures", triviaFailures);
        nbt.putInt("RegenCap", healthThresholdTrigger);
        nbt.putInt("RitualsDone", ritualsDone);
        nbt.putBoolean("RitualActive", isPerformingHatRitual);
        nbt.putInt("RitualTicks", hatThrowTicks);
        if (healerId != null) nbt.putUUID("HealerId", healerId);
        if (homePos != null) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }

        ListTag horde = new ListTag();
        for (UUID id : hordeIds) horde.add(NbtUtils.createUUID(id));
        nbt.put("Horde", horde);

        ListTag players = new ListTag();
        for (UUID id : participants) players.add(NbtUtils.createUUID(id));
        nbt.put("Participants", players);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);

        if (nbt.contains("Sitting")) this.entityData.set(IS_SITTING, nbt.getBoolean("Sitting"));
        triviaFailures = nbt.getInt("TriviaFailures");
        healthThresholdTrigger = nbt.contains("RegenCap") ? nbt.getInt("RegenCap") : 75;
        isPerformingHatRitual = nbt.getBoolean("RitualActive");
        hatThrowTicks = nbt.getInt("RitualTicks");
        retriggerRitualAnim = isPerformingHatRitual;
        healerId = nbt.hasUUID("HealerId") ? nbt.getUUID("HealerId") : null;
        designatedHealer = null;
        if (nbt.contains("HomeX")) {
            homePos = new BlockPos(nbt.getInt("HomeX"), nbt.getInt("HomeY"), nbt.getInt("HomeZ"));
        }

        hordeIds.clear();
        for (Tag e : nbt.getList("Horde", Tag.TAG_INT_ARRAY)) {
            hordeIds.add(NbtUtils.loadUUID(e));
        }

        participants.clear();
        for (Tag e : nbt.getList("Participants", Tag.TAG_INT_ARRAY)) {
            participants.add(NbtUtils.loadUUID(e));
        }

        if (nbt.getBoolean("BossMode")) {
            int phase = Math.max(1, nbt.getInt("BossPhase"));
            this.entityData.set(BOSS_MODE, true);
            this.entityData.set(IS_SITTING, false);
            this.entityData.set(BOSS_PHASE, phase);

            if (nbt.contains("RitualsDone")) {
                ritualsDone = nbt.getInt("RitualsDone");
            } else if (phase >= 3) {
                ritualsDone = healthThresholdTrigger == 50 ? 2 : 1;
            }
            activateBossAI();
        }
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 5, state -> {
            boolean sitting = this.entityData.get(IS_SITTING);
            boolean greeting = this.entityData.get(IS_GREETING);
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