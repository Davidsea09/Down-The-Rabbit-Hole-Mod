package net.emanueljdf09.dtrhmod.entity.client.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.entity.custom.WhiteRabbitEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class MadHatterModel extends GeoModel<MadHatterEntity> {


    @Override
    public Identifier getModelResource(MadHatterEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "geo/entity/mad_hatter.geo.json");
    }

    @Override
    public Identifier getTextureResource(MadHatterEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "textures/entity/mad_hatter.png");
    }

    @Override
    public Identifier getAnimationResource(MadHatterEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "animations/entity/mad_hatter.animation.json");
    }

    @Override
    public void setCustomAnimations(MadHatterEntity animatable, long instanceId, AnimationState<MadHatterEntity> animationState) {
        CoreGeoBone head = getAnimationProcessor().getBone("head");

        if (head != null) {
            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
            head.setRotX(entityData.headPitch() * MathHelper.RADIANS_PER_DEGREE);
            head.setRotY(entityData.netHeadYaw() * MathHelper.RADIANS_PER_DEGREE);
        }
    }

}
