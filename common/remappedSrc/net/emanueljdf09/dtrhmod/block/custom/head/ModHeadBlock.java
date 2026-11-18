package net.emanueljdf09.dtrhmod.block.custom.head;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ModHeadBlock extends BaseEntityBlock implements Equipable {
    public static final MapCodec<ModHeadBlock> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ModHeadType.CODEC.fieldOf("head_type").forGetter(ModHeadBlock::getHeadType),
                    propertiesCodec()
            ).apply(instance, ModHeadBlock::new)
    );

    public static final IntegerProperty ROTATION = BlockStateProperties.ROTATION_16;
    public static final int MAX_ROTATIONS = RotationSegment.getMaxSegmentIndex() + 1;

    protected static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

    private final ModHeadType headType;

    public ModHeadBlock(ModHeadType headType, Properties settings) {
        super(settings);
        this.headType = headType;
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, 0));
    }

    public ModHeadType getHeadType() {
        return headType;
    }

    @Override
    protected MapCodec<ModHeadBlock> codec() {
        return CODEC;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(ROTATION, RotationSegment.convertToSegment(ctx.getRotation() + 180.0F));
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(ROTATION, rotation.rotate(state.getValue(ROTATION), MAX_ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(ROTATION, mirror.mirror(state.getValue(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
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