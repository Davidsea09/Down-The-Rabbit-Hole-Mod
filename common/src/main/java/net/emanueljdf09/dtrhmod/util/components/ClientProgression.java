package net.emanueljdf09.dtrhmod.util.components;

import net.minecraft.nbt.CompoundTag;

public class ClientProgression {
    private static ProgressionComponentImpl data = new ProgressionComponentImpl(() -> { });

    private ClientProgression() { }

    public static ProgressionComponent get() {
        return data;
    }

    public static void receive(CompoundTag tag) {
        ProgressionComponentImpl fresh = new ProgressionComponentImpl(() -> { });
        fresh.load(tag);
        data = fresh;
    }

    public static void reset() {
        data = new ProgressionComponentImpl(() -> { });
    }
}
