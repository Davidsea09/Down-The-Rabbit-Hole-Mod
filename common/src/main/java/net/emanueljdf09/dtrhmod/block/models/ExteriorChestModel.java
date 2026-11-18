package net.emanueljdf09.dtrhmod.block.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.entity.ExteriorChestEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExteriorChestModel extends GeoModel<ExteriorChestEntity> {
    @Override
    public ResourceLocation getModelResource(ExteriorChestEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/block/exterior_chest.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExteriorChestEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/block/exterior_chest.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExteriorChestEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/block/exterior_chest.animation.json");
    }
}