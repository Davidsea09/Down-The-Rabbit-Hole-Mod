package net.emanueljdf09.dtrhmod.block.custom.signs;

import net.emanueljdf09.dtrhmod.block.entity.ModHangingSignBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.WallHangingSignBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModWallHangingSignBlock extends WallHangingSignBlock {
    public ModWallHangingSignBlock(WoodType woodType, Properties properties) {
        super(woodType, properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new ModHangingSignBlockEntity(blockPos, blockState);
    }
}
