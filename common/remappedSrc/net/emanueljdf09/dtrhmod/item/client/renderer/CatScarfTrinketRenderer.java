package net.emanueljdf09.dtrhmod.item.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.item.custom.CatScarfItem;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.model.DefaultedItemGeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CatScarfTrinketRenderer implements TrinketRenderer {
    private final GeoItemRenderer<CatScarfItem> renderer;

    public CatScarfTrinketRenderer() {
        this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cat_scarf")));
    }

    @Override
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel, PoseStack matrices, MultiBufferSource vertexConsumers, int light, LivingEntity entity, float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw, float headPitch) {
        matrices.pushPose();

        if (contextModel instanceof PlayerModel<?> playerModel) {

            playerModel.body.translateAndRotate(matrices);
            matrices.mulPose(Axis.XP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
            matrices.translate(-0.5f, -2.0f, -0.5f);

            matrices.scale(1.0f, 1.0f, 1.0f);
        }

        this.renderer.render(stack, ItemDisplayContext.FIXED, matrices, vertexConsumers, light, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }
}