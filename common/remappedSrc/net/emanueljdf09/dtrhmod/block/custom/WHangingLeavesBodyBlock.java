package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.GrowingPlantBodyBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WHangingLeavesBodyBlock extends GrowingPlantBodyBlock {
    public static final MapCodec<WHangingLeavesBodyBlock> CODEC = simpleCodec(WHangingLeavesBodyBlock::new);
    public static final VoxelShape SHAPE = Block.box(4.0, 9.0, 4.0, 12.0, 16.0, 12.0);

    public WHangingLeavesBodyBlock(Properties settings) {
        super(settings, Direction.DOWN, SHAPE, false);
    }

    @Override
    protected GrowingPlantHeadBlock getHeadBlock() {
        return (GrowingPlantHeadBlock) ModBlocks.WW_HANGING_LEAVES;
    }

    @Override
    protected boolean canAttachTo(BlockState state) {
        return super.canAttachTo(state) || state.is(ModBlocks.WW_LOG) || state.is(ModBlocks.WW_LEAVES);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        BlockState aboveState = world.getBlockState(pos.above());
        return aboveState.is(this) || aboveState.is(ModBlocks.WW_HANGING_LEAVES) || aboveState.is(ModBlocks.WW_LEAVES);
    }

    @Override
    protected MapCodec<? extends GrowingPlantBodyBlock> codec() {
        return CODEC;
    }
}
