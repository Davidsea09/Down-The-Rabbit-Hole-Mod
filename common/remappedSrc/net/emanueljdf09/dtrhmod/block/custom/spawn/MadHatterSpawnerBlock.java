package net.emanueljdf09.dtrhmod.block.custom.spawn;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.spawn.MadHatterSpawnerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class MadHatterSpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<MadHatterSpawnerBlock> CODEC = simpleCodec(MadHatterSpawnerBlock::new);

    public MadHatterSpawnerBlock(Properties settings) {
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
        return new MadHatterSpawnerBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.MAD_HATTER_SPAWNER_BE
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> MadHatterSpawnerBlockEntity.tick(world1, pos, state1, (MadHatterSpawnerBlockEntity) blockEntity)
                : null;
    }

}
