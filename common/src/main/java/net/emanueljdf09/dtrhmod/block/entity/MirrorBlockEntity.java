package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorBlock;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorType;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import java.util.UUID;


public class MirrorBlockEntity extends BlockEntity {

    public MirrorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MIRROR_BLOCK_ENTITY.get(), pos, state);
    }

    public boolean isPlayerInTrance(UUID playerUuid) {
        return TeleportUtil.isPlayerInTranceAt(playerUuid, this.worldPosition);
    }

    public void activateWonderlandPortal(Level world, BlockState lowerState, BlockPos lowerPos) {
        BlockPos upperPos = lowerPos.above();
        BlockState upperState = world.getBlockState(upperPos);

        world.setBlock(lowerPos, lowerState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), Block.UPDATE_ALL);
        if (upperState.is(lowerState.getBlock())) {
            world.setBlock(upperPos, upperState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), Block.UPDATE_ALL);
        }
        world.playSound(null, lowerPos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    public void startTeleportSequence(ServerPlayer serverPlayer, ServerLevel serverWorld) {
        TeleportUtil.startMirrorTrance(serverPlayer, this.worldPosition, serverWorld);
    }


}
