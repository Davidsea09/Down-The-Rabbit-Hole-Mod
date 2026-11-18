package net.emanueljdf09.dtrhmod.util.components.Mirror;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;

public class MirrorComponentImpl implements MirrorComponent {

    private final Map<BlockPos, BlockPos> links = new HashMap<>();

    @Override
    public Optional<BlockPos> getDestination(BlockPos sourcePos) {
        return Optional.ofNullable(links.get(sourcePos));
    }

    @Override
    public void linkMirrors(BlockPos posA, BlockPos posB) {
        links.put(posA, posB);
        links.put(posB, posA);
    }

    @Override
    public void removeLink(BlockPos pos) {
        BlockPos destination = links.remove(pos);
        if (destination != null) {
            links.remove(destination);
        }
    }


    public void load(CompoundTag nbt) {
        links.clear();
        ListTag list = nbt.getList("Links", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag pair = list.getCompound(i);
            Optional<BlockPos> a = readPos(pair, "PosA");
            Optional<BlockPos> b = readPos(pair, "PosB");
            if (a.isPresent() && b.isPresent()) {
                links.put(a.get(), b.get());
            }
        }
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

    public void save(CompoundTag nbt) {
        ListTag list = new ListTag();

        Map<BlockPos, BlockPos> tracked = new HashMap<>(links);

        for (Map.Entry<BlockPos, BlockPos> entry : tracked.entrySet()) {
            CompoundTag pair = new CompoundTag();
            pair.put("PosA", NbtUtils.writeBlockPos(entry.getKey()));
            pair.put("PosB", NbtUtils.writeBlockPos(entry.getValue()));
            list.add(pair);
        }
        nbt.put("Links", list);
    }
}
