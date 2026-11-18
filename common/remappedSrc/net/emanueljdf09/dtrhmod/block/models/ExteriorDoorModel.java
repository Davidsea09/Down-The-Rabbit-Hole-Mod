package net.emanueljdf09.dtrhmod.block.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.entity.ExteriorDoorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ExteriorDoorModel extends GeoModel<ExteriorDoorEntity> {
    @Override
    public ResourceLocation getModelResource(ExteriorDoorEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/block/exterior_door.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(ExteriorDoorEntity animatable) {
       return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/block/exterior_door.png");
    }

    @Override
    public ResourceLocation getAnimationResource(ExteriorDoorEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/block/exterior_door.animation.json");
    }
}
