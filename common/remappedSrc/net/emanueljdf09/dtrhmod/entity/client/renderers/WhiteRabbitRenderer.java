package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.emanueljdf09.dtrhmod.fabric.entity.client.models.WhiteRabbitModel;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.WhiteRabbitEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WhiteRabbitRenderer extends GeoEntityRenderer<WhiteRabbitEntity> {
    public WhiteRabbitRenderer(EntityRendererProvider.Context context) {
        super(context, new WhiteRabbitModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(WhiteRabbitEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}
