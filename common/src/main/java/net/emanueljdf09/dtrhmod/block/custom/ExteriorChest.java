package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.entity.ExteriorChestEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ExteriorChest extends BaseEntityBlock {
    public static final MapCodec<ExteriorChest> CODEC = simpleCodec(ExteriorChest::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public VoxelShape SHAPE_HOR = makeHorShape();
    public VoxelShape SHAPE_VER = makeVerShape();

    public ExteriorChest(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.WEST));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ExteriorChestEntity chestEntity) {
            return chestEntity.onUse(player, player.swingingArm);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> SHAPE_VER;
            default -> SHAPE_HOR;
        };
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case EAST, WEST -> SHAPE_VER;
            default -> SHAPE_HOR;
        };
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }



    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExteriorChestEntity(pos, state);
    }

    public VoxelShape makeHorShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0, 0.1875, 0.875, 0.0625, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0625, 0.21875, 0.8125, 0.3125, 0.25), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0625, 0.75, 0.8125, 0.3125, 0.78125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.0625, 0.25, 0.1875, 0.3125, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0625, 0.25, 0.84375, 0.3125, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0625, 0.1875, 0.875, 0.3125, 0.25), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.0625, 0.75, 0.875, 0.3125, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.0625, 0.75, 0.1875, 0.3125, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.0625, 0.1875, 0.1875, 0.3125, 0.25), BooleanOp.OR);

        return shape;
    }

    public VoxelShape makeVerShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0, 0.125, 0.8125, 0.0625, 0.875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.1875, 0.78125, 0.3125, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.21875, 0.0625, 0.1875, 0.25, 0.3125, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.15625, 0.75, 0.3125, 0.1875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.25, 0.0625, 0.8125, 0.75, 0.3125, 0.84375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.8125, 0.8125, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0625, 0.8125, 0.25, 0.3125, 0.875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.1875, 0.0625, 0.125, 0.25, 0.3125, 0.1875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.75, 0.0625, 0.125, 0.8125, 0.3125, 0.1875), BooleanOp.OR);

        return shape;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

}
