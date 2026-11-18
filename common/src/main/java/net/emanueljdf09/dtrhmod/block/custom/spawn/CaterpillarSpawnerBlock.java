package net.emanueljdf09.dtrhmod.block.custom.spawn;


import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.spawn.CaterpillarSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class CaterpillarSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<CaterpillarSpawnerBlock> CODEC = simpleCodec(CaterpillarSpawnerBlock::new);

    public CaterpillarSpawnerBlock(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CaterpillarSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.CATERPILLAR_SPAWNER_BE.get()
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> CaterpillarSpawnerBlockEntity.tick(world1, pos, state1, (CaterpillarSpawnerBlockEntity) blockEntity)
                : null;
    }

}
