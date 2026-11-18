package net.emanueljdf09.dtrhmod.item;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.item.custom.DynamicTeaCupItem;
import net.emanueljdf09.dtrhmod.fabric.recipe.TeapotRecipe;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import java.util.List;

public class ModItemGroups {

    public static final CreativeModeTab DTRH_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "dtrh"),
            FabricItemGroup.builder().displayName(Component.translatable("itemgroup.dtrh"))
                    .icon(() -> new ItemStack(ModItems.POCKETWATCH)).entries((displayContext, entries) -> {

                        entries.add(ModItems.POCKETWATCH);
                        entries.add(ModItems.EXTERIOR_KEY);
                        entries.add(ModItems.EMPTY_CUP);
                        entries.add(ModBlocks.TEAPOT_BLOCK.asItem());
                        entries.add(ModBlocks.BLUE_MUSHROOM.asItem());
                        entries.add(ModBlocks.BLUE_MUSHROOM_BLOCK.asItem());
                        entries.add(ModBlocks.YELLOW_MUSHROOM_BLOCK.asItem());
                        entries.add(ModBlocks.YELLOW_MUSHROOM.asItem());
                        entries.add(ModBlocks.MAGENTA_MUSHROOM_BLOCK.asItem());
                        entries.add(ModBlocks.MAGENTA_MUSHROOM.asItem());

                        entries.add(ModItems.DRINK_ME);
                        entries.add(ModItems.EAT_ME);
                        entries.add(ModItems.WHITE_RABBIT_SPAWN_EGG);
                        entries.add(ModItems.WEEPING_PLAYERS_SPAWN_EGG);
                        entries.add(ModItems.MAD_HATTER_SPAWN_EGG);
                        entries.add(ModItems.CHESHIRE_CAT_SPAWN_EGG);
                        entries.add(ModItems.SINGING_FLOWER_SPAWN_EGG);
                        entries.add(ModItems.MOCK_TURTLE_SPAWN_EGG);
                        entries.add(ModItems.CATERPILLAR_SPAWN_EGG);
                        entries.add(ModItems.HARE_SPAWN_EGG);

                        entries.add(ModItems.VINE_LASSO_ITEM);

                        entries.add(ModItems.CAT_SCARF_ITEM);
                        entries.add(ModItems.PIPE_ITEM);
                        entries.add(ModItems.CAT_FUR);
                        entries.add(ModItems.MOCK_TURTLE_STEW);

                        entries.add(ModItems.MAD_HAT_ITEM);
                        entries.add(ModItems.RED_RIDING_HOOD_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.CINDERELLA_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.SNOW_WHITE_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.JACK_AND_THE_BEANSTALK_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.RAPUNZEL_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.AURORA_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.THE_LITTLE_MERMAID_STORYBOOK.getDefaultStack());
                        entries.add(ModItems.THREE_LITTLE_PIGS_STORYBOOK.getDefaultStack());



                        entries.add(ModBlocks.TH_SAPLING.asItem());
                        entries.add(ModBlocks.TH_LEAVES.asItem());
                        entries.add(ModBlocks.TH_LOG.asItem());
                        entries.add(ModBlocks.TH_WOOD.asItem());
                        entries.add(ModBlocks.STRIPPED_TH_LOG.asItem());
                        entries.add(ModBlocks.STRIPPED_TH_WOOD.asItem());
                        entries.add(ModBlocks.TH_PLANKS.asItem());
                        entries.add(ModBlocks.TH_STAIRS.asItem());
                        entries.add(ModBlocks.TH_SLABS.asItem());
                        entries.add(ModBlocks.TH_FENCE.asItem());
                        entries.add(ModBlocks.TH_FENCE_GATE.asItem());
                        entries.add(ModBlocks.TH_DOOR.asItem());
                        entries.add(ModBlocks.TH_TRAPDOOR.asItem());
                        entries.add(ModBlocks.TH_BUTTON.asItem());
                        entries.add(ModBlocks.TH_PRESSURE_PLATE.asItem());
                        entries.add(ModItems.TH_SIGN);
                        entries.add(ModItems.HANGING_TH_SIGN);
                        entries.add(ModItems.TH_BOAT);

                        entries.add(ModBlocks.WW_SAPLING.asItem());
                        entries.add(ModBlocks.WW_LEAVES.asItem());
                        entries.add(ModItems.WW_HANGING_LEAVES);
                        entries.add(ModBlocks.WW_LOG.asItem());
                        entries.add(ModBlocks.WW_WOOD.asItem());
                        entries.add(ModBlocks.STRIPPED_WW_LOG.asItem());
                        entries.add(ModBlocks.STRIPPED_WW_WOOD.asItem());
                        entries.add(ModBlocks.WW_PLANKS.asItem());
                        entries.add(ModBlocks.WW_STAIRS.asItem());
                        entries.add(ModBlocks.WW_SLABS.asItem());
                        entries.add(ModBlocks.WW_FENCE.asItem());
                        entries.add(ModBlocks.WW_FENCE_GATE.asItem());
                        entries.add(ModBlocks.WW_DOOR.asItem());
                        entries.add(ModBlocks.WW_TRAPDOOR.asItem());
                        entries.add(ModBlocks.WW_BUTTON.asItem());
                        entries.add(ModBlocks.WW_PRESSURE_PLATE.asItem());
                        entries.add(ModItems.WW_SIGN);
                        entries.add(ModItems.HANGING_WW_SIGN);
                        entries.add(ModItems.WW_BOAT);

                        entries.add(ModBlocks.BB_SAPLING.asItem());
                        entries.add(ModBlocks.BB_LEAVES.asItem());
                        entries.add(ModBlocks.BB_LOG.asItem());
                        entries.add(ModBlocks.BB_WOOD.asItem());
                        entries.add(ModBlocks.STRIPPED_BB_LOG.asItem());
                        entries.add(ModBlocks.STRIPPED_BB_WOOD.asItem());
                        entries.add(ModBlocks.BB_PLANKS.asItem());
                        entries.add(ModBlocks.BB_STAIRS.asItem());
                        entries.add(ModBlocks.BB_SLABS.asItem());
                        entries.add(ModBlocks.BB_FENCE.asItem());
                        entries.add(ModBlocks.BB_FENCE_GATE.asItem());
                        entries.add(ModBlocks.BB_DOOR.asItem());
                        entries.add(ModBlocks.BB_TRAPDOOR.asItem());
                        entries.add(ModBlocks.BB_BUTTON.asItem());
                        entries.add(ModBlocks.BB_PRESSURE_PLATE.asItem());
                        entries.add(ModItems.BB_SIGN);
                        entries.add(ModItems.HANGING_BB_SIGN);
                        entries.add(ModItems.BB_BOAT);

                        entries.add(ModBlocks.HH_SAPLING.asItem());
                        entries.add(ModBlocks.HH_LEAVES.asItem());
                        entries.add(ModBlocks.HH_LOG.asItem());
                        entries.add(ModBlocks.HH_WOOD.asItem());
                        entries.add(ModBlocks.STRIPPED_HH_LOG.asItem());
                        entries.add(ModBlocks.STRIPPED_HH_WOOD.asItem());
                        entries.add(ModBlocks.HH_PLANKS.asItem());
                        entries.add(ModBlocks.HH_STAIRS.asItem());
                        entries.add(ModBlocks.HH_SLABS.asItem());
                        entries.add(ModBlocks.HH_FENCE.asItem());
                        entries.add(ModBlocks.HH_FENCE_GATE.asItem());
                        entries.add(ModBlocks.HH_DOOR.asItem());
                        entries.add(ModBlocks.HH_TRAPDOOR.asItem());
                        entries.add(ModBlocks.HH_BUTTON.asItem());
                        entries.add(ModBlocks.HH_PRESSURE_PLATE.asItem());
                        entries.add(ModItems.HH_SIGN);
                        entries.add(ModItems.HANGING_HH_SIGN);
                        entries.add(ModItems.HH_BOAT);
                        entries.add(ModItems.HH_CHEST_BOAT);



                        entries.add(ModBlocks.LAWN_DAISY_PATCH.asItem());
                        entries.add(ModBlocks.WONDER_DIRT.asItem());
                        entries.add(ModBlocks.WONDER_GRASS.asItem());
                        entries.add(ModBlocks.WONDER_DIRT_PATH.asItem());
                        entries.add(ModBlocks.RABBIT_HOLE.asItem());
                        entries.add(ModBlocks.FLOWER_PORTAL.asItem());
                        entries.add(ModItems.MIRROR);

                        entries.add(ModItems.MAD_HATTER_HEAD_ITEM);
                        entries.add(ModItems.CATERPILLAR_HEAD_ITEM);

                        MinecraftClient client = MinecraftClient.getInstance();
                        if (client.world != null) {
                            RecipeManager recipeManager = client.world.getRecipeManager();
                            List<RecipeEntry<TeapotRecipe>> teaRecipes = recipeManager.listAllOfType(TeapotRecipe.Type.INSTANCE);

                            Item[] fluids = new Item[]{Items.WATER_BUCKET, Items.MILK_BUCKET, Items.LAVA_BUCKET};

                            for (RecipeEntry<TeapotRecipe> entry : teaRecipes) {
                                for (Item fluid : fluids) {
                                    ItemStack teaVariant = DynamicTeaCupItem.createTeaVariant(
                                            entry.value(),
                                            fluid,
                                            displayContext.lookup()
                                    );
                                    entries.add(teaVariant);
                                }
                            }
                        }
                    }).build());

    public static void registerItemGroups() {
        DownTheRabbitHole.LOGGER.info("Registering Item Groups for " + DownTheRabbitHole.MOD_ID);
    }
}
