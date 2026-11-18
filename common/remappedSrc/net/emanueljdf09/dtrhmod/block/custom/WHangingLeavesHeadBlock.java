package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WHangingLeavesHeadBlock extends GrowingPlantHeadBlock {
    public static final MapCodec<WHangingLeavesHeadBlock> CODEC = simpleCodec(WHangingLeavesHeadBlock::new);
    public static final VoxelShape SHAPE = Block.box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

    public WHangingLeavesHeadBlock(Properties settings) {
        super(settings, Direction.DOWN, SHAPE, false, 0.3);
    }

    @Override
    protected MapCodec<? extends GrowingPlantHeadBlock> codec() {
        return CODEC;
    }

    @Override
    protected int getBlocksToGrowWhenBonemealed(RandomSource random) {
        return 1;
    }

    @Override
    protected boolean canGrowInto(BlockState state) {
        return state.isAir();
    }

    @Override
    protected Block getBodyBlock() {
        return ModBlocks.WW_HANGING_LEAVES_PLANT;
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
         return super.canAttachTo(state) || state.is(ModBlocks.WW_LOG) || state.is(ModBlocks.WW_LEAVES) || state.is(ModBlocks.WW_HANGING_LEAVES_PLANT);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.above());
        return aboveState.is(this) || aboveState.is(ModBlocks.WW_HANGING_LEAVES) || aboveState.is(ModBlocks.WW_LEAVES) || aboveState.is(ModBlocks.WW_HANGING_LEAVES_PLANT);
    }
}