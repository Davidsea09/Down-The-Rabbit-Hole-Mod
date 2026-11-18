package net.emanueljdf09.dtrhmod.block.models.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.head.ModWallHeadBlock;
import net.emanueljdf09.dtrhmod.block.entity.ModHeadBlockEntity;
import net.emanueljdf09.dtrhmod.block.models.ModHeadBlockModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

public class ModHeadBlockEntityRenderer extends GeoBlockRenderer<ModHeadBlockEntity> {

    // Set to true if your model points North by default in Blockbench
    private static final boolean MODEL_FACES_NORTH = true;

    public ModHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        super(new ModHeadBlockModel());
    }

    @Override
    public void actuallyRender(PoseStack poseStack, ModHeadBlockEntity animatable, BakedGeoModel model, @Nullable RenderType renderType, MultiBufferSource bufferSource, @Nullable VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        BlockState state = animatable.getBlockState();

        float facingYaw = 0.0F;
        boolean wall = false;

        if (state.getBlock() instanceof ModWallHeadBlock) {
            Direction direction = state.getValue(ModWallHeadBlock.FACING);
            facingYaw = direction.toYRot();
            wall = true;
        } else if (state.getBlock() instanceof ModHeadBlock) {
            int rotation = state.getValue(ModHeadBlock.ROTATION);
            facingYaw = rotation * 22.5F;
        }

        poseStack.pushPose();

        // Center the model in the block grid
        poseStack.translate(0.5D, 0.0D, 0.5D);

        // Apply rotation depending on whether it's a wall head or floor head
        if (wall) {
            // Wall heads face outward from the wall
            poseStack.mulPose(Axis.YP.rotationDegrees(-facingYaw));
            // Push it slightly away from the attached wall face
            poseStack.translate(0.0D, 0.25D, 0.25D);
        } else {
            // Floor heads rotate around 360 degrees (16-position rotation)
            float turn = MODEL_FACES_NORTH ? 180.0F - facingYaw : -facingYaw;
            poseStack.mulPose(Axis.YP.rotationDegrees(turn));
        }

        super.actuallyRender(poseStack, animatable, model, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);

        poseStack.popPose();
    }
}