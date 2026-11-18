package net.emanueljdf09.dtrhmod.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(FogRenderer.class)
public class BackgroundRendererMixin {
    private static float currentFogStart = -8.0F;
    private static float currentFogEnd = 15.0F;

    @Inject(method = "setupFog", at = @At("TAIL"))
    private static void onApplyFog(Camera camera, FogRenderer.FogMode fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        ClientLevel world = client.level;

        float targetStart = -8.0F;
        float targetEnd = viewDistance;

        if (world != null && camera.getEntity() != null) {
            BlockPos pos = camera.getBlockPosition();
            Optional<ResourceKey<Biome>> biomeKey = world.getBiome(pos).unwrapKey();

            if (biomeKey.isPresent()) {
                ResourceLocation biomeId = biomeKey.get().location();

                if (biomeId.getNamespace().equals(DownTheRabbitHole.MOD_ID)) {
                    String path = biomeId.getPath();

                    if (path.equals("tulgey_wood")) {
                        targetStart = 4.0F;
                        targetEnd = 40.0F;
                    } else if (path.equals("vale_of_tears")) {
                        targetStart = 4.0F;
                        targetEnd = 32.0F;
                    } else if (path.equals("chessboard_fields")) {
                        targetStart = 10.0F;
                        targetEnd = 160.0F;
                    }
                }
            }
        }

        float transitionSpeed = 0.05F;
        currentFogStart += (targetStart - currentFogStart) * transitionSpeed;
        currentFogEnd += (targetEnd - currentFogEnd) * transitionSpeed;

        RenderSystem.setShaderFogStart(currentFogStart);
        RenderSystem.setShaderFogEnd(currentFogEnd);
    }
}