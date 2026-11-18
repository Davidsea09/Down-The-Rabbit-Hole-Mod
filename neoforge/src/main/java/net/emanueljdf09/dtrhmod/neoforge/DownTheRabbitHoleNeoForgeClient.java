package net.emanueljdf09.dtrhmod.neoforge;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.client.DownTheRabbitHoleClient;
import net.emanueljdf09.dtrhmod.util.ModModelLayers;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = DownTheRabbitHole.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class DownTheRabbitHoleNeoForgeClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(DownTheRabbitHoleClient::init);
    }

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(ModModelLayers.TH_SIGN, SignRenderer::createSignLayer);
        event.registerLayerDefinition(ModModelLayers.WW_SIGN, SignRenderer::createSignLayer);
        event.registerLayerDefinition(ModModelLayers.BB_SIGN, SignRenderer::createSignLayer);
        event.registerLayerDefinition(ModModelLayers.HH_SIGN, SignRenderer::createSignLayer);

        event.registerLayerDefinition(ModModelLayers.TH_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);
        event.registerLayerDefinition(ModModelLayers.WW_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);
        event.registerLayerDefinition(ModModelLayers.BB_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);
        event.registerLayerDefinition(ModModelLayers.HH_HANGING_SIGN, HangingSignRenderer::createHangingSignLayer);
    }
}