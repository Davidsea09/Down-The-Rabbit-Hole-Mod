package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class CheshireCatSpawnerBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public CheshireCatSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CHESHIRE_CAT_SPAWNER_BE, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, CheshireCatSpawnerBlockEntity blockEntity) {
        if (world.isClientSide) return;

        blockEntity.scanCooldown++;
        if (blockEntity.scanCooldown < 80) return;
        blockEntity.scanCooldown = 0;

        ServerLevel serverWorld = (ServerLevel) world;
        AABB scanArea = new AABB(pos).inflate(80.0);

        List<CheshireCatEntity> existingCats = serverWorld.getEntitiesOfClass(
                CheshireCatEntity.class,
                scanArea,
                c -> true
        );
        if (!existingCats.isEmpty()) return;

        AABB playerScanArea = new AABB(pos).inflate(32.0);
        List<ServerPlayer> nearbyPlayers = serverWorld.getEntitiesOfClass(
                ServerPlayer.class,
                playerScanArea,
                player -> true
        );

        if (nearbyPlayers.isEmpty()) return;

        CheshireCatEntity cat = ModEntities.CHESHIRE_CAT.create(serverWorld);
        if (cat != null) {
            cat.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            cat.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(pos), MobSpawnType.STRUCTURE, null);
            serverWorld.addFreshEntity(cat);
        }
    }
}