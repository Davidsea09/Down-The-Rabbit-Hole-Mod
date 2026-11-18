package net.emanueljdf09.dtrhmod.entity.custom;


import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.CaterpillarLetters.Letter;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.AdvancementUtil;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.WonderlandProgressionUtil;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.entity.LetterPuffs;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.BodyRotationControl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animation.AnimatableManager;
import software.bernie.geckolib.animation.AnimationController;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.*;

public class CaterpillarEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation PUFF = RawAnimation.begin().thenPlayAndHold("puff");
    private static final RawAnimation TRANSFORM = RawAnimation.begin().thenPlayAndHold("transform");
    private static final RawAnimation CHARGE = RawAnimation.begin().thenPlayAndHold("charge");
    private static final RawAnimation SLAM = RawAnimation.begin().thenPlayAndHold("slam");
    private static final RawAnimation DIZZY_ANIM = RawAnimation.begin().thenLoop("dizzy");
    private static final RawAnimation WINDUP = RawAnimation.begin().thenPlayAndHold("windup");
    private static final RawAnimation DASH = RawAnimation.begin().thenLoop("dash");

    public static final float MAX_HEALTH = 200.0f;
    private static final float PHASE2_AT = 0.5f;
    private static final float PHASE3_AT = 0.25f;
    private static final double ARMOR_HEAVY = 20.0;
    private static final double TOUGHNESS_HEAVY = 12.0;
    private static final double ARMOR_BUTTERFLY = 6.0;
    private static final double TOUGHNESS_BUTTERFLY = 2.0;

    private static final double ARENA_RADIUS = 30.0;
    private static final int EMPTY_RESET_TICKS = 200;
    private static final int TRANSFORM_TICKS_CATERPILLAR = 64;
    private static final int TRANSFORM_TICKS_COCOON = 50;
    private static final int SLAM_LAND_TICKS = 24;
    private static final int DIZZY_TICKS = 120;

    private static final int PUFF_RELEASE_TICK = 25;
    private static final int PUFF_TOTAL_TICKS = 38;
    private static final double PUFF_SPEED = 0.35;
    private static final int PUFF_LIFETIME = 90;
    private static final double PUFF_HIT_RADIUS = 0.85;
    private static final float O_DAMAGE = 8.0f;

    private static final int CHARGE_TICKS = 40;
    private static final int CHARGE_TRACK_TICKS = 28;
    private static final double CHARGE_SPEED = 0.3;
    private static final double SLAM_SPEED = 1.8;
    private static final double SLAM_RADIUS = 4.5;
    private static final float SLAM_DAMAGE = 12.0f;
    private static final int SLAMS_BEFORE_DIZZY = 3;

    private static final double HOVER_HEIGHT = 3.0;
    private static final double DASH_APPROACH = 10.0;
    private static final double DASH_DISTANCE = 20.0;
    private static final double DASH_SPEED = 1.3;
    private static final int DASH_WINDUP_TICKS = 30;
    private static final float DASH_DAMAGE = 9.0f;
    private static final int DASHES_BEFORE_DIZZY = 3;

    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(CaterpillarEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> STATE = SynchedEntityData.defineId(CaterpillarEntity.class, EntityDataSerializers.INT);

    public enum State {
        NEUTRAL, IDLE, PUFFING, TRANSFORMING, CHARGING, SLAMMING, DIZZY, DASH_WINDUP, DASHING;

        public static State byId(int id) {
            State[] values = values();
            return values[Math.floorMod(id, values.length)];
        }
    }

    private final ServerBossEvent bossBar = new ServerBossEvent(
            Component.translatable("entity.dtrhmod.caterpillar"),
            BossEvent.BossBarColor.YELLOW,
            BossEvent.BossBarOverlay.PROGRESS);

    private final Set<UUID> participants = new HashSet<>();
    private final Set<UUID> dashHit = new HashSet<>();

    @Nullable
    private BlockPos homePos = null;
    private boolean allowDeath = false;
    private int stateTicks = 0;
    private int idleTicks = 0;
    private int emptyTicks = 0;
    private int slamCount = 0;
    private boolean slamLanded = false;

    private State lastClientState = State.NEUTRAL;
    private int clientStateTicks = 0;
    private boolean puffBurstPending = false;
    private int dashCount = 0;
    private Letter currentLetter = Letter.O;
    private double chargeStartY = 0;
    private double chargeRise = 3;
    private Vec3 dashStart = Vec3.ZERO;
    private Vec3 dashDir = new Vec3(1, 0, 0);
    private Vec3 dashOrigin = Vec3.ZERO;
    @Nullable
    private Vec3 repositionGoal = null;
    private int repositionTicks = 0;

    public CaterpillarEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.setPersistenceRequired();
        applyPhaseStats();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PHASE, 1);
        builder.define(STATE, State.NEUTRAL.ordinal());
    }

    public static AttributeSupplier.Builder setAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 48.0)
                .add(Attributes.ARMOR, ARMOR_HEAVY)
                .add(Attributes.ARMOR_TOUGHNESS, TOUGHNESS_HEAVY);
    }

    public int getPhase() { return this.entityData.get(PHASE); }
    public State getState() { return State.byId(this.entityData.get(STATE)); }

    private void setState(State state) {
        this.entityData.set(STATE, state.ordinal());
        this.stateTicks = 0;
    }

    public void setHome(BlockPos pos) { this.homePos = pos; }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> data) {
        super.onSyncedDataUpdated(data);
        if (PHASE.equals(data)) {
            this.refreshDimensions();
        }
    }

    @Override
    protected EntityDimensions getDefaultDimensions(Pose pose) {
        return switch (getPhase()) {
            case 2 -> EntityDimensions.fixed(1.9f, 3.8f);
            case 3 -> EntityDimensions.fixed(2.2f, 2.2f);
            default -> EntityDimensions.fixed(2.4f, 2.8f);
        };
    }

    private float floorHealth() {
        return switch (getPhase()) {
            case 1 -> getMaxHealth() * PHASE2_AT;
            case 2 -> getMaxHealth() * PHASE3_AT;
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

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (getState() == State.TRANSFORMING && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }


    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (this.level().isClientSide()) return super.hurt(source, amount);

        allowDeath = source.is(DamageTypeTags.BYPASSES_INVULNERABILITY);
        boolean hurt;
        try {
            hurt = super.hurt(source, amount);
        } finally {
            allowDeath = false;
        }

        if (hurt && source.getEntity() instanceof ServerPlayer player) {
            participants.add(player.getUUID());
            if (getState() == State.NEUTRAL) {
                startFight();
            }
        }
        return hurt;
    }

    private void startFight() {
        setState(State.IDLE);
        idleTicks = 50;
        emptyTicks = 0;
        sfx(SoundEvents.RAVAGER_ROAR, 1.5f, 1.2f);
    }

    @Override
    public void knockback(double strength, double x, double z) { }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void doPush(Entity entity) { }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) { return false; }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) {
            tickClientState();
            return;
        }
        if (!(this.level() instanceof ServerLevel world)) return;
        if (!this.isAlive()) return;

        stateTicks++;

        if (getState() == State.NEUTRAL) {
            return;
        }

        if (syncBossBar(world)) {
            emptyTicks = 0;
        } else if (++emptyTicks >= EMPTY_RESET_TICKS) {
            resetToNeutral();
            return;
        }

        Player target = world.getNearestPlayer(this, 48.0);

        if (target != null && getState() != State.NEUTRAL && getState() != State.TRANSFORMING && getState() != State.DIZZY) {
            faceTarget(target);
        }
        checkPhaseTransition();

        switch (getState()) {
            case IDLE -> tickIdle(world, target);
            case PUFFING -> tickPuffing(world, target);
            case TRANSFORMING -> tickTransforming(world);
            case CHARGING -> tickCharging(world, target);
            case SLAMMING -> tickSlamming(world);
            case DIZZY -> tickDizzy(world);
            case DASH_WINDUP -> tickDashWindup(world);
            case DASHING -> tickDashing(world);
            default -> { }
        }
    }

    private void tickClientState() {
        State s = getState();
        if (s != lastClientState) {
            lastClientState = s;
            clientStateTicks = 0;
        }
        clientStateTicks++;
        if (s == State.PUFFING && clientStateTicks == PUFF_RELEASE_TICK) {
            puffBurstPending = true;
        }
    }

    public int getClientStateTicks() { return clientStateTicks; }
    public int getPuffReleaseTick() { return PUFF_RELEASE_TICK; }

    public boolean consumePuffBurst() {
        boolean pending = puffBurstPending;
        puffBurstPending = false;
        return pending;
    }

    private boolean syncBossBar(ServerLevel world) {
        List<ServerPlayer> players = world.getEntitiesOfClass(
                ServerPlayer.class,
                this.getBoundingBox().inflate(48.0),
                p -> p.isAlive() && !p.isSpectator());

        for (ServerPlayer p : new ArrayList<>(bossBar.getPlayers())) {
            if (!players.contains(p)) bossBar.removePlayer(p);
        }
        for (ServerPlayer p : players) {
            bossBar.addPlayer(p);
        }
        bossBar.setProgress(Mth.clamp(getHealth() / getMaxHealth(), 0f, 1f));
        return !players.isEmpty();
    }

    private void checkPhaseTransition() {
        State s = getState();
        if (s != State.IDLE && s != State.DIZZY) return;
        float hp = getHealth() / getMaxHealth();
        if ((getPhase() == 1 && hp <= PHASE2_AT + 0.001f) || (getPhase() == 2 && hp <= PHASE3_AT + 0.001f)) {
            beginTransform();
        }
    }

    private void beginTransform() {
        setState(State.TRANSFORMING);
        this.setNoGravity(false);
        this.setDeltaMovement(Vec3.ZERO);
        sfx(SoundEvents.EVOKER_PREPARE_SUMMON, 2.0f, 0.6f);
    }

    private void tickTransforming(ServerLevel world) {
        this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        if (stateTicks % 3 == 0) {
            world.sendParticles(ParticleTypes.CLOUD, getX(), getY() + getBbHeight() * 0.5, getZ(), 6, 0.6, 0.6, 0.6, 0.02);
            world.sendParticles(ParticleTypes.END_ROD, getX(), getY() + getBbHeight() * 0.5, getZ(), 2, 0.5, 0.5, 0.5, 0.05);
        }
        int length = getPhase() == 1 ? TRANSFORM_TICKS_CATERPILLAR : TRANSFORM_TICKS_COCOON;
        if (stateTicks >= length) {
            completeTransform(world);
        }
    }

    private void completeTransform(ServerLevel world) {
        int newPhase = getPhase() + 1;
        this.entityData.set(PHASE, newPhase);
        slamCount = 0;
        dashCount = 0;
        setState(State.IDLE);
        idleTicks = 50;
        applyPhaseStats();
        this.setNoGravity(newPhase == 3);
        this.refreshDimensions();

        bossBar.setColor(newPhase == 2 ? BossEvent.BossBarColor.PURPLE : BossEvent.BossBarColor.PINK);
        world.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 1.0, getZ(), 1, 0, 0, 0, 0);
        sfx(SoundEvents.EVOKER_CELEBRATE, 2.0f, 0.7f);
    }

    private void applyPhaseStats() {
        double armor;
        double toughness;
        if (getState() == State.DIZZY) {
            armor = 0.0;
            toughness = 0.0;
        } else if (getPhase() == 3) {
            armor = ARMOR_BUTTERFLY;
            toughness = TOUGHNESS_BUTTERFLY;
        } else {
            armor = ARMOR_HEAVY;
            toughness = TOUGHNESS_HEAVY;
        }
        AttributeInstance a = this.getAttribute(Attributes.ARMOR);
        if (a != null) a.setBaseValue(armor);
        AttributeInstance t = this.getAttribute(Attributes.ARMOR_TOUGHNESS);
        if (t != null) t.setBaseValue(toughness);
    }

    private void resetToNeutral() {
        LetterPuffs.clearOwnedBy((ServerLevel) this.level(), this.getUUID());
        participants.clear();
        dashHit.clear();
        repositionGoal = null;
        bossBar.removeAllPlayers();
        bossBar.setColor(BossEvent.BossBarColor.YELLOW);

        this.entityData.set(PHASE, 1);
        setState(State.NEUTRAL);
        this.setNoGravity(false);
        this.setDeltaMovement(Vec3.ZERO);
        applyPhaseStats();
        this.refreshDimensions();
        this.setHealth(this.getMaxHealth());

        slamCount = 0;
        dashCount = 0;
        emptyTicks = 0;
        if (homePos != null) {
            this.teleportTo(homePos.getX() + 0.5, homePos.getY() + 1.0, homePos.getZ() + 0.5);
        }
    }

    private void tickIdle(ServerLevel world, @Nullable Player target) {
        switch (getPhase()) {
            case 1 -> {
                this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
                if (target != null && --idleTicks <= 0) startPuff();
            }
            case 2 -> {
                this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
                if (target != null && --idleTicks <= 0) startCharge();
            }
            default -> {
                if (repositionGoal != null) {
                    tickReposition(target);
                } else {
                    hover();
                    if (target != null && --idleTicks <= 0) beginReposition(target);
                }
            }
        }
    }

    private void startPuff() {
        currentLetter = Letter.values()[this.random.nextInt(Letter.values().length)];
        setState(State.PUFFING);
        sfx(SoundEvents.PLAYER_BREATH, 1.2f, 0.6f);
    }

    private void tickPuffing(ServerLevel world, @Nullable Player target) {
        this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);

        if (stateTicks == PUFF_RELEASE_TICK) {
            releasePuff(world, target);
        }
        if (stateTicks >= PUFF_TOTAL_TICKS) {
            setState(State.IDLE);
            idleTicks = 50 + this.random.nextInt(30);
        }
    }

    private void releasePuff(ServerLevel world, @Nullable Player target) {
        Vec3 dir;
        if (target != null) {
            dir = new Vec3(target.getX() - getX(), 0.0, target.getZ() - getZ());
        } else {
            dir = Vec3.directionFromRotation(0f, this.getYRot());
        }
        dir = dir.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0f, this.getYRot()) : dir.normalize();

        LetterPuffs.spawn(world, position().add(dir.scale(1.5)), dir, currentLetter, this, O_DAMAGE);
        sfx(SoundEvents.FIRE_EXTINGUISH, 1.5f, 0.6f);
    }

    private void startCharge() {
        setState(State.CHARGING);
        this.setNoGravity(true);
        chargeStartY = getY();
        chargeRise = Math.max(1.0, Math.min(5.0, clearAbove(6)));
        sfx(SoundEvents.PHANTOM_FLAP, 2.0f, 0.5f);
    }

    private void tickCharging(ServerLevel world, @Nullable Player target) {
        int t = stateTicks;

        double vy = 0.0;
        double goal = chargeStartY + chargeRise;
        if (getY() < goal - 0.05) {
            vy = Math.min(0.35, (goal - getY()) * 0.3 + 0.05);
        }

        double vx = 0.0;
        double vz = 0.0;
        if (t < CHARGE_TRACK_TICKS && target != null) {
            double dx = target.getX() - getX();
            double dz = target.getZ() - getZ();
            double d = Math.sqrt(dx * dx + dz * dz);
            if (d > 0.3) {
                double speed = Math.min(CHARGE_SPEED, d * 0.25);
                vx = dx / d * speed;
                vz = dz / d * speed;
            }
        }
        this.setDeltaMovement(vx, vy, vz);

        if (t >= CHARGE_TICKS) {
            startSlam();
        }
    }

    private void startSlam() {
        setState(State.SLAMMING);
        slamLanded = false;
        this.setNoGravity(false);
        this.setDeltaMovement(0.0, -SLAM_SPEED, 0.0);
        sfx(SoundEvents.PHANTOM_SWOOP, 2.0f, 0.6f);
    }

    private void tickSlamming(ServerLevel world) {
        if (slamLanded) {
            this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
            if (stateTicks >= SLAM_LAND_TICKS) {
                slamLanded = false;
                slamCount++;
                if (slamCount >= SLAMS_BEFORE_DIZZY) {
                    startDizzy();
                } else {
                    setState(State.IDLE);
                    idleTicks = 20;
                }
            }
            return;
        }

        this.setDeltaMovement(0.0, -SLAM_SPEED, 0.0);
        if ((stateTicks >= 3 && (this.onGround() || this.verticalCollision)) || stateTicks > 60) {
            slamImpact(world);
        }
    }

    private void slamImpact(ServerLevel world) {
        for (ServerPlayer p : world.players()) {
            if (!p.isAlive() || p.isSpectator()) continue;
            double dx = p.getX() - getX();
            double dz = p.getZ() - getZ();
            double dist = Math.sqrt(dx * dx + dz * dz);
            if (dist > SLAM_RADIUS || Math.abs(p.getY() - getY()) > 3.5) continue;

            float damage = dist < SLAM_RADIUS * 0.5 ? SLAM_DAMAGE : SLAM_DAMAGE * 0.6f;
            p.hurt(world.damageSources().mobAttack(this), damage);
            p.knockback(0.9, getX() - p.getX(), getZ() - p.getZ());
            p.hurtMarked = true;
        }

        BlockState below = world.getBlockState(this.blockPosition().below());
        if (!below.isAir()) {
            world.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, below),
                    getX(), getY() + 0.2, getZ(), 60, SLAM_RADIUS * 0.3, 0.2, SLAM_RADIUS * 0.3, 0.2);
        }
        world.sendParticles(ParticleTypes.EXPLOSION, getX(), getY() + 0.3, getZ(), 3, 1.0, 0.1, 1.0, 0.0);
        world.playSound(null, getX(), getY(), getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 1.5f, 0.7f);
        world.playSound(null, getX(), getY(), getZ(), SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 1.2f, 0.6f);

        slamLanded = true;
        stateTicks = 0;
        this.setDeltaMovement(Vec3.ZERO);
    }

    private void startDizzy() {
        setState(State.DIZZY);
        this.setNoGravity(false);
        applyPhaseStats();
        sfx(SoundEvents.VILLAGER_NO, 1.5f, 0.6f);
    }

    private void tickDizzy(ServerLevel world) {
        this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);

        if (stateTicks % 5 == 0) {
            double angle = stateTicks * 0.5;
            world.sendParticles(ParticleTypes.CRIT,
                    getX() + Math.cos(angle) * 0.7, getY() + getBbHeight() + 0.3, getZ() + Math.sin(angle) * 0.7,
                    2, 0.1, 0.05, 0.1, 0.05);
        }

        if (stateTicks >= DIZZY_TICKS) {
            slamCount = 0;
            dashCount = 0;
            setState(State.IDLE);
            idleTicks = 40;
            applyPhaseStats();
            this.setNoGravity(getPhase() == 3);
        }
    }

    private void hover() {
        double desired = groundY(getX(), getY(), getZ()) + HOVER_HEIGHT;
        double vy = Mth.clamp((desired - getY()) * 0.2, -0.3, 0.3);
        this.setDeltaMovement(0.0, vy, 0.0);
    }

    private void beginReposition(Player target) {
        Level w = this.level();
        Vec3 tp = target.position();
        Vec3 best = null;
        for (int attempt = 0; attempt < 10; attempt++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            Vec3 goal = new Vec3(tp.x + Math.cos(angle) * DASH_APPROACH, target.getY() + 0.2, tp.z + Math.sin(angle) * DASH_APPROACH);
            BlockPos gp = BlockPos.containing(goal);
            boolean free = w.getBlockState(gp).getCollisionShape(w, gp).isEmpty()
                    && w.getBlockState(gp.above()).getCollisionShape(w, gp.above()).isEmpty();
            if (free && insideArena(goal)) {
                best = goal;
                break;
            }
        }
        repositionGoal = best != null ? best : position();
        repositionTicks = 0;
    }

    private void tickReposition(@Nullable Player target) {
        if (target == null) {
            repositionGoal = null;
            idleTicks = 20;
            return;
        }
        repositionTicks++;
        Vec3 toGoal = repositionGoal.subtract(position());
        if (toGoal.length() < 1.0 || repositionTicks > 50) {
            repositionGoal = null;
            this.setDeltaMovement(Vec3.ZERO);
            startDashWindup(target);
            return;
        }
        Vec3 v = toGoal.scale(0.25);
        if (v.length() > 1.1) v = v.normalize().scale(1.1);
        this.setDeltaMovement(v);
    }

    private void startDashWindup(Player target) {
        Vec3 d = new Vec3(target.getX() - getX(), 0.0, target.getZ() - getZ());
        d = d.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0f, this.getYRot()) : d.normalize();

        dashDir = d;
        dashStart = position();
        dashHit.clear();
        setState(State.DASH_WINDUP);
        sfx(SoundEvents.BAT_TAKEOFF, 2.0f, 0.6f);
    }

    private boolean insideArena(Vec3 p) {
        if (homePos == null) return true;
        double dx = p.x - (homePos.getX() + 0.5);
        double dz = p.z - (homePos.getZ() + 0.5);
        return dx * dx + dz * dz <= ARENA_RADIUS * ARENA_RADIUS;
    }

    private void tickDashWindup(ServerLevel world) {
        this.setDeltaMovement(Vec3.ZERO);

        if (stateTicks >= DASH_WINDUP_TICKS) {
            setState(State.DASHING);
            dashOrigin = position();
            sfx(SoundEvents.PHANTOM_SWOOP, 2.0f, 0.9f);
        }
    }

    private void tickDashing(ServerLevel world) {
        this.setDeltaMovement(dashDir.scale(DASH_SPEED));

        for (ServerPlayer p : world.getEntitiesOfClass(ServerPlayer.class,
                this.getBoundingBox().inflate(1.0), pl -> pl.isAlive() && !pl.isSpectator())) {
            if (!dashHit.add(p.getUUID())) continue;
            p.hurt(world.damageSources().mobAttack(this), DASH_DAMAGE);
            Vec3 side = new Vec3(-dashDir.z, 0.0, dashDir.x);
            double sign = side.dot(p.position().subtract(position())) >= 0 ? 1.0 : -1.0;
            p.knockback(0.8, -side.x * sign, -side.z * sign);
            p.hurtMarked = true;
        }

        world.sendParticles(ParticleTypes.CLOUD, getX(), getY() + 0.5, getZ(), 3, 0.3, 0.2, 0.3, 0.01);

        double dx = getX() - dashOrigin.x;
        double dz = getZ() - dashOrigin.z;
        double traveled = Math.sqrt(dx * dx + dz * dz);
        if (traveled >= DASH_DISTANCE || this.horizontalCollision || stateTicks > 40) {
            endDash();
        }
    }

    private void endDash() {
        this.setDeltaMovement(Vec3.ZERO);
        dashCount++;
        if (dashCount >= DASHES_BEFORE_DIZZY) {
            startDizzy();
        } else {
            setState(State.IDLE);
            idleTicks = 25;
        }
    }

    private int clearAbove(int max) {
        Level w = this.level();
        for (int i = 0; i < max; i++) {
            BlockPos p = BlockPos.containing(getX(), getY() + getBbHeight() + i, getZ());
            if (!w.getBlockState(p).getCollisionShape(w, p).isEmpty()) return i;
        }
        return max;
    }

    private double groundY(double x, double fromY, double z) {
        Level w = this.level();
        BlockPos.MutableBlockPos p = new BlockPos.MutableBlockPos(x, fromY, z);
        for (int i = 0; i < 24; i++) {
            if (!w.getBlockState(p).getCollisionShape(w, p).isEmpty()) return p.getY() + 1.0;
            p.move(Direction.DOWN);
        }
        return fromY;
    }

    @Override
    protected BodyRotationControl createBodyControl() {
        return new BodyRotationControl(this) {
            @Override
            public void clientTick() {
                if (CaterpillarEntity.this.getPhase() == 1) {
                    CaterpillarEntity.this.yBodyRot = CaterpillarEntity.this.getYRot();
                } else {
                    super.clientTick();
                }
            }
        };
    }

    private void faceTarget(@Nullable Entity target) {
        if (target == null) return;
        this.getLookControl().setLookAt(target, 30.0F, 30.0F);
        if (getPhase() > 1) {
            float targetYaw = this.getYHeadRot();
            this.setYRot(targetYaw);
            this.yBodyRot = targetYaw;
        }
    }

    private void sfx(SoundEvent sound, float volume, float pitch) {
        this.level().playSound(null, getX(), getY(), getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        if (getState() == State.DIZZY || getPhase() == 3) return SoundEvents.BEE_HURT;
        return SoundEvents.CHAIN_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BEE_DEATH;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);

        if (!(this.level() instanceof ServerLevel world)) return;
        MinecraftServer server = world.getServer();
        if (server == null) return;

        this.setNoGravity(false);
        LetterPuffs.clearOwnedBy(world, this.getUUID());
        bossBar.removeAllPlayers();

        if (source.getEntity() instanceof ServerPlayer killer) participants.add(killer.getUUID());

        for (UUID id : participants) {
            ServerPlayer player = server.getPlayerList().getPlayer(id);
            if (player == null) continue;

            ProgressionComponent component = PlayerProgressionManager.get(server).get(player.getUUID());
            if (!component.hasKilledCaterpillar()) {
                component.setKilledCaterpillar(true);
                AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "boss_root"));
                AdvancementUtil.grant(player, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "killed_caterpillar"));

                this.spawnAtLocation(new ItemStack(ModItems.PIPE_ITEM));
                this.spawnAtLocation(new ItemStack(ModItems.CATERPILLAR_HEAD_ITEM));
                WonderlandProgressionUtil.triggerMilestone(player, 2, null);
            }
        }
    }

    @Override
    public int getBaseExperienceReward() {
        return 30;
    }

    @Override
    public void remove(RemovalReason reason) {
        if (this.level() instanceof ServerLevel serverWorld) {
            LetterPuffs.clearOwnedBy(serverWorld, this.getUUID());
        }
        bossBar.removeAllPlayers();
        super.remove(reason);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("CaterpillarPhase", getPhase());
        nbt.putBoolean("Provoked", getState() != State.NEUTRAL);
        if (homePos != null) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.entityData.set(PHASE, Mth.clamp(nbt.getInt("CaterpillarPhase"), 1, 3));
        if (nbt.contains("HomeX")) {
            this.homePos = new BlockPos(nbt.getInt("HomeX"), nbt.getInt("HomeY"), nbt.getInt("HomeZ"));
        }

        setState(nbt.getBoolean("Provoked") ? State.IDLE : State.NEUTRAL);
        idleTicks = 60;
        this.setNoGravity(getPhase() == 3);
        applyPhaseStats();
        this.refreshDimensions();
        bossBar.setColor(switch (getPhase()) {
            case 2 -> BossEvent.BossBarColor.PURPLE;
            case 3 -> BossEvent.BossBarColor.PINK;
            default -> BossEvent.BossBarColor.YELLOW;
        });
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "main", 4, state -> {
            state.setAndContinue(switch (getState()) {
                case PUFFING -> PUFF;
                case TRANSFORMING -> TRANSFORM;
                case CHARGING -> CHARGE;
                case SLAMMING -> SLAM;
                case DIZZY -> DIZZY_ANIM;
                case DASH_WINDUP -> WINDUP;
                case DASHING -> DASH;
                default -> IDLE;
            });
            return PlayState.CONTINUE;
        }));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}