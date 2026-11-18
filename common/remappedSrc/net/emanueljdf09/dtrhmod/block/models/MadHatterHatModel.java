package net.emanueljdf09.dtrhmod.block.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.entity.MadHatterHatBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MadHatterHatModel extends GeoModel<MadHatterHatBlockEntity> {

    @Override
    public ResourceLocation getModelResource(MadHatterHatBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/block/mad_hat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MadHatterHatBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/block/mad_hat.png");
    }

    public ResourceLocation getAnimationResource(MadHatterHatBlockEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/block/hat.animation.json");
    }

}
