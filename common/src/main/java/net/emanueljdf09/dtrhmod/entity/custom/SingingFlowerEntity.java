package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.projectile.PlantFangEntity;
import net.emanueljdf09.dtrhmod.entity.projectile.SkySeedProjectile;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
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
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class SingingFlowerEntity extends PathfinderMob implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation STARTLED_ANIM = RawAnimation.begin().thenLoop("startled");
    private static final RawAnimation BURY = RawAnimation.begin().thenPlayAndHold("bury");
    private static final RawAnimation BURIED_ANIM = RawAnimation.begin().thenLoop("buried");
    private static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("emerge");
    private static final RawAnimation CHOMP_CAST = RawAnimation.begin().thenPlay("cast_chomp");
    private static final RawAnimation VINE_CAST = RawAnimation.begin().thenPlay("cast_vine");
    private static final RawAnimation SKY_CAST = RawAnimation.begin().thenPlay("cast_sky");

    public static final float MAX_HEALTH = 40.0f;
    public static final int BURY_TICKS = 20;
    public static final int EMERGE_TICKS = 20;
    private static final double TOO_CLOSE = 4.0;
    private static final double MIN_EMERGE_DIST = 6.0;
    private static final double MAX_EMERGE_DIST = 14.0;
    private static final double ARENA_RADIUS = 24.0;
    private static final float FINAL_SCALE = 3.0f;

    private static final double ARC_SPAN = Math.toRadians(150.0);
    private static final double ARC_RADIUS_MIN = 8.0;
    private static final double ARC_RADIUS_MAX = 10.0;

    public static final int STARTLE_CHOMP_TICKS = 60;
    public static final int STARTLE_VINE_CUT_TICKS = 80;
    public static final int STARTLE_VINE_EXPIRE_TICKS = 40;
    private static final float STARTLED_DAMAGE_MULTIPLIER = 1.5f;

    private static final int CAST_LEAD_TICKS = 8;
    private static final int FAN_LINE_STEP = 4;
    private static final double VINE_MIN_DIST = 7.0;
    private static final double VINE_MAX_DIST = 18.0;

    private static final float[] PENTATONIC = {1.0f, 1.122f, 1.26f, 1.498f, 1.682f};

    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(SingingFlowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> PHASE = SynchedEntityData.defineId(SingingFlowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> BURROW_STATE = SynchedEntityData.defineId(SingingFlowerEntity.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> FINAL_FORM = SynchedEntityData.defineId(SingingFlowerEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STARTLED = SynchedEntityData.defineId(SingingFlowerEntity.class, EntityDataSerializers.BOOLEAN);

    public enum BurrowState {
        SURFACED, BURYING, BURIED, EMERGING;

        public static BurrowState byId(int id) {
            BurrowState[] values = values();
            return values[Math.floorMod(id, values.length)];
        }
    }

    public enum AttackType {
        CHOMP, VINE
    }

    @Nullable
    private BlockPos homePos = null;
    private boolean goingHome = false;

    private int stateTicks = 0;
    private int surfaceTicks = 120;
    private int buriedTicks = 30;
    private int skyCooldown = 100;

    @Nullable
    private VineAnchorEntity activeVine = null;

    @Nullable
    private AttackType pendingAttack = null;
    private int pendingDelay = 0;
    private int pendingSweep = 1;
    @Nullable
    private UUID pendingTarget = null;
    private boolean castAnimPlayed = false;

    private int startledTicks = 0;
    private int standaloneCooldown = 60;

    private final List<PendingStrike> strikes = new ArrayList<>();

    public SingingFlowerEntity(EntityType<? extends PathfinderMob> entityType, Level world) {
        super(entityType, world);
        this.setPersistenceRequired();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(VARIANT, 0);
        builder.define(PHASE, 1);
        builder.define(BURROW_STATE, BurrowState.SURFACED.ordinal());
        builder.define(FINAL_FORM, false);
        builder.define(STARTLED, false);
    }

    public static AttributeSupplier.Builder setAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, MAX_HEALTH)
                .add(Attributes.MOVEMENT_SPEED, 0.0)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
                .add(Attributes.FOLLOW_RANGE, 40.0)
                .add(Attributes.ATTACK_DAMAGE, 6.0)
                .add(Attributes.ARMOR, 4.0);
    }

    public int getVariant() { return this.entityData.get(VARIANT); }
    public void setVariant(int variant) { this.entityData.set(VARIANT, Math.floorMod(variant, 5)); }

    public int getPhase() { return this.entityData.get(PHASE); }
    public void setPhase(int phase) { this.entityData.set(PHASE, phase); }

    public BurrowState getBurrowState() { return BurrowState.byId(this.entityData.get(BURROW_STATE)); }
    private void setBurrowState(BurrowState state) {
        this.entityData.set(BURROW_STATE, state.ordinal());
        this.stateTicks = 0;
    }

    public boolean isFinalForm() { return this.entityData.get(FINAL_FORM); }
    public boolean isStartled() { return this.entityData.get(STARTLED); }

    public void setHome(BlockPos pos) { this.homePos = pos; }
    @Nullable
    public BlockPos getHome() { return this.homePos; }

    @Nullable
    private SingingFlowersSpawnerBlockEntity getSpawner() {
        if (homePos == null) return null;
        return this.level().getBlockEntity(homePos) instanceof SingingFlowersSpawnerBlockEntity be ? be : null;
    }

    private boolean isOrphaned() {
        if (homePos == null) return false;
        SingingFlowersSpawnerBlockEntity spawner = getSpawner();
        return spawner == null || !spawner.isTracking(this.getUUID());
    }

    public boolean isReadyToAttack() {
        return isAlive() && !isRemoved()
                && getBurrowState() == BurrowState.SURFACED
                && startledTicks == 0
                && pendingAttack == null
                && !hasActiveVine();
    }

    public boolean hasPendingAttack() {
        return pendingAttack != null;
    }

    public boolean hasActiveVine() {
        if (activeVine != null && activeVine.isRemoved()) activeVine = null;
        return activeVine != null;
    }

    public boolean canVineTarget(Player target) {
        double distSq = this.distanceToSqr(target);
        return distSq >= VINE_MIN_DIST * VINE_MIN_DIST && distSq <= VINE_MAX_DIST * VINE_MAX_DIST;
    }

    public void queueAttack(AttackType type, int delay, int sweep, Player target) {
        this.pendingAttack = type;
        this.pendingDelay = Math.max(1, delay);
        this.pendingSweep = sweep >= 0 ? 1 : -1;
        this.pendingTarget = target.getUUID();
        this.castAnimPlayed = false;
    }

    public static Vec3 rightOf(Entity viewer) {
        double yaw = Math.toRadians(viewer.getYRot());
        return new Vec3(-Math.cos(yaw), 0.0, -Math.sin(yaw));
    }

    public void startle(int ticks) {
        if (getBurrowState() != BurrowState.SURFACED || !isAlive()) return;
        boolean wasStartled = startledTicks > 0;
        startledTicks = Math.max(startledTicks, ticks);
        pendingAttack = null;
        if (!wasStartled) {
            this.entityData.set(STARTLED, true);
            sfx(SoundEvents.VILLAGER_NO, 1.0f, 1.6f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level().isClientSide()) return;

        if (!this.isAlive()) return;

        stateTicks++;

        if (this.tickCount > 100 && this.tickCount % 40 == 0 && isOrphaned()) {
            this.discard();
            return;
        }

        if (!strikes.isEmpty() && this.level() instanceof ServerLevel strikeWorld) {
            processStrikes(strikeWorld);
        }

        Player target = this.level().getNearestPlayer(this, 40.0D);

        switch (getBurrowState()) {
            case SURFACED -> tickSurfaced(target);
            case BURYING -> {
                if (stateTicks >= BURY_TICKS) enterBuried();
            }
            case BURIED -> tickBuried(target);
            case EMERGING -> {
                if (stateTicks >= EMERGE_TICKS) finishEmerging();
            }
        }
    }

    private void tickSurfaced(@Nullable Player target) {
        if (target != null) {
            this.lookAt(target, 30.0F, 30.0F);

            double dX = target.getX() - this.getX();
            double dZ = target.getZ() - this.getZ();
            float targetYaw = (float) (Mth.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;

            this.setYRot(targetYaw);
            this.yBodyRot = targetYaw;
            this.yHeadRot = targetYaw;
        }

        boolean busy = startledTicks > 0 || pendingAttack != null || hasActiveVine();

        if (!isFinalForm() && !busy) {
            boolean tooClose = target != null && this.distanceToSqr(target) < TOO_CLOSE * TOO_CLOSE;
            BlockState blockBelow = this.level().getBlockState(this.blockPosition().below());
            if ((tooClose || stateTicks >= surfaceTicks) && canBurrowOn(blockBelow)) {
                startBurying();
                return;
            }
        }

        if (startledTicks > 0) {
            tickStartled();
            return;
        }

        if (!(this.level() instanceof ServerLevel world)) return;

        if (pendingAttack != null) {
            tickPendingAttack(world);
        } else if (this.random.nextInt(70) == 0) {
            sing();
        }

        if (homePos == null && target != null && pendingAttack == null && isReadyToAttack()) {
            if (--standaloneCooldown <= 0) {
                queueAttack(AttackType.CHOMP, 12, this.random.nextBoolean() ? 1 : -1, target);
                standaloneCooldown = 70 + this.random.nextInt(20);
            }
        }

        if (isFinalForm() && target != null && this.distanceToSqr(target) <= 24.0 * 24.0) {
            tickSky(world, target);
        }
    }

    private void tickStartled() {
        startledTicks--;
        if (this.level() instanceof ServerLevel world && this.tickCount % 5 == 0) {

            world.sendParticles(ParticleTypes.CRIT, this.getX(), this.getY() + this.getBbHeight() + 0.2, this.getZ(),
                    3, 0.3, 0.1, 0.3, 0.1);
        }
        if (startledTicks <= 0) {
            startledTicks = 0;
            this.entityData.set(STARTLED, false);
        }
    }

    private void tickPendingAttack(ServerLevel world) {
        AttackType type = pendingAttack;
        Player target = pendingTarget == null ? null : world.getPlayerByUUID(pendingTarget);
        if (type == null || target == null || !target.isAlive() || target.isSpectator()) {
            pendingAttack = null;
            return;
        }

        if (pendingDelay > 0) {
            pendingDelay--;

            if (!castAnimPlayed && pendingDelay <= CAST_LEAD_TICKS) {
                castAnimPlayed = true;
                this.triggerAnim("attack_controller", type == AttackType.CHOMP ? "cast_chomp" : "cast_vine");
                sfx(SoundEvents.EVOKER_PREPARE_ATTACK, 1.0f, type == AttackType.CHOMP ? 1.0f : 0.8f);
            }
            if (this.tickCount % 3 == 0) {
                world.sendParticles(ParticleTypes.NOTE, this.getX(), this.getY() + this.getBbHeight() * 0.9, this.getZ(),
                        1, 0.4, 0.2, 0.4, 0.0);
            }
            if (pendingDelay > 0) return;
        }

        pendingAttack = null;
        switch (type) {
            case CHOMP -> {
                castChomp(world, target);
                startle(STARTLE_CHOMP_TICKS);
            }
            case VINE -> castVine(world, target);
        }
    }

    private void castChomp(ServerLevel world, Player target) {
        sfx(SoundEvents.EVOKER_CAST_SPELL, 1.0f, isFinalForm() ? 0.7f : 1.2f);

        int lines = isFinalForm() ? 5 : 1;
        Vec3 right = rightOf(target);
        for (int k = 0; k < lines; k++) {
            double lateral = (k - (lines - 1) / 2.0) * 2.0;
            int order = pendingSweep > 0 ? k : (lines - 1 - k);
            castFangLine(world,
                    target.getX() + right.x * lateral,
                    target.getZ() + right.z * lateral,
                    order * FAN_LINE_STEP);
        }
    }

    private void castFangLine(ServerLevel world, double tx, double tz, int extraDelay) {
        double dx = tx - this.getX();
        double dz = tz - this.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return;
        dx /= len;
        dz /= len;

        float yaw = (float) Mth.atan2(dz, dx);
        double start = this.getBbWidth() / 2.0 + 1.0;
        int count = Mth.clamp((int) (len / 1.25) + 3, 6, 16);

        for (int i = 0; i < count; i++) {
            double x = this.getX() + dx * (start + i * 1.25);
            double z = this.getZ() + dz * (start + i * 1.25);

            BlockPos ground = findStandable(BlockPos.containing(x, this.getY(), z));
            if (ground == null) continue;

            world.addFreshEntity(new PlantFangEntity(world, x, ground.getY(), z, yaw, extraDelay + i * 2, this));
        }
    }

    private void castVine(ServerLevel world, Player target) {
        if (!(target instanceof ServerPlayer victim)) return;
        if (hasActiveVine() || !canVineTarget(victim)) return;

        VineAnchorEntity vine = ModEntities.VINE_ANCHOR.get().create(world);
        if (vine == null) return;

        vine.bind(this, victim);
        world.addFreshEntity(vine);
        activeVine = vine;
    }

    @Override
    public boolean isAlliedTo(Entity other) {
        return other instanceof SingingFlowerEntity || super.isAlliedTo(other);
    }

    private static class PendingStrike {
        final Vec3 ground;
        int ticksLeft;

        PendingStrike(Vec3 ground, int ticksLeft) {
            this.ground = ground;
            this.ticksLeft = ticksLeft;
        }
    }

    private void tickSky(ServerLevel world, Player target) {
        if (skyCooldown > 0) {
            skyCooldown--;
            return;
        }

        this.triggerAnim("attack_controller", "cast_sky");
        sfx(SoundEvents.EVOKER_PREPARE_SUMMON, 1.5f, 0.7f);

        queueStrike(target.getX(), target.getY(), target.getZ());
        for (int i = 0; i < 5; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dist = 2.0 + this.random.nextDouble() * 6.0;
            queueStrike(target.getX() + Math.cos(angle) * dist, target.getY(), target.getZ() + Math.sin(angle) * dist);
        }
        skyCooldown = 120 + this.random.nextInt(40);
    }

    private void queueStrike(double x, double y, double z) {
        BlockPos ground = findStandable(BlockPos.containing(x, y, z));
        if (ground == null) return;
        strikes.add(new PendingStrike(new Vec3(x, ground.getY(), z), 30 + this.random.nextInt(20)));
    }

    private void processStrikes(ServerLevel world) {
        Iterator<PendingStrike> it = strikes.iterator();
        while (it.hasNext()) {
            PendingStrike strike = it.next();
            if (strike.ticksLeft % 3 == 0) {
                warningRing(world, strike.ground);
            }
            if (--strike.ticksLeft <= 0) {
                dropSeed(world, strike.ground);
                it.remove();
            }
        }
    }

    private void warningRing(ServerLevel world, Vec3 ground) {
        int points = 14;
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            world.sendParticles(DustParticleOptions.REDSTONE,
                    ground.x + Math.cos(angle) * SkySeedProjectile.BLAST_RADIUS,
                    ground.y + 0.1,
                    ground.z + Math.sin(angle) * SkySeedProjectile.BLAST_RADIUS,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void dropSeed(ServerLevel world, Vec3 ground) {
        BlockPos g = BlockPos.containing(ground);
        int height = 2;
        while (height < 16) {
            BlockPos p = g.above(height);
            if (!world.getBlockState(p).getCollisionShape(world, p).isEmpty()) break;
            height++;
        }

        SkySeedProjectile seed = new SkySeedProjectile(world, this);
        seed.setPos(ground.x, g.getY() + height - 0.5, ground.z);
        seed.absMoveTo(0.0, -1.0, 0.0);
        world.addFreshEntity(seed);

        world.playSound(null, ground.x, ground.y + 3.0, ground.z,
                SoundEvents.SPLASH_POTION_THROW, SoundSource.HOSTILE, 0.7f, 0.6f);
    }

    private void startBurying() {
        setBurrowState(BurrowState.BURYING);
        sfx(SoundEvents.ROOTED_DIRT_PLACE, 1.2f, 0.9f);
        dirtBurst();
    }

    private void enterBuried() {
        setBurrowState(BurrowState.BURIED);
        this.buriedTicks = 30 + this.random.nextInt(20);
        dirtBurst();
    }

    private void tickBuried(@Nullable Player target) {
        if (stateTicks < buriedTicks) return;

        Vec3 dest = pickDestination(target);
        if (dest != null) {
            this.teleportTo(dest.x, dest.y, dest.z);
        }
        setBurrowState(BurrowState.EMERGING);
        sfx(SoundEvents.ROOTED_DIRT_BREAK, 1.2f, 0.8f);
        sfx(SoundEvents.AMETHYST_BLOCK_CHIME, 0.8f, PENTATONIC[getVariant() % PENTATONIC.length]);
        dirtBurst();
    }

    private boolean canBurrowOn(BlockState state) {
        return state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.MUD) || state.is(ModBlocks.WONDER_GRASS) || state.is(ModBlocks.WONDER_DIRT_PATH) || state.is(ModBlocks.WONDER_DIRT);
    }

    private void finishEmerging() {
        setBurrowState(BurrowState.SURFACED);
        this.surfaceTicks = 100 + this.random.nextInt(60);

        if (goingHome && !isFinalForm()) {
            growUp();
        }
    }

    public void beginFinalForm() {
        startledTicks = 0; this.entityData.set(STARTLED, false);
        this.goingHome = true;
        if (getBurrowState() == BurrowState.SURFACED) {
            pendingAttack = null;
            startBurying();
        }
    }

    public void startEmerging() {
        setBurrowState(BurrowState.EMERGING);
        sfx(SoundEvents.AMETHYST_BLOCK_CHIME, 0.8f, PENTATONIC[getVariant() % PENTATONIC.length]);
    }

    private void growUp() {
        this.entityData.set(FINAL_FORM, true);
        sfx(SoundEvents.EVOKER_CELEBRATE, 2.0f, 0.5f);
        ScaleData scale = ScaleTypes.BASE.getScaleData(this);
        scale.setTargetScale(FINAL_SCALE);
        scale.setScaleTickDelay(60);
    }

    @Nullable
    private Vec3 homeDestination() {
        return homePos == null ? null : Vec3.atBottomCenterOf(homePos.above());
    }

    @Nullable
    private Vec3 pickDestination(@Nullable Player target) {
        if (goingHome) return homeDestination();
        if (target == null) return null;

        double yaw = Math.toRadians(target.getYRot());
        double forward = Math.atan2(Math.cos(yaw), -Math.sin(yaw));
        double slot = getVariant() / (double) (SingingFlowersSpawnerBlockEntity.FLOWER_COUNT - 1);
        double slotOffset = (slot - 0.5) * ARC_SPAN;

        for (int attempt = 0; attempt < 16; attempt++) {
            double angle;
            double dist;
            if (attempt < 8) {
                angle = forward + slotOffset + (this.random.nextDouble() - 0.5) * 0.25;
                dist = ARC_RADIUS_MIN + this.random.nextDouble() * (ARC_RADIUS_MAX - ARC_RADIUS_MIN);
            } else {
                angle = this.random.nextDouble() * Math.PI * 2.0;
                dist = MIN_EMERGE_DIST + this.random.nextDouble() * (MAX_EMERGE_DIST - MIN_EMERGE_DIST);
            }
            double x = target.getX() + Math.cos(angle) * dist;
            double z = target.getZ() + Math.sin(angle) * dist;

            BlockPos ground = findStandable(BlockPos.containing(x, target.getY(), z));
            if (ground == null) continue;

            BlockState groundState = this.level().getBlockState(ground.below());
            if (!canBurrowOn(groundState)) continue;

            if (homePos != null && ground.distSqr(homePos) > ARENA_RADIUS * ARENA_RADIUS) continue;

            Vec3 spot = Vec3.atBottomCenterOf(ground);

            if (this.level().getNearestPlayer(spot.x, spot.y, spot.z, TOO_CLOSE + 1.5, false) != null) continue;
            return spot;
        }
        return null;
    }

    @Nullable
    private BlockPos findStandable(BlockPos start) {
        Level w = this.level();
        for (int dy = 3; dy >= -4; dy--) {
            BlockPos p = start.above(dy);
            boolean floor = w.getBlockState(p.below()).isFaceSturdy(w, p.below(), Direction.UP);
            boolean room = w.getBlockState(p).getCollisionShape(w, p).isEmpty()
                    && w.getBlockState(p.above()).getCollisionShape(w, p.above()).isEmpty();
            if (floor && room) return p;
        }
        return null;
    }

    private void dirtBurst() {
        if (this.level() instanceof ServerLevel serverWorld) {
            BlockState below = serverWorld.getBlockState(this.blockPosition().below());
            if (below.isAir()) return;
            serverWorld.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, below),
                    this.getX(), this.getY() + 0.1, this.getZ(), 20, 0.5, 0.1, 0.5, 0.1);
        }
    }

    private void sfx(SoundEvent sound, float volume, float pitch) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundSource.HOSTILE, volume, pitch);
    }

    private void sing() {
        int idx = (getVariant() + this.random.nextInt(2) * 2) % PENTATONIC.length;
        float pitch = PENTATONIC[idx] * (isFinalForm() ? 0.5f : 1.0f);
        sfx(SoundEvents.AMETHYST_BLOCK_CHIME, 0.9f, pitch);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.AZALEA_LEAVES_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.AZALEA_LEAVES_BREAK;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (getBurrowState() != BurrowState.SURFACED && !source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean isPickable() {
        return getBurrowState() == BurrowState.SURFACED && super.isPickable();
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (!this.level().isClientSide() && startledTicks > 0) {
            amount *= STARTLED_DAMAGE_MULTIPLIER;
        }
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide() && source.getEntity() instanceof ServerPlayer player) {
            SingingFlowersSpawnerBlockEntity spawner = getSpawner();
            if (spawner != null) spawner.addParticipant(player);
        }
        return hurt;
    }

    @Override
    public void die(DamageSource source) {
        super.die(source);
        if (!this.level().isClientSide()) {
            SingingFlowersSpawnerBlockEntity spawner = getSpawner();
            if (spawner != null) spawner.onFlowerKilled(this, source);
        }
    }

    @Override
    public void knockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void doPush(Entity entity) { }

    @Override
    public boolean removeWhenFarAway(double distanceSquared) { return false; }


    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("Variant", getVariant());
        nbt.putInt("FlowerPhase", getPhase());
        nbt.putInt("BurrowState", getBurrowState().ordinal());
        nbt.putBoolean("FinalForm", isFinalForm());
        nbt.putBoolean("GoingHome", goingHome);
        if (homePos != null) {
            nbt.putInt("HomeX", homePos.getX());
            nbt.putInt("HomeY", homePos.getY());
            nbt.putInt("HomeZ", homePos.getZ());
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        setVariant(nbt.getInt("Variant"));
        setPhase(Math.max(1, nbt.getInt("FlowerPhase")));
        this.entityData.set(FINAL_FORM, nbt.getBoolean("FinalForm"));
        this.goingHome = nbt.getBoolean("GoingHome");
        if (nbt.contains("HomeX")) {
            this.homePos = new BlockPos(nbt.getInt("HomeX"), nbt.getInt("HomeY"), nbt.getInt("HomeZ"));
        }

        BurrowState saved = BurrowState.byId(nbt.getInt("BurrowState"));
        setBurrowState(saved == BurrowState.SURFACED ? BurrowState.SURFACED : BurrowState.EMERGING);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 3, state ->
                state.setAndContinue(switch (getBurrowState()) {
                    case BURYING -> BURY;
                    case BURIED -> BURIED_ANIM;
                    case EMERGING -> EMERGE;
                    default -> isStartled() ? STARTLED_ANIM : IDLE;
                })));

        controllers.add(new AnimationController<>(this, "attack_controller", 2, state -> PlayState.STOP)
                .triggerableAnim("cast_chomp", CHOMP_CAST)
                .triggerableAnim("cast_vine", VINE_CAST)
                .triggerableAnim("cast_sky", SKY_CAST));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
}