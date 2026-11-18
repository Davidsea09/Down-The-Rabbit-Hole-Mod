package net.emanueljdf09.dtrhmod.util.components.Mirror;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MirrorSavedData extends SavedData implements MirrorComponent {
    private static final String ID = "dtrhmod_mirrors";
    private final Map<BlockPos, BlockPos> links = new HashMap<>();

    public static MirrorSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                new SavedData.Factory<>(MirrorSavedData::new, MirrorSavedData::load, null),
                ID
        );
    }

    @Override
    public Optional<BlockPos> getDestination(BlockPos sourcePos) {
        return Optional.ofNullable(links.get(sourcePos));
    }

    @Override
    public void linkMirrors(BlockPos posA, BlockPos posB) {
        links.put(posA, posB);
        links.put(posB, posA);
        setDirty();
    }

    @Override
    public void removeLink(BlockPos pos) {
        BlockPos destination = links.remove(pos);
        if (destination != null) {
            links.remove(destination);
            setDirty();
        }
    }

    public static MirrorSavedData load(CompoundTag tag, HolderLookup.Provider registryLookup) {
        MirrorSavedData data = new MirrorSavedData();
        data.links.clear();
        ListTag list = tag.getList("Links", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag pair = list.getCompound(i);
            Optional<BlockPos> a = readPos(pair, "PosA");
            Optional<BlockPos> b = readPos(pair, "PosB");
            if (a.isPresent() && b.isPresent()) {
                data.links.put(a.get(), b.get());
            }
        }
        return data;
    }

    private static Optional<BlockPos> readPos(CompoundTag pair, String key) {
        Optional<BlockPos> pos = NbtUtils.readBlockPos(pair, key);
        if (pos.isPresent()) return pos;

        if (pair.contains(key, Tag.TAG_COMPOUND)) {
            CompoundTag old = pair.getCompound(key);
            return Optional.of(new BlockPos(old.getInt("X"), old.getInt("Y"), old.getInt("Z")));
        }
        return Optional.empty();
    }

    @Override
    public CompoundTag save(CompoundTag nbt, HolderLookup.Provider registryLookup) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, BlockPos> entry : links.entrySet()) {
            CompoundTag pair = new CompoundTag();
            pair.put("PosA", NbtUtils.writeBlockPos(entry.getKey()));
            pair.put("PosB", NbtUtils.writeBlockPos(entry.getValue()));
            list.add(pair);
        }
        nbt.put("Links", list);
        return nbt;
    }
}
