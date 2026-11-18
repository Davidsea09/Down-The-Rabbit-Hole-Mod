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
import net.minecraft.world.level.saveddata.SavedData;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerReturnManager extends SavedData {
    private final Map<UUID, ReturnLocation> playerReturnLocations = new HashMap<>();

    public record ReturnLocation(ResourceKey<Level> dimension, BlockPos pos) {}

    private static final Factory<PlayerReturnManager> TYPE = new Factory<>(
            PlayerReturnManager::new,
            PlayerReturnManager::readNbt,
            null
    );

    public static PlayerReturnManager getServerState(MinecraftServer server) {
        ServerLevel overworld = server.getLevel(Level.OVERWORLD);
        if (overworld == null) {
            return new PlayerReturnManager();
        }
        return overworld.getDataStorage().computeIfAbsent(TYPE, "player_return_locations");
    }

    public void registerReturn(UUID playerUuid, ResourceKey<Level> originDimKey, BlockPos originPos) {
        playerReturnLocations.put(playerUuid, new ReturnLocation(originDimKey, originPos));
        setDirty();
    }

    public ReturnLocation getReturnLocation(UUID playerUuid) {
        return playerReturnLocations.getOrDefault(
                playerUuid,
                new ReturnLocation(Level.OVERWORLD, new BlockPos(0, 64, 0))
        );
    }

    public static PlayerReturnManager readNbt(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        PlayerReturnManager manager = new PlayerReturnManager();
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
                } catch (Exception ignored) {}
            }
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
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