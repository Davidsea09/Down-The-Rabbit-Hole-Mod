package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.RabbitHoleBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public class RabbitHoleBlock extends BaseEntityBlock {
    public static final MapCodec<RabbitHoleBlock> CODEC = simpleCodec(RabbitHoleBlock::new);

    public RabbitHoleBlock(Properties settings) {
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

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) return;

        if (entity instanceof ServerPlayer player && world.getBlockEntity(pos) instanceof RabbitHoleBlockEntity rabbitHole) {
            rabbitHole.handlePlayerCollision(player);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RabbitHoleBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.RABBIT_HOLE_BLOCK_ENTITY.get()
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> RabbitHoleBlockEntity.tick(world1, pos, state1, (RabbitHoleBlockEntity) blockEntity)
                : null;
    }
}