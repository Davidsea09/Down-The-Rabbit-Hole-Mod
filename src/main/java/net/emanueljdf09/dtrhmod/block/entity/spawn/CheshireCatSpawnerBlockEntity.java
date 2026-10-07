package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.List;

public class CheshireCatSpawnerBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public CheshireCatSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHESHIRE_CAT_SPAWNER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CheshireCatSpawnerBlockEntity blockEntity) {
        if (world.isClient) return;

        blockEntity.scanCooldown++;
        if (blockEntity.scanCooldown < 80) return;
        blockEntity.scanCooldown = 0;

        ServerWorld serverWorld = (ServerWorld) world;
        Box scanArea = new Box(pos).expand(80.0);

        List<CheshireCatEntity> existingCats = serverWorld.getEntitiesByClass(
                CheshireCatEntity.class,
                scanArea,
                c -> true
        );
        if (!existingCats.isEmpty()) return;

        Box playerScanArea = new Box(pos).expand(32.0);
        List<ServerPlayerEntity> nearbyPlayers = serverWorld.getEntitiesByClass(
                ServerPlayerEntity.class,
                playerScanArea,
                player -> true
        );

        if (nearbyPlayers.isEmpty()) return;

        CheshireCatEntity cat = ModEntities.CHESHIRE_CAT.create(serverWorld);
        if (cat != null) {
            cat.setPosition(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            cat.initialize(serverWorld, serverWorld.getLocalDifficulty(pos), SpawnReason.STRUCTURE, null, null);
            serverWorld.spawnEntity(cat);
        }
    }
}