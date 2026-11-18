package net.emanueljdf09.dtrhmod.world.features;

import dev.corgitaco.ohthetreesyoullgrow.world.level.levelgen.feature.TYGFeatures;
import dev.corgitaco.ohthetreesyoullgrow.world.level.levelgen.feature.configurations.TreeFromStructureNBTConfig;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.world.features.tree.deco.HangingLeavesTreeDeco;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.valueproviders.BiasedToBottomInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.WeightedPlacedFeature;
import net.minecraft.world.level.levelgen.feature.configurations.DiskConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.RuleBasedBlockStateProvider;
import net.minecraft.world.level.levelgen.feature.stateproviders.WeightedStateProvider;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import java.util.List;
import java.util.Set;

public class ModConfiguredFeatures {

    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_ROCKS = registerKey("forest_rocks");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_FLOWERBEDS = registerKey("forest_flowerbeds");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_FLOWERS= registerKey("forest_flowers");
    public static final ResourceKey<ConfiguredFeature<?, ?>> FOREST_GROUND_MUSHROOMS = registerKey("forest_ground_mushrooms");

    public static final ResourceKey<ConfiguredFeature<?, ?>> DISK_CLAY = registerKey("disk_clay");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DISK_SAND = registerKey("disk_sand");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DISK_GRAVEL = registerKey("disk_gravel");

