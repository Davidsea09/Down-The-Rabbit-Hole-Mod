package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.emanueljdf09.dtrhmod.entity.client.models.MadHatterModel;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MadHatterRenderer extends GeoEntityRenderer<MadHatterEntity> {
    public MadHatterRenderer(EntityRendererProvider.Context context) {
        super(context, new MadHatterModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(MadHatterEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}
