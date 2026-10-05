package net.emanueljdf09.dtrhmod.item.client.renderer;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.block.MadHatItem;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MadHatterHatTrinketRenderer implements TrinketRenderer {
    private final GeoItemRenderer<MadHatItem> renderer;

    public MadHatterHatTrinketRenderer() {
        this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(new Identifier(DownTheRabbitHole.MOD_ID, "mad_hat")));
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.push();

        if (contextModel instanceof PlayerEntityModel<?> playerModel) {
            playerModel.head.rotate(matrices);

            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(13.23f));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-11.56f));

            matrices.translate(-0.5f, -1.49f, -0.6f);
            matrices.scale(1.15f, 1.15f, 1.15f);
        }

        this.renderer.render(stack, ModelTransformationMode.FIXED, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV);

        matrices.pop();
    }
}
