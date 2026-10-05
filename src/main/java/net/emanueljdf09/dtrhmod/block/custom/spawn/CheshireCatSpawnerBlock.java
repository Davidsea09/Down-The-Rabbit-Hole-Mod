package net.emanueljdf09.dtrhmod.block.custom.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CheshireCatSpawnerBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.spawn.MadHatterSpawnerBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CheshireCatSpawnerBlock extends BlockWithEntity {
    public CheshireCatSpawnerBlock(Settings settings) {
        super(settings);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CheshireCatSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : checkType(type, ModBlockEntities.CHESHIRE_CAT_SPAWNER_BE, CheshireCatSpawnerBlockEntity::tick);
    }
}
