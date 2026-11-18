package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.entity.ExteriorDoorEntity;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.block.*;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ExteriorDoor extends BaseEntityBlock {
    public static final MapCodec<ExteriorDoor> CODEC = simpleCodec(ExteriorDoor::new);


    protected static final VoxelShape CLOSED_SHAPE = makeClosedShape();
    protected static final VoxelShape OPEN_SHAPE = makeOpenShape();


    public ExteriorDoor(Properties settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExteriorDoorEntity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (world.getBlockEntity(pos) instanceof ExteriorDoorEntity doorEntity) {
            return doorEntity.onUse(player, player.swingingArm);
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {

        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player) {
            if (ModComponents.PROGRESSION_COMPONENT.get(player).hasOpenedExtDoor()) {
                return OPEN_SHAPE;
            }
        }
        return CLOSED_SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext && entityContext.getEntity() instanceof Player player) {
            ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
            if (component.hasOpenedExtDoor()) {
                return OPEN_SHAPE;
            }
        }
        return CLOSED_SHAPE;
    }

    public static VoxelShape makeClosedShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 1.0625, 0.8125, 1, 1.25, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 1, 0.8125, 0.125, 1.0625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 1, 0.8125, 1, 1.0625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.8125, 0.8125, 1.1875, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.25, 0.8125, 0.8125, 0.125, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(1, 0, 0.8125, 1.1875, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.25, 0, 0.8125, -0.0625, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.125, 0.8125, 0.875, 0.8125, 1.0625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 0, 0.875, 1, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.03125, 0.125, 0.8125, 0.34375, 0.8125, 0.875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.03125, 0.125, 0.8125, 0.34375, 0.8125, 0.875), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.375, 0.5625, 0.28125, 0.5625, 0.75), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.15625, 0.4375, 0.75, 0.21875, 0.5, 0.8125), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.09375, 0.125, 0.75, 0.28125, 0.3125, 0.8125), BooleanOp.OR);

        return shape;
    }

    public static VoxelShape makeOpenShape(){
        VoxelShape shape = Shapes.empty();
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 1.0625, 0.8125, 1, 1.25, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.0625, 1, 0.8125, 0.125, 1.0625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 1, 0.8125, 1, 1.0625, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(0.8125, 0.8125, 0.8125, 1.1875, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.25, 0.8125, 0.8125, 0.125, 1, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(1, 0, 0.8125, 1.1875, 0.8125, 1), BooleanOp.OR);
        shape = Shapes.joinUnoptimized(shape, Shapes.box(-0.25, 0, 0.8125, -0.0625, 0.8125, 1), BooleanOp.OR);

        return shape;
    }


}
