package net.emanueljdf09.dtrhmod.entity.ai.goals;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class StalkerGoal extends Goal {
    protected final PathfinderMob mob;
    protected Player targetPlayer;
    private boolean isFrozen = false;

    private int stalkDelayTimer = 0;
    private int attackDelayTimer = 0;
    private static final int TWO_SECONDS_IN_TICKS = 40;

    public StalkerGoal(PathfinderMob mob) {
        this.mob = mob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        this.targetPlayer = this.mob.level().getNearestPlayer(
                this.mob.getX(), this.mob.getY(), this.mob.getZ(), 32.0,
                player -> player.isAlive() && !player.isSpectator()
        );
        return this.targetPlayer != null;
    }

    @Override
    public void start() {
        this.stalkDelayTimer = 0;
        this.attackDelayTimer = 0;
    }

    @Override
    public void tick() {
        if (this.targetPlayer == null || !this.targetPlayer.isAlive() || this.targetPlayer.isSpectator() || this.targetPlayer.isCreative() || this.mob.distanceToSqr(this.targetPlayer) > 1024.0D) {
            this.targetPlayer = this.mob.level().getNearestPlayer(
                    this.mob.getX(), this.mob.getY(), this.mob.getZ(), 32.0,
                    player -> player.isAlive() && !player.isSpectator()
            );
        }

        if (this.targetPlayer == null) return;

        if (isAnyPlayerLookingAtMob()) {
            this.isFrozen = true;
            this.mob.getNavigation().stop();
            this.mob.setDeltaMovement(Vec3.ZERO);
            this.stalkDelayTimer = 0;
            this.attackDelayTimer = 0;
            return;
        }

        this.isFrozen = false;

        if (this.stalkDelayTimer < TWO_SECONDS_IN_TICKS) {
            this.stalkDelayTimer++;
            this.mob.getNavigation().stop();
            this.mob.setDeltaMovement(Vec3.ZERO);
            return;
        }

        double distanceSq = this.mob.distanceToSqr(this.targetPlayer);

        if (distanceSq <= 4.0D) {
            this.mob.getNavigation().stop();
            this.mob.setDeltaMovement(Vec3.ZERO);

            if (this.attackDelayTimer < TWO_SECONDS_IN_TICKS) {
                this.attackDelayTimer++;
            } else {
                float damage = (float) this.mob.getAttributeValue(Attributes.ATTACK_DAMAGE);
                this.targetPlayer.hurt(this.mob.damageSources().mobAttack(this.mob), damage);
                this.targetPlayer.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40));

                this.attackDelayTimer = 0;
            }
        } else {
            this.attackDelayTimer = 0;

            double angleDegrees = (this.mob.getId() * 45) % 360;
            double angleRadians = Math.toRadians(angleDegrees);

            double radius = 1.2D;

            double targetX = this.targetPlayer.getX() + (Math.cos(angleRadians) * radius);
            double targetZ = this.targetPlayer.getZ() + (Math.sin(angleRadians) * radius);
            double targetY = this.targetPlayer.getY();

            this.mob.getNavigation().moveTo(targetX, targetY, targetZ, 1.4D);
        }
    }

    public boolean isFrozen() {
        return this.isFrozen;
    }

    private boolean isAnyPlayerLookingAtMob() {
        List<Player> nearbyPlayers = this.mob.level().getEntitiesOfClass(
                Player.class,
                this.mob.getBoundingBox().inflate(32.0D),
                player -> player.isAlive() && !player.isSpectator()
        );

        for (Player player : nearbyPlayers) {
            if (isPlayerLooking(player)) {
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerLooking(Player player) {
        Vec3 lookVec = player.getViewVector(1.0F).normalize();
        Vec3 toMobVec = new Vec3(
                this.mob.getX() - player.getX(),
                this.mob.getEyeY() - player.getEyeY(),
                this.mob.getZ() - player.getZ()
        );

        toMobVec = toMobVec.normalize();
        double dotProduct = lookVec.dot(toMobVec);

        if (dotProduct > 0.7) {
            return player.hasLineOfSight(this.mob);
        }
        return false;
    }

    @Override
    public void stop() {
        this.targetPlayer = null;
        this.isFrozen = false;
    }
}