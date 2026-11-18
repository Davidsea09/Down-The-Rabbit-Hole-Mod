package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.TeapotBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class TeapotBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<TeapotBlock> CODEC = simpleCodec(TeapotBlock::new);
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    private static final Map<Direction, VoxelShape> SHAPES = createDirectionalShapes();


    public TeapotBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPES.get(Direction.NORTH));
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), SHAPES.get(Direction.NORTH));
    }

    private static Map<Direction, VoxelShape> createDirectionalShapes() {
        Map<Direction, VoxelShape> map = new EnumMap<>(Direction.class);

        VoxelShape northShape = Shapes.empty();
        northShape = Shapes.joinUnoptimized(northShape, Shapes.box(0.1875, 0.0625, 0.3125, 0.8125, 0.4375, 0.6875), BooleanOp.OR);
        northShape = Shapes.joinUnoptimized(northShape, Shapes.box(0.25, 0.4375, 0.375, 0.75, 0.5, 0.625), BooleanOp.OR);
        northShape = Shapes.joinUnoptimized(northShape, Shapes.box(0.25, 0, 0.375, 0.75, 0.0625, 0.625), BooleanOp.OR);
        map.put(Direction.NORTH, northShape.optimize());
        map.put(Direction.SOUTH, northShape.optimize());


        VoxelShape eastShape = Shapes.empty();
        eastShape = Shapes.joinUnoptimized(eastShape, Shapes.box(0.3125, 0.0625, 0.1875, 0.6875, 0.4375, 0.8125), BooleanOp.OR);
        eastShape = Shapes.joinUnoptimized(eastShape, Shapes.box(0.375, 0.4375, 0.25, 0.625, 0.5, 0.75), BooleanOp.OR);
        eastShape = Shapes.joinUnoptimized(eastShape, Shapes.box(0.375, 0, 0.25, 0.625, 0.0625, 0.75), BooleanOp.OR);

        map.put(Direction.EAST, eastShape.optimize());
        map.put(Direction.WEST, eastShape.optimize());

        return map;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeapotBlockEntity(pos, state);
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof TeapotBlockEntity) {
                Containers.dropContents(world, pos, (TeapotBlockEntity)blockEntity);
                world.updateNeighbourForOutputSignal(pos,this);
            }
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClientSide) {
            MenuProvider screenHandlerFactory = ((TeapotBlockEntity) world.getBlockEntity(pos));

            if (screenHandlerFactory != null) {
                player.openMenu(screenHandlerFactory);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(Component.translatable("block.dtrhmod.teapot.tooltip").withStyle(ChatFormatting.RED));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.TEAPOT_BLOCK_ENTITY
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> TeapotBlockEntity.tick(world1, pos, state1, (TeapotBlockEntity) blockEntity)
                : null;
    }
}