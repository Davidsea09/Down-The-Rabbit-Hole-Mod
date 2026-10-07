package net.emanueljdf09.dtrhmod.entity.ai.goals.mad_hatter;

import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.entity.projectile.TeapotProjectile;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class MadHatterBossBehaviorGoal extends Goal {

    private static final int PATH_INTERVAL = 8;
    private static final int PHASE3_CHARGE_COOLDOWN = 100;

    private static final int PHASE2_POISON_AMP = 0;
    private static final int PHASE3_POISON_AMP = 1;
    private static final int POISON_TICKS = 400;

    private final MadHatterEntity hatter;

    private int attackCooldown = 0;
    private int subTimer = 0;
    private int pathCooldown = 0;
    private Vec3d chargeTargetPos = null;

    private int phase1State = 0;

    private int phase2State = 0;
    private int phase2AttackTimer = 0;
    private int strafeDirection = 1;
    private int strafeTimer = 0;

    private int phase3SubState = 0;
    private int phase3AttackTimer = 0;
    private int phase3StrafeDirection = 1;
    private int phase3StrafeTimer = 0;

    public MadHatterBossBehaviorGoal(MadHatterEntity hatter) {
        this.hatter = hatter;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public void resetTaskState() {
        this.attackCooldown = 0;
        this.subTimer = 0;
        this.pathCooldown = 0;
        this.phase1State = 0;
        this.phase2State = 0;
        this.phase2AttackTimer = 0;
        this.strafeTimer = 0;
        this.phase3SubState = 0;
        this.phase3AttackTimer = 0;
        this.phase3StrafeTimer = 0;
        this.chargeTargetPos = null;
    }

    @Override
    public boolean canStart() {
        LivingEntity target = hatter.getTarget();
        return hatter.isBossMode() && target != null && target.isAlive();
    }

    @Override
    public void start() {
        this.subTimer = 0;
        this.attackCooldown = 0;
        this.phase2AttackTimer = 160;
    }

    @Override
    public void stop() {
        this.hatter.getNavigation().stop();
        this.hatter.setCharging(false);
        this.hatter.setRunning(false);
        this.resetTaskState();
    }

    @Override
    public void tick() {
        if (hatter.isPerformingHatRitual()) {
            hatter.getNavigation().stop();
            hatter.setCharging(false);
            hatter.setRunning(false);
            return;
        }

        LivingEntity target = hatter.getTarget();
        if (target == null || !target.isAlive()) return;

        int phase = hatter.getBossPhase();
        hatter.getLookControl().lookAt(target, 45.0F, 45.0F);
        double distSq = hatter.squaredDistanceTo(target);

        if (attackCooldown > 0) attackCooldown--;
        if (pathCooldown > 0) pathCooldown--;

        switch (phase) {
            case 1:
                tickPhase1(target, distSq);
                break;
            case 2:
                tickPhase2(target, distSq);
                break;
            case 3:
                tickPhase3(target, distSq);
                break;
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

    private void tickPhase1(LivingEntity target, double distSq) {
        subTimer++;

        if (subTimer > 60 && hatter.getChargeCooldownTicks() <= 0 && distSq > 36.0D && distSq < 144.0D) {
            phase1State = 1;
            subTimer = 0;
        }

        switch (phase1State) {
            case 0:
                double targetRadiusSq = 64.0D;

                if (distSq > targetRadiusSq + 16.0D) {
                    pathTo(target, 1.2D);
                } else if (distSq < targetRadiusSq - 16.0D) {
                    Vec3d fleeDir = hatter.getPos().subtract(target.getPos()).normalize().multiply(5.0D);
                    pathTo(hatter.getX() + fleeDir.x, hatter.getY(), hatter.getZ() + fleeDir.z, 1.1D);
                } else {
                    Vec3d toTarget = target.getPos().subtract(hatter.getPos());
                    Vec3d strafeDir = new Vec3d(-toTarget.z, 0, toTarget.x).normalize().multiply(4.0D);
                    Vec3d circlePos = hatter.getPos().add(strafeDir);

                    pathTo(circlePos.x, circlePos.y, circlePos.z, 1.1D);
                }

                if (distSq <= 4.0D && attackCooldown <= 0) {
                    if (hatter.tryAttack(target)) {
                        attackCooldown = 20;
                    }
                }
                break;

            case 1:
                hatter.getNavigation().stop();
                hatter.setCharging(true);
                if (subTimer >= 20) {
                    phase1State = 2;
                    subTimer = 0;
                    hatter.setCharging(false);
                    hatter.setRunning(true);
                    chargeTargetPos = target.getPos();
                    hatter.getNavigation().startMovingTo(chargeTargetPos.x, chargeTargetPos.y, chargeTargetPos.z, 2.0D);
                }
                break;

            case 2:
                if (distSq <= 4.0D || subTimer > 40 || (chargeTargetPos != null && hatter.squaredDistanceTo(chargeTargetPos) <= 2.0D)) {
                    if (distSq <= 4.0D) {
                        hatter.tryAttack(target);
                    }
                    phase1State = 0;
                    subTimer = 0;
                    hatter.setRunning(false);
                    hatter.setChargeCooldownTicks(60);
                }
                break;
        }
    }

    private void tickPhase2(LivingEntity target, double distSq) {
        if (phase2AttackTimer > 0) {
            phase2AttackTimer--;
        }

        strafeTimer++;
        if (strafeTimer > 80 + hatter.getRandom().nextInt(80)) {
            strafeDirection *= -1;
            strafeTimer = 0;
        }

        switch (phase2State) {
            case 0:
                double targetRadiusSq = 36.0D;

                if (distSq > targetRadiusSq + 16.0D) {
                    pathTo(target, 1.3D);
                } else if (distSq < targetRadiusSq - 16.0D) {
                    Vec3d fleeDir = hatter.getPos().subtract(target.getPos()).normalize().multiply(5.0D);
                    pathTo(hatter.getX() + fleeDir.x, hatter.getY(), hatter.getZ() + fleeDir.z, 1.2D);
                } else {
                    Vec3d toTarget = target.getPos().subtract(hatter.getPos());

                    Vec3d forwardPull = toTarget.normalize().multiply((Math.sqrt(distSq) - 6.0D) * 0.3D);
                    Vec3d strafeDir = new Vec3d(-toTarget.z, 0, toTarget.x).normalize().multiply(5.0D * strafeDirection);
                    Vec3d circlePos = hatter.getPos().add(strafeDir.add(forwardPull));
                    pathTo(circlePos.x, circlePos.y, circlePos.z, 1.1D);
                }

                if (distSq <= 9.0D && attackCooldown <= 0) {
                    hatter.getNavigation().stop();
                    if (hatter.tryAttack(target)) {
                        target.takeKnockback(1.0F, hatter.getX() - target.getX(), hatter.getZ() - target.getZ());
                        attackCooldown = 20;
                    }
                    return;
                }

                if (phase2AttackTimer <= 0 && hatter.getVisibilityCache().canSee(target)) {
                    hatter.getNavigation().stop();
                    hatter.triggerAnim("attack_controller", "attack_ranged");
                    phase2State = 1;
                    subTimer = 0;
                    phase2AttackTimer = 160;
                }
                break;

            case 1:
                subTimer++;
                hatter.getNavigation().stop();

                if (subTimer >= 10) {
                    throwTeapot(target, PHASE2_POISON_AMP, POISON_TICKS, 1.3F);
                    phase2State = 0;
                    subTimer = 0;
                }
                break;
        }
    }

    private void tickPhase3(LivingEntity target, double distSq) {
        subTimer++;

        if (phase3AttackTimer > 0) {
            phase3AttackTimer--;
        }

        phase3StrafeTimer++;
        if (phase3StrafeTimer > 80 + hatter.getRandom().nextInt(80)) {
            phase3StrafeDirection *= -1;
            phase3StrafeTimer = 0;
        }

        switch (phase3SubState) {
            case 0:
                double targetRadiusSq = 36.0D;

                if (distSq > targetRadiusSq + 16.0D) {
                    pathTo(target, 1.3D);
                } else if (distSq < targetRadiusSq - 16.0D) {
                    Vec3d fleeDir = hatter.getPos().subtract(target.getPos()).normalize().multiply(5.0D);
                    pathTo(hatter.getX() + fleeDir.x, hatter.getY(), hatter.getZ() + fleeDir.z, 1.2D);
                } else {
                    Vec3d toTarget = target.getPos().subtract(hatter.getPos());

                    Vec3d forwardPull = toTarget.normalize().multiply((Math.sqrt(distSq) - 6.0D) * 0.3D);
                    Vec3d strafeDir = new Vec3d(-toTarget.z, 0, toTarget.x).normalize().multiply(5.0D * phase3StrafeDirection);
                    Vec3d circlePos = hatter.getPos().add(strafeDir.add(forwardPull));
                    pathTo(circlePos.x, circlePos.y, circlePos.z, 1.1D);
                }

                if (subTimer >= 60 && hatter.getChargeCooldownTicks() <= 0 && distSq >= 16.0D) {
                    phase3SubState = 1;
                    subTimer = 0;
                    break;
                }

                if (distSq <= 9.0D && attackCooldown <= 0) {
                    hatter.getNavigation().stop();
                    if (hatter.tryAttack(target)) {
                        target.takeKnockback(1.0F, hatter.getX() - target.getX(), hatter.getZ() - target.getZ());
                        attackCooldown = 20;
                    }
                    return;
                }

                if (phase3AttackTimer <= 0 && hatter.getVisibilityCache().canSee(target)) {
                    hatter.getNavigation().stop();
                    hatter.triggerAnim("attack_controller", "attack_ranged");
                    phase3SubState = 3;
                    subTimer = 0;
                    phase3AttackTimer = 100;
                }
                break;

            case 1:
                hatter.getNavigation().stop();
                hatter.setCharging(true);

                if (subTimer >= 12) {
                    phase3SubState = 2;
                    subTimer = 0;
                    hatter.setCharging(false);
                    hatter.setRunning(true);
                    chargeTargetPos = target.getPos();

                    boolean pathValid = hatter.getNavigation().startMovingTo(chargeTargetPos.x, chargeTargetPos.y, chargeTargetPos.z, 2.3D);
                    if (!pathValid) {
                        endPhase3Charge();
                    }
                }
                break;

            case 2:
                if (distSq <= 4.0D) {
                    if (hatter.tryAttack(target)) {
                        target.takeKnockback(1.5F, hatter.getX() - target.getX(), hatter.getZ() - target.getZ());
                    }
                    endPhase3Charge();
                    break;
                }

                if (subTimer >= 35 || (chargeTargetPos != null && hatter.squaredDistanceTo(chargeTargetPos) <= 4.0D)) {
                    endPhase3Charge();
                }
                break;

            case 3:
                hatter.getNavigation().stop();

                if (subTimer >= 10) {
                    throwTeapot(target, PHASE3_POISON_AMP, POISON_TICKS, 1.4F);
                    phase3SubState = 0;
                    subTimer = 0;
                }
                break;
        }
    }

    private void endPhase3Charge() {
        phase3SubState = 0;
        subTimer = 0;
        hatter.setCharging(false);
        hatter.setRunning(false);
        hatter.setChargeCooldownTicks(PHASE3_CHARGE_COOLDOWN);
    }

    private void throwTeapot(LivingEntity target, int poisonAmplifier, int poisonTicks, float velocity) {
        TeapotProjectile teapot = new TeapotProjectile(
                hatter.getWorld(), hatter, TeapotProjectile.createPoisonedTeaStack(poisonAmplifier, poisonTicks));

        double d = target.getX() - hatter.getX();
        double e = target.getBodyY(0.5D) - teapot.getY();
        double f = target.getZ() - hatter.getZ();
        double flat = Math.sqrt(d * d + f * f);

        double flightTicks = flat / velocity;
        double lift = 0.5D * 0.03D * flightTicks * flightTicks;

        teapot.setVelocity(d, e + lift, f, velocity, 3.0F);
        hatter.getWorld().spawnEntity(teapot);
    }
}