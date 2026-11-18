package net.emanueljdf09.dtrhmod.util.components;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerProgressionManager extends SavedData {
    private static final String ID = "dtrhmod_player_progression";

    private final Map<UUID, ProgressionComponentImpl> players = new HashMap<>();
    @Nullable
    private MinecraftServer server;

    public static PlayerProgressionManager get(MinecraftServer server) {
        ServerLevel overworld = server.overworld();
        PlayerProgressionManager manager = overworld.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(PlayerProgressionManager::new, PlayerProgressionManager::load, null), ID);
        manager.server = server;
        return manager;
    }

    public ProgressionComponentImpl get(UUID playerId) {
        return players.computeIfAbsent(playerId, this::create);
    }

    private ProgressionComponentImpl create(UUID playerId) {
        return new ProgressionComponentImpl(() -> onChanged(playerId));
    }

    /** A setter was called: save, and tell that player's client if they are online. */
    private void onChanged(UUID playerId) {
        setDirty();
        if (server != null) {
            ServerPlayer player = server.getPlayerList().getPlayer(playerId);
            ProgressionComponentImpl data = players.get(playerId);
            if (player != null && data != null) {
                ProgressionSync.send(player, data);
            }
        }
    }

    public static PlayerProgressionManager load(CompoundTag tag, HolderLookup.Provider registries) {
        PlayerProgressionManager manager = new PlayerProgressionManager();
        CompoundTag all = tag.getCompound("Players");
        for (String key : all.getAllKeys()) {
            try {
                UUID id = UUID.fromString(key);
                ProgressionComponentImpl data = manager.create(id);
                data.load(all.getCompound(key));
                manager.players.put(id, data);
            } catch (IllegalArgumentException ignored) {
            }
        }
        return manager;
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag all = new CompoundTag();
        for (Map.Entry<UUID, ProgressionComponentImpl> entry : players.entrySet()) {
            CompoundTag data = new CompoundTag();
            entry.getValue().save(data);
            all.put(entry.getKey().toString(), data);
        }
        tag.put("Players", all);
        return tag;
    }
}