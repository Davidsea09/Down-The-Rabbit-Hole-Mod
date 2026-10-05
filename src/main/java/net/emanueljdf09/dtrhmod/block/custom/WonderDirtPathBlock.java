package net.emanueljdf09.dtrhmod.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.DirtPathBlock;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;

public class WonderDirtPathBlock extends DirtPathBlock {
    public WonderDirtPathBlock(Settings settings) {
        super(settings);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }
}
