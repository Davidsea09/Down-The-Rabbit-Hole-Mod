package net.emanueljdf09.dtrhmod.entity.spawners;

import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.WeepingPlayerEntity;
import net.emanueljdf09.dtrhmod.fabric.world.biome.ModBiomes;
import net.emanueljdf09.dtrhmod.fabric.world.dimension.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import java.util.List;
import java.util.Random;

public class WeepingPlayersSpawner {
    private static final int CHECK_INTERVAL = 200;
    private static int tickCounter = 0;

    public static void register() {
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (!world.getRegistryKey().equals(ModDimensions.WONDERLAND_LEVEL_KEY)) {
                return;
            }

            tickCounter++;
            if (tickCounter < CHECK_INTERVAL) return;
            tickCounter = 0;

            List<WeepingPlayerEntity> activeWeepingPlayers = world.getEntitiesByClass(
                    WeepingPlayerEntity.class,
                    new Box(-30000, -64, -30000, 30000, 320, 30000),
                    entity -> true
            );

            if (activeWeepingPlayers.isEmpty()) {
                world.getPlayers().forEach(player -> {
                    if (world.getBiome(player.getBlockPos()).matchesKey(ModBiomes.VALE_OF_TEARS)) {
                        spawnGroup(world, player.getBlockPos());
                    }
                });
            }
        });
    }

    private static void spawnGroup(ServerLevel world, BlockPos centerPos) {
        Random random = new Random();
        int spawnedCount = 0;

        for (int i = 0; i < 3; i++) {
            int offsetX = random.nextInt(32) - 16;
            int offsetZ = random.nextInt(32) - 16;
            BlockPos spawnPos = world.getHeightmapPos(Heightmap.Types.WORLD_SURFACE_WG, centerPos.offset(offsetX, 0, offsetZ));

            if (world.getBiome(spawnPos).is(ModBiomes.VALE_OF_TEARS)) {
                WeepingPlayerEntity weepingPlayer = ModEntities.WEEPING_PLAYER.create(world);
                if (weepingPlayer != null) {
                    weepingPlayer.refreshPositionAndAngles(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, random.nextFloat() * 360f, 0.0f);
                    world.addFreshEntity(weepingPlayer);
                    spawnedCount++;
                }
            }
        }
    }
}

