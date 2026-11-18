package net.emanueljdf09.dtrhmod.block.custom.spawn;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CheshireCatSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class CheshireCatSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<CheshireCatSpawnerBlock> CODEC = simpleCodec(CheshireCatSpawnerBlock::new);
    public CheshireCatSpawnerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CheshireCatSpawnerBlockEntity(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.CHESHIRE_CAT_SPAWNER_BE.get()
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> CheshireCatSpawnerBlockEntity.tick(world1, pos, state1, (CheshireCatSpawnerBlockEntity) blockEntity)
                : null;
    }

}
