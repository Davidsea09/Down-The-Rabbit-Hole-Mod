package net.emanueljdf09.dtrhmod.entity.client.renderers;

import com.mojang.blaze3d.vertex.PoseStack;
import net.emanueljdf09.dtrhmod.entity.client.models.MockTurtleModel;
import net.emanueljdf09.dtrhmod.entity.custom.MockTurtleEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class MockTurtleRenderer extends GeoEntityRenderer<MockTurtleEntity> {
    public MockTurtleRenderer(EntityRendererProvider.Context context) {
        super(context, new MockTurtleModel());
        this.shadowRadius = 0.8f;
    }

    @Override
    public void render(MockTurtleEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }
}
