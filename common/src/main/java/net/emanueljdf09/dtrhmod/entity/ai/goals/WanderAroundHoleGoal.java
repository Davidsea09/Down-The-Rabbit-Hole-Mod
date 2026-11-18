package net.emanueljdf09.dtrhmod.entity.ai.goals;

import net.emanueljdf09.dtrhmod.entity.custom.WhiteRabbitEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import java.util.EnumSet;

public class WanderAroundHoleGoal extends Goal {
    private final WhiteRabbitEntity rabbit;
    private final double maxDistance;

    private int idleTimeRemaining = 0;

    public WanderAroundHoleGoal(WhiteRabbitEntity rabbit, double maxDistance) {
        this.rabbit = rabbit;
        this.maxDistance = maxDistance;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.idleTimeRemaining > 0) {
            this.idleTimeRemaining--;
            return false;
        }

        return !rabbit.isReturning() && !rabbit.isGreeting() && rabbit.getNavigation().isDone();
    }

    @Override
    public boolean canContinueToUse() {
        return !rabbit.isReturning() && !rabbit.isGreeting() && !rabbit.getNavigation().isDone();
    }

    @Override
    public void start() {
        rabbit.getRabbitHolePos().ifPresent(origin -> {
            Vec3 target = findTargetNearEdge(origin);
            if (target != null) {
                this.rabbit.getNavigation().moveTo(target.x, target.y, target.z, 0.6D);
            }
        });
    }

    @Override
    public void stop() {
        super.stop();

        if (!rabbit.isReturning() && !rabbit.isGreeting()) {
            this.idleTimeRemaining = this.rabbit.getRandom().nextIntBetweenInclusive(40, 100);
        }
    }

    private Vec3 findTargetNearEdge(BlockPos origin) {
        double angle = this.rabbit.getRandom().nextDouble() * 2.0 * Math.PI;
        double minRadius = maxDistance * 0.6;
        double distance = minRadius + (this.rabbit.getRandom().nextDouble() * (maxDistance - minRadius));

        double xOffset = Math.cos(angle) * distance;
        double zOffset = Math.sin(angle) * distance;

        BlockPos targetPos = origin.offset((int) xOffset, 0, (int) zOffset);

        for (int yOffset = 3; yOffset >= -3; yOffset--) {
            BlockPos checkFloor = targetPos.above(yOffset);

            if (this.rabbit.level().getBlockState(checkFloor).isAir() &&
                    !this.rabbit.level().getBlockState(checkFloor.below()).isAir() &&
                    this.rabbit.level().getFluidState(checkFloor).isEmpty() &&
                    this.rabbit.level().getFluidState(checkFloor.below()).isEmpty()) {

                return Vec3.atBottomCenterOf(checkFloor);
            }
        }

        BlockPos fallbackPos = this.rabbit.level().getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, targetPos);
        if (!this.rabbit.level().getFluidState(fallbackPos).isEmpty() || !this.rabbit.level().getFluidState(fallbackPos.below()).isEmpty()) {
            return null;
        }

        return Vec3.atBottomCenterOf(fallbackPos);
    }
}