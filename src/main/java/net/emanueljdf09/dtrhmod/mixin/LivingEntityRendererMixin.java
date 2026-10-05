package net.emanueljdf09.dtrhmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.emi.trinkets.api.TrinketsApi;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin<T extends LivingEntity> {

    @Inject(method = "render", at = @At("HEAD"))
    private void applyCatScarfTransparency(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    RenderSystem.enableBlend();
                    RenderSystem.setShaderColor(0.45f, 0.65f, 0.85f, 0.3f);
                }
            });
        }
    }

    @Inject(method = "render", at = @At("RETURN"))
    private void resetCatScarfTransparency(T entity, float yaw, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        if (entity instanceof PlayerEntity player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
                    RenderSystem.disableBlend();
                }
            });
        }
    }
}