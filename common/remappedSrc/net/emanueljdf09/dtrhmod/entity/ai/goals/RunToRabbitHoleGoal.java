package net.emanueljdf09.dtrhmod.entity.ai.goals;

import net.emanueljdf09.dtrhmod.fabric.entity.custom.WhiteRabbitEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import java.util.EnumSet;

public class RunToRabbitHoleGoal extends Goal {
    private final WhiteRabbitEntity rabbit;

    public RunToRabbitHoleGoal(WhiteRabbitEntity rabbit) {
        this.rabbit = rabbit;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return rabbit.isReturning() && rabbit.getRabbitHolePos().isPresent();
    }

    @Override
    public void start() {
        rabbit.getRabbitHolePos().ifPresent(pos -> {
            rabbit.getNavigation().startMovingTo(pos.getX(), pos.getY(), pos.getZ(), 1.6D);
        });
    }

    @Override
    public boolean canContinueToUse() {
        return rabbit.isReturning() && !rabbit.getNavigation().isIdle();
    }
}
