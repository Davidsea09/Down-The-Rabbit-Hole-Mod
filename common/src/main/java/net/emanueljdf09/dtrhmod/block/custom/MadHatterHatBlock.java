package net.emanueljdf09.dtrhmod.block.custom;

import com.mojang.serialization.MapCodec;
import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.entity.MadHatterHatBlockEntity;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class MadHatterHatBlock extends BaseEntityBlock implements EntityBlock {
    public static final MapCodec<MadHatterHatBlock> CODEC = simpleCodec(MadHatterHatBlock::new);
    public static final EnumProperty<HatState> STATE = EnumProperty.create("state", HatState.class);

    private static final VoxelShape IDLE_SHAPE = Block.box(3.0, 0.0, 3.0, 13.0, 10.0, 13.0);

    public MadHatterHatBlock(Properties settings) {
        super(settings);
        this.registerDefaultState(this.stateDefinition.any().setValue(STATE, HatState.IDLE));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);
        if (!world.isClientSide && placer instanceof Player player) {
            if (world.getBlockEntity(pos) instanceof MadHatterHatBlockEntity hatEntity) {
                hatEntity.setPortalOwnerUuid(player.getUUID());
            }
        }
    }

    @Override
    public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.is(newState.getBlock())) {
            if (!world.isClientSide) {
                BlockEntity be = world.getBlockEntity(pos);
                if (be instanceof MadHatterHatBlockEntity hatEntity) {
                    UUID ownerUuid = hatEntity.getPortalOwnerUuid();
                    ItemStack hatToRefund = new ItemStack(this);

                    if (ownerUuid != null && world.getServer() != null) {
                        Player owner = world.getServer().getPlayerList().getPlayer(ownerUuid);
                        if (owner != null) {
                            if (!owner.getInventory().add(hatToRefund)) {
                                popResource(world, pos, hatToRefund);
                            }
                        } else {
                            popResource(world, pos, hatToRefund);
                        }
                    } else {
                        popResource(world, pos, hatToRefund);
                    }
                }
            }
        }
        super.onRemove(state, world, pos, newState, moved);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(STATE);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return IDLE_SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!world.isClientSide && state.getValue(STATE) == HatState.PORTAL && entity instanceof ServerPlayer serverPlayer) {
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof MadHatterHatBlockEntity hatBe) {
                TeleportUtil.startHatTrance(
                        serverPlayer,
                        pos,
                        hatBe.getTargetDimension(),
                        hatBe.isInstanceDimension(),
                        hatBe.getStructureId(),
                        hatBe.getOriginDimension(),
                        hatBe.getPortalOwnerUuid()
                );
            }
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MadHatterHatBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.MAD_HATTER_HAT.get()
                ? (BlockEntityTicker<T>) (world1, pos, state1, blockEntity) -> MadHatterHatBlockEntity.tick(world1, pos, state1, (MadHatterHatBlockEntity) blockEntity)
                : null;
    }

    public enum HatState implements StringRepresentable {
        IDLE("idle"),
        PORTAL("portal");

        private final String name;
        HatState(String name) { this.name = name; }
        @Override public String getSerializedName() { return this.name; }
    }
}