    public static final ResourceKey<ConfiguredFeature<?, ?>> TH_TREE1 = registerKey("th_tree1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TH_TREE2 = registerKey("th_tree2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TH_TREE3 = registerKey("th_tree3");


    public static final ResourceKey<ConfiguredFeature<?, ?>> BB_TREE1 = registerKey("bb_tree1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BB_TREE2 = registerKey("bb_tree2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BB_TREE3 = registerKey("bb_tree3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BB_STUMP = registerKey("bb_stump");

    public static final ResourceKey<ConfiguredFeature<?, ?>> WW_TREE1 = registerKey("ww_tree1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WW_TREE2 = registerKey("ww_tree2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WW_TREE3 = registerKey("ww_tree3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> WW_TREE4 = registerKey("ww_tree4");
    public static final ResourceKey<ConfiguredFeature<?, ?>> DEAD_WW_TREE = registerKey("dead_ww_tree");

    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE1 = registerKey("hh_tree1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE2 = registerKey("hh_tree2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE3 = registerKey("hh_tree3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE4 = registerKey("hh_tree4");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE5 = registerKey("hh_tree5");

    public static final ResourceKey<ConfiguredFeature<?, ?>> WW_TREE_SELECTOR = registerKey("ww_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TH_TREE_SELECTOR = registerKey("th_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BB_TREE_SELECTOR = registerKey("bb_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> HH_TREE_SELECTOR = registerKey("hh_tree");
    public static final ResourceKey<ConfiguredFeature<?, ?>> TULGEY_TREE_SELECTOR = registerKey("tulgey_tree");


    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BLUE_MUSHROOM1 = registerKey("big_blue_mushroom1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BLUE_MUSHROOM2 = registerKey("big_blue_mushroom2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BLUE_MUSHROOM3 = registerKey("big_blue_mushroom3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BLUE_MUSHROOM4 = registerKey("big_blue_mushroom4");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_BLUE_MUSHROOM_SELECTOR = registerKey("big_blue_mushroom");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_MAGENTA_MUSHROOM1 = registerKey("big_magenta_mushroom1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_MAGENTA_MUSHROOM2 = registerKey("big_magenta_mushroom2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_MAGENTA_MUSHROOM3 = registerKey("big_magenta_mushroom3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_MAGENTA_MUSHROOM4 = registerKey("big_magenta_mushroom4");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_MAGENTA_MUSHROOM_SELECTOR = registerKey("big_magenta_mushroom");

    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_YELLOW_MUSHROOM1 = registerKey("big_yellow_mushroom1");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_YELLOW_MUSHROOM2 = registerKey("big_yellow_mushroom2");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_YELLOW_MUSHROOM3 = registerKey("big_yellow_mushroom3");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BIG_YELLOW_MUSHROOM_SELECTOR = registerKey("big_yellow_mushroom");

    public static final ResourceKey<ConfiguredFeature<?, ?>> VALE_OF_TEARS_SELECTOR = registerKey("vale_of_tears_vegetation");

    public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context) {
        var placedLookup = context.lookup(Registries.PLACED_FEATURE);
        var configuredLookup = context.lookup(Registries.CONFIGURED_FEATURE);

        register(context, FOREST_ROCKS,Feature.SIMPLE_BLOCK,
                new SimpleBlockConfiguration(BlockStateProvider.simple(Blocks.STONE_SLAB))
        );

        SimpleWeightedRandomList.Builder<BlockState> builder_flowerbeds = new SimpleWeightedRandomList.Builder<>();

        for (int i = 1; i <= 4; ++i) {
            for (Direction direction : Direction.values()) {
                if (direction.getAxis().isHorizontal()) {
                    builder_flowerbeds.add(
                            ModBlocks.LAWN_DAISY_PATCH.get().defaultBlockState()
                                    .setValue(PinkPetalsBlock.AMOUNT, i)
                                    .setValue(PinkPetalsBlock.FACING, direction),
                            1
                    );
                }
            }
        }

        register(context, FOREST_FLOWERBEDS, Feature.FLOWER,
                new RandomPatchConfiguration(
                        16,
                        6,
                        2,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        new WeightedStateProvider(builder_flowerbeds)
                                )
                        )
                )
        );

        register(context, FOREST_GROUND_MUSHROOMS, Feature.FLOWER,
                new RandomPatchConfiguration(
                        16,
                        6,
                        2,
                        PlacementUtils.onlyWhenEmpty(
                                Feature.SIMPLE_BLOCK,
                                new SimpleBlockConfiguration(
                                        new WeightedStateProvider(
                                                SimpleWeightedRandomList.<BlockState>builder()
                                                        .add(ModBlocks.YELLOW_MUSHROOM.get().defaultBlockState(), 3)
                                                        .add(ModBlocks.BLUE_MUSHROOM.get().defaultBlockState(), 2)
                                                        .add(ModBlocks.MAGENTA_MUSHROOM.get().defaultBlockState(), 1)
                                                        .build()
                                        )
                                )
                        )
                )
        );

        register(context, FOREST_FLOWERS, Feature.FLOWER,
                new RandomPatchConfiguration(
                16,
                6,
                2,
                PlacementUtils.onlyWhenEmpty(
                        Feature.SIMPLE_BLOCK,
                        new SimpleBlockConfiguration(
                                new WeightedStateProvider(
                                        SimpleWeightedRandomList.<BlockState>builder()
                                                .add(ModBlocks.FLOWER_PORTAL.get().defaultBlockState(), 3)
                                                .build()
                                )
                        )
                )
                )
        );



        register(context, DISK_CLAY, Feature.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.CLAY), BlockPredicate.matchesBlocks(List.of(ModBlocks.WONDER_DIRT.get(), Blocks.CLAY)), UniformInt.of(2, 3), 1));
        register(context, DISK_GRAVEL, Feature.DISK, new DiskConfiguration(RuleBasedBlockStateProvider.simple(Blocks.GRAVEL), BlockPredicate.matchesBlocks(List.of(ModBlocks.WONDER_DIRT.get(), Blocks.GRASS_BLOCK)), UniformInt.of(2, 5), 2));
        register(context, DISK_SAND, Feature.DISK, new DiskConfiguration(new RuleBasedBlockStateProvider(BlockStateProvider.simple(Blocks.SAND), List.of(new RuleBasedBlockStateProvider.Rule(BlockPredicate.matchesBlocks(Direction.DOWN.getNormal(), Blocks.AIR), BlockStateProvider.simple(Blocks.SANDSTONE)))), BlockPredicate.matchesBlocks(List.of(ModBlocks.WONDER_DIRT.get(), ModBlocks.WONDER_GRASS.get())), UniformInt.of(2, 6), 2));

        register(context, BB_TREE1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/forgotten_tree_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/forgotten_tree_canopy1"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.BB_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BB_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, BB_TREE2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/enchanted_trunk_2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/enchanted_canopy_2"))
                        .height(BiasedToBottomInt.of(1, 5))
                        .logProvider(BlockStateProvider.simple(ModBlocks.BB_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BB_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, BB_TREE3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/enchanted_trunk_3"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/enchanted_canopy_3"))
                        .height(BiasedToBottomInt.of(1, 5))
                        .logProvider(BlockStateProvider.simple(ModBlocks.BB_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BB_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
//                      .treeDecorators(List.of(
//                                new HangFromTreeDeco(
//                                        1,
//                                        1,
//                                        2,
//                                        4,
//                                        2,
//                                        BlockStateProvider.of(Blocks.CHAIN),
//                                        BlockStateProvider.of(Blocks.LANTERN.getDefaultState().with(HANGING, true))))
//                        )
                        .build()


        );

        register(context, BB_STUMP, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/woodlands_stump_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/bb/woodlands_stump_canopy1"))
                        .height(BiasedToBottomInt.of(2, 5))
                        .logProvider(BlockStateProvider.simple(ModBlocks.BB_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BB_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, BB_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BB_TREE1),
                                        List.of())),
                                0.35f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BB_TREE2),
                                        List.of())),
                                0.35f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BB_TREE3), List.of()))
        ));


