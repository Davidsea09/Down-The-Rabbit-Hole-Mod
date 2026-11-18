package net.emanueljdf09.dtrhmod.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


public class StorybookInstanceManager extends SavedData {
    private static final int INSTANCE_SPACING = 5000;
    private int nextInstanceIndex = 0;
    private final Map<UUID, BlockPos> playerInstances = new HashMap<>();

    public record ReturnLocation(ResourceKey<Level> dimension, BlockPos pos) {}
    private final Map<UUID, ReturnLocation> playerReturnLocations = new HashMap<>();

    private static final Factory<StorybookInstanceManager> TYPE = new Factory<>(
            StorybookInstanceManager::new,
            StorybookInstanceManager::readNbt,
            null
    );

    public static StorybookInstanceManager getServerState(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return new StorybookInstanceManager();
        }

        return overworld.getDataStorage().computeIfAbsent(TYPE,   "storybook_instances");
    }


    public void registerVisitorReturn(UUID playerUuid, ResourceKey<Level> originDimKey, BlockPos originPos) {
        playerReturnLocations.put(playerUuid, new ReturnLocation(originDimKey, originPos));
        setDirty();
    }

    public Map<UUID, BlockPos> getPlayerInstancesMap() {
        return playerInstances;
    }

    public BlockPos getOrCreatePlayerInstance(
            MinecraftServer server,
            UUID ownerUuid,
            ResourceKey<Level> targetDimKey,
            ResourceLocation structureId,
            ResourceKey<Level> originDimKey,
            BlockPos originPos
    ) {
        registerVisitorReturn(ownerUuid, originDimKey, originPos);

        ServerLevel targetWorld = server.getLevel(targetDimKey);
        BlockPos spawnPos;

        if (playerInstances.containsKey(ownerUuid)) {
            spawnPos = playerInstances.get(ownerUuid);
        } else {
            int x = nextInstanceIndex * INSTANCE_SPACING;
            int y = 64;
            int z = 0;
            spawnPos = new BlockPos(x, y, z);

            nextInstanceIndex++;
            playerInstances.put(ownerUuid, spawnPos);
        }

        if (targetWorld != null) {
            generateStructure(server, targetWorld, spawnPos, structureId);
        }

        setDirty();
        return spawnPos;
    }

    public ReturnLocation getReturnLocation(UUID ownerUuid) {
        return playerReturnLocations.getOrDefault(
                ownerUuid,
                new ReturnLocation(Level.OVERWORLD, new BlockPos(0, 64, 0))
        );
    }

    private void generateStructure(
            MinecraftServer server,
            ServerLevel world,
            BlockPos pos,
            ResourceLocation structureId
    ) {
        StructureTemplateManager templateManager = server.getStructureManager();
        Optional<StructureTemplate> templateOpt = templateManager.get(structureId);

        if (templateOpt.isPresent()) {
            System.out.println("[DTRH SUCCESS] Placed structure: " + structureId + " at " + pos);

            StructurePlaceSettings placementData = new StructurePlaceSettings()
                    .setRotation(Rotation.NONE)
                    .setIgnoreEntities(false);

            templateOpt.get().placeInWorld(world, pos, pos, placementData, world.getRandom(), 3);
        } else {
            System.err.println("[DTRH ERROR] Missing structure file for ID: " + structureId);
            System.err.println("[DTRH ERROR] File path should be: assets/" + structureId.getNamespace() + "/structures/" + structureId.getPath() + ".nbt");
        }
    }

    public static StorybookInstanceManager readNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        StorybookInstanceManager manager = new StorybookInstanceManager();
        manager.nextInstanceIndex = nbt.getInt("NextIndex");

        CompoundTag playersNbt = nbt.getCompound("PlayerInstances");
        for (String key : playersNbt.getAllKeys()) {
            try {
                UUID uuid = UUID.fromString(key);
                long posLong = playersNbt.getLong(key);
                manager.playerInstances.put(uuid, BlockPos.of(posLong));
            } catch (IllegalArgumentException ignored) {
            }
        }

        if (nbt.contains("ReturnLocations")) {
            CompoundTag returnNbt = nbt.getCompound("ReturnLocations");
            for (String key : returnNbt.getAllKeys()) {
                try {
                    UUID uuid = UUID.fromString(key);
                    CompoundTag entryData = returnNbt.getCompound(key);

                    String dimStr = entryData.getString("Dim");
                    ResourceKey<Level> dimKey = ResourceKey.create(
                            Registries.DIMENSION,
                            ResourceLocation.parse(dimStr.isEmpty() ? "minecraft:overworld" : dimStr)
                    );
                    BlockPos pos = BlockPos.of(entryData.getLong("Pos"));

                    manager.playerReturnLocations.put(uuid, new ReturnLocation(dimKey, pos));
                } catch (Exception ignored) {
                }
            }
        }

        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        nbt.putInt("NextIndex", nextInstanceIndex);

        CompoundTag playersNbt = new CompoundTag();
        for (Map.Entry<UUID, BlockPos> entry : playerInstances.entrySet()) {
            playersNbt.putLong(entry.getKey().toString(), entry.getValue().asLong());
        }
        nbt.put("PlayerInstances", playersNbt);

        CompoundTag returnNbt = new CompoundTag();
        for (Map.Entry<UUID, ReturnLocation> entry : playerReturnLocations.entrySet()) {
            CompoundTag entryData = new CompoundTag();
            entryData.putString("Dim", entry.getValue().dimension().location().toString());
            entryData.putLong("Pos", entry.getValue().pos().asLong());
            returnNbt.put(entry.getKey().toString(), entryData);
        }
        nbt.put("ReturnLocations", returnNbt);

        return nbt;
    }
}