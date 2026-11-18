package net.emanueljdf09.dtrhmod.block.custom.mirror;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

public enum MirrorType implements StringRepresentable {
    normal("normal"),
    structure("structure"),
    wonderland("wonderland");

    public static final Codec<MirrorType> CODEC = StringRepresentable.fromEnum(MirrorType::values);

    private final String name;

    MirrorType(String name) {
        this.name = name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}