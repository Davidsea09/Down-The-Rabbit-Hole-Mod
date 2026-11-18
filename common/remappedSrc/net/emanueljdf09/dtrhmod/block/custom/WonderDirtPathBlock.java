package net.emanueljdf09.dtrhmod.block.custom;

import net.minecraft.world.level.block.DirtPathBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

public class WonderDirtPathBlock extends DirtPathBlock {
    public WonderDirtPathBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

}
