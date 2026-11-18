package net.emanueljdf09.dtrhmod.block.custom;

import net.emanueljdf09.dtrhmod.util.components.ClientProgression;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockState;

public class FlowerPortal extends FlowerBlock {


    public FlowerPortal(Holder<MobEffect> stewEffect, float effectLengthInSeconds, Properties settings) {
        super(stewEffect, effectLengthInSeconds, settings);
    }

    private static boolean hasKilledMadHatter(Player player) {
        ProgressionComponent component;
        if (player.level().isClientSide()) {
            component = ClientProgression.get();
        } else if (player instanceof ServerPlayer serverPlayer && serverPlayer.getServer() != null) {
            component = PlayerProgressionManager.get(serverPlayer.getServer()).get(serverPlayer.getUUID());
        } else {
            return false;
        }
        return component != null && component.hasKilledMadHatter();
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (world.isClientSide) return;

        if (!(entity instanceof ServerPlayer player)) return;

        if (!hasKilledMadHatter(player)) return;

        if (player.hasEffect(ModEffects.SHRINK)) {
            TeleportUtil.startBlockTrance(player, pos, (ServerLevel) world, () -> {
                TeleportUtil.teleportToArena(player, pos, (ServerLevel) world);
            }, 40);
        }
    }
}

