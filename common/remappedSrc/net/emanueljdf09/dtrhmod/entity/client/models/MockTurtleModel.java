package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.MockTurtleEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MockTurtleModel extends GeoModel<MockTurtleEntity> {
    @Override
    public ResourceLocation getModelResource(MockTurtleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "geo/entity/mock_turtle.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MockTurtleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/mock_turtle.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MockTurtleEntity animatable) {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "animations/entity/mock_turtle.animation.json");
    }

    @Override
    public void setCustomAnimations(MockTurtleEntity animatable, long instanceId, AnimationState<MockTurtleEntity> animationState) {
        GeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
        }
    }
}
