package net.emanueljdf09.dtrhmod.block.custom.head;

import com.mojang.serialization.Codec;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;

public enum ModHeadType implements StringRepresentable {
    MAD_HATTER("mad_hatter", ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/block/mad_hatter_head.geo.json"), ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/mad_hatter.png")),
    CATERPILLAR("caterpillar", ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/block/caterpillar_head.geo.json"), ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/butterfly.png"));

    public static final Codec<ModHeadType> CODEC = StringRepresentable.fromEnum(ModHeadType::values);

    private final String name;
    private final ResourceLocation modelId;
    private final ResourceLocation textureId;

    ModHeadType(String name, ResourceLocation modelId, ResourceLocation textureId) {
        this.name = name;
        this.modelId = modelId;
        this.textureId = textureId;
    }

    public String getName() {
        return name;
    }

    public ResourceLocation getModelId() {
        return modelId;
    }

    public ResourceLocation getTextureId() {
        return textureId;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}