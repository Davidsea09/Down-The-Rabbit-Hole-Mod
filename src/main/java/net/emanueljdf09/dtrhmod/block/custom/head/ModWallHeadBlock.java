package net.emanueljdf09.dtrhmod.block.custom.head;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.item.Equipment;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ModWallHeadBlock extends BlockWithEntity implements Equipment {
    private final ModHeadType headType;
    public static final IntProperty ROTATION = IntProperty.of("rotation", 0, 3);

    private static final Map<Integer, VoxelShape> ROTATION_TO_SHAPE = Maps.newHashMap(Map.of(
            0, Block.createCuboidShape(4.0D, 4.0D, 0.0D, 12.0D, 12.0D, 8.0D),
            1, Block.createCuboidShape(8.0D, 4.0D, 4.0D, 16.0D, 12.0D, 12.0D),
            2, Block.createCuboidShape(4.0D, 4.0D, 8.0D, 12.0D, 12.0D, 16.0D),
            3, Block.createCuboidShape(0.0D, 4.0D, 4.0D, 8.0D, 12.0D, 12.0D)
    ));

    public ModWallHeadBlock(ModHeadType headType, Settings settings) {
        super(settings);
        this.headType = headType;
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    public ModHeadType getHeadType() {
        return headType;
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public String getTranslationKey() {
        return this.asItem().getTranslationKey();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return ROTATION_TO_SHAPE.get(state.get(ROTATION));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockState blockState = this.getDefaultState();
        Direction clickedFace = ctx.getSide();

        int rot = switch (clickedFace) {
            case NORTH -> 2;
            case SOUTH -> 0;
            case WEST -> 1;
            case EAST -> 3;
            default -> 0;
        };

        return blockState.with(ROTATION, rot);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        int current = state.get(ROTATION);
        int added = switch (rotation) {
            case CLOCKWISE_90 -> 1;
            case CLOCKWISE_180 -> 2;
            case COUNTERCLOCKWISE_90 -> 3;
            default -> 0;
        };
        return state.with(ROTATION, (current + added) % 4);
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Override
    public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
        return false;
    }

    @Override
    public EquipmentSlot getSlotType() {
        return EquipmentSlot.HEAD;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ModHeadBlockEntity(pos, state);
    }
}