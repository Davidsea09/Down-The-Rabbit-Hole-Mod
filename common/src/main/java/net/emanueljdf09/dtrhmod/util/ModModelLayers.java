package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

public class ModModelLayers {

    public static final ModelLayerLocation TH_SIGN = createSignLayer("th");
    public static final ModelLayerLocation WW_SIGN = createSignLayer("ww");
    public static final ModelLayerLocation BB_SIGN = createSignLayer("bb");
    public static final ModelLayerLocation HH_SIGN = createSignLayer("hh");

    public static final ModelLayerLocation TH_HANGING_SIGN = createHangingSignLayer("th");
    public static final ModelLayerLocation WW_HANGING_SIGN = createHangingSignLayer("ww");
    public static final ModelLayerLocation BB_HANGING_SIGN = createHangingSignLayer("bb");
    public static final ModelLayerLocation HH_HANGING_SIGN = createHangingSignLayer("hh");

    private static ModelLayerLocation createSignLayer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "sign/" + name), "main");
    }

    private static ModelLayerLocation createHangingSignLayer(String name) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hanging_sign/" + name), "main");
    }
}
