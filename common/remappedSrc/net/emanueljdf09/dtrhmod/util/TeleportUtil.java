package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.block.custom.mirror.MirrorBlock;
import net.emanueljdf09.dtrhmod.fabric.block.custom.mirror.MirrorType;
import net.emanueljdf09.dtrhmod.fabric.util.ModEffects;
import net.emanueljdf09.dtrhmod.fabric.util.PlayerReturnManager;
import net.emanueljdf09.dtrhmod.fabric.util.components.Mirror.MirrorComponent;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.world.biome.ModBiomes;
import net.emanueljdf09.dtrhmod.fabric.world.dimension.ModDimensions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.portal.DimensionTransition;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TeleportUtil {

    private static final Map<UUID, MirrorTranceData> ACTIVE_TRANCES = new HashMap<>();
    private static final Map<UUID, HatTranceData> ACTIVE_HAT_TRANCES = new HashMap<>();
    private static final Map<UUID, BlockTranceData> ACTIVE_BLOCK_TRANCES = new HashMap<>();

    private static final Set<UUID> TELEPORTING_PLAYERS = new HashSet<>();
    private static final Map<UUID, Integer> GROWTH_TIMER_MAP = new HashMap<>();

    private static final Map<UUID, Long> TELEPORT_COOLDOWNS = new HashMap<>();
    private static final long COOLDOWN_MS = 5000;

    public static boolean hasTeleportCooldown(ServerPlayer player) {
        long currentTime = System.currentTimeMillis();
        long lastTeleport = TELEPORT_COOLDOWNS.getOrDefault(player.getUUID(), 0L);
        return (currentTime - lastTeleport) < COOLDOWN_MS;
    }

    public static void updateTeleportCooldown(ServerPlayer player) {
        TELEPORT_COOLDOWNS.put(player.getUUID(), System.currentTimeMillis());
    }


    public static class HatTranceData {
        public final BlockPos hatPos;
        public final ResourceKey<Level> targetDim;
        public final boolean isInstance;
        public final ResourceLocation structureId;
        public final ResourceKey<Level> originDim;
        public final UUID ownerUuid;
        public int ticksRemaining;

        public HatTranceData(BlockPos hatPos, ResourceKey<Level> targetDim, boolean isInstance, ResourceLocation structureId, ResourceKey<Level> originDim, UUID ownerUuid, int durationTicks) {
            this.hatPos = hatPos;
            this.targetDim = targetDim;
            this.isInstance = isInstance;
            this.structureId = structureId;
            this.originDim = originDim;
            this.ownerUuid = ownerUuid;
            this.ticksRemaining = durationTicks;
        }
    }

    public static void startHatTrance(ServerPlayer player, BlockPos hatPos, ResourceKey<Level> targetDim, boolean isInstance, ResourceLocation structureId, ResourceKey<Level> originDim, UUID ownerUuid) {
        UUID uuid = player.getUUID();
        if (ACTIVE_HAT_TRANCES.containsKey(uuid)) return;

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 40, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 255, false, false, false));

        player.level().playSound(null, hatPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.4f, 0.5f);

        ACTIVE_HAT_TRANCES.put(uuid, new HatTranceData(hatPos, targetDim, isInstance, structureId, originDim, ownerUuid, 30));
    }

    public static void tickHatTrances(MinecraftServer server) {
        if (ACTIVE_HAT_TRANCES.isEmpty()) return;

        List<UUID> completed = new ArrayList<>();

        for (Map.Entry<UUID, HatTranceData> entry : ACTIVE_HAT_TRANCES.entrySet()) {
            UUID playerUuid = entry.getKey();
            HatTranceData data = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);

            if (player == null) {
                completed.add(playerUuid);
                continue;
            }

            data.ticksRemaining--;

            if (data.ticksRemaining <= 0) {
                if (data.isInstance) {
                    teleportToPlayerInstance(player, data.ownerUuid, data.targetDim, data.structureId, data.originDim, data.hatPos);
                } else {
                    teleportFromHat(player, data.targetDim, data.hatPos);
                }

                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                completed.add(playerUuid);
            }
        }

        for (UUID uuid : completed) {
            ACTIVE_HAT_TRANCES.remove(uuid);
        }
    }

    public static class BlockTranceData {
        public final BlockPos blockPos;
        public final ServerLevel sourceWorld;
        public final Runnable onComplete;
        public int ticksRemaining;

        public BlockTranceData(BlockPos blockPos, ServerLevel sourceWorld, Runnable onComplete, int durationTicks) {
            this.blockPos = blockPos;
            this.sourceWorld = sourceWorld;
            this.onComplete = onComplete;
            this.ticksRemaining = durationTicks;
        }
    }

    public static void startBlockTrance(ServerPlayer player, BlockPos blockPos, ServerLevel world, Runnable onComplete, int durationTicks) {
        UUID uuid = player.getUUID();
        if (ACTIVE_BLOCK_TRANCES.containsKey(uuid)) return;

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, durationTicks + 10, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, durationTicks + 10, 255, false, false, false));

        world.playSound(null, blockPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.4f, 0.5f);

        ACTIVE_BLOCK_TRANCES.put(uuid, new BlockTranceData(blockPos, world, onComplete, durationTicks));
    }

    public static void tickBlockTrances(MinecraftServer server) {
        if (ACTIVE_BLOCK_TRANCES.isEmpty()) return;

        List<UUID> completedTrances = new ArrayList<>();

        for (Map.Entry<UUID, BlockTranceData> entry : ACTIVE_BLOCK_TRANCES.entrySet()) {
            UUID playerUuid = entry.getKey();
            BlockTranceData data = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);

            if (player == null) {
                completedTrances.add(playerUuid);
                continue;
            }

            data.ticksRemaining--;

            if (data.ticksRemaining <= 0) {
                if (data.onComplete != null) {
                    data.onComplete.run();
                }
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                completedTrances.add(playerUuid);
            }
        }

        for (UUID uuid : completedTrances) {
            ACTIVE_BLOCK_TRANCES.remove(uuid);
        }
    }

    public static class MirrorTranceData {
        public final BlockPos mirrorPos;
        public final ServerLevel sourceWorld;
        public int ticksRemaining;

        public MirrorTranceData(BlockPos mirrorPos, ServerLevel sourceWorld, int durationTicks) {
            this.mirrorPos = mirrorPos;
            this.sourceWorld = sourceWorld;
            this.ticksRemaining = durationTicks;
        }
    }

    public static boolean isPlayerInTranceAt(UUID playerUuid, BlockPos mirrorPos) {
        MirrorTranceData data = ACTIVE_TRANCES.get(playerUuid);
        return data != null && data.mirrorPos.equals(mirrorPos);
    }

    public static void teleport(ServerPlayer player, ResourceKey<Level> targetDim,
                                double x, double y, double z) {

        MinecraftServer server = player.getServer();
        if (server == null) return;

        ServerLevel world = server.getLevel(targetDim);
        if (world == null) return;

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0f;

        player.teleportTo(world, x, y, z, player.getYRot(), player.getXRot());


    }

    public static void teleportToPlayerInstance(
            ServerPlayer player,
            UUID ownerUuid,
            ResourceKey<Level> targetDimKey,
            ResourceLocation structureId,
            ResourceKey<Level> originDimKey,
            BlockPos originPos
    ) {
        if (TELEPORTING_PLAYERS.contains(player.getUUID())) return;
        TELEPORTING_PLAYERS.add(player.getUUID());

        MinecraftServer server = player.getServer();
        if (server == null) {
            TELEPORTING_PLAYERS.remove(player.getUUID());
            return;
        }

        ServerLevel destWorld = server.getLevel(targetDimKey);
        if (destWorld == null) {
            System.out.println("[DTRH ERROR] Could not find world for dimension: " + targetDimKey.location());
            return;
        }

        StorybookInstanceManager manager = StorybookInstanceManager.getServerState(server);

        manager.registerVisitorReturn(player.getUUID(), originDimKey, originPos);

        BlockPos instancePos = manager.getOrCreatePlayerInstance(
                server,
                ownerUuid,
                targetDimKey,
                structureId,
                originDimKey,
                originPos
        );

        System.out.println("[DTRH DEBUG] Teleporting player to: " + instancePos.toString() + " in " + targetDimKey.location());

        ChunkPos chunkPos = new ChunkPos(instancePos);
        destWorld.getChunkSource().addRegionTicket(
                TicketType.START,
                chunkPos,
                2,
                Unit.INSTANCE
        );

        teleport(player, targetDimKey, instancePos.getX() + 0.5, instancePos.getY() + 1.0, instancePos.getZ() + 0.5);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));

        server.execute(() -> {

            server.getLevel(targetDimKey).getServer().submit(() -> {

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TELEPORTING_PLAYERS.remove(player.getUUID());
                    }
                }, 1000);
            });
        });
    }

    public static void teleportToArena(ServerPlayer player, BlockPos flowerPos, ServerLevel originWorld) {
        if (TELEPORTING_PLAYERS.contains(player.getUUID())) return;
        TELEPORTING_PLAYERS.add(player.getUUID());

        MinecraftServer server = player.getServer();
        if (server == null) {
            TELEPORTING_PLAYERS.remove(player.getUUID());
            return;
        }

        ServerLevel targetWorld = server.getLevel(ModDimensions.LAND_OF_FLOWERS_LEVEL_KEY);
        if (targetWorld == null) {
            TELEPORTING_PLAYERS.remove(player.getUUID());
            return;
        }

        net.emanueljdf09.dtrhmod.util.PlayerReturnManager manager = net.emanueljdf09.dtrhmod.util.PlayerReturnManager.getServerState(server);
        manager.registerReturn(player.getUUID(), originWorld.dimension(), flowerPos);

        double targetX = 24.0 + 0.5;
        double targetY = 70.0;
        double targetZ = 45.0 + 0.5;

        ChunkPos chunkPos = new ChunkPos(new BlockPos(24, 70, 45));
        targetWorld.getChunkSource().addRegionTicket(
                TicketType.START,
                chunkPos,
                2,
                Unit.INSTANCE
        );

        player.teleportTo(
                targetWorld,
                targetX,
                targetY,
                targetZ,
                player.getYRot(),
                player.getXRot()
        );
        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0f;
        player.removeEffect(net.emanueljdf09.dtrhmod.util.ModEffects.SHRINK);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0, false, false, false));

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                TELEPORTING_PLAYERS.remove(player.getUUID());
            }
        }, 1000);
    }

    public static void checkAndHandleFlowerVoidReturn(ServerPlayer player) {
        if (player.level().dimension().equals(ModDimensions.LAND_OF_FLOWERS_LEVEL_KEY) && player.getY() < -5) {
            returnPlayerToOrigin(player);
        }
    }

    public static void checkGrowthPotionExit(ServerPlayer player) {
        UUID uuid = player.getUUID();

        if (!player.level().dimension().equals(ModDimensions.LAND_OF_FLOWERS_LEVEL_KEY)) {
            GROWTH_TIMER_MAP.remove(uuid);
            return;
        }

        if (player.hasEffect(net.emanueljdf09.dtrhmod.util.ModEffects.GROW)) {
            int currentTicks = GROWTH_TIMER_MAP.getOrDefault(uuid, 0) + 1;
            GROWTH_TIMER_MAP.put(uuid, currentTicks);

            if (currentTicks >= 60) {
                GROWTH_TIMER_MAP.remove(uuid);
                returnPlayerToOrigin(player);
                player.removeEffect(net.emanueljdf09.dtrhmod.util.ModEffects.GROW);
            }
        } else {
            GROWTH_TIMER_MAP.remove(uuid);
        }
    }

    public static void returnPlayerToOrigin(ServerPlayer player) {
        if (TELEPORTING_PLAYERS.contains(player.getUUID())) return;
        TELEPORTING_PLAYERS.add(player.getUUID());

        MinecraftServer server = player.getServer();
        if (server == null) {
            TELEPORTING_PLAYERS.remove(player.getUUID());
            return;
        }

        net.emanueljdf09.dtrhmod.util.PlayerReturnManager manager = net.emanueljdf09.dtrhmod.util.PlayerReturnManager.getServerState(server);
        net.emanueljdf09.dtrhmod.util.PlayerReturnManager.ReturnLocation returnLoc = manager.getReturnLocation(player.getUUID());

        if (returnLoc != null) {
            ServerLevel originWorld = server.getLevel(returnLoc.dimension());
            if (originWorld != null) {
                BlockPos pos = returnLoc.pos();

                player.teleportTo(
                        originWorld,
                        pos.getX() + 0.5,
                        pos.getY() + 1.0,
                        pos.getZ() + 0.5,
                        player.getYRot(),
                        player.getXRot()
                );
                player.setDeltaMovement(0, 0, 0);
                player.fallDistance = 0.0f;

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TELEPORTING_PLAYERS.remove(player.getUUID());
                    }
                }, 1000);
            }
        }
    }

    public static void teleportFromHat(ServerPlayer player, ResourceKey<Level> targetDim, BlockPos targetPos) {
        if (TELEPORTING_PLAYERS.contains(player.getUUID())) return;
        TELEPORTING_PLAYERS.add(player.getUUID());

        MinecraftServer server = player.getServer();
        if (server == null) {
            TELEPORTING_PLAYERS.remove(player.getUUID());
            return;
        }

        ServerLevel destWorld = server.getLevel(targetDim);
        if (destWorld == null) return;

        BlockPos finalTargetPos = targetPos;
        if (targetDim.equals(Level.END)) {
            finalTargetPos = new BlockPos(0, targetPos.getY() + 10, 0);
        }

        ChunkPos chunkPos = new ChunkPos(finalTargetPos);
        destWorld.getChunkSource().addRegionTicket(
                TicketType.START,
                chunkPos,
                2,
                Unit.INSTANCE
        );

        destWorld.getChunk(chunkPos.x, chunkPos.z);

        BlockPos surfacePos = findCustomSurface(destWorld, finalTargetPos.getX(), finalTargetPos.getZ(), 120);

        teleport(player, targetDim, surfacePos.getX() + 0.5, surfacePos.getY() + 1.0, surfacePos.getZ() + 0.5);
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));

        server.execute(() -> {

            server.getLevel(targetDim).getServer().submit(() -> {

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        TELEPORTING_PLAYERS.remove(player.getUUID());
                    }
                }, 1000);
            });
        });
    }

    public static void checkAndHandleVoidReturn(ServerPlayer player) {
        if (player.level().dimension().equals(ModDimensions.STORYBOOK_LEVEL_KEY) && player.getY() < -5) {
            MinecraftServer server = player.getServer();
            if (server == null) return;

            StorybookInstanceManager manager = StorybookInstanceManager.getServerState(server);

            StorybookInstanceManager.ReturnLocation returnLoc = manager.getReturnLocation(player.getUUID());

            if (returnLoc == null || (returnLoc.dimension().equals(Level.OVERWORLD) && returnLoc.pos().equals(new BlockPos(0, 64, 0)))) {
                double playerX = player.getX();
                int instanceIndex = (int) Math.floor((playerX + 2500) / 5000);

                for (Map.Entry<UUID, BlockPos> entry : manager.getPlayerInstancesMap().entrySet()) {
                    BlockPos instanceCenter = entry.getValue();
                    if (Math.abs(instanceCenter.getX() - playerX) < 2500) {
                        StorybookInstanceManager.ReturnLocation ownerReturn = manager.getReturnLocation(entry.getKey());
                        if (ownerReturn != null) {
                            returnLoc = ownerReturn;
                            break;
                        }
                    }
                }
            }

            if (returnLoc != null) {
                ServerLevel originWorld = server.getLevel(returnLoc.dimension());
                if (originWorld != null) {
                    BlockPos originPos = returnLoc.pos();
                    double spawnX = originPos.getX() + 0.5;

                    int safeY = originWorld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, originPos).getY();
                    if (safeY <= originWorld.getMinBuildHeight()) safeY = 80;

                    updateTeleportCooldown(player);
                    player.teleportTo(originWorld, spawnX, safeY + 1.0, originPos.getZ() - 1.5, player.getYRot(), player.getXRot());
                    player.setDeltaMovement(0, 0, 0);
                    player.fallDistance = 0.0f;
                    player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0));
                    return;
                }
            }

            ServerLevel overworld = server.overworld();
            if (overworld != null) {
                BlockPos worldSpawn = overworld.getSharedSpawnPos();
                int safeSpawnY = overworld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, worldSpawn).getY();

                updateTeleportCooldown(player);
                player.teleportTo(overworld, worldSpawn.getX() + 0.5, safeSpawnY + 1.0, worldSpawn.getZ() + 0.5, player.getYRot(), player.getXRot());
                player.setDeltaMovement(0, 0, 0);
                player.fallDistance = 0.0f;
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 60, 0));
            }
        }
    }

    public static void checkAndHandleWonderlandVoidReturn(ServerPlayer player) {
        if (!player.level().dimension().equals(ModDimensions.WONDERLAND_LEVEL_KEY) || player.getY() >= -5) return;
        if (player.getServer() == null) return;

            DimensionTransition target = player.findRespawnPositionAndUseSpawnBlock(true, DimensionTransition.DO_NOTHING);

        if (target.missingRespawnBlock()) {
            player.displayClientMessage(Component.translatable("block.minecraft.spawn_obstructed"), true);
        }

        updateTeleportCooldown(player);
        player.changeDimension(target);

        player.setDeltaMovement(Vec3.ZERO);
        player.fallDistance = 0.0f;
    }

    public static void teleportToWonderland(ServerPlayer player) {
        ProgressionComponent component = ModComponents.PROGRESSION_COMPONENT.get(player);
        BlockPos savedPos = component.getWonderlandSpawn();

        if (savedPos != null) {

            TeleportUtil.teleport(player, ModDimensions.WONDERLAND_LEVEL_KEY,
                    savedPos.getX() + 0.5, savedPos.getY() + 5, savedPos.getZ() + 0.5);
            player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));
        } else {

            BlockPos found = TeleportUtil.teleportToSafeBiomeWithStructure(
                    player,
                    ModDimensions.WONDERLAND_LEVEL_KEY,
                    ModBiomes.TULGEY_WOOD,
                    null,
                    ModBiomes.VALE_OF_TEARS
            );
            component.setWonderlandSpawn(found);
        }
    }

    private static BlockPos locateBiomePos(ServerLevel world, ResourceKey<Biome> biomeKey) {
        BlockPos searchOrigin = new BlockPos(8, 64, 8);

        var result = world.findClosestBiome3d(holder -> holder.unwrapKey().map(key -> key.equals(biomeKey)).orElse(false), searchOrigin, 10000, 32, 64);

        if (result != null) {
            BlockPos initialPos = result.getFirst();

           BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
            for (int x = -48; x <= 48; x += 16) {
                for (int z = -48; z <= 48; z += 16) {
                    mutable.set(initialPos.getX() + x, 64, initialPos.getZ() + z);
                    var currentBiomeHolder = world.getBiome(mutable);

                    boolean matches = currentBiomeHolder.unwrapKey().map(key -> key.equals(biomeKey)).orElse(false);
                    if (matches) {
                        return mutable.immutable();
                    }
                }
            }
            return initialPos;
        }
        return null;
    }

    public static BlockPos teleportToSafeBiomeWithStructure(ServerPlayer player, ResourceKey<Level> targetDim, ResourceKey<Biome> primaryBiome, @Nullable ResourceLocation structureToSpawn, ResourceKey<Biome>... fallbacks) {
        MinecraftServer server = player.getServer();
        if (server == null) return null;

        ServerLevel destWorld = server.getLevel(targetDim);
        if (destWorld == null) return null;

        BlockPos targetPos = locateBiomePos(destWorld, primaryBiome);

        if (targetPos == null && fallbacks != null) {
            for (ResourceKey<Biome> fallbackKey : fallbacks) {
                targetPos = locateBiomePos(destWorld, fallbackKey);
                if (targetPos != null) break;
            }
        }

        if (targetPos == null) {
            targetPos = new BlockPos(0, 80, 0);
        }

        ChunkPos chunkPos = new ChunkPos(targetPos);
        destWorld.getChunkSource().addRegionTicket(TicketType.START, chunkPos, 2, Unit.INSTANCE);
        destWorld.getChunk(chunkPos.x, chunkPos.z);

        BlockPos surfacePos = findCustomSurface(destWorld, targetPos.getX(), targetPos.getZ(), 120);
        System.out.println("[DTRH DEBUG] Helper Scanner Found Surface at: " + surfacePos);

       clearLogsAndLeavesInRadius(destWorld, surfacePos, 1, 20);

        if (structureToSpawn != null) {
            StructureTemplateManager templateManager = server.getStructureManager();
            Optional<StructureTemplate> templateOpt = templateManager.get(structureToSpawn);

            if (templateOpt.isPresent()) {
                StructurePlaceSettings placementData = new StructurePlaceSettings();
                templateOpt.get().placeInWorld(destWorld, surfacePos, surfacePos, placementData, destWorld.getRandom(), 3);
            }
        }

        int spawnOffset = 10;
        double spawnX = surfacePos.getX() + 0.5;
        double spawnY = surfacePos.getY() + 1.0 + spawnOffset;
        double spawnZ = surfacePos.getZ() + 0.5;

        player.teleportTo(destWorld, spawnX, spawnY, spawnZ, player.getYRot(), player.getXRot());
        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 600, 0));

        return surfacePos;
    }

    public static void clearLogsAndLeavesInRadius(ServerLevel world, BlockPos center, int horizontalRadius, int verticalHeight) {
        System.out.println("[DTRH DEBUG] Clearing logs and leaves around: " + center);

        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        int logsRemoved = 0;
        int leavesRemoved = 0;

        for (int x = -horizontalRadius; x <= horizontalRadius; x++) {
            for (int z = -horizontalRadius; z <= horizontalRadius; z++) {
                for (int y = 0; y <= verticalHeight; y++) {
                    mutable.set(center.getX() + x, center.getY() + y, center.getZ() + z);

                    BlockState state = world.getBlockState(mutable);

                    if (state.is(BlockTags.LOGS)) {
                        world.removeBlock(mutable, false);
                        logsRemoved++;
                    } else if (state.is(BlockTags.LEAVES)) {
                        world.removeBlock(mutable, false);
                        leavesRemoved++;
                    }
                }
            }
        }

        System.out.println("[DTRH DEBUG] Clear finished! Logs removed: " + logsRemoved + ", Leaves removed: " + leavesRemoved);
    }

    public static BlockPos findCustomSurface(ServerLevel world, int x, int z, int startY) {
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos(x, startY, z);

        while (scanPos.getY() > world.getMinBuildHeight()) {
            BlockState state = world.getBlockState(scanPos);

            if (world.dimension() == Level.END) {
                if (state.isSolid() && state.is(Blocks.END_STONE) ) {
                    BlockState blockAbove = world.getBlockState(scanPos.above());
                    if (blockAbove.isAir()) {
                        return scanPos.immutable();
                    }
                }
            }

            if (state.isSolid() && !state.is(BlockTags.LEAVES) && !state.is(BlockTags.LOGS) && !state.is(Blocks.BEDROCK) ) {
                BlockState blockAbove = world.getBlockState(scanPos.above());
                if (blockAbove.isAir()) {
                    return scanPos.immutable();
                }
            }

            scanPos.move(Direction.DOWN);
        }

        return new BlockPos(x, 80, z);
    }


    public static void startMirrorTrance(ServerPlayer player, BlockPos clickedPos, ServerLevel world) {
        UUID uuid = player.getUUID();
        if (ACTIVE_TRANCES.containsKey(uuid)) return;

        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 100, 0, false, false, false));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 255, false, false, false));

        world.playSound(null, clickedPos, SoundEvents.PORTAL_TRAVEL, SoundSource.BLOCKS, 0.4f, 0.5f);

        MinecraftServer server = player.getServer();
        if (server != null) {
            ResourceKey<Level> targetDimKey = (world.dimension() == Level.OVERWORLD) ? ModDimensions.WONDERLAND_LEVEL_KEY : Level.OVERWORLD;
            ServerLevel targetWorld = server.getLevel(targetDimKey);
            if (targetWorld != null) {
                targetWorld.getChunkSource().addRegionTicket(
                        TicketType.START,
                        new ChunkPos(clickedPos),
                        2,
                        Unit.INSTANCE
                );
            }
        }

        ACTIVE_TRANCES.put(uuid, new MirrorTranceData(clickedPos, world, 60));
    }

    public static void tickMirrorTrances(MinecraftServer server) {
        if (ACTIVE_TRANCES.isEmpty()) return;

        List<UUID> completedTrances = new ArrayList<>();

        for (Map.Entry<UUID, MirrorTranceData> entry : ACTIVE_TRANCES.entrySet()) {
            UUID playerUuid = entry.getKey();
            MirrorTranceData data = entry.getValue();
            ServerPlayer player = server.getPlayerList().getPlayer(playerUuid);

            if (player == null) {
                completedTrances.add(playerUuid);
                continue;
            }

            data.ticksRemaining--;

            if (data.ticksRemaining <= 0) {
                handleMirrorTeleport(player, data.mirrorPos, data.sourceWorld);
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                completedTrances.add(playerUuid);
            }
        }

        for (UUID uuid : completedTrances) {
            ACTIVE_TRANCES.remove(uuid);
        }
    }

    public static void handleMirrorTeleport(ServerPlayer player, BlockPos clickedPos, ServerLevel sourceWorld) {
        ResourceKey<Level> currentDimKey = sourceWorld.dimension();

        ServerLevel overworld = player.getServer().getLevel(Level.OVERWORLD);
        ServerLevel wonderland = player.getServer().getLevel(ModDimensions.WONDERLAND_LEVEL_KEY);
        if (overworld == null || wonderland == null) return;

        MirrorComponent overworldNetwork = ModComponents.MIRROR_COMPONENT.get(overworld);
        MirrorComponent wonderlandNetwork = ModComponents.MIRROR_COMPONENT.get(wonderland);

        MirrorComponent currentNetwork = (currentDimKey == Level.OVERWORLD) ? overworldNetwork : wonderlandNetwork;
        Optional<BlockPos> destinationOpt = currentNetwork.getDestination(clickedPos);

        ResourceKey<Level> targetDim = (currentDimKey == Level.OVERWORLD) ? ModDimensions.WONDERLAND_LEVEL_KEY : Level.OVERWORLD;
        ServerLevel targetWorld = (currentDimKey == Level.OVERWORLD) ? wonderland : overworld;

        BlockPos targetPos;

        if (destinationOpt.isPresent()) {
            targetPos = destinationOpt.get();
            teleportPlayerToMirrorFront(player, targetWorld, targetPos);
            return;
        }

        BlockPos existingMirrorNearby = findMirrorInRadiusColumn(targetWorld, clickedPos, 8);
        if (existingMirrorNearby != null) {
            targetPos = existingMirrorNearby;

            overworldNetwork.linkMirrors((currentDimKey == Level.OVERWORLD) ? clickedPos : targetPos, (currentDimKey == Level.OVERWORLD) ? targetPos : clickedPos);
            wonderlandNetwork.linkMirrors((currentDimKey == Level.OVERWORLD) ? targetPos : clickedPos, (currentDimKey == Level.OVERWORLD) ? clickedPos : targetPos);

            teleportPlayerToMirrorFront(player, targetWorld, targetPos);
            return;
        }

        BlockState sourceState = sourceWorld.getBlockState(clickedPos);
        MirrorType sourceMirrorType = MirrorType.wonderland;
        Direction mirrorFacing = Direction.NORTH;

        if (sourceState.is(ModBlocks.MIRROR_BLOCK)) {
            sourceMirrorType = sourceState.getValue(MirrorBlock.TYPE);
            mirrorFacing = sourceState.getValue(MirrorBlock.FACING);
        }

        int spawnY = targetWorld.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clickedPos).getY();

        if (spawnY <= targetWorld.getMinBuildHeight()) {
            spawnY = (targetDim == Level.OVERWORLD) ? 70 : 90;
        }
        targetPos = new BlockPos(clickedPos.getX(), spawnY, clickedPos.getZ());

        BlockPos actualExitMirrorPos = targetPos;

        if (sourceMirrorType == MirrorType.structure) {
            MinecraftServer server = player.getServer();
            if (server != null) {
                StructureTemplateManager templateManager = server.getStructureManager();

                ResourceLocation targetTemplateId = (targetDim == Level.OVERWORLD)
                        ? ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "portal/mirror_room_ruined")
                        : ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "portal/mirror_room_complete");

                Optional<StructureTemplate> templateOpt = templateManager.get(targetTemplateId);

                if (templateOpt.isPresent()) {
                    Rotation targetRotation = Rotation.NONE;
                    switch (mirrorFacing) {
                        case SOUTH -> targetRotation = Rotation.CLOCKWISE_180;
                        case WEST -> targetRotation = Rotation.COUNTERCLOCKWISE_90;
                        case EAST -> targetRotation = Rotation.CLOCKWISE_90;
                    }

                    StructurePlaceSettings placementData = new StructurePlaceSettings().setRotation(targetRotation);
                    templateOpt.get().placeInWorld(targetWorld, targetPos, targetPos, placementData, targetWorld.getRandom(), 3);

                    boolean mirrorFound = false;
                    BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

                    for (int x = -8; x < 8; x++) {
                        for (int y = -2; y < 10; y++) {
                            for (int z = -8; z < 8; z++) {
                                mutablePos.set(targetPos.getX() + x, targetPos.getY() + y, targetPos.getZ() + z);

                                BlockState checkState = targetWorld.getBlockState(mutablePos);
                                if (checkState.is(ModBlocks.MIRROR_BLOCK) && checkState.getValue(MirrorBlock.HALF) == DoubleBlockHalf.LOWER) {
                                    actualExitMirrorPos = mutablePos.immutable();
                                    mirrorFound = true;
                                    break;
                                }
                            }
                            if (mirrorFound) break;
                        }
                        if (mirrorFound) break;
                    }

                    BlockState exitLowerState = targetWorld.getBlockState(actualExitMirrorPos);
                    BlockState exitUpperState = targetWorld.getBlockState(actualExitMirrorPos.above());

                    if (exitLowerState.is(ModBlocks.MIRROR_BLOCK)) {
                        targetWorld.setBlock(actualExitMirrorPos, exitLowerState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), 3);
                    }
                    if (exitUpperState.is(ModBlocks.MIRROR_BLOCK)) {
                        targetWorld.setBlock(actualExitMirrorPos.above(), exitUpperState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), 3);
                    }
                }
            }
        }

        else {
            BlockState lowerState = ModBlocks.MIRROR_BLOCK.getDefaultState()
                    .with(MirrorBlock.HALF, DoubleBlockHalf.LOWER)
                    .with(MirrorBlock.TYPE, MirrorType.wonderland)
                    .with(MirrorBlock.FACING, mirrorFacing);

            targetWorld.setBlock(targetPos, lowerState, 3);
            targetWorld.setBlock(targetPos.above(), lowerState.setValue(MirrorBlock.HALF, DoubleBlockHalf.UPPER), 3);

            actualExitMirrorPos = targetPos;
        }

        BlockState sourceLowerState = sourceWorld.getBlockState(clickedPos);
        BlockState sourceUpperState = sourceWorld.getBlockState(clickedPos.above());
        if (sourceLowerState.is(ModBlocks.MIRROR_BLOCK)) {
            sourceWorld.setBlock(clickedPos, sourceLowerState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), 3);
        }
        if (sourceUpperState.is(ModBlocks.MIRROR_BLOCK)) {
            sourceWorld.setBlock(clickedPos.above(), sourceUpperState.setValue(MirrorBlock.TYPE, MirrorType.wonderland), 3);
        }

        overworldNetwork.linkMirrors((currentDimKey == Level.OVERWORLD) ? clickedPos : actualExitMirrorPos, (currentDimKey == Level.OVERWORLD) ? actualExitMirrorPos : clickedPos);
        wonderlandNetwork.linkMirrors((currentDimKey == Level.OVERWORLD) ? actualExitMirrorPos : clickedPos, (currentDimKey == Level.OVERWORLD) ? clickedPos : actualExitMirrorPos);

        targetPos = actualExitMirrorPos;
        teleportPlayerToMirrorFront(player, targetWorld, targetPos);
    }

    private static void teleportPlayerToMirrorFront(ServerPlayer player, ServerLevel targetWorld, BlockPos targetPos) {
        BlockState targetState = targetWorld.getBlockState(targetPos);
        Direction facing = Direction.NORTH;

        if (targetState.is(ModBlocks.MIRROR_BLOCK)) {
            facing = targetState.getValue(MirrorBlock.FACING);
        }

        double teleportX = targetPos.getX() + 0.5;
        double teleportY = targetPos.getY();
        double teleportZ = targetPos.getZ() + 0.5;
        float targetYaw = player.getYRot();

        switch (facing) {
            case NORTH -> { teleportZ -= 1.2; targetYaw = 180.0F; }
            case SOUTH -> { teleportZ += 1.2; targetYaw = 0.0F; }
            case WEST  -> { teleportX -= 1.2; targetYaw = 90.0F; }
            case EAST  -> { teleportX += 1.2; targetYaw = 270.0F; }
        }

        MinecraftServer server = player.getServer();
        if (server != null) {
            player.teleportTo(targetWorld, teleportX, teleportY, teleportZ, targetYaw, player.getXRot());
        }

        player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 200, 0));
    }

    @Nullable
    private static BlockPos findMirrorInRadiusColumn(ServerLevel targetWorld, BlockPos center, int radius) {
        MirrorComponent targetNetwork = ModComponents.MIRROR_COMPONENT.get(targetWorld);
        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        int minY = targetWorld.getMinBuildHeight();
        int maxY = targetWorld.getMaxBuildHeight();

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = minY; y < maxY; y++) {
                    mutablePos.set(center.getX() + x, y, center.getZ() + z);
                    BlockState state = targetWorld.getBlockState(mutablePos);

                    if (state.is(ModBlocks.MIRROR_BLOCK)) {
                        BlockPos lowerBasePos = (state.getValue(MirrorBlock.HALF) == DoubleBlockHalf.LOWER) ? mutablePos.immutable() : mutablePos.below().immutable();
                        BlockState verifiedState = targetWorld.getBlockState(lowerBasePos);

                        if (verifiedState.is(ModBlocks.MIRROR_BLOCK) && verifiedState.getValue(MirrorBlock.HALF) == DoubleBlockHalf.LOWER) {
                            MirrorType type = verifiedState.getValue(MirrorBlock.TYPE);
                            if (type == MirrorType.wonderland || type == MirrorType.structure) {
                                if (targetNetwork.getDestination(lowerBasePos).isEmpty()) {
                                    return lowerBasePos;
                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}

