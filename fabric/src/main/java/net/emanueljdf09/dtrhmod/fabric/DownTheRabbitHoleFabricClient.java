package net.emanueljdf09.dtrhmod.fabric;


import dev.architectury.registry.client.level.entity.EntityModelLayerRegistry;
import net.emanueljdf09.dtrhmod.client.DownTheRabbitHoleClient;
import net.emanueljdf09.dtrhmod.util.ModModelLayers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;

public class DownTheRabbitHoleFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        DownTheRabbitHoleClient.init();

        EntityModelLayerRegistry.register(ModModelLayers.TH_SIGN, SignRenderer::createSignLayer);
        EntityModelLayerRegistry.register(ModModelLayers.TH_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);

        EntityModelLayerRegistry.register(ModModelLayers.WW_SIGN, SignRenderer::createSignLayer);
        EntityModelLayerRegistry.register(ModModelLayers.WW_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);

        EntityModelLayerRegistry.register(ModModelLayers.BB_SIGN, SignRenderer::createSignLayer);
        EntityModelLayerRegistry.register(ModModelLayers.BB_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);

        EntityModelLayerRegistry.register(ModModelLayers.HH_SIGN, SignRenderer::createSignLayer);
        EntityModelLayerRegistry.register(ModModelLayers.HH_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);
    }
}
