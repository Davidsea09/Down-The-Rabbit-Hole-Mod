package net.emanueljdf09.dtrhmod.block.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class ModHeadBlockModel extends GeoModel<ModHeadBlockEntity> {

    @Override
    public ResourceLocation getModelResource(ModHeadBlockEntity animatable) {
        return animatable.getHeadType().getModelId();
    }

    @Override
    public ResourceLocation getTextureResource(ModHeadBlockEntity animatable) {
        return animatable.getHeadType().getTextureId();
    }

    @Override
    public ResourceLocation getAnimationResource(ModHeadBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/head.animation.json");
    }
}