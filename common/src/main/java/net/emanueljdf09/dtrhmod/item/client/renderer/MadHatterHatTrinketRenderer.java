package net.emanueljdf09.dtrhmod.item.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import io.wispforest.accessories.api.client.AccessoryRenderer;
import io.wispforest.accessories.api.slot.SlotReference;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.block.MadHatItem;
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

public class MadHatterHatTrinketRenderer implements AccessoryRenderer {
    private final GeoItemRenderer<MadHatItem> renderer;

    public MadHatterHatTrinketRenderer() {
        this.renderer = new GeoItemRenderer<>(new DefaultedBlockGeoModel<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hat")));
    }

    @Override
    public <M extends LivingEntity> void render(ItemStack stack, SlotReference reference, PoseStack matrices, EntityModel<M> model, MultiBufferSource multiBufferSource, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrices.pushPose();

        if (model instanceof PlayerModel<?> playerModel) {
            playerModel.head.translateAndRotate(matrices);

            matrices.mulPose(Axis.YP.rotationDegrees(13.23f));
            matrices.mulPose(Axis.ZP.rotationDegrees(-11.56f));

            matrices.translate(-0.5f, -1.49f, -0.6f);
            matrices.scale(1.15f, 1.15f, 1.15f);
        }

        this.renderer.renderByItem(stack, ItemDisplayContext.FIXED, matrices, multiBufferSource, light, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }
}
