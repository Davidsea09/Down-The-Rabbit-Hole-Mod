package net.emanueljdf09.dtrhmod.entity.client.renderers;

import net.emanueljdf09.dtrhmod.entity.client.models.CheshireCatModel;
import net.emanueljdf09.dtrhmod.entity.client.models.MadHatterModel;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CheshireCatRenderer extends GeoEntityRenderer<CheshireCatEntity> {

    public CheshireCatRenderer(EntityRendererFactory.Context context) {
        super(context, new CheshireCatModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(CheshireCatEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}

