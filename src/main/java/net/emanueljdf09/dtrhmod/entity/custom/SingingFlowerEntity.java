package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.EvokerFangsEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.emanueljdf09.dtrhmod.entity.projectile.SkySeedProjectile;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import virtuoel.pehkui.api.ScaleData;
import virtuoel.pehkui.api.ScaleTypes;

public class SingingFlowerEntity extends PathAwareEntity implements GeoEntity {
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

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PHASE = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BURROW_STATE = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FINAL_FORM = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> STARTLED = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

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

    public SingingFlowerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(PHASE, 1);
        this.dataTracker.startTracking(BURROW_STATE, BurrowState.SURFACED.ordinal());
        this.dataTracker.startTracking(FINAL_FORM, false);
        this.dataTracker.startTracking(STARTLED, false);
    }

    public static DefaultAttributeContainer.Builder setAttributes() {
        return PathAwareEntity.createMobAttributes()
                .add(EntityAttributes.GENERIC_MAX_HEALTH, MAX_HEALTH)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0)
                .add(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE, 1.0)
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 40.0)
                .add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
                .add(EntityAttributes.GENERIC_ARMOR, 4.0);
    }

    public int getVariant() { return this.dataTracker.get(VARIANT); }
    public void setVariant(int variant) { this.dataTracker.set(VARIANT, Math.floorMod(variant, 5)); }

    public int getPhase() { return this.dataTracker.get(PHASE); }
    public void setPhase(int phase) { this.dataTracker.set(PHASE, phase); }

    public BurrowState getBurrowState() { return BurrowState.byId(this.dataTracker.get(BURROW_STATE)); }
    private void setBurrowState(BurrowState state) {
        this.dataTracker.set(BURROW_STATE, state.ordinal());
        this.stateTicks = 0;
    }

    public boolean isFinalForm() { return this.dataTracker.get(FINAL_FORM); }
    public boolean isStartled() { return this.dataTracker.get(STARTLED); }

    public void setHome(BlockPos pos) { this.homePos = pos; }
    @Nullable
    public BlockPos getHome() { return this.homePos; }

    @Nullable
    private SingingFlowersSpawnerBlockEntity getSpawner() {
        if (homePos == null) return null;
        return this.getWorld().getBlockEntity(homePos) instanceof SingingFlowersSpawnerBlockEntity be ? be : null;
    }

    private boolean isOrphaned() {
        if (homePos == null) return false;
        SingingFlowersSpawnerBlockEntity spawner = getSpawner();
        return spawner == null || !spawner.isTracking(this.getUuid());
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

    public boolean canVineTarget(PlayerEntity target) {
        double distSq = this.squaredDistanceTo(target);
        return distSq >= VINE_MIN_DIST * VINE_MIN_DIST && distSq <= VINE_MAX_DIST * VINE_MAX_DIST;
    }

    public void queueAttack(AttackType type, int delay, int sweep, PlayerEntity target) {
        this.pendingAttack = type;
        this.pendingDelay = Math.max(1, delay);
        this.pendingSweep = sweep >= 0 ? 1 : -1;
        this.pendingTarget = target.getUuid();
        this.castAnimPlayed = false;
    }

    public static Vec3d rightOf(Entity viewer) {
        double yaw = Math.toRadians(viewer.getYaw());
        return new Vec3d(-Math.cos(yaw), 0.0, -Math.sin(yaw));
    }

    public void startle(int ticks) {
        if (getBurrowState() != BurrowState.SURFACED || !isAlive()) return;
        boolean wasStartled = startledTicks > 0;
        startledTicks = Math.max(startledTicks, ticks);
        pendingAttack = null;
        if (!wasStartled) {
            this.dataTracker.set(STARTLED, true);
            sfx(SoundEvents.ENTITY_VILLAGER_NO, 1.0f, 1.6f);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;

        if (!this.isAlive()) return;

        stateTicks++;

        if (this.age > 100 && this.age % 40 == 0 && isOrphaned()) {
            this.discard();
            return;
        }

        if (!strikes.isEmpty() && this.getWorld() instanceof ServerWorld strikeWorld) {
            processStrikes(strikeWorld);
        }

        PlayerEntity target = this.getWorld().getClosestPlayer(this, 40.0D);

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

    private void tickSurfaced(@Nullable PlayerEntity target) {
        if (target != null) {
            this.lookAtEntity(target, 30.0F, 30.0F);

            double dX = target.getX() - this.getX();
            double dZ = target.getZ() - this.getZ();
            float targetYaw = (float) (MathHelper.atan2(dZ, dX) * (180D / Math.PI)) - 90.0F;

            this.setYaw(targetYaw);
            this.bodyYaw = targetYaw;
            this.headYaw = targetYaw;
        }

        boolean busy = startledTicks > 0 || pendingAttack != null || hasActiveVine();

        if (!isFinalForm() && !busy) {
            boolean tooClose = target != null && this.squaredDistanceTo(target) < TOO_CLOSE * TOO_CLOSE;
            BlockState blockBelow = this.getWorld().getBlockState(this.getBlockPos().down());
            if ((tooClose || stateTicks >= surfaceTicks) && canBurrowOn(blockBelow)) {
                startBurying();
                return;
            }
        }

        if (startledTicks > 0) {
            tickStartled();
            return;
        }

        if (!(this.getWorld() instanceof ServerWorld world)) return;

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

        if (isFinalForm() && target != null && this.squaredDistanceTo(target) <= 24.0 * 24.0) {
            tickSky(world, target);
        }
    }

    private void tickStartled() {
        startledTicks--;
        if (this.getWorld() instanceof ServerWorld world && this.age % 5 == 0) {

            world.spawnParticles(ParticleTypes.CRIT, this.getX(), this.getY() + this.getHeight() + 0.2, this.getZ(),
                    3, 0.3, 0.1, 0.3, 0.1);
        }
        if (startledTicks <= 0) {
            startledTicks = 0;
            this.dataTracker.set(STARTLED, false);
        }
    }

    private void tickPendingAttack(ServerWorld world) {
        AttackType type = pendingAttack;
        PlayerEntity target = pendingTarget == null ? null : world.getPlayerByUuid(pendingTarget);
        if (type == null || target == null || !target.isAlive() || target.isSpectator()) {
            pendingAttack = null;
            return;
        }

        if (pendingDelay > 0) {
            pendingDelay--;

            if (!castAnimPlayed && pendingDelay <= CAST_LEAD_TICKS) {
                castAnimPlayed = true;
                this.triggerAnim("attack_controller", type == AttackType.CHOMP ? "cast_chomp" : "cast_vine");
                sfx(SoundEvents.ENTITY_EVOKER_PREPARE_ATTACK, 1.0f, type == AttackType.CHOMP ? 1.0f : 0.8f);
            }
            if (this.age % 3 == 0) {
                world.spawnParticles(ParticleTypes.NOTE, this.getX(), this.getY() + this.getHeight() * 0.9, this.getZ(),
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

    private void castChomp(ServerWorld world, PlayerEntity target) {
        sfx(SoundEvents.ENTITY_EVOKER_CAST_SPELL, 1.0f, isFinalForm() ? 0.7f : 1.2f);

        int lines = isFinalForm() ? 5 : 1;
        Vec3d right = rightOf(target);
        for (int k = 0; k < lines; k++) {
            double lateral = (k - (lines - 1) / 2.0) * 2.0;
            int order = pendingSweep > 0 ? k : (lines - 1 - k);
            castFangLine(world,
                    target.getX() + right.x * lateral,
                    target.getZ() + right.z * lateral,
                    order * FAN_LINE_STEP);
        }
    }

    private void castFangLine(ServerWorld world, double tx, double tz, int extraDelay) {
        double dx = tx - this.getX();
        double dz = tz - this.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return;
        dx /= len;
        dz /= len;

        float yaw = (float) MathHelper.atan2(dz, dx);
        double start = this.getWidth() / 2.0 + 1.0;
        int count = MathHelper.clamp((int) (len / 1.25) + 3, 6, 16);

        for (int i = 0; i < count; i++) {
            double x = this.getX() + dx * (start + i * 1.25);
            double z = this.getZ() + dz * (start + i * 1.25);

            BlockPos ground = findStandable(BlockPos.ofFloored(x, this.getY(), z));
            if (ground == null) continue;

            world.spawnEntity(new EvokerFangsEntity(world, x, ground.getY(), z, yaw, extraDelay + i * 2, this));
        }
    }

    private void castVine(ServerWorld world, PlayerEntity target) {
        if (!(target instanceof ServerPlayerEntity victim)) return;
        if (hasActiveVine() || !canVineTarget(victim)) return;

        VineAnchorEntity vine = ModEntities.VINE_ANCHOR.create(world);
        if (vine == null) return;

        vine.bind(this, victim);
        world.spawnEntity(vine);
        activeVine = vine;
    }

    @Override
    public boolean isTeammate(Entity other) {
        return other instanceof SingingFlowerEntity || super.isTeammate(other);
    }

    private static class PendingStrike {
        final Vec3d ground;
        int ticksLeft;

        PendingStrike(Vec3d ground, int ticksLeft) {
            this.ground = ground;
            this.ticksLeft = ticksLeft;
        }
    }

    private void tickSky(ServerWorld world, PlayerEntity target) {
        if (skyCooldown > 0) {
            skyCooldown--;
            return;
        }

        this.triggerAnim("attack_controller", "cast_sky");
        sfx(SoundEvents.ENTITY_EVOKER_PREPARE_SUMMON, 1.5f, 0.7f);

        queueStrike(target.getX(), target.getY(), target.getZ());
        for (int i = 0; i < 5; i++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dist = 2.0 + this.random.nextDouble() * 6.0;
            queueStrike(target.getX() + Math.cos(angle) * dist, target.getY(), target.getZ() + Math.sin(angle) * dist);
        }
        skyCooldown = 120 + this.random.nextInt(40);
    }

    private void queueStrike(double x, double y, double z) {
        BlockPos ground = findStandable(BlockPos.ofFloored(x, y, z));
        if (ground == null) return;
        strikes.add(new PendingStrike(new Vec3d(x, ground.getY(), z), 30 + this.random.nextInt(20)));
    }

    private void processStrikes(ServerWorld world) {
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

    private void warningRing(ServerWorld world, Vec3d ground) {
        int points = 14;
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            world.spawnParticles(DustParticleEffect.DEFAULT,
                    ground.x + Math.cos(angle) * SkySeedProjectile.BLAST_RADIUS,
                    ground.y + 0.1,
                    ground.z + Math.sin(angle) * SkySeedProjectile.BLAST_RADIUS,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
    }

    private void dropSeed(ServerWorld world, Vec3d ground) {
        BlockPos g = BlockPos.ofFloored(ground);
        int height = 2;
        while (height < 16) {
            BlockPos p = g.up(height);
            if (!world.getBlockState(p).getCollisionShape(world, p).isEmpty()) break;
            height++;
        }

        SkySeedProjectile seed = new SkySeedProjectile(world, this);
        seed.setPosition(ground.x, g.getY() + height - 0.5, ground.z);
        seed.setVelocity(0.0, -1.0, 0.0);
        world.spawnEntity(seed);

        world.playSound(null, ground.x, ground.y + 3.0, ground.z,
                SoundEvents.ENTITY_SPLASH_POTION_THROW, SoundCategory.HOSTILE, 0.7f, 0.6f);
    }

    private void startBurying() {
        setBurrowState(BurrowState.BURYING);
        sfx(SoundEvents.BLOCK_ROOTED_DIRT_PLACE, 1.2f, 0.9f);
        dirtBurst();
    }

    private void enterBuried() {
        setBurrowState(BurrowState.BURIED);
        this.buriedTicks = 30 + this.random.nextInt(20);
        dirtBurst();
    }

    private void tickBuried(@Nullable PlayerEntity target) {
        if (stateTicks < buriedTicks) return;

        Vec3d dest = pickDestination(target);
        if (dest != null) {
            this.requestTeleport(dest.x, dest.y, dest.z);
        }
        setBurrowState(BurrowState.EMERGING);
        sfx(SoundEvents.BLOCK_ROOTED_DIRT_BREAK, 1.2f, 0.8f);
        sfx(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 0.8f, PENTATONIC[getVariant() % PENTATONIC.length]);
        dirtBurst();
    }

    private boolean canBurrowOn(BlockState state) {
        return state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT) || state.isOf(Blocks.MUD) || state.isOf(ModBlocks.WONDER_GRASS) || state.isOf(ModBlocks.WONDER_DIRT_PATH) || state.isOf(ModBlocks.WONDER_DIRT);
    }

    private void finishEmerging() {
        setBurrowState(BurrowState.SURFACED);
        this.surfaceTicks = 100 + this.random.nextInt(60);

        if (goingHome && !isFinalForm()) {
            growUp();
        }
    }

    public void beginFinalForm() {
        startledTicks = 0; this.dataTracker.set(STARTLED, false);
        this.goingHome = true;
        if (getBurrowState() == BurrowState.SURFACED) {
            pendingAttack = null;
            startBurying();
        }
    }

    public void startEmerging() {
        setBurrowState(BurrowState.EMERGING);
        sfx(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 0.8f, PENTATONIC[getVariant() % PENTATONIC.length]);
    }

    private void growUp() {
        this.dataTracker.set(FINAL_FORM, true);
        sfx(SoundEvents.ENTITY_EVOKER_CELEBRATE, 2.0f, 0.5f);
        ScaleData scale = ScaleTypes.BASE.getScaleData(this);
        scale.setTargetScale(FINAL_SCALE);
        scale.setScaleTickDelay(60);
    }

    @Nullable
    private Vec3d homeDestination() {
        return homePos == null ? null : Vec3d.ofBottomCenter(homePos.up());
    }

    @Nullable
    private Vec3d pickDestination(@Nullable PlayerEntity target) {
        if (goingHome) return homeDestination();
        if (target == null) return null;

        double yaw = Math.toRadians(target.getYaw());
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

            BlockPos ground = findStandable(BlockPos.ofFloored(x, target.getY(), z));
            if (ground == null) continue;

            BlockState groundState = this.getWorld().getBlockState(ground.down());
            if (!canBurrowOn(groundState)) continue;

            if (homePos != null && ground.getSquaredDistance(homePos) > ARENA_RADIUS * ARENA_RADIUS) continue;

            Vec3d spot = Vec3d.ofBottomCenter(ground);

            if (this.getWorld().getClosestPlayer(spot.x, spot.y, spot.z, TOO_CLOSE + 1.5, false) != null) continue;
            return spot;
        }
        return null;
    }

    @Nullable
    private BlockPos findStandable(BlockPos start) {
        World w = this.getWorld();
        for (int dy = 3; dy >= -4; dy--) {
            BlockPos p = start.up(dy);
            boolean floor = w.getBlockState(p.down()).isSideSolidFullSquare(w, p.down(), Direction.UP);
            boolean room = w.getBlockState(p).getCollisionShape(w, p).isEmpty()
                    && w.getBlockState(p.up()).getCollisionShape(w, p.up()).isEmpty();
            if (floor && room) return p;
        }
        return null;
    }

    private void dirtBurst() {
        if (this.getWorld() instanceof ServerWorld serverWorld) {
            BlockState below = serverWorld.getBlockState(this.getBlockPos().down());
            if (below.isAir()) return;
            serverWorld.spawnParticles(new BlockStateParticleEffect(ParticleTypes.BLOCK, below),
                    this.getX(), this.getY() + 0.1, this.getZ(), 20, 0.5, 0.1, 0.5, 0.1);
        }
    }

    private void sfx(SoundEvent sound, float volume, float pitch) {
        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), sound, SoundCategory.HOSTILE, volume, pitch);
    }

    private void sing() {
        int idx = (getVariant() + this.random.nextInt(2) * 2) % PENTATONIC.length;
        float pitch = PENTATONIC[idx] * (isFinalForm() ? 0.5f : 1.0f);
        sfx(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 0.9f, pitch);
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.BLOCK_AZALEA_LEAVES_HIT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.BLOCK_AZALEA_LEAVES_BREAK;
    }

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        if (getBurrowState() != BurrowState.SURFACED && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    @Override
    public boolean canHit() {
        return getBurrowState() == BurrowState.SURFACED && super.canHit();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (!this.getWorld().isClient() && startledTicks > 0) {
            amount *= STARTLED_DAMAGE_MULTIPLIER;
        }
        boolean hurt = super.damage(source, amount);
        if (hurt && !this.getWorld().isClient() && source.getAttacker() instanceof ServerPlayerEntity player) {
            SingingFlowersSpawnerBlockEntity spawner = getSpawner();
            if (spawner != null) spawner.addParticipant(player);
        }
        return hurt;
    }

    @Override
    public void onDeath(DamageSource source) {
        super.onDeath(source);
        if (!this.getWorld().isClient()) {
            SingingFlowersSpawnerBlockEntity spawner = getSpawner();
            if (spawner != null) spawner.onFlowerKilled(this, source);
        }
    }

    @Override
    public void takeKnockback(double strength, double x, double z) {
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void pushAway(Entity entity) { }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }


    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
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
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        setVariant(nbt.getInt("Variant"));
        setPhase(Math.max(1, nbt.getInt("FlowerPhase")));
        this.dataTracker.set(FINAL_FORM, nbt.getBoolean("FinalForm"));
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