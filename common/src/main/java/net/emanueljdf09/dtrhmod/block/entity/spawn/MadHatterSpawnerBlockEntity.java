package net.emanueljdf09.dtrhmod.block.entity.spawn;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.util.components.PlayerProgressionManager;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class MadHatterSpawnerBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public MadHatterSpawnerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.MAD_HATTER_SPAWNER_BE.get(), pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, MadHatterSpawnerBlockEntity blockEntity) {
        if (world.isClientSide) return;

        blockEntity.scanCooldown++;
        if (blockEntity.scanCooldown < 40) return;
        blockEntity.scanCooldown = 0;

        ServerLevel serverWorld = (ServerLevel) world;

        AABB checkHatterBox = new AABB(pos).inflate(32);
        List<MadHatterEntity> existingHatters = serverWorld.getEntitiesOfClass(
                MadHatterEntity.class,
                checkHatterBox,
                hatter -> true
        );
        if (!existingHatters.isEmpty()) return;

        AABB checkPlayerBox = new AABB(pos).inflate(16);
        List<ServerPlayer> nearbyPlayers = serverWorld.getEntitiesOfClass(
                ServerPlayer.class,
                checkPlayerBox,
                player -> true
        );

        if (nearbyPlayers.isEmpty()) return;

        MinecraftServer server = serverWorld.getServer();
        if (server == null) return;

        boolean shouldSpawn = false;
        for (ServerPlayer player : nearbyPlayers) {
            ProgressionComponent component = PlayerProgressionManager.get(server).get(player.getUUID());

            if (!component.hasKilledMadHatter()) {
                shouldSpawn = true;
                break;
            }
        }

        if (shouldSpawn) {
            MadHatterEntity hatter = ModEntities.MAD_HATTER.get().create(serverWorld);
            if (hatter != null) {

                double spawnX = pos.getX() + 0.5;
                double spawnY = pos.getY() + 1.0;
                double spawnZ = pos.getZ() + 0.5;

                hatter.moveTo(spawnX, spawnY, spawnZ, world.random.nextFloat() * 360F, 0);
                serverWorld.addFreshEntityWithPassengers(hatter);
            }
        }
    }
}
