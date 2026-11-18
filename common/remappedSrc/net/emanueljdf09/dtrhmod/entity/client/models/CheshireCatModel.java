package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.CheshireCatEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CheshireCatModel extends GeoModel<CheshireCatEntity> {
    @Override
    public ResourceLocation getModelResource(CheshireCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/cheshire_cat.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CheshireCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/cheshire_cat.png");
    }

    @Override
    public ResourceLocation getAnimationResource(CheshireCatEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/cheshire_cat.animation.json");
    }

    @Override
    public void setCustomAnimations(CheshireCatEntity animatable, long instanceId, AnimationState<CheshireCatEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
