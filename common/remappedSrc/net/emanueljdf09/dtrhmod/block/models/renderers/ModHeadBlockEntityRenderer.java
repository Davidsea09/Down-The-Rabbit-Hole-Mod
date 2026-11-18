package net.emanueljdf09.dtrhmod.block.models.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.emanueljdf09.dtrhmod.fabric.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.fabric.block.custom.head.ModWallHeadBlock;
import net.emanueljdf09.dtrhmod.fabric.block.entity.ModHeadBlockEntity;
import net.emanueljdf09.dtrhmod.fabric.block.models.ModHeadBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ModHeadBlockEntityRenderer extends GeoBlockRenderer<ModHeadBlockEntity> {

    private static final boolean MODEL_FACES_NORTH = true;

    public ModHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ModHeadBlockModel());
    }


    @Override
    protected Direction getFacing(ModHeadBlockEntity head) {
        return Direction.NORTH;
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ModHeadBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = animatable.getCachedState();

        float facingYaw;
        boolean wall = false;
        if (state.getBlock() instanceof ModWallHeadBlock) {
            facingYaw = state.getValue(ModWallHeadBlock.FACING).asRotation();
            wall = true;
        } else if (state.getBlock() instanceof ModHeadBlock) {
            facingYaw = state.getValue(ModHeadBlock.ROTATION) * 22.5F;
        } else {
            super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
            return;
        }

        float turn = MODEL_FACES_NORTH ? 180.0F - facingYaw : -facingYaw;

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(turn));
        if (wall) {
            poseStack.translate(0.0, 0.25, MODEL_FACES_NORTH ? 0.25 : -0.25);
        }
        poseStack.translate(-0.5, 0.0, -0.5);
        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
    }

}