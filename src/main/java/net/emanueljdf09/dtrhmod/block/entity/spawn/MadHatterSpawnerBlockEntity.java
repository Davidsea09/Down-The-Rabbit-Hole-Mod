package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

import java.util.List;

public class MadHatterSpawnerBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public MadHatterSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAD_HATTER_SPAWNER_BE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, MadHatterSpawnerBlockEntity blockEntity) {
        if (world.isClient) return;

        blockEntity.scanCooldown++;
        if (blockEntity.scanCooldown < 40) return;
        blockEntity.scanCooldown = 0;

        ServerWorld serverWorld = (ServerWorld) world;

        Box checkHatterBox = new Box(pos).expand(32);
        List<MadHatterEntity> existingHatters = serverWorld.getEntitiesByClass(
                MadHatterEntity.class,
                checkHatterBox,
                hatter -> true
        );
        if (!existingHatters.isEmpty()) return;

        Box checkPlayerBox = new Box(pos).expand(16);
        List<ServerPlayerEntity> nearbyPlayers = serverWorld.getEntitiesByClass(
                ServerPlayerEntity.class,
                checkPlayerBox,
                player -> true
        );

        if (nearbyPlayers.isEmpty()) return;

        boolean shouldSpawn = false;
        for (ServerPlayerEntity player : nearbyPlayers) {
            ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);

            if (!component.hasKilledMadHatter()) {
                shouldSpawn = true;
                break;
            }
        }

        if (shouldSpawn) {
            MadHatterEntity hatter = ModEntities.MAD_HATTER.create(serverWorld);
            if (hatter != null) {

                double spawnX = pos.getX() + 0.5;
                double spawnY = pos.getY() + 1.0;
                double spawnZ = pos.getZ() + 0.5;

                hatter.refreshPositionAndAngles(spawnX, spawnY, spawnZ, world.random.nextFloat() * 360F, 0);
                serverWorld.spawnEntityAndPassengers(hatter);
            }
        }
    }
}
