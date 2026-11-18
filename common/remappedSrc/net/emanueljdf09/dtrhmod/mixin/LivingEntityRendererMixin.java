package net.emanueljdf09.dtrhmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.emi.trinkets.api.TrinketsApi;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {
    @Inject(method = "render", at = @At("HEAD"))
    private void applyCatScarfTransparency(T entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof Player player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(0.45f, 0.65f, 0.85f, 0.3f);
                }
            });
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void resetCatScarfTransparency(T entity, float yaw, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof Player player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.disableBlend();
                }
            });
        }
    }
}