package net.emanueljdf09.dtrhmod.block.models;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.head.ModWallHeadBlock;
import net.emanueljdf09.dtrhmod.block.entity.MadHatterHatBlockEntity;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.model.GeoModel;

public class ModHeadBlockModel extends GeoModel<ModHeadBlockEntity> {

    @Override
    public Identifier getModelResource(ModHeadBlockEntity animatable) {
        boolean isWall = animatable.getCachedState().getBlock() instanceof ModWallHeadBlock;
        if (isWall) {
            return new Identifier(DownTheRabbitHole.MOD_ID, "geo/block/" + animatable.getHeadType().getName() + "_wall_head.geo.json");
        }
        return animatable.getHeadType().getModelId();
    }

    @Override
    public Identifier getTextureResource(ModHeadBlockEntity animatable) {
        return animatable.getHeadType().getTextureId();
    }

    @Override
    public Identifier getAnimationResource(ModHeadBlockEntity animatable) {
        return new Identifier(DownTheRabbitHole.MOD_ID, "animations/head.animation.json");
    }

    @Override
    public void setCustomAnimations(ModHeadBlockEntity animatable, long instanceId, software.bernie.geckolib.core.animation.AnimationState<ModHeadBlockEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);

        CoreGeoBone headBone = this.getAnimationProcessor().getBone("head");
        if (headBone == null) return;

        BlockState state = animatable.getCachedState();
        if (state.getBlock() instanceof ModHeadBlock) {
            int rotationIndex = state.get(ModHeadBlock.ROTATION);
            float yaw = (rotationIndex * 360.0f) / ModHeadBlock.MAX_ROTATIONS;
            headBone.setRotY((float) Math.toRadians(-yaw));
        } else if (state.getBlock() instanceof ModWallHeadBlock) {
            int rotationIndex = state.get(ModWallHeadBlock.ROTATION);
            float yaw = rotationIndex * 90.0f;
            headBone.setRotY((float) Math.toRadians(-(yaw + 180.0F)));
        }
    }
}