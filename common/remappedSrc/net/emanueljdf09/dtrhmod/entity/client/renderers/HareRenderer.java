package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.emanueljdf09.dtrhmod.fabric.entity.client.models.HareModel;
import net.emanueljdf09.dtrhmod.fabric.entity.custom.HareEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class HareRenderer extends GeoEntityRenderer<HareEntity> {
    public HareRenderer(EntityRendererProvider.Context context) {
        super(context, new HareModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(HareEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}
