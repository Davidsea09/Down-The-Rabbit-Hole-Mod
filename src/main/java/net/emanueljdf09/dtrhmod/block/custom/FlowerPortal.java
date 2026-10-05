package net.emanueljdf09.dtrhmod.block.custom;

import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class FlowerPortal extends Block {
    public FlowerPortal(Settings settings) {
        super(settings);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient) return;

        if (!(entity instanceof ServerPlayerEntity player)) return;

        if (player.hasStatusEffect(ModEffects.SHRINK)){
            TeleportUtil.startBlockTrance(player, pos, (ServerWorld) world, () -> {
                TeleportUtil.teleportToFlowerInstance(player, pos, (ServerWorld) world);
            }, 40);
        }
    }
}

