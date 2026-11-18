package net.emanueljdf09.dtrhmod.datagen;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.recipe.HatRitualRecipeJsonBuilder;
import net.emanueljdf09.dtrhmod.fabric.recipe.TeapotRecipeJsonBuilder;
import net.emanueljdf09.dtrhmod.fabric.util.ModEffects;
import net.emanueljdf09.dtrhmod.fabric.util.ModTags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeOutput exporter) {

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.OAK_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.WEAKNESS, 200, 0)
                .criterion(hasItem(Items.OAK_LEAVES), conditionsFromItem(Items.OAK_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "oak_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.SWEET_BERRIES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.MOVEMENT_SLOWDOWN, 200, 0)
                .criterion(hasItem(Items.SWEET_BERRIES), conditionsFromItem(Items.SWEET_BERRIES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "berry_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.BIRCH_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.MOVEMENT_SPEED, 200, 0)
                .criterion(hasItem(Items.BIRCH_LEAVES), conditionsFromItem(Items.BIRCH_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "birch_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.JUNGLE_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.JUMP, 200, 0)
                .criterion(hasItem(Items.JUNGLE_LEAVES), conditionsFromItem(Items.JUNGLE_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "jungle_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.MANGROVE_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.WATER_BREATHING, 200, 0)
                .criterion(hasItem(Items.MANGROVE_LEAVES), conditionsFromItem(Items.MANGROVE_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mangrove_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.DARK_OAK_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.HEAL, 200, 0)
                .criterion(hasItem(Items.DARK_OAK_LEAVES), conditionsFromItem(Items.DARK_OAK_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "dark_oak_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.ACACIA_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.DAMAGE_BOOST, 200, 0)
                .criterion(hasItem(Items.ACACIA_LEAVES), conditionsFromItem(Items.ACACIA_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "acacia_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.RED_MUSHROOM)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.HARM, 200, 0)
                .criterion(hasItem(Items.RED_MUSHROOM), conditionsFromItem(Items.RED_MUSHROOM))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mushroom_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.CHERRY_LEAVES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.REGENERATION, 200, 0)
                .criterion(hasItem(Items.CHERRY_LEAVES), conditionsFromItem(Items.CHERRY_LEAVES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "cherry_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.GLOW_BERRIES)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.NIGHT_VISION, 200, 0)
                .criterion(hasItem(Items.GLOW_BERRIES), conditionsFromItem(Items.GLOW_BERRIES))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "glow_berry_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.FIRE_RESISTANCE, 200, 0)
                .criterion(hasItem(Items.CRIMSON_FUNGUS), conditionsFromItem(Items.CRIMSON_FUNGUS))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "crimson_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.BROWN_MUSHROOM)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.POISON, 200, 0)
                .criterion(hasItem(Items.BROWN_MUSHROOM), conditionsFromItem(Items.BROWN_MUSHROOM))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "brown_mushroom_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(Items.WARPED_FUNGUS)
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.INVISIBILITY, 200, 0)
                .criterion(hasItem(Items.WARPED_FUNGUS), conditionsFromItem(Items.WARPED_FUNGUS))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "warped_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(ModBlocks.BLUE_MUSHROOM.asItem())
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(ModEffects.SHRINK, 200, 0)
                .criterion(hasItem(ModBlocks.BLUE_MUSHROOM.asItem()), conditionsFromItem(ModBlocks.BLUE_MUSHROOM.asItem()))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "blue_mushroom_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(ModBlocks.MAGENTA_MUSHROOM.asItem())
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(ModEffects.GROW, 200, 0)
                .criterion(hasItem(ModBlocks.MAGENTA_MUSHROOM.asItem()), conditionsFromItem(ModBlocks.MAGENTA_MUSHROOM.asItem()))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "magenta_mushroom_tea"));

        TeapotRecipeJsonBuilder.create(ModItems.FILLED_TEA_CUP)
                .addIngredient(ModBlocks.YELLOW_MUSHROOM.asItem())
                .fluid(Items.WATER_BUCKET)
                .cupCount(1)
                .addEffect(MobEffects.LUCK, 200, 0)
                .criterion(hasItem(ModBlocks.YELLOW_MUSHROOM.asItem()), conditionsFromItem(ModBlocks.YELLOW_MUSHROOM.asItem()))
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "yellow_mushroom_tea"));


        HatRitualRecipeJsonBuilder.create(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "storybook"), true)
                .structureId(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "portal/mirror_room_complete"))
                .addIngredient(ModItems.SNOW_WHITE_STORYBOOK)
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "snow_white_hat_ritual"));

        HatRitualRecipeJsonBuilder.create(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "wonderland"), false)
                .addIngredient(ModItems.POCKETWATCH)
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "wonderland_hat_ritual"));

        HatRitualRecipeJsonBuilder.create(Level.OVERWORLD.location(), false)
                .addIngredient(Items.COBBLESTONE)
                .addIngredient(Items.GRASS_BLOCK)
                .addIngredient(Items.APPLE)
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "overworld_hat_ritual"));

        HatRitualRecipeJsonBuilder.create(Level.NETHER.location(), false)
                .addIngredient(Items.OBSIDIAN)
                .addIngredient(Items.NETHERRACK)
                .addIngredient(Items.CRIMSON_FUNGUS)
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "nether_hat_ritual"));

        HatRitualRecipeJsonBuilder.create(Level.END.location(), false)
                .addIngredient(Items.END_STONE)
                .addIngredient(Items.GHAST_TEAR)
                .addIngredient(Items.CHORUS_FRUIT)
                .offerTo(exporter, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "end_hat_ritual"));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.TEAPOT_BLOCK.asItem(), 1)
                .pattern("NIN")
                .pattern("IBI")
                .pattern("IPI")
                .input('N', Items.IRON_NUGGET)
                .input('I', Items.IRON_INGOT)
                .input('B', Items.BUCKET)
                .input('P', Items.IRON_BLOCK)
                .criterion(hasItem(Items.IRON_INGOT), conditionsFromItem(Items.IRON_INGOT))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(ModBlocks.TEAPOT_BLOCK)));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.POCKETWATCH, 1)
                .pattern("NNN")
                .pattern("N N")
                .pattern(" C ")
                .input('N', Items.GOLD_NUGGET)
                .input('C', Items.CLOCK)
                .criterion(hasItem(Items.CLOCK), conditionsFromItem(Items.CLOCK))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(ModItems.POCKETWATCH)));

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EMPTY_CUP, 5)
                .pattern("   ")
                .pattern("G G")
                .pattern("GGG")
                .input('G', Items.GLASS)
                .criterion(hasItem(Items.GLASS), conditionsFromItem(Items.GLASS))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(ModItems.EMPTY_CUP)));

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.CAT_SCARF_ITEM, 1)
                .pattern("CCC")
                .pattern("CWC")
                .pattern("C W")
                .input('C', ModItems.CAT_FUR)
                .input('W', Items.BLACK_WOOL)
                .criterion(hasItem(ModItems.CAT_FUR), conditionsFromItem(ModItems.CAT_FUR))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(ModItems.CAT_SCARF_ITEM)));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.LIGHT_GRAY_DYE, 1)
                .requires(ModBlocks.LAWN_DAISY_PATCH.asItem())
                .criterion(hasItem(ModBlocks.LAWN_DAISY_PATCH.asItem()), conditionsFromItem(ModBlocks.LAWN_DAISY_PATCH.asItem()))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(Items.LIGHT_GRAY_DYE)));

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.WHITE_DYE, 1)
                .requires(ModBlocks.FLOWER_PORTAL.asItem())
                .criterion(hasItem(ModBlocks.FLOWER_PORTAL.asItem()), conditionsFromItem(ModBlocks.FLOWER_PORTAL.asItem()))
                .offerTo(exporter, ResourceLocation.parse(getRecipeName(Items.WHITE_DYE)));


        offerWoodRecipe(exporter, ModBlocks.TH_WOOD, ModBlocks.TH_LOG);
        offerWoodRecipe(exporter, ModBlocks.STRIPPED_TH_WOOD, ModBlocks.STRIPPED_TH_LOG);
        offerPlanksRecipe(exporter, ModBlocks.TH_PLANKS, ModTags.Items.TH_LOGS, 4);
        generateFamily(exporter, ModBlocks.TH_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
        offerHangingSignRecipe(exporter, ModItems.HANGING_TH_SIGN, ModBlocks.STRIPPED_TH_LOG);
        offerBoatRecipe(exporter, ModItems.TH_BOAT, ModBlocks.TH_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.TH_CHEST_BOAT, ModBlocks.TH_PLANKS);

        offerWoodRecipe(exporter, ModBlocks.BB_WOOD, ModBlocks.BB_LOG);
        offerWoodRecipe(exporter, ModBlocks.STRIPPED_BB_WOOD, ModBlocks.STRIPPED_BB_LOG);
        offerPlanksRecipe(exporter, ModBlocks.BB_PLANKS, ModTags.Items.BB_LOGS, 4);
        generateFamily(exporter, ModBlocks.BB_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
        offerHangingSignRecipe(exporter, ModItems.HANGING_BB_SIGN, ModBlocks.STRIPPED_BB_LOG);
        offerBoatRecipe(exporter, ModItems.BB_BOAT, ModBlocks.BB_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.BB_CHEST_BOAT, ModBlocks.BB_PLANKS);

        offerWoodRecipe(exporter, ModBlocks.WW_WOOD, ModBlocks.WW_LOG);
        offerWoodRecipe(exporter, ModBlocks.STRIPPED_WW_WOOD, ModBlocks.STRIPPED_WW_LOG);
        offerPlanksRecipe(exporter, ModBlocks.WW_PLANKS, ModTags.Items.WW_LOGS, 4);
        generateFamily(exporter, ModBlocks.WW_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
        offerHangingSignRecipe(exporter, ModItems.HANGING_WW_SIGN, ModBlocks.STRIPPED_WW_LOG);
        offerBoatRecipe(exporter, ModItems.WW_BOAT, ModBlocks.WW_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.WW_CHEST_BOAT, ModBlocks.WW_PLANKS);

        offerWoodRecipe(exporter, ModBlocks.HH_WOOD, ModBlocks.HH_LOG);
        offerWoodRecipe(exporter, ModBlocks.STRIPPED_HH_WOOD, ModBlocks.STRIPPED_HH_LOG);
        offerPlanksRecipe(exporter, ModBlocks.HH_PLANKS, ModTags.Items.HH_LOGS, 4);
        generateFamily(exporter, ModBlocks.HH_FAMILY, FeatureFlagSet.of(FeatureFlags.VANILLA));
        offerHangingSignRecipe(exporter, ModItems.HANGING_HH_SIGN, ModBlocks.STRIPPED_HH_LOG);
        offerBoatRecipe(exporter, ModItems.HH_BOAT, ModBlocks.HH_PLANKS);
        offerChestBoatRecipe(exporter, ModItems.HH_CHEST_BOAT, ModBlocks.HH_PLANKS);

    }

    public static void offerWoodRecipe(RecipeOutput exporter, ItemLike output, ItemLike input) {
        createWoodRecipe(output, Ingredient.of(input))
                .unlockedBy(hasItem(input), conditionsFromItem(input))
                .offerTo(exporter);
    }

    public static RecipeBuilder createWoodRecipe(ItemLike output, Ingredient input) {
        return ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, output, 3)
                .pattern("LL")
                .pattern("LL")
                .define('L', input);
    }
}