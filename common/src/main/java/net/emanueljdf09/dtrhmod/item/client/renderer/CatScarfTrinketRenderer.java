package net.emanueljdf09.dtrhmod.item.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.custom.CatScarfItem;
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

public class CatScarfTrinketRenderer implements AccessoryRenderer {
    private final GeoItemRenderer<CatScarfItem> renderer;

    public CatScarfTrinketRenderer() {
        this.renderer = new GeoItemRenderer<>(new DefaultedItemGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cat_scarf")));
    }

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrices.pushPose();

        if (model instanceof PlayerModel<?> playerModel) {

            playerModel.body.translateAndRotate(matrices);
            matrices.mulPose(Axis.XP.rotationDegrees(180.0f));
            matrices.mulPose(Axis.YP.rotationDegrees(180.0f));
            matrices.translate(-0.5f, -2.0f, -0.5f);

            matrices.scale(1.0f, 1.0f, 1.0f);
        }

        this.renderer.renderByItem(stack, ItemDisplayContext.FIXED, matrices, multiBufferSource, light, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }
}