package net.emanueljdf09.dtrhmod.world.features;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.data.worldgen.placement.VegetationPlacements;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.gen.placementmodifier.*;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.BlockPredicateFilter;
import net.minecraft.world.level.levelgen.placement.CountPlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import java.util.List;

public class ModPlacedFeatures {

    public static final ResourceKey<PlacedFeature> FOREST_ROCKS_PLACED = registerKey("forest_rocks_placed");
    public static final ResourceKey<PlacedFeature> FOREST_FLOWERBEDS_PLACED = registerKey("forest_flowerbeds_placed");
    public static final ResourceKey<PlacedFeature> FOREST_FLOWERS_PLACED = registerKey("forest_flowers_placed");
    public static final ResourceKey<PlacedFeature> FOREST_GROUND_MUSHROOMS_PLACED = registerKey("forest_ground_mushrooms_placed");

    public static final ResourceKey<PlacedFeature> DISK_CLAY_PLACED = registerKey("disk_clay_placed");
    public static final ResourceKey<PlacedFeature> DISK_SAND_PLACED = registerKey("disk_sand_placed");
    public static final ResourceKey<PlacedFeature> DISK_GRAVEL_PLACED = registerKey("disk_gravel_placed");

    public static final ResourceKey<PlacedFeature> TH_TREE_PLACED = registerKey("th_tree_placed");
    public static final ResourceKey<PlacedFeature> BB_TREE_PLACED = registerKey("bb_tree_placed");
    public static final ResourceKey<PlacedFeature> WW_TREE_PLACED = registerKey("ww_tree_placed");
    public static final ResourceKey<PlacedFeature> HH_TREE_PLACED = registerKey("hh_tree_placed");
    public static final ResourceKey<PlacedFeature> TULGEY_TREE_PLACED = registerKey("tulgey_tree_placed");

    public static final ResourceKey<PlacedFeature> BIG_BLUE_MUSHROOM_PLACED = registerKey("big_blue_mushroom_placed");
    public static final ResourceKey<PlacedFeature> BIG_YELLOW_MUSHROOM_PLACED = registerKey("big_yellow_mushroom_placed");
    public static final ResourceKey<PlacedFeature> BIG_MAGENTA_MUSHROOM_PLACED = registerKey("big_magenta_mushroom_placed");

    public static final ResourceKey<PlacedFeature> VALE_OF_TEARS_SELECTOR_PLACED = registerKey("vale_of_tears_vegetation_placed");

    public static void bootstrap(BootstrapContext<PlacedFeature> context) {
        var configuredFeatureRegistryEntryLookup = context.lookup(Registries.CONFIGURED_FEATURE);


        register(context, FOREST_ROCKS_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FOREST_ROCKS),
                List.of(
                        RarityFilter.onAverageOnceEvery(6),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()
                )
        );

        register(context, FOREST_FLOWERBEDS_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FOREST_FLOWERBEDS),
                List.of(
                        RarityFilter.onAverageOnceEvery(8),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()
                )
        );

        register(context, FOREST_GROUND_MUSHROOMS_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FOREST_GROUND_MUSHROOMS),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()
                )
        );

 register(context, FOREST_FLOWERS_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.FOREST_FLOWERS),
                List.of(
                        RarityFilter.onAverageOnceEvery(4),
                        InSquarePlacement.spread(),
                        PlacementUtils.HEIGHTMAP_TOP_SOLID,
                        SurfaceWaterDepthFilter.forMaxDepth(0),
                        BiomeFilter.biome()
                )
        );


        register(context, DISK_CLAY_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DISK_CLAY), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(new Fluid[]{Fluids.WATER})), BiomeFilter.biome());
        register(context, DISK_GRAVEL_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DISK_GRAVEL), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(new Fluid[]{Fluids.WATER})), BiomeFilter.biome());
        register(context, DISK_SAND_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.DISK_SAND), CountPlacement.of(3), InSquarePlacement.spread(), PlacementUtils.HEIGHTMAP_TOP_SOLID, BlockPredicateFilter.forPredicate(BlockPredicate.matchesFluids(new Fluid[]{Fluids.WATER})), BiomeFilter.biome());


        register(context, TH_TREE_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.TH_TREE_SELECTOR),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.2f, 1),
                        ModBlocks.TH_SAPLING));

        register(context, BB_TREE_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BB_TREE_SELECTOR),
                VegetationPlacements.treePlacement(PlacementUtils.countExtra(6, 0.2f, 1),
                        ModBlocks.BB_SAPLING));

        register(context, TULGEY_TREE_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.TULGEY_TREE_SELECTOR),
                VegetationPlacements.treePlacement(
                        PlacementUtils.countExtra(6, 0.2f, 2),
                        ModBlocks.TH_SAPLING));

        register(context, WW_TREE_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WW_TREE_SELECTOR),
                VegetationPlacements.treePlacement(
                        PlacementUtils.countExtra(4, 0.2f, 1),
                        ModBlocks.WW_SAPLING));

        register(context, HH_TREE_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.WW_TREE_SELECTOR),
                VegetationPlacements.treePlacement(
                        PlacementUtils.countExtra(2, 0.2f, 1),
                        ModBlocks.HH_SAPLING));

        register(context, BIG_BLUE_MUSHROOM_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM_SELECTOR),
                RarityFilter.onAverageOnceEvery(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());
        register(context, BIG_YELLOW_MUSHROOM_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM_SELECTOR),
                RarityFilter.onAverageOnceEvery(2),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());
        register(context, BIG_MAGENTA_MUSHROOM_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM_SELECTOR),
                RarityFilter.onAverageOnceEvery(1),
                InSquarePlacement.spread(),
                PlacementUtils.HEIGHTMAP,
                BiomeFilter.biome());

        register(context, VALE_OF_TEARS_SELECTOR_PLACED, configuredFeatureRegistryEntryLookup.getOrThrow(ModConfiguredFeatures.VALE_OF_TEARS_SELECTOR),
                VegetationPlacements.treePlacement(
                        PlacementUtils.countExtra(2, 0.2f, 1),
                        ModBlocks.WW_SAPLING));

    }

    public static ResourceKey<PlacedFeature> registerKey(String name) {
        return ResourceKey.create(Registries.PLACED_FEATURE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
    }

    private static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration,
                                 List<PlacementModifier> modifiers) {

        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    public static void register(BootstrapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, Holder<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers) {
        register(context, key, configuration, List.of(modifiers));
    }
}
