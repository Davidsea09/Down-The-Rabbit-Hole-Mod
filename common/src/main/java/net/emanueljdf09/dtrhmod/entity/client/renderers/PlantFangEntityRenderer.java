package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.entity.client.models.PlantFangEntityModel;
import net.emanueljdf09.dtrhmod.entity.projectile.PlantFangEntity;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class PlantFangEntityRenderer extends EntityRenderer<PlantFangEntity> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/entity/plant_fangs.png");
    private final PlantFangEntityModel<PlantFangEntity> model;

    public PlantFangEntityRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new PlantFangEntityModel<>(context.bakeLayer(ModelLayers.EVOKER_FANGS));
    }

    public void render(PlantFangEntity plantFangEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
        float h = plantFangEntity.getAnimationProgress(g);
        if (h != 0.0F) {
            float j = 2.0F;
            if (h > 0.9F) {
                j *= (1.0F - h) / 0.1F;
            }

            matrixStack.pushPose();
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F - plantFangEntity.getYRot()));
            matrixStack.scale(-j, -j, j);
            float k = 0.03125F;
            matrixStack.translate((double) 0.0F, -0.626, (double) 0.0F);
            matrixStack.scale(0.5F, 0.5F, 0.5F);

            this.model.setupAnim(plantFangEntity, h, 0.0F, 0.0F, plantFangEntity.getYRot(), plantFangEntity.getXRot());

            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(this.model.renderType(TEXTURE));
            this.model.renderToBuffer(matrixStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY);
            matrixStack.popPose();
            super.render(plantFangEntity, f, g, matrixStack, vertexConsumerProvider, i);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(PlantFangEntity entity) {
        return TEXTURE;
    }
}