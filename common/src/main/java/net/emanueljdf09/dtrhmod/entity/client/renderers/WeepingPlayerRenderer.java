package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.client.models.WeepingPlayerModel;
import net.emanueljdf09.dtrhmod.entity.custom.WeepingPlayerEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;


public class WeepingPlayerRenderer extends GeoEntityRenderer<WeepingPlayerEntity> {

    private static final ResourceLocation BASE_STONE_TEX = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/weeping_statue.png");

    public WeepingPlayerRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WeepingPlayerModel());

    }

    @Override
    public void renderRecursively(PoseStack poseStack, WeepingPlayerEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, int colour) {
        if (bone.getName().equals("statue_base")) {
            renderType = RenderType.entityCutoutNoCull(BASE_STONE_TEX);
            buffer = bufferSource.getBuffer(renderType);
        }

        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, buffer, isReRender, partialTick, packedLight, packedOverlay, colour);
    }

    @Override
    public ResourceLocation getTextureLocation(WeepingPlayerEntity animatable) {
        return this.model.getTextureResource(animatable);
    }
}
