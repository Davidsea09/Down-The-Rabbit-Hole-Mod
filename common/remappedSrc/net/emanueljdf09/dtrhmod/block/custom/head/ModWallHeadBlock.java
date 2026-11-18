package net.emanueljdf09.dtrhmod.block.custom.head;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ModWallHeadBlock extends BaseEntityBlock implements Equipable {
    public static final MapCodec<ModWallHeadBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ModHeadType.CODEC.fieldOf("head_type").forGetter(ModWallHeadBlock::getHeadType),
                    propertiesCodec()
            ).apply(instance, ModWallHeadBlock::new)
    );

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    private static final Map<Direction, VoxelShape> SHAPES = Maps.newEnumMap(Map.of(
            Direction.NORTH, Block.box(4.0, 4.0, 8.0, 12.0, 12.0, 16.0),
            Direction.SOUTH, Block.box(4.0, 4.0, 0.0, 12.0, 12.0, 8.0),
            Direction.EAST, Block.box(0.0, 4.0, 4.0, 8.0, 12.0, 12.0),
            Direction.WEST, Block.box(8.0, 4.0, 4.0, 16.0, 12.0, 12.0)));

    private final ModHeadType headType;

    public ModWallHeadBlock(ModHeadType headType, Properties settings) {
        super(settings);
        this.headType = headType;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public ModHeadType getHeadType() {
        return headType;
    }

    @Override
    protected MapCodec<ModWallHeadBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPES.get(state.getValue(FACING));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockState state = this.defaultBlockState();
        BlockGetter world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();

        for (Direction direction : ctx.getNearestLookingDirections()) {
            if (direction.getAxis().isHorizontal()) {
                Direction facing = direction.getOpposite();
                state = state.setValue(FACING, facing);
                if (!world.getBlockState(pos.relative(direction)).canBeReplaced(ctx)) {
                    return state;
                }
            }
        }
        return null;
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
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }

    @Override
    public EquipmentSlot getEquipmentSlot() {
        return EquipmentSlot.HEAD;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ModHeadBlockEntity(pos, state);
    }
}