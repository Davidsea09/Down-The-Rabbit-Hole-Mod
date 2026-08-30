package net.emanueljdf09.dtrhmod.entity.client.renderers;

import net.emanueljdf09.dtrhmod.block.models.MadHatterHatModel;
import net.emanueljdf09.dtrhmod.entity.client.models.MadHatterModel;
import net.emanueljdf09.dtrhmod.entity.client.models.WhiteRabbitModel;
import net.emanueljdf09.dtrhmod.entity.custom.MadHatterEntity;
import net.emanueljdf09.dtrhmod.entity.custom.WhiteRabbitEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MadHatterRenderer extends GeoEntityRenderer<MadHatterEntity> {
    public MadHatterRenderer(EntityRendererFactory.Context context) {
        super(context, new MadHatterModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(MadHatterEntity entity, float entityYaw, float partialTick, MatrixStack poseStack, VertexConsumerProvider bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

}
