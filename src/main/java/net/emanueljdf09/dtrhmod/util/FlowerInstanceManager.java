package net.emanueljdf09.dtrhmod.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class FlowerInstanceManager extends PersistentState {
    private static final int INSTANCE_SPACING = 5000;
    private int nextInstanceIndex = 0;

    private final Map<Long, BlockPos> flowerInstances = new HashMap<>();

    public record ReturnLocation(RegistryKey<World> dimension, BlockPos pos) {}
    private final Map<UUID, ReturnLocation> playerReturnLocations = new HashMap<>();

    public static FlowerInstanceManager getServerState(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return new FlowerInstanceManager();
        }

        return overworld.getPersistentStateManager().getOrCreate(
                FlowerInstanceManager::readNbt,
                FlowerInstanceManager::new,
                "flower_instances"
        );
    }

    public void registerVisitorReturn(UUID playerUuid, RegistryKey<World> originDimKey, BlockPos originPos) {
        playerReturnLocations.put(playerUuid, new ReturnLocation(originDimKey, originPos));
        markDirty();
    }

    public BlockPos getOrCreateFlowerInstance(
            MinecraftServer server,
            BlockPos flowerPos,
            RegistryKey<World> targetDimKey,
            Identifier structureId,
            RegistryKey<World> originDimKey,
            BlockPos originPos,
            UUID visitorUuid
    ) {
        registerVisitorReturn(visitorUuid, originDimKey, originPos);

        ServerWorld targetWorld = server.getWorld(targetDimKey);
        BlockPos spawnPos;
        long flowerKey = flowerPos.asLong();

        if (flowerInstances.containsKey(flowerKey)) {
            spawnPos = flowerInstances.get(flowerKey);
        } else {
            int x = nextInstanceIndex * INSTANCE_SPACING;
            int y = 64;
            int z = 0;
            spawnPos = new BlockPos(x, y, z);

            nextInstanceIndex++;
            flowerInstances.put(flowerKey, spawnPos);
        }

        if (targetWorld != null) {
            generateStructure(server, targetWorld, spawnPos, structureId);
        }

        markDirty();
        return spawnPos;
    }

    public ReturnLocation getReturnLocation(UUID ownerUuid) {
        return playerReturnLocations.getOrDefault(
                ownerUuid,
                new ReturnLocation(World.OVERWORLD, new BlockPos(0, 64, 0))
        );
    }

    private void generateStructure(
            MinecraftServer server,
            ServerWorld world,
            BlockPos pos,
            Identifier structureId
    ) {
        StructureTemplateManager templateManager = server.getStructureTemplateManager();
        Optional<StructureTemplate> templateOpt = templateManager.getTemplate(structureId);

        if (templateOpt.isPresent()) {
            StructurePlacementData placementData = new StructurePlacementData()
                    .setRotation(BlockRotation.NONE)
                    .setIgnoreEntities(false);

            templateOpt.get().place(world, pos, pos, placementData, world.getRandom(), 3);
        }
    }

    public static FlowerInstanceManager readNbt(NbtCompound nbt) {
        FlowerInstanceManager manager = new FlowerInstanceManager();
        manager.nextInstanceIndex = nbt.getInt("NextIndex");

        NbtCompound instancesNbt = nbt.getCompound("FlowerInstances");
        for (String key : instancesNbt.getKeys()) {
            try {
                long flowerLong = Long.parseLong(key);
                long posLong = instancesNbt.getLong(key);
                manager.flowerInstances.put(flowerLong, BlockPos.fromLong(posLong));
            } catch (NumberFormatException ignored) {}
        }

        if (nbt.contains("ReturnLocations")) {
            NbtCompound returnNbt = nbt.getCompound("ReturnLocations");
            for (String key : returnNbt.getKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    NbtCompound entryData = returnNbt.getCompound(key);

                    String dimStr = entryData.getString("Dim");
                    RegistryKey<World> dimKey = RegistryKey.of(
                            RegistryKeys.WORLD,
                            new Identifier(dimStr.isEmpty() ? "minecraft:overworld" : dimStr)
                    );
                    BlockPos pos = BlockPos.fromLong(entryData.getLong("Pos"));

                    manager.playerReturnLocations.put(uuid, new ReturnLocation(dimKey, pos));
                } catch (Exception ignored) {}
            }
        }

        return manager;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("NextIndex", nextInstanceIndex);

        NbtCompound instancesNbt = new NbtCompound();
        for (Map.Entry<Long, BlockPos> entry : flowerInstances.entrySet()) {
            instancesNbt.putLong(entry.getKey().toString(), entry.getValue().asLong());
        }
        nbt.put("FlowerInstances", instancesNbt);

        NbtCompound returnNbt = new NbtCompound();
        for (Map.Entry<UUID, ReturnLocation> entry : playerReturnLocations.entrySet()) {
            NbtCompound entryData = new NbtCompound();
            entryData.putString("Dim", entry.getValue().dimension().getValue().toString());
            entryData.putLong("Pos", entry.getValue().pos().asLong());
            returnNbt.put(entry.getKey().toString(), entryData);
        }
        nbt.put("ReturnLocations", returnNbt);

        return nbt;
    }
}
