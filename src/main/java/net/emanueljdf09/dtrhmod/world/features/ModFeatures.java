package net.emanueljdf09.dtrhmod.world.features;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.world.features.biome.TulgeyPathFeature;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;

public class ModFeatures {

    public static void register() {
        DownTheRabbitHole.LOGGER.info("Registering Features for " + DownTheRabbitHole.MOD_ID);
    }
}
