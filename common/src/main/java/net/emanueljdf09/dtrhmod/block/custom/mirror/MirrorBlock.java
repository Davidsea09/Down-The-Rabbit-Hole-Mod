package net.emanueljdf09.dtrhmod.block.custom.mirror;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.entity.MirrorBlockEntity;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.util.components.Mirror.MirrorSavedData;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.world.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MirrorBlock extends BaseEntityBlock {
    public static final MapCodec<MirrorBlock> CODEC = simpleCodec(MirrorBlock::new);
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final EnumProperty<MirrorType> TYPE = EnumProperty.create("type", MirrorType.class);

    public MirrorBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(TYPE, MirrorType.normal));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos blockPos = ctx.getClickedPos();
        Level world = ctx.getLevel();

        if (blockPos.getY() < world.getMaxBuildHeight() - 1 && world.getBlockState(blockPos.above()).canBeReplaced(ctx)) {
            return this.defaultBlockState()
                    .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(TYPE, MirrorType.normal);
        }
        return null;
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            super.onRemove(state, world, pos, newState, moved);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        world.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState blockState = world.getBlockState(pos.below());
            return blockState.is(this) && blockState.getValue(HALF) == DoubleBlockHalf.LOWER;
        }
        return super.canSurvive(state, world, pos);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        DoubleBlockHalf doubleBlockHalf = state.getValue(HALF);

        if (direction.getAxis() == Direction.Axis.Y && (doubleBlockHalf == DoubleBlockHalf.LOWER) == (direction == Direction.UP)) {
            if (neighborState.is(this) && neighborState.getValue(HALF) != doubleBlockHalf) {
                return state.setValue(FACING, neighborState.getValue(FACING)).setValue(TYPE, neighborState.getValue(TYPE));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClientSide && world.getServer() != null) {
            BlockPos lowerPos = (state.getValue(HALF) == DoubleBlockHalf.LOWER) ? pos : pos.below();

            ServerLevel overworld = world.getServer().getLevel(Level.OVERWORLD);
            ServerLevel wonderland = world.getServer().getLevel(ModDimensions.WONDERLAND_LEVEL_KEY);

            if (overworld != null) MirrorSavedData.get(overworld).removeLink(lowerPos);
            if (wonderland != null) MirrorSavedData.get(wonderland).removeLink(lowerPos);
        }
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        ItemStack stack = player.getMainHandItem();

        BlockPos lowerPos = (state.getValue(HALF) == DoubleBlockHalf.LOWER) ? pos : pos.below();
        BlockState lowerState = world.getBlockState(lowerPos);

        if (!lowerState.is(this)) return InteractionResult.PASS;
        MirrorType currentType = lowerState.getValue(TYPE);

        if (world.getBlockEntity(lowerPos) instanceof MirrorBlockEntity mirrorEntity) {

            if (currentType == MirrorType.normal) {
                if (stack.is(ModItems.POCKETWATCH)) {
                    if (!world.isClientSide) {
                        mirrorEntity.activateWonderlandPortal(world, lowerState, lowerPos);
                    }
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.PASS;
            }

            if (!world.isClientSide() && player instanceof ServerPlayer serverPlayer) {
                MinecraftServer server = player.getServer();

                if (ModConfigData.isBiomeLockingEnabled()) {
                    ProgressionComponent story = PlayerProgressionManager.get(server).get(player.getUUID());
                    if (!story.isWonderlandMirrorUnlocked()) {
                        serverPlayer.displayClientMessage(Component.literal("You can't use this yet."), true);

                        world.playSound(null, pos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 0.75f, 0.5f);
                        return InteractionResult.SUCCESS;
                    }
                }

                mirrorEntity.startTeleportSequence(serverPlayer, (ServerLevel) world);
                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, TYPE);
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
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    private static final VoxelShape LOWER_NORTH = makeShapeNorthBt();
    private static final VoxelShape LOWER_SOUTH = makeShapeSouthBt();
    private static final VoxelShape LOWER_EAST = makeShapeEastBt();
    private static final VoxelShape LOWER_WEST = makeShapeWestBt();
    private static final VoxelShape UPPER_NORTH = makeShapeNorthUp();
    private static final VoxelShape UPPER_SOUTH = makeShapeSouthUp();
    private static final VoxelShape UPPER_EAST = makeShapeEastUp();
    private static final VoxelShape UPPER_WEST = makeShapeWestUp();

    public static VoxelShape makeShapeNorthBt(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.046875, 0.421875, 0.90625, 0.15625, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.828125, 0.328125, 0.90625, 0.90625, 0.421875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.234375, 0.90625, 0.234375, 0.328125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.078125, 0.90625, 0.390625, 0.171875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.203125, 0.140625, 0.90625, 0.28125, 0.234375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.328125, 0.90625, 0.171875, 0.421875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.421875, 0.90625, 0.953125, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.390625, 0.03125, 0.90625, 0.609375, 0.1875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.234375, 0.90625, 0.265625, 0.3125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.359375, 0.90625, 0.09375, 0.421875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.359375, 0.90625, 0.9375, 0.421875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.328125, 0.90625, 0.21875, 0.375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.265625, 0.90625, 0.890625, 0.328125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.796875, 0.171875, 0.90625, 0.828125, 0.234375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.09375, 0.90625, 0.765625, 0.140625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.109375, 0.265625, 0.90625, 0.15625, 0.328125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.046875, 0.90625, 0.671875, 0.078125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.171875, 0.90625, 0.203125, 0.234375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.328125, 0.046875, 0.90625, 0.390625, 0.078125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.734375, 0.234375, 0.90625, 0.765625, 0.3125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.671875, 0.171875, 0.90625, 0.71875, 0.21875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.09375, 0.90625, 0.28125, 0.140625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.78125, 0.328125, 0.90625, 0.828125, 0.375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.171875, 0.90625, 0.328125, 0.21875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.453125, 0.1875, 0.90625, 0.546875, 0.21875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0, 0.90625, 0.5625, 0.03125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.234375, 0.90625, 0.84375, 0.328125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.078125, 0.90625, 0.71875, 0.171875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.140625, 0.90625, 0.796875, 0.234375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.171875, 0.953125, 0.765625, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.265625, 0.953125, 0.859375, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.140625, 0.265625, 0.953125, 0.234375, 1, 1), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeWestBt(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.421875, 0.84375, 1, 1, 0.953125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.328125, 0.09375, 1, 0.421875, 0.171875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.234375, 0.765625, 1, 0.328125, 0.84375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.078125, 0.609375, 1, 0.171875, 0.71875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.140625, 0.71875, 1, 0.234375, 0.796875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.328125, 0.828125, 1, 0.421875, 0.90625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.421875, 0.046875, 1, 1, 0.15625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.03125, 0.390625, 1, 0.1875, 0.609375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.234375, 0.734375, 1, 0.3125, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.359375, 0.90625, 1, 0.421875, 0.9375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.359375, 0.0625, 1, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.328125, 0.78125, 1, 0.375, 0.828125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.265625, 0.109375, 1, 0.328125, 0.15625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.171875, 0.171875, 1, 0.234375, 0.203125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.09375, 0.234375, 1, 0.140625, 0.28125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.265625, 0.84375, 1, 0.328125, 0.890625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.046875, 0.328125, 1, 0.078125, 0.390625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.171875, 0.796875, 1, 0.234375, 0.828125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.046875, 0.609375, 1, 0.078125, 0.671875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.234375, 0.234375, 1, 0.3125, 0.265625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.171875, 0.28125, 1, 0.21875, 0.328125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.09375, 0.71875, 1, 0.140625, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.328125, 0.171875, 1, 0.375, 0.21875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.171875, 0.671875, 1, 0.21875, 0.71875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.1875, 0.453125, 1, 0.21875, 0.546875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0, 0.4375, 1, 0.03125, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.234375, 0.15625, 1, 0.328125, 0.234375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.078125, 0.28125, 1, 0.171875, 0.390625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.140625, 0.203125, 1, 0.234375, 0.28125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.953125, 0.171875, 0.234375, 1, 1, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.953125, 0.265625, 0.140625, 1, 1, 0.234375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.953125, 0.265625, 0.765625, 1, 1, 0.859375), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeEastBt(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.421875, 0.046875, 0.09375, 1, 0.15625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.328125, 0.828125, 0.09375, 0.421875, 0.90625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.234375, 0.15625, 0.09375, 0.328125, 0.234375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.078125, 0.28125, 0.09375, 0.171875, 0.390625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.140625, 0.203125, 0.09375, 0.234375, 0.28125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.328125, 0.09375, 0.09375, 0.421875, 0.171875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.421875, 0.84375, 0.09375, 1, 0.953125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.03125, 0.390625, 0.09375, 0.1875, 0.609375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.234375, 0.234375, 0.09375, 0.3125, 0.265625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.359375, 0.0625, 0.09375, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.359375, 0.90625, 0.09375, 0.421875, 0.9375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.328125, 0.171875, 0.09375, 0.375, 0.21875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.265625, 0.84375, 0.09375, 0.328125, 0.890625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.171875, 0.796875, 0.09375, 0.234375, 0.828125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.09375, 0.71875, 0.09375, 0.140625, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.265625, 0.109375, 0.09375, 0.328125, 0.15625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.046875, 0.609375, 0.09375, 0.078125, 0.671875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.171875, 0.171875, 0.09375, 0.234375, 0.203125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.046875, 0.328125, 0.09375, 0.078125, 0.390625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.234375, 0.734375, 0.09375, 0.3125, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.171875, 0.671875, 0.09375, 0.21875, 0.71875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.09375, 0.234375, 0.09375, 0.140625, 0.28125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.328125, 0.78125, 0.09375, 0.375, 0.828125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.171875, 0.28125, 0.09375, 0.21875, 0.328125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.1875, 0.453125, 0.09375, 0.21875, 0.546875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0, 0.4375, 0.09375, 0.03125, 0.5625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.234375, 0.765625, 0.09375, 0.328125, 0.84375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.078125, 0.609375, 0.09375, 0.171875, 0.71875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.140625, 0.71875, 0.09375, 0.234375, 0.796875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.171875, 0.234375, 0.046875, 1, 0.765625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.265625, 0.765625, 0.046875, 1, 0.859375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0, 0.265625, 0.140625, 0.046875, 1, 0.234375), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeSouthBt(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.421875, 0, 0.953125, 1, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.328125, 0, 0.171875, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.234375, 0, 0.84375, 0.328125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.078125, 0, 0.71875, 0.171875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.140625, 0, 0.796875, 0.234375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.828125, 0.328125, 0, 0.90625, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.046875, 0.421875, 0, 0.15625, 1, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.390625, 0.03125, 0, 0.609375, 0.1875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.734375, 0.234375, 0, 0.765625, 0.3125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.359375, 0, 0.9375, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.359375, 0, 0.09375, 0.421875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.78125, 0.328125, 0, 0.828125, 0.375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.109375, 0.265625, 0, 0.15625, 0.328125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.171875, 0, 0.203125, 0.234375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.09375, 0, 0.28125, 0.140625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.265625, 0, 0.890625, 0.328125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.328125, 0.046875, 0, 0.390625, 0.078125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.796875, 0.171875, 0, 0.828125, 0.234375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.046875, 0, 0.671875, 0.078125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.234375, 0, 0.265625, 0.3125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.171875, 0, 0.328125, 0.21875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.09375, 0, 0.765625, 0.140625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.328125, 0, 0.21875, 0.375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.671875, 0.171875, 0, 0.71875, 0.21875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.453125, 0.1875, 0, 0.546875, 0.21875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0, 0, 0.5625, 0.03125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.234375, 0, 0.234375, 0.328125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.078125, 0, 0.390625, 0.171875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.203125, 0.140625, 0, 0.28125, 0.234375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.171875, 0, 0.765625, 1, 0.046875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.140625, 0.265625, 0, 0.234375, 1, 0.046875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.265625, 0, 0.859375, 1, 0.046875), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeNorthUp(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0, 0.90625, 0.953125, 0.578125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.578125, 0.90625, 0.171875, 0.671875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.671875, 0.90625, 0.84375, 0.765625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.828125, 0.90625, 0.71875, 0.921875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.765625, 0.90625, 0.796875, 0.859375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.828125, 0.578125, 0.90625, 0.90625, 0.671875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.046875, 0, 0.90625, 0.15625, 0.578125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.390625, 0.8125, 0.90625, 0.609375, 0.96875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.734375, 0.6875, 0.90625, 0.765625, 0.765625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.578125, 0.90625, 0.9375, 0.640625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.578125, 0.90625, 0.09375, 0.640625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.78125, 0.625, 0.90625, 0.828125, 0.671875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.109375, 0.671875, 0.90625, 0.15625, 0.734375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.765625, 0.90625, 0.203125, 0.828125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.859375, 0.90625, 0.28125, 0.90625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.671875, 0.90625, 0.890625, 0.734375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.328125, 0.921875, 0.90625, 0.390625, 0.953125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.796875, 0.765625, 0.90625, 0.828125, 0.828125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.921875, 0.90625, 0.671875, 0.953125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.6875, 0.90625, 0.265625, 0.765625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.78125, 0.90625, 0.328125, 0.828125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.859375, 0.90625, 0.765625, 0.90625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.625, 0.90625, 0.21875, 0.671875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.671875, 0.78125, 0.90625, 0.71875, 0.828125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.453125, 0.78125, 0.90625, 0.546875, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.96875, 0.90625, 0.5625, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.671875, 0.90625, 0.234375, 0.765625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.828125, 0.90625, 0.390625, 0.921875, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.203125, 0.765625, 0.90625, 0.28125, 0.859375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0, 0.953125, 0.765625, 0.828125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.140625, 0, 0.953125, 0.234375, 0.734375, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0, 0.953125, 0.859375, 0.734375, 1), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeSouthUp(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.046875, 0, 0, 0.15625, 0.578125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.828125, 0.578125, 0, 0.90625, 0.671875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.671875, 0, 0.234375, 0.765625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.828125, 0, 0.390625, 0.921875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.203125, 0.765625, 0, 0.28125, 0.859375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.578125, 0, 0.171875, 0.671875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0, 0, 0.953125, 0.578125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.390625, 0.8125, 0, 0.609375, 0.96875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.6875, 0, 0.265625, 0.765625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.0625, 0.578125, 0, 0.09375, 0.640625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.90625, 0.578125, 0, 0.9375, 0.640625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.625, 0, 0.21875, 0.671875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.84375, 0.671875, 0, 0.890625, 0.734375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.796875, 0.765625, 0, 0.828125, 0.828125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.859375, 0, 0.765625, 0.90625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.109375, 0.671875, 0, 0.15625, 0.734375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.921875, 0, 0.671875, 0.953125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.171875, 0.765625, 0, 0.203125, 0.828125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.328125, 0.921875, 0, 0.390625, 0.953125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.734375, 0.6875, 0, 0.765625, 0.765625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.671875, 0.78125, 0, 0.71875, 0.828125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0.859375, 0, 0.28125, 0.90625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.78125, 0.625, 0, 0.828125, 0.671875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.28125, 0.78125, 0, 0.328125, 0.828125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.453125, 0.78125, 0, 0.546875, 0.8125, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.4375, 0.96875, 0, 0.5625, 1, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0.671875, 0, 0.84375, 0.765625, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.609375, 0.828125, 0, 0.71875, 0.921875, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.71875, 0.765625, 0, 0.796875, 0.859375, 0.09375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.234375, 0, 0, 0.765625, 0.828125, 0.046875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.765625, 0, 0, 0.859375, 0.734375, 0.046875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.140625, 0, 0, 0.234375, 0.734375, 0.046875), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeWestUp(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0, 0.846875, 1.003125, 0.578125, 0.95625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.578125, 0.096875, 1.003125, 0.671875, 0.175), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.671875, 0.76875, 1.003125, 0.765625, 0.846875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.828125, 0.6125, 1.003125, 0.921875, 0.721875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.765625, 0.721875, 1.003125, 0.859375, 0.8), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.578125, 0.83125, 1.003125, 0.671875, 0.909375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0, 0.05, 1.003125, 0.578125, 0.159375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.8125, 0.39375, 1.003125, 0.96875, 0.6125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.6875, 0.7375, 1.003125, 0.765625, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.578125, 0.909375, 1.003125, 0.640625, 0.940625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.578125, 0.065625, 1.003125, 0.640625, 0.096875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.625, 0.784375, 1.003125, 0.671875, 0.83125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.671875, 0.1125, 1.003125, 0.734375, 0.159375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.765625, 0.175, 1.003125, 0.828125, 0.20625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.859375, 0.2375, 1.003125, 0.90625, 0.284375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.671875, 0.846875, 1.003125, 0.734375, 0.89375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.921875, 0.33125, 1.003125, 0.953125, 0.39375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.765625, 0.8, 1.003125, 0.828125, 0.83125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.921875, 0.6125, 1.003125, 0.953125, 0.675), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.6875, 0.2375, 1.003125, 0.765625, 0.26875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.78125, 0.284375, 1.003125, 0.828125, 0.33125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.859375, 0.721875, 1.003125, 0.90625, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.625, 0.175, 1.003125, 0.671875, 0.221875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.78125, 0.675, 1.003125, 0.828125, 0.721875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.78125, 0.45625, 1.003125, 0.8125, 0.55), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.96875, 0.440625, 1.003125, 1, 0.565625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.671875, 0.159375, 1.003125, 0.765625, 0.2375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.828125, 0.284375, 1.003125, 0.921875, 0.39375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.909375, 0.765625, 0.20625, 1.003125, 0.859375, 0.284375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.95625, 0, 0.2375, 1.003125, 0.828125, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.95625, 0, 0.14375, 1.003125, 0.734375, 0.2375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.95625, 0, 0.76875, 1.003125, 0.734375, 0.8625), BooleanOp.OR);
        return shape;
    }

    public static VoxelShape makeShapeEastUp(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0, 0.846875, 0.090625, 0.578125, 0.95625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.578125, 0.096875, 0.090625, 0.671875, 0.175), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.671875, 0.76875, 0.090625, 0.765625, 0.846875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.828125, 0.6125, 0.090625, 0.921875, 0.721875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.765625, 0.721875, 0.090625, 0.859375, 0.8), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.578125, 0.83125, 0.090625, 0.671875, 0.909375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0, 0.05, 0.090625, 0.578125, 0.159375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.8125, 0.39375, 0.090625, 0.96875, 0.6125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.6875, 0.7375, 0.090625, 0.765625, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.578125, 0.909375, 0.090625, 0.640625, 0.940625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.578125, 0.065625, 0.090625, 0.640625, 0.096875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.625, 0.784375, 0.090625, 0.671875, 0.83125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.671875, 0.1125, 0.090625, 0.734375, 0.159375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.765625, 0.175, 0.090625, 0.828125, 0.20625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.859375, 0.2375, 0.090625, 0.90625, 0.284375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.671875, 0.846875, 0.090625, 0.734375, 0.89375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.921875, 0.33125, 0.090625, 0.953125, 0.39375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.765625, 0.8, 0.090625, 0.828125, 0.83125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.921875, 0.6125, 0.090625, 0.953125, 0.675), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.6875, 0.2375, 0.090625, 0.765625, 0.26875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.78125, 0.284375, 0.090625, 0.828125, 0.33125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.859375, 0.721875, 0.090625, 0.90625, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.625, 0.175, 0.090625, 0.671875, 0.221875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.78125, 0.675, 0.090625, 0.828125, 0.721875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.78125, 0.45625, 0.090625, 0.8125, 0.55), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.96875, 0.440625, 0.090625, 1, 0.565625), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.671875, 0.159375, 0.090625, 0.765625, 0.2375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.828125, 0.284375, 0.090625, 0.921875, 0.39375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0.765625, 0.20625, 0.090625, 0.859375, 0.284375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0, 0.2375, 0.04375, 0.828125, 0.76875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0, 0.14375, 0.04375, 0.734375, 0.2375), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.003125, 0, 0.76875, 0.04375, 0.734375, 0.8625), BooleanOp.OR);
        return shape;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);

        if (half == DoubleBlockHalf.LOWER) {
            return switch (facing) {
                case SOUTH -> LOWER_SOUTH;
                case WEST -> LOWER_WEST;
                case EAST -> LOWER_EAST;
                default -> LOWER_NORTH;
            };
        } else {
            return switch (facing) {
                case SOUTH -> UPPER_SOUTH;
                case WEST -> UPPER_WEST;
                case EAST -> UPPER_EAST;
                default -> UPPER_NORTH;
            };
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        DoubleBlockHalf half = state.getValue(HALF);

        if (half == DoubleBlockHalf.LOWER) {
            return switch (facing) {
                case SOUTH -> LOWER_SOUTH;
                case WEST -> LOWER_WEST;
                case EAST -> LOWER_EAST;
                default -> LOWER_NORTH;
            };
        } else {
            return switch (facing) {
                case SOUTH -> UPPER_SOUTH;
                case WEST -> UPPER_WEST;
                case EAST -> UPPER_EAST;
                default -> UPPER_NORTH;
            };
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            return new MirrorBlockEntity(pos, state);
        }
        return null;
    }
}