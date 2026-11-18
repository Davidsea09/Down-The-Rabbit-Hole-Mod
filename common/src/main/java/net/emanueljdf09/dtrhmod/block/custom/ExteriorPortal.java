package net.emanueljdf09.dtrhmod.block.custom;

import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;

public class ExteriorPortal extends Block {
    public ExteriorPortal(Properties settings) {
        super(settings);
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) return;

        if (!(entity instanceof ServerPlayer player)) return;

        TeleportUtil.startBlockTrance(player, pos, (ServerLevel) world, () -> {
            TeleportUtil.teleportToWonderland(player);

            player.removeEffect(ModEffects.SHRINK);
        }, 40);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }
}
