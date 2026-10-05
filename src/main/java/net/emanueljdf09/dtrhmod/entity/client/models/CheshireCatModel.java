package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class CheshireCatModel extends GeoModel<CheshireCatEntity> {
    @Override
    public Identifier getModelResource(CheshireCatEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "geo/entity/cheshire_cat.geo.json");
    }

    @Override
    public Identifier getTextureResource(CheshireCatEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "textures/entity/cheshire_cat.png");
    }

    @Override
    public Identifier getAnimationResource(CheshireCatEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "animations/entity/cheshire_cat.animation.json");
    }

    @Override
    public void setCustomAnimations(CheshireCatEntity animatable, long instanceId, AnimationState<CheshireCatEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("heads");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }
}
