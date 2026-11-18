package net.emanueljdf09.dtrhmod.block.entity;

import net.emanueljdf09.dtrhmod.block.ModBlockEntities;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.entity.custom.WhiteRabbitEntity;
import net.emanueljdf09.dtrhmod.util.ModComponents;
import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.emanueljdf09.dtrhmod.util.TeleportUtil;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.world.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import java.util.List;

public class RabbitHoleBlockEntity extends BlockEntity {
    private int scanCooldown = 0;

    public RabbitHoleBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RABBIT_HOLE_BLOCK_ENTITY, pos, state);
    }

    public void handlePlayerCollision(ServerPlayer player) {
        if (!player.isVisuallySwimming() || player.hasEffect(ModEffects.GROW)) return;

        ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
        ResourceKey<Level> currentDim = player.level().dimension();

        if (currentDim == ModDimensions.WONDERLAND_LEVEL_KEY) {
            handleReturnToSpawn(player);
            return;
        }

        if (currentDim == Level.OVERWORLD) {
            if (!component.hasDoneExterior()) {
                TeleportUtil.startBlockTrance(player, worldPosition, (ServerLevel) level, () -> {
                    TeleportUtil.teleport(player, ModDimensions.EXTERIOR_LEVEL_KEY, 3, 70, 2);
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
                    component.setExteriorDone(true);
                }, 40);
            } else {
                TeleportUtil.startBlockTrance(player, worldPosition, (ServerLevel) level, () -> {
                    TeleportUtil.teleportToWonderland(player);
                }, 40);
            }
        }
    }

    private void handleReturnToSpawn(ServerPlayer player) {
        BlockPos spawnPos = player.getRespawnPosition();
        ResourceKey<Level> spawnDim = player.getRespawnDimension();
        float spawnAngle = player.getRespawnAngle();

        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel targetWorld = server.getLevel(spawnDim != null ? spawnDim : Level.OVERWORLD);
        if (targetWorld == null) {
            targetWorld = server.overworld();
        }

        if (spawnPos != null) {
            player.teleportTo(targetWorld, spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, spawnAngle, 0.0F);
        } else {
            BlockPos worldSpawn = targetWorld.getSharedSpawnPos();
            player.teleportTo(targetWorld, worldSpawn.getX() + 0.5, worldSpawn.getY(), worldSpawn.getZ() + 0.5, spawnAngle, 0.0F);
        }
    }

    public static void tick(Level world, BlockPos pos, BlockState state, RabbitHoleBlockEntity blockEntity) {
        if (world.isClientSide()) {
            return;
        }

        blockEntity.scanCooldown++;
        if (blockEntity.scanCooldown < 20) return;
        blockEntity.scanCooldown = 0;

        ServerLevel serverWorld = (ServerLevel) world;

        if (world.dimension() != Level.OVERWORLD) return;

        AABB checkRabbitBox = new AABB(pos).inflate(100);
        List<WhiteRabbitEntity> nearbyRabbits = serverWorld.getEntitiesOfClass(
                WhiteRabbitEntity.class,
                checkRabbitBox,
                rabbit -> !rabbit.isReturning()
        );
        if (!nearbyRabbits.isEmpty()) return;

        AABB checkPlayerBox = new AABB(pos).inflate(100);
        List<ServerPlayer> nearbyPlayers = serverWorld.getEntitiesOfClass(
                ServerPlayer.class,
                checkPlayerBox,
                player -> true
        );

        for (ServerPlayer player : nearbyPlayers) {
            ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);

            if (!component.hasMetWhiteRabbit()) {
                WhiteRabbitEntity rabbit = ModEntities.WHITE_RABBIT.create(serverWorld);
                if (rabbit != null) {
                    int minDistance = 15;
                    int maxDistance = 40;

                    int offsetX = (world.random.nextBoolean() ? 1 : -1) * (minDistance + world.random.nextInt(maxDistance - minDistance));
                    int offsetZ = (world.random.nextBoolean() ? 1 : -1) * (minDistance + world.random.nextInt(maxDistance - minDistance));

                    BlockPos targetPos = pos.offset(offsetX, 0, offsetZ);

                    BlockPos spawnPos = serverWorld.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, targetPos);
                    rabbit.moveTo(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5, world.random.nextFloat() * 360F, 0);
                    rabbit.setRabbitHolePos(pos);
                    serverWorld.addFreshEntityWithPassengers(rabbit);
                    break;
                }
            }
        }
    }
}