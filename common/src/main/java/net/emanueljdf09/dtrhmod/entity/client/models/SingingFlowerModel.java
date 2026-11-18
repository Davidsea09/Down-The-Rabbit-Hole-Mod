package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class SingingFlowerModel extends GeoModel<SingingFlowerEntity> {

    @Override
    public ResourceLocation getModelResource(SingingFlowerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/singing_flower.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(SingingFlowerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/singing_flower_" + entity.getVariant() + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(SingingFlowerEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/singing_flower.animation.json");
    }

    @Override
    public void setCustomAnimations(SingingFlowerEntity animatable, long instanceId, AnimationState<SingingFlowerEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}