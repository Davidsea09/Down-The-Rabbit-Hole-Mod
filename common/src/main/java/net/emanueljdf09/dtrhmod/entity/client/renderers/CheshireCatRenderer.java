package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.emanueljdf09.dtrhmod.entity.client.models.CheshireCatModel;
import net.emanueljdf09.dtrhmod.entity.custom.CheshireCatEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CheshireCatRenderer extends GeoEntityRenderer<CheshireCatEntity> {

    public CheshireCatRenderer(EntityRendererProvider.Context context) {
        super(context, new CheshireCatModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(CheshireCatEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}

