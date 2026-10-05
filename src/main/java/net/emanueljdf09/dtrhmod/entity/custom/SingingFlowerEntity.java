package net.emanueljdf09.dtrhmod.entity.custom;

import net.emanueljdf09.dtrhmod.block.entity.spawn.SingingFlowersSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.minecraft.block.BlockState;
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

    // ---- Animation names: make these match your Blockbench animation names ----
    private static final RawAnimation IDLE = RawAnimation.begin().thenLoop("idle");
    private static final RawAnimation BURY = RawAnimation.begin().thenPlayAndHold("bury");
    private static final RawAnimation BURIED_ANIM = RawAnimation.begin().thenLoop("buried");
    private static final RawAnimation EMERGE = RawAnimation.begin().thenPlayAndHold("emerge");
    private static final RawAnimation CHOMP_CAST = RawAnimation.begin().thenPlay("cast_chomp");
    private static final RawAnimation VINE_CAST = RawAnimation.begin().thenPlay("cast_vine");
    private static final RawAnimation SKY_CAST = RawAnimation.begin().thenPlay("cast_sky");

    // ---- Tuning ----
    public static final float MAX_HEALTH = 40.0f;
    public static final int BURY_TICKS = 20;     // length of the "bury" animation
    public static final int EMERGE_TICKS = 20;   // length of the "emerge" animation
    private static final double TOO_CLOSE = 4.0;           // player closer than this -> flower buries
    private static final double MIN_EMERGE_DIST = 6.0;     // emerge at least this far from the player
    private static final double MAX_EMERGE_DIST = 14.0;    // ...and at most this far (the 14 block radius)
    private static final double ARENA_RADIUS = 24.0;       // never emerge further than this from the spawner
    private static final float FINAL_SCALE = 3.0f;

    private static final TrackedData<Integer> VARIANT = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> PHASE = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> BURROW_STATE = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> FINAL_FORM = DataTracker.registerData(SingingFlowerEntity.class, TrackedDataHandlerRegistry.BOOLEAN);

    public enum BurrowState {
        SURFACED, BURYING, BURIED, EMERGING;

        public static BurrowState byId(int id) {
            BurrowState[] values = values();
            return values[Math.floorMod(id, values.length)];
        }
    }

    @Nullable
    private BlockPos homePos = null;
    private boolean goingHome = false;

    private int stateTicks = 0;
    private int surfaceTicks = 120;
    private int buriedTicks = 30;
    private int attackTicks = 0;
    private int fangCooldown = 40;
    private int vineCooldown = 80;
    @Nullable
    private VineAnchorEntity activeVine = null;
    private int skyCooldown = 100;
    private final List<PendingStrike> strikes = new ArrayList<>();
    @Nullable
    private Vec3d destination = null;

    public SingingFlowerEntity(EntityType<? extends PathAwareEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent(); // bosses must never despawn
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(VARIANT, 0);
        this.dataTracker.startTracking(PHASE, 1);
        this.dataTracker.startTracking(BURROW_STATE, BurrowState.SURFACED.ordinal());
        this.dataTracker.startTracking(FINAL_FORM, false);
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

    // ------------------------------------------------------------------
    // Accessors
    // ------------------------------------------------------------------

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

    public void setHome(BlockPos pos) { this.homePos = pos; }
    @Nullable
    public BlockPos getHome() { return this.homePos; }

    // ------------------------------------------------------------------
    // Spawner link
    // ------------------------------------------------------------------

    @Nullable
    private SingingFlowersSpawnerBlockEntity getSpawner() {
        if (homePos == null) return null;
        return this.getWorld().getBlockEntity(homePos) instanceof SingingFlowersSpawnerBlockEntity be ? be : null;
    }

    /** True if the spawner no longer knows this flower (fight reset, block broken...). */
    private boolean isOrphaned() {
        if (homePos == null) return false; // summoned by command for testing
        SingingFlowersSpawnerBlockEntity spawner = getSpawner();
        return spawner == null || !spawner.isTracking(this.getUuid());
    }

    // ------------------------------------------------------------------
    // Tick / burrow state machine
    // ------------------------------------------------------------------

    @Override
    public void tick() {
        super.tick();
        if (this.getWorld().isClient()) return;

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
                if (stateTicks >= BURY_TICKS) enterBuried(target);
            }
            case BURIED -> tickBuried();
            case EMERGING -> {
                if (stateTicks >= EMERGE_TICKS) finishEmerging();
            }
        }
    }

    private void tickSurfaced(@Nullable PlayerEntity target) {
        if (target != null) {
            this.lookAtEntity(target, 30.0F, 30.0F);
        }

        // Final form: stays on the surface in the center and fights.
        // A flower that is holding a player with a vine can't bury either.
        if (!isFinalForm() && !hasActiveVine()) {
            boolean tooClose = target != null && this.squaredDistanceTo(target) < TOO_CLOSE * TOO_CLOSE;
            if (tooClose || stateTicks >= surfaceTicks) {
                startBurying();
                return;
            }
        }

        attackTicks++;
        performAttacks(target);
    }

    /** ATTACK HOOK: the fang line, vine and sky attacks get called from here. */
    protected void performAttacks(@Nullable PlayerEntity target) {
        if (target == null || !(this.getWorld() instanceof ServerWorld world)) return;
        if (this.squaredDistanceTo(target) > 24.0 * 24.0) return;

        // attack 1: chomper domino line (all phases)
        if (fangCooldown > 0) {
            fangCooldown--;
        } else {
            castFangLine(world, target);
            int base = isFinalForm() ? 40 : (getPhase() >= 2 ? 50 : 70);
            fangCooldown = base + this.random.nextInt(20);
        }

        // attack 2: vine lead (phase 2 and up, includes the final form)
        if (getPhase() >= 2) {
            tickVine(world, target);
        }

        // attack 3: seed bombs from the sky (final form only)
        if (isFinalForm()) {
            tickSky(world, target);
        }
    }

    // ------------------------------------------------------------------
    // Attack 3: seed bombs from the sky (final form)
    // ------------------------------------------------------------------

    /** A spot on the ground that is marked with a warning ring and will be hit when the timer runs out. */
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

        // one right on the player (they have to move), the rest scattered around them
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
        if (ground == null) return; // no floor there (void, wall...)
        // 1.5 to 2.5 seconds of warning
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

    /** A red ring on the ground, exactly as wide as the blast. */
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

    /** Drops a seed from up to 16 blocks above the spot, or from just under the ceiling if there is one. */
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
    }

    private boolean hasActiveVine() {
        return activeVine != null && !activeVine.isRemoved();
    }

    /** Each flower owns at most one vine at a time, so 3 flowers left means up to 3 vines. */
    private void tickVine(ServerWorld world, PlayerEntity target) {
        if (activeVine != null && activeVine.isRemoved()) {
            activeVine = null;
            vineCooldown = 100 + this.random.nextInt(40); // pause after a vine breaks or ends
        }
        if (activeVine != null) return;

        if (vineCooldown > 0) {
            vineCooldown--;
            return;
        }

        if (!(target instanceof ServerPlayerEntity victim)) return;

        // Only cast from a distance: the leash radius (2.5) plus melee reach (~3) must stay
        // short of the flower, so a held player can't just hit the flower and ignore the vine.
        double distSq = this.squaredDistanceTo(victim);
        if (distSq < 7.0 * 7.0 || distSq > 18.0 * 18.0) return;

        VineAnchorEntity vine = ModEntities.VINE_ANCHOR.create(world);
        if (vine == null) return;

        vine.bind(this, victim);
        world.spawnEntity(vine);
        activeVine = vine;
        this.triggerAnim("attack_controller", "cast_vine");
    }

    /** A line of evoker fangs toward the player, each one delayed a bit more: the domino effect. */
    private void castFangLine(ServerWorld world, PlayerEntity target) {
        double dx = target.getX() - this.getX();
        double dz = target.getZ() - this.getZ();
        double len = Math.sqrt(dx * dx + dz * dz);
        if (len < 0.001) return;
        dx /= len;
        dz /= len;

        float yaw = (float) MathHelper.atan2(dz, dx); // fangs take radians
        double start = this.getWidth() / 2.0 + 1.0;   // start past the flower's body (bigger in final form)
        int count = MathHelper.clamp((int) (len / 1.25) + 3, 6, 16); // reach a bit past the player

        this.triggerAnim("attack_controller", "cast_chomp");

        for (int i = 0; i < count; i++) {
            double x = this.getX() + dx * (start + i * 1.25);
            double z = this.getZ() + dz * (start + i * 1.25);

            BlockPos ground = findStandable(BlockPos.ofFloored(x, this.getY(), z));
            if (ground == null) continue; // skip gaps in the floor, keep the line going

            world.spawnEntity(new EvokerFangsEntity(world, x, ground.getY(), z, yaw, i * 2, this));
        }
    }

    /** Fangs skip "teammates" of their owner, so this stops flowers from chomping each other. */
    @Override
    public boolean isTeammate(net.minecraft.entity.Entity other) {
        return other instanceof SingingFlowerEntity || super.isTeammate(other);
    }

    private void startBurying() {
        setBurrowState(BurrowState.BURYING);
        dirtBurst();
    }

    private void enterBuried(@Nullable PlayerEntity target) {
        setBurrowState(BurrowState.BURIED);
        this.buriedTicks = 30 + this.random.nextInt(20);
        this.destination = pickDestination(target);
        dirtBurst();
    }

    private void tickBuried() {
        if (stateTicks < buriedTicks) return;

        Vec3d dest = goingHome ? homeDestination() : destination;
        if (dest != null) {
            this.requestTeleport(dest.x, dest.y, dest.z);
        }
        destination = null;
        setBurrowState(BurrowState.EMERGING);
        dirtBurst();
    }

    private void finishEmerging() {
        setBurrowState(BurrowState.SURFACED);
        this.surfaceTicks = 100 + this.random.nextInt(60);
        this.attackTicks = 0;

        if (goingHome && !isFinalForm()) {
            growUp();
        }
    }

    /** Called by the spawner when only this flower is left. */
    public void beginFinalForm() {
        this.goingHome = true;
        if (getBurrowState() == BurrowState.SURFACED) {
            startBurying();
        }
    }

    /** Called by the spawner right after spawning so flowers rise out of the ground. */
    public void startEmerging() {
        setBurrowState(BurrowState.EMERGING);
    }

    private void growUp() {
        this.dataTracker.set(FINAL_FORM, true);
        ScaleData scale = ScaleTypes.BASE.getScaleData(this);
        scale.setTargetScale(FINAL_SCALE);
        scale.setScaleTickDelay(60);
    }

    // ------------------------------------------------------------------
    // Picking where to emerge
    // ------------------------------------------------------------------

    @Nullable
    private Vec3d homeDestination() {
        return homePos == null ? null : Vec3d.ofBottomCenter(homePos.up());
    }

    @Nullable
    private Vec3d pickDestination(@Nullable PlayerEntity target) {
        if (goingHome) return homeDestination();
        if (target == null) return null;

        for (int attempt = 0; attempt < 16; attempt++) {
            double angle = this.random.nextDouble() * Math.PI * 2.0;
            double dist = MIN_EMERGE_DIST + this.random.nextDouble() * (MAX_EMERGE_DIST - MIN_EMERGE_DIST);
            double x = target.getX() + Math.cos(angle) * dist;
            double z = target.getZ() + Math.sin(angle) * dist;

            BlockPos ground = findStandable(BlockPos.ofFloored(x, target.getY(), z));
            if (ground == null) continue;
            if (homePos != null && ground.getSquaredDistance(homePos) > ARENA_RADIUS * ARENA_RADIUS) continue;

            Vec3d spot = Vec3d.ofBottomCenter(ground);
            // don't emerge next to any player
            if (this.getWorld().getClosestPlayer(spot.x, spot.y, spot.z, TOO_CLOSE + 1.5, false) != null) continue;
            return spot;
        }
        return null; // no valid spot: just re-emerge where it is
    }

    /** Finds a floor with 2 blocks of headroom within a few blocks vertically of start. */
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

    // ------------------------------------------------------------------
    // Damage / hit rules
    // ------------------------------------------------------------------

    @Override
    public boolean isInvulnerableTo(DamageSource source) {
        // buried / burying / emerging flowers can't be damaged (except by /kill and the void)
        if (getBurrowState() != BurrowState.SURFACED && !source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            return true;
        }
        return super.isInvulnerableTo(source);
    }

    /** Lets the player's crosshair and arrows pass through a buried flower. */
    @Override
    public boolean canHit() {
        return getBurrowState() == BurrowState.SURFACED && super.canHit();
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
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
        // flowers are rooted
    }

    @Override
    public boolean isPushable() { return false; }

    @Override
    protected void pushAway(net.minecraft.entity.Entity entity) { }

    @Override
    public boolean canImmediatelyDespawn(double distanceSquared) { return false; }

    // ------------------------------------------------------------------
    // Saving
    // ------------------------------------------------------------------

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

        // The planned destination isn't saved, so a flower that was underground
        // simply re-emerges where it is.
        BurrowState saved = BurrowState.byId(nbt.getInt("BurrowState"));
        setBurrowState(saved == BurrowState.SURFACED ? BurrowState.SURFACED : BurrowState.EMERGING);
    }

    // ------------------------------------------------------------------
    // GeckoLib
    // ------------------------------------------------------------------

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "movement_controller", 3, state ->
                state.setAndContinue(switch (getBurrowState()) {
                    case BURYING -> BURY;
                    case BURIED -> BURIED_ANIM;
                    case EMERGING -> EMERGE;
                    default -> IDLE;
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