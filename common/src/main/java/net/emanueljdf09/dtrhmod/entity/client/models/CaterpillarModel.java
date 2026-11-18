package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.CaterpillarEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CaterpillarModel extends GeoModel<CaterpillarEntity> {

    private static final float HEAD_PITCH_FACTOR = 0.0F;

    private static String form(CaterpillarEntity entity) {
        return switch (entity.getPhase()) {
            case 2 -> "cocoon";
            case 3 -> "butterfly";
            default -> "caterpillar";
        };
    }

    @Override
    public ResourceLocation getModelResource(CaterpillarEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/" + form(entity) + ".geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(CaterpillarEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/" + form(entity) + ".png");
    }

    @Override
    public ResourceLocation getAnimationResource(CaterpillarEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/" + form(entity) + ".animation.json");
    }


    @Override
    public void setCustomAnimations(CaterpillarEntity animatable, long instanceId, AnimationState<CaterpillarEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        GeoBone head = getAnimationProcessor().getBone("head");
        if (head == null) return;

        EntityModelData data = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        float yaw = Mth.clamp(data.netHeadYaw(), -75.0F, 75.0F);
        head.setRotZ(head.getRotZ() + data.headPitch() * Mth.DEG_TO_RAD * HEAD_PITCH_FACTOR);
        head.setRotY(head.getRotY() + yaw * Mth.DEG_TO_RAD);
    }
}