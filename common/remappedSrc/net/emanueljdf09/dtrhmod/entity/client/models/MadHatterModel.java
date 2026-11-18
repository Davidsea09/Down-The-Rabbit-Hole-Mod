package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.MadHatterEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MadHatterModel extends GeoModel<MadHatterEntity> {


    @Override
    public ResourceLocation getModelResource(MadHatterEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/mad_hatter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MadHatterEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/mad_hatter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MadHatterEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/mad_hatter.animation.json");
    }

    @Override
    public void setCustomAnimations(MadHatterEntity animatable, long instanceId, AnimationState<MadHatterEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }

}