        register(context, TH_TREE1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_canopy1"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.TH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.TH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.DARK_OAK_LOG))
                        .leavesTarget(Set.of(Blocks.DARK_OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, TH_TREE2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_canopy2"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.TH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.TH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, TH_TREE3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_trunk3"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/th/ancient_tree_canopy3"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.TH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.TH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, TH_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.TH_TREE1),
                                        List.of())),
                                0.33f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.TH_TREE2),
                                        List.of())),
                                0.33f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.TH_TREE3), List.of()))
        ));

        register(context, TULGEY_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.TH_TREE_SELECTOR),
                                        List.of())),
                                0.50f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BB_STUMP),
                                        List.of())),
                                0.02f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BB_TREE_SELECTOR), List.of()))
        ));

        register(context, WW_TREE1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_canopy1"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.WW_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.WW_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(10)
                        .treeDecorators(List.of(new HangingLeavesTreeDeco(0.35f, 2, 5)))
                        .build()


        );

        register(context, WW_TREE2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_canopy2"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.WW_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.WW_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(10)
                        .treeDecorators(List.of(new HangingLeavesTreeDeco(0.35f, 2, 5)))
                        .build()


        );

        register(context, WW_TREE3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_canopy3"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.WW_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.WW_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(10)
                        .treeDecorators(List.of(new HangingLeavesTreeDeco(0.35f, 2, 5)))
                        .build()


        );

        register(context, WW_TREE4, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/willow_canopy4"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.WW_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.WW_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(10)
                        .treeDecorators(List.of(new HangingLeavesTreeDeco(0.35f, 2, 5)))
                        .build()


        );

        register(context, DEAD_WW_TREE, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/dead_willow_trunk"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/ww/dead_willow_canopy"))
                        .height(BiasedToBottomInt.of(5, 15))
                        .logProvider(BlockStateProvider.simple(ModBlocks.WW_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.WW_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(10)
                        .build()


        );



        register(context, WW_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.DEAD_WW_TREE),
                                        List.of())),
                                0.01f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.WW_TREE1),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.WW_TREE2),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.WW_TREE3),
                                        List.of())),
                                0.25f
                        )
        ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.WW_TREE4), List.of()))
        ));

        register(context, HH_TREE1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_canopy1"))
                        .height(BiasedToBottomInt.of(5, 7))
                        .logProvider(BlockStateProvider.simple(ModBlocks.HH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.HH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, HH_TREE2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_canopy2"))
                        .height(BiasedToBottomInt.of(2, 5))
                        .logProvider(BlockStateProvider.simple(ModBlocks.HH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.HH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(5)
                        .build()


        );

        register(context, HH_TREE3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_trunk3"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_canopy3"))
                        .height(BiasedToBottomInt.of(5, 8))
                        .logProvider(BlockStateProvider.simple(ModBlocks.HH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.HH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, HH_TREE4, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_canopy3"))
                        .height(BiasedToBottomInt.of(5, 8))
                        .logProvider(BlockStateProvider.simple(ModBlocks.HH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.HH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, HH_TREE5, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_trunk3"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "trees/hh/hh_tree_canopy1"))
                        .height(BiasedToBottomInt.of(5, 8))
                        .logProvider(BlockStateProvider.simple(ModBlocks.HH_LOG.get()))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.HH_LEAVES.get()))
                        .logTarget(Set.of(Blocks.OAK_LOG))
                        .leavesTarget(Set.of(Blocks.OAK_LEAVES))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.DIRT))
                        .maxLogDepth(3)
                        .build()


        );


        register(context, HH_TREE_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.HH_TREE1),
                                        List.of())),
                                0.20f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.HH_TREE2),
                                        List.of())),
                                0.20f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.HH_TREE3),
                                        List.of())),
                                0.20f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.HH_TREE4),
                                        List.of())),
                                0.20f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.HH_TREE5), List.of()))
        ));


        register(context, BIG_BLUE_MUSHROOM1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy1"))
                        .height(BiasedToBottomInt.of(2, 4))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_BLUE_MUSHROOM2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy2"))
                        .height(BiasedToBottomInt.of(2, 4))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_BLUE_MUSHROOM3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy5"))
                        .height(BiasedToBottomInt.of(3, 4))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_BLUE_MUSHROOM4, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy6"))
                        .height(BiasedToBottomInt.of(2, 4))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.BLUE_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_BLUE_MUSHROOM_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM1),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM2),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM3),
                                        List.of())),
                                0.25f
                                )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM4), List.of()))
        ));


        register(context, BIG_MAGENTA_MUSHROOM1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy2"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.MAGENTA_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_MAGENTA_MUSHROOM2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy3"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.MAGENTA_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_MAGENTA_MUSHROOM3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy6"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.MAGENTA_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_MAGENTA_MUSHROOM4, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy3"))
                        .height(BiasedToBottomInt.of(3, 4))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.MAGENTA_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_MAGENTA_MUSHROOM_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM1),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM2),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM3),
                                        List.of())),
                                0.25f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM4), List.of()))
        ));

        register(context, BIG_YELLOW_MUSHROOM1, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy3"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.YELLOW_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_YELLOW_MUSHROOM2, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk1"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy4"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.YELLOW_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_YELLOW_MUSHROOM3, TYGFeatures.TREE_FROM_NBT_V1.get(),
                new TreeFromStructureNBTConfig.Builder()
                        .baseLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/trunk2"))
                        .canopyLocation(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushrooms/canopy3"))
                        .height(BiasedToBottomInt.of(3, 5))
                        .logProvider(BlockStateProvider.simple(Blocks.MUSHROOM_STEM))
                        .leavesProvider(BlockStateProvider.simple(ModBlocks.YELLOW_MUSHROOM_BLOCK.get()))
                        .logTarget(Set.of(Blocks.MUSHROOM_STEM))
                        .leavesTarget(Set.of(Blocks.RED_MUSHROOM_BLOCK))
                        .growableOn(BlockPredicate.matchesTag(BlockTags.MUSHROOM_GROW_BLOCK))
                        .maxLogDepth(3)
                        .build()


        );

        register(context, BIG_YELLOW_MUSHROOM_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM2),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM3),
                                        List.of())),
                                0.25f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM1), List.of()))
        ));

        register(context, VALE_OF_TEARS_SELECTOR, Feature.RANDOM_SELECTOR, new RandomFeatureConfiguration(
                List.of(
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM_SELECTOR),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM_SELECTOR),
                                        List.of())),
                                0.25f
                        ),
                        new WeightedPlacedFeature(
                                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.BIG_BLUE_MUSHROOM_SELECTOR),
                                        List.of())),
                                0.25f
                        )
                ),
                Holder.direct(new PlacedFeature(configuredLookup.getOrThrow(ModConfiguredFeatures.WW_TREE_SELECTOR), List.of()))
        ));


    }



    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
    }

    public static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context,
                                                                                  ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {

        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }
}
