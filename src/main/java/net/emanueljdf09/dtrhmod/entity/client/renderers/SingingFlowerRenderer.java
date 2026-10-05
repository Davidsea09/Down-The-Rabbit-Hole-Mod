package net.emanueljdf09.dtrhmod.entity.client.renderers;

import net.emanueljdf09.dtrhmod.entity.client.models.SingingFlowerModel;
import net.emanueljdf09.dtrhmod.entity.custom.SingingFlowerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EntityType;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class SingingFlowerRenderer extends GeoEntityRenderer<SingingFlowerEntity> {
    public SingingFlowerRenderer(EntityRendererFactory.Context context) {
        super(context, new SingingFlowerModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(SingingFlowerEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
