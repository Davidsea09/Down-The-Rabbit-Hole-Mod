package net.emanueljdf09.dtrhmod.block.custom.head;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.util.Identifier;

public enum ModHeadType {
    MAD_HATTER("mad_hatter", new Identifier(DownTheRabbitHole.MOD_ID, "geo/block/mad_hatter_head.geo.json"), new Identifier(DownTheRabbitHole.MOD_ID, "textures/entity/mad_hatter.png"));

    private final String name;
    private final Identifier modelId;
    private final Identifier textureId;

    ModHeadType(String name, Identifier modelId, Identifier textureId) {
        this.name = name;
        this.modelId = modelId;
        this.textureId = textureId;
    }

    public String getName() {
        return name;
    }

    public Identifier getModelId() {
        return modelId;
    }

    public Identifier getTextureId() {
        return textureId;
    }
}
