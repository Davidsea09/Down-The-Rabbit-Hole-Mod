package net.emanueljdf09.dtrhmod.item.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.item.block.MadHatItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedBlockGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class MadHatterHatTrinketRenderer implements TrinketRenderer {
    private final GeoItemRenderer<MadHatItem> renderer;

    public MadHatterHatTrinketRenderer() {
        this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hat")));
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.pushPose();

        if (contextModel instanceof PlayerModel<?> playerModel) {
            playerModel.head.translateAndRotate(matrices);

            matrices.mulPose(Axis.YP.rotationDegrees(13.23f));
            matrices.mulPose(Axis.ZP.rotationDegrees(-11.56f));

            matrices.translate(-0.5f, -1.49f, -0.6f);
            matrices.scale(1.15f, 1.15f, 1.15f);
        }

        this.renderer.render(stack, ItemDisplayContext.FIXED, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }
}
