package net.emanueljdf09.dtrhmod.world.features;

import net.minecraft.data.worldgen.BiomeDefaultFeatures;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.levelgen.GenerationStep;

public class WonderBiomeFeatures {

    public static void addDefaultDisks(BiomeGenerationSettings.Builder builder) {
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.DISK_SAND_PLACED);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.DISK_CLAY_PLACED);
        builder.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, ModPlacedFeatures.DISK_GRAVEL_PLACED);
    }

    public static void addTulgeyWoodsVegetation(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addForestGrass(builder);
        BiomeDefaultFeatures.addMossyStoneBlock(builder);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.TULGEY_TREE_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_FLOWERBEDS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_FLOWERS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_GROUND_MUSHROOMS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_ROCKS_PLACED);

    }

    public static void addValeOfTearsVegetation(BiomeGenerationSettings.Builder builder) {
        BiomeDefaultFeatures.addForestGrass(builder);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.VALE_OF_TEARS_SELECTOR_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_FLOWERBEDS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_GROUND_MUSHROOMS_PLACED);
        builder.addFeature(GenerationStep.Decoration.VEGETAL_DECORATION, ModPlacedFeatures.FOREST_ROCKS_PLACED);

    }

}
