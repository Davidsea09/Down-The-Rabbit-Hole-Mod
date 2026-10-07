package net.emanueljdf09.dtrhmod.util;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerReturnManager extends PersistentState {
    private final Map<UUID, ReturnLocation> playerReturnLocations = new HashMap<>();

    public record ReturnLocation(RegistryKey<World> dimension, BlockPos pos) {}

    public static PlayerReturnManager getServerState(MinecraftServer server) {
        ServerWorld overworld = server.getWorld(World.OVERWORLD);
        if (overworld == null) {
            return new PlayerReturnManager();
        }
        return overworld.getPersistentStateManager().getOrCreate(
                PlayerReturnManager::readNbt,
                PlayerReturnManager::new,
                "player_return_locations"
        );
    }

    public void registerReturn(UUID playerUuid, RegistryKey<World> originDimKey, BlockPos originPos) {
        playerReturnLocations.put(playerUuid, new ReturnLocation(originDimKey, originPos));
        markDirty();
    }

    public ReturnLocation getReturnLocation(UUID playerUuid) {
        return playerReturnLocations.getOrDefault(
                playerUuid,
                new ReturnLocation(World.OVERWORLD, new BlockPos(0, 64, 0))
        );
    }

    public static PlayerReturnManager readNbt(NbtCompound nbt) {
        PlayerReturnManager manager = new PlayerReturnManager();
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