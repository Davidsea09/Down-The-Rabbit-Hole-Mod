package net.emanueljdf09.dtrhmod.entity.ai.goals.mad_hatter;

import net.emanueljdf09.dtrhmod.fabric.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.fabric.entity.projectile.TeapotProjectile;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MadHatterBossBehaviorGoal extends Goal {

    private enum Step { MOVE, CHARGE_WINDUP, CHARGE_RUN, TEAPOT_WINDUP }

    private static final int PATH_INTERVAL = 8;

    private static final int RETARGET_MIN_TICKS = 60;
    private static final int RETARGET_EXTRA_TICKS = 40;
    private static final int RECENT_ATTACKER_TICKS = 100;
    private static final double MAX_TARGET_DISTANCE_SQ = 48.0D * 48.0D;

    private static final double MELEE_RANGE_SQ = 9.0D;
    private static final double CHARGE_MIN_DIST_SQ = 36.0D;
    private static final double CHARGE_MAX_DIST_SQ = 256.0D;
    private static final double CHARGE_OVERSHOOT = 4.0D;
    private static final double CHARGE_MAX_LENGTH = 18.0D;
    private static final double CHARGE_HIT_GROW = 0.9D;
    private static final int CHARGE_MAX_RUN_TICKS = 45;

    private static final double TEAPOT_MIN_DIST_SQ = 25.0D;
    private static final int TEAPOT_RELEASE_TICKS = 10;
    private static final int POISON_TICKS = 400;
    private static final int PHASE2_POISON_AMP = 0;
    private static final int PHASE3_POISON_AMP = 1;

    private final MadHatterEntity hatter;

    private Step step = Step.MOVE;
    private int stepTicks = 0;
    private int moveTicks = 0;

    private int meleeCooldown = 0;
    private int teapotCooldown = 60;
    private int retargetCooldown = 0;
    private int pathCooldown = 0;
    private int strafeDirection = 1;
    private int strafeTimer = 0;

    private Vec3 chargeEnd = Vec3.ZERO;
    private final Set<UUID> chargeHit = new HashSet<>();
    private final List<Vec3> aimPoints = new ArrayList<>();

    public MadHatterBossBehaviorGoal(MadHatterEntity hatter) {
        this.hatter = hatter;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    public void resetTaskState() {
        this.step = Step.MOVE;
        this.stepTicks = 0;
        this.moveTicks = 0;
        this.meleeCooldown = 0;
        this.teapotCooldown = 60;
        this.retargetCooldown = 0;
        this.pathCooldown = 0;
        this.chargeHit.clear();
        this.aimPoints.clear();
    }

    @Override
    public boolean canUse() {
        return hatter.isBossMode();
    }

    @Override
    public void start() {
        resetTaskState();
    }

    @Override
    public void stop() {
        this.hatter.getNavigation().stop();
        this.hatter.setCharging(false);
        this.hatter.setRunning(false);
        resetTaskState();
    }

    @Override
    public void tick() {
        if (hatter.isPerformingHatRitual()) {
            abortAttack();
            hatter.getNavigation().stop();
            return;
        }

        if (meleeCooldown > 0) meleeCooldown--;
        if (teapotCooldown > 0) teapotCooldown--;
        if (pathCooldown > 0) pathCooldown--;
        if (retargetCooldown > 0) retargetCooldown--;

        switch (step) {
            case CHARGE_WINDUP -> {
                tickChargeWindup();
                return;
            }
            case CHARGE_RUN -> {
                tickChargeRun();
                return;
            }
            case TEAPOT_WINDUP -> {
                tickTeapotWindup();
                return;
            }
            default -> { }
        }

        LivingEntity target = hatter.getTarget();
        if (!validTarget(target) || retargetCooldown <= 0) {
            target = pickTarget();
            hatter.setTarget(target);
            retargetCooldown = RETARGET_MIN_TICKS + hatter.getRandom().nextInt(RETARGET_EXTRA_TICKS);
        }
        if (target == null) {
            hatter.getNavigation().stop();
            return;
        }

        hatter.getLookControl().lookAt(target, 45.0F, 45.0F);
        tickMove(target);
    }

    private boolean validTarget(@Nullable LivingEntity target) {
        if (target == null || !target.isAlive()) return false;
        if (target instanceof Player player && player.isSpectator()) return false;
        return hatter.squaredDistanceTo(target) < MAX_TARGET_DISTANCE_SQ;
    }

    private List<ServerPlayer> candidates() {
        List<ServerPlayer> list = new ArrayList<>();
        for (ServerPlayer p : hatter.getFightPlayers()) {
            if (p.isAlive() && !p.isSpectator() && hatter.squaredDistanceTo(p) < MAX_TARGET_DISTANCE_SQ) list.add(p);
        }
        return list;
    }

    @Nullable
    private ServerPlayer pickTarget() {
        List<ServerPlayer> players = candidates();
        if (players.isEmpty()) return null;

        ServerPlayer recent = hatter.getRecentAttacker(RECENT_ATTACKER_TICKS);
        if (recent != null && players.contains(recent) && hatter.getRandom().nextFloat() < 0.7f) {
            return recent;
        }

        if (hatter.getRandom().nextBoolean()) {
            return players.stream().min(Comparator.comparingDouble(hatter::squaredDistanceTo)).orElse(null);
        }
        return players.get(hatter.getRandom().nextInt(players.size()));
    }

    @Nullable
    private ServerPlayer pickOtherPlayer(LivingEntity not) {
        List<ServerPlayer> others = new ArrayList<>();
        for (ServerPlayer p : candidates()) {
            if (p != not && hatter.squaredDistanceTo(p) >= TEAPOT_MIN_DIST_SQ) others.add(p);
        }
        return others.isEmpty() ? null : others.get(hatter.getRandom().nextInt(others.size()));
    }


    private void tickMove(LivingEntity target) {
        int phase = hatter.getBossPhase();
        double distSq = hatter.squaredDistanceTo(target);
        moveTicks++;

        strafeTimer++;
        if (strafeTimer > 80 + hatter.getRandom().nextInt(80)) {
            strafeDirection *= -1;
            strafeTimer = 0;
        }

        if (distSq <= MELEE_RANGE_SQ && meleeCooldown <= 0) {
            hatter.getNavigation().stop();
            if (hatter.tryAttack(target)) {
                target.knockback(1.0F, hatter.getX() - target.getX(), hatter.getZ() - target.getZ());
                meleeCooldown = 20;
            }
            return;
        }

        circle(target, distSq, phase == 1 ? 64.0D : 36.0D);

        if (moveTicks < minMoveTicks(phase)) return;
        boolean canSee = hatter.getVisibilityCache().canSee(target);
        if (!canSee) return;

        boolean canCharge = hatter.getChargeCooldownTicks() <= 0
                && distSq >= CHARGE_MIN_DIST_SQ && distSq <= CHARGE_MAX_DIST_SQ;
        boolean canTeapot = phase >= 2 && teapotCooldown <= 0 && distSq >= TEAPOT_MIN_DIST_SQ;

        if (canCharge && canTeapot) {
            if (hatter.getRandom().nextBoolean()) beginCharge(target); else beginTeapot(target);
        } else if (canCharge) {
            beginCharge(target);
        } else if (canTeapot) {
            beginTeapot(target);
        }
    }

    private static int minMoveTicks(int phase) {
        return switch (phase) {
            case 1 -> 50;
            case 2 -> 35;
            default -> 25;
        };
    }

    private void circle(LivingEntity target, double distSq, double idealSq) {
        if (distSq > idealSq + 16.0D) {
            pathTo(target, 1.2D);
        } else if (distSq < idealSq - 16.0D) {
            Vec3 flee = hatter.getPos().subtract(target.position()).normalize().multiply(5.0D);
            pathTo(hatter.getX() + flee.x, hatter.getY(), hatter.getZ() + flee.z, 1.1D);
        } else {
            Vec3 toTarget = target.position().subtract(hatter.getPos());
            Vec3 strafe = new Vec3(-toTarget.z, 0, toTarget.x).normalize().scale(5.0D * strafeDirection);
            Vec3 pull = toTarget.normalize().scale((Math.sqrt(distSq) - Math.sqrt(idealSq)) * 0.3D);
            Vec3 goal = hatter.getPos().add(strafe.add(pull));
            pathTo(goal.x, goal.y, goal.z, 1.1D);
        }
    }

    private void pathTo(double x, double y, double z, double speed) {
        if (pathCooldown > 0 && !hatter.getNavigation().isIdle()) return;
        hatter.getNavigation().startMovingTo(x, y, z, speed);
        pathCooldown = PATH_INTERVAL;
    }

    private void pathTo(Entity entity, double speed) {
        if (pathCooldown > 0 && !hatter.getNavigation().isIdle()) return;
        hatter.getNavigation().startMovingTo(entity, speed);
        pathCooldown = PATH_INTERVAL;
    }

    private int chargeWindupTicks(int phase) {
        return switch (phase) {
            case 1 -> 22;
            case 2 -> 18;
            default -> 14;
        };
    }

    private void beginCharge(LivingEntity target) {
        Vec3 from = hatter.getPos();
        Vec3 dir = new Vec3(target.getX() - from.x, 0.0, target.getZ() - from.z);
        double length = Math.min(CHARGE_MAX_LENGTH, dir.length() + CHARGE_OVERSHOOT);
        dir = dir.lengthSqr() < 1.0E-4 ? Vec3.directionFromRotation(0f, hatter.getYaw()) : dir.normalize();

        chargeEnd = from.add(dir.scale(length));
        chargeHit.clear();
        step = Step.CHARGE_WINDUP;
        stepTicks = 0;

        hatter.getNavigation().stop();
        hatter.setCharging(true);
        if (hatter.getBossPhase() >= 3) hatter.rallyHorde();
    }

    private void tickChargeWindup() {
        hatter.getNavigation().stop();
        hatter.getLookControl().lookAt(chargeEnd.x, chargeEnd.y + 1.0, chargeEnd.z);

        if (++stepTicks >= chargeWindupTicks(hatter.getBossPhase())) {
            hatter.setCharging(false);
            hatter.setRunning(true);
            step = Step.CHARGE_RUN;
            stepTicks = 0;

            double speed = 2.0D + 0.15D * (hatter.getBossPhase() - 1);
            boolean pathValid = hatter.getNavigation().startMovingTo(chargeEnd.x, chargeEnd.y, chargeEnd.z, speed);
            if (!pathValid) endCharge();
        }
    }

    private void tickChargeRun() {
        stepTicks++;

        for (ServerPlayer p : hatter.getWorld().getEntitiesByClass(ServerPlayer.class,
                hatter.getBoundingBox().expand(CHARGE_HIT_GROW), pl -> pl.isAlive() && !pl.isSpectator())) {
            if (!chargeHit.add(p.getUUID())) continue;
            if (hatter.tryAttack(p)) {
                p.knockback(1.2F, hatter.getX() - p.getX(), hatter.getZ() - p.getZ());
            }
        }

        if (hatter.getNavigation().isIdle() || stepTicks > CHARGE_MAX_RUN_TICKS
                || hatter.squaredDistanceTo(chargeEnd) <= 2.25D) {
            endCharge();
        }
    }

    private void endCharge() {
        hatter.setCharging(false);
        hatter.setRunning(false);
        hatter.getNavigation().stop();
        hatter.setChargeCooldownTicks(switch (hatter.getBossPhase()) {
            case 1 -> 100;
            case 2 -> 90;
            default -> 80;
        });
        meleeCooldown = 15;
        chargeHit.clear();
        step = Step.MOVE;
        stepTicks = 0;
        moveTicks = 0;
    }

    private int teapotLeadTicks(int phase) {
        return phase >= 3 ? 8 : 12;
    }

    private void beginTeapot(LivingEntity target) {
        aimPoints.clear();
        aimPoints.add(target.position());

        if (hatter.getBossPhase() >= 3) {
            ServerPlayer other = pickOtherPlayer(target);
            if (other != null) {
                aimPoints.add(other.position());
            } else {
                double angle = hatter.getRandom().nextDouble() * Math.PI * 2.0;
                aimPoints.add(target.position().add(Math.cos(angle) * 2.5, 0.0, Math.sin(angle) * 2.5));
            }
        }

        step = Step.TEAPOT_WINDUP;
        stepTicks = 0;
        hatter.getNavigation().stop();
    }

    private void tickTeapotWindup() {
        hatter.getNavigation().stop();
        if (aimPoints.isEmpty()) {
            endTeapot();
            return;
        }

        int phase = hatter.getBossPhase();
        int lead = teapotLeadTicks(phase);
        Vec3 first = aimPoints.get(0);
        hatter.getLookControl().lookAt(first.x, first.y + 1.0, first.z);

        if (stepTicks == lead) {
            hatter.triggerAnim("attack_controller", "attack_ranged");
        }

        if (++stepTicks >= lead + TEAPOT_RELEASE_TICKS) {
            int amp = phase >= 3 ? PHASE3_POISON_AMP : PHASE2_POISON_AMP;
            float velocity = phase >= 3 ? 1.4F : 1.3F;
            for (Vec3 aim : aimPoints) {
                throwTeapot(aim, amp, POISON_TICKS, velocity);
            }
            teapotCooldown = phase >= 3 ? 110 : 160;
            endTeapot();
        }
    }

    private void endTeapot() {
        aimPoints.clear();
        meleeCooldown = Math.max(meleeCooldown, 10);
        step = Step.MOVE;
        stepTicks = 0;
        moveTicks = 0;
    }

    private void throwTeapot(Vec3 aim, int poisonAmplifier, int poisonTicks, float velocity) {
        TeapotProjectile teapot = new TeapotProjectile(
                hatter.getWorld(), hatter, TeapotProjectile.createPoisonedTeaStack(poisonAmplifier, poisonTicks));

        double d = aim.x - hatter.getX();
        double e = (aim.y + 0.9D) - teapot.getY();
        double f = aim.z - hatter.getZ();
        double flat = Math.sqrt(d * d + f * f);

        double flightTicks = flat / velocity;
        double lift = 0.5D * 0.03D * flightTicks * flightTicks;

        teapot.setVelocity(d, e + lift, f, velocity, 1.5F);
        hatter.getWorld().spawnEntity(teapot);
    }

    private void abortAttack() {
        hatter.setCharging(false);
        hatter.setRunning(false);
        step = Step.MOVE;
        stepTicks = 0;
        chargeHit.clear();
        aimPoints.clear();
    }
}