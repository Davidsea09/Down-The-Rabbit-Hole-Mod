package net.emanueljdf09.dtrhmod.datagen;

import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.data.models.BlockModelGenerators;
import net.minecraft.data.models.ItemModelGenerators;
import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.data.models.model.ModelTemplates;
import net.minecraft.world.item.Item;

public class ModModelProvider extends FabricModelProvider {
    public ModModelProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {

        blockStateModelGenerator.createFlowerBed(ModBlocks.LAWN_DAISY_PATCH);

        blockStateModelGenerator.createTrivialCube(ModBlocks.MAD_HATTER_SPAWNER_BLOCK);
        blockStateModelGenerator.createTrivialCube(ModBlocks.CHESHIRE_CAT_SPAWNER_BLOCK);

        blockStateModelGenerator.createPlant(ModBlocks.FLOWER_PORTAL, ModBlocks.POTTED_FLOWER_PORTAL, BlockStateModelGenerator.TintType.NOT_TINTED);



        blockStateModelGenerator.createMushroomBlock(ModBlocks.BLUE_MUSHROOM_BLOCK);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.BLUE_MUSHROOM, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.createMushroomBlock(ModBlocks.YELLOW_MUSHROOM_BLOCK);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.YELLOW_MUSHROOM, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.createMushroomBlock(ModBlocks.MAGENTA_MUSHROOM_BLOCK);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.MAGENTA_MUSHROOM, BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.createTrivialCube(ModBlocks.EXTERIOR_PORTAL);

        blockStateModelGenerator.createTrivialCube(ModBlocks.WONDER_DIRT);

        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.TH_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.WW_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.BB_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);
        blockStateModelGenerator.createCrossBlockWithDefaultItem(ModBlocks.HH_SAPLING, BlockStateModelGenerator.TintType.NOT_TINTED);


        blockStateModelGenerator.woodProvider(ModBlocks.TH_LOG).log(ModBlocks.TH_LOG).wood(ModBlocks.TH_WOOD);
        blockStateModelGenerator.woodProvider(ModBlocks.STRIPPED_TH_LOG).log(ModBlocks.STRIPPED_TH_LOG).wood(ModBlocks.STRIPPED_TH_WOOD);
        blockStateModelGenerator.createTrivialCube(ModBlocks.TH_LEAVES);

        blockStateModelGenerator.family(ModBlocks.TH_PLANKS).family(ModBlocks.TH_FAMILY);

        blockStateModelGenerator.woodProvider(ModBlocks.WW_LOG).log(ModBlocks.WW_LOG).wood(ModBlocks.WW_WOOD);
        blockStateModelGenerator.woodProvider(ModBlocks.STRIPPED_WW_LOG).log(ModBlocks.STRIPPED_WW_LOG).wood(ModBlocks.STRIPPED_WW_WOOD);
        blockStateModelGenerator.createTrivialCube(ModBlocks.WW_LEAVES);
        blockStateModelGenerator.createGrowingPlant(ModBlocks.WW_HANGING_LEAVES_PLANT, ModBlocks.WW_HANGING_LEAVES, BlockStateModelGenerator.TintType.NOT_TINTED);

        blockStateModelGenerator.family(ModBlocks.WW_PLANKS).family(ModBlocks.WW_FAMILY);

        blockStateModelGenerator.woodProvider(ModBlocks.BB_LOG).log(ModBlocks.BB_LOG).wood(ModBlocks.BB_WOOD);
        blockStateModelGenerator.woodProvider(ModBlocks.STRIPPED_BB_LOG).log(ModBlocks.STRIPPED_BB_LOG).wood(ModBlocks.STRIPPED_BB_WOOD);
        blockStateModelGenerator.createTrivialCube(ModBlocks.BB_LEAVES);

        blockStateModelGenerator.family(ModBlocks.BB_PLANKS).family(ModBlocks.BB_FAMILY);

        blockStateModelGenerator.woodProvider(ModBlocks.HH_LOG).log(ModBlocks.HH_LOG).wood(ModBlocks.HH_WOOD);
        blockStateModelGenerator.woodProvider(ModBlocks.STRIPPED_HH_LOG).log(ModBlocks.STRIPPED_HH_LOG).wood(ModBlocks.STRIPPED_HH_WOOD);
        blockStateModelGenerator.createTrivialCube(ModBlocks.HH_LEAVES);

        blockStateModelGenerator.family(ModBlocks.HH_PLANKS).family(ModBlocks.HH_FAMILY);

        blockStateModelGenerator.delegateItemModel(ModItems.WHITE_RABBIT_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.WEEPING_PLAYERS_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.MAD_HATTER_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.CHESHIRE_CAT_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.SINGING_FLOWER_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.MOCK_TURTLE_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.CATERPILLAR_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));
        blockStateModelGenerator.delegateItemModel(ModItems.HARE_SPAWN_EGG, ModelLocationUtils.decorateItemModelLocation("template_spawn_egg"));




    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {


        for (Item item : ModItems.STORYBOOK_ITEMS) {
            itemModelGenerator.generateFlatItem(item, ModelTemplates.FLAT_ITEM);
        }

        itemModelGenerator.generateFlatItem(ModItems.WW_HANGING_LEAVES, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.MIRROR, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.HANGING_WW_SIGN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.HANGING_HH_SIGN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.HANGING_BB_SIGN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.HANGING_TH_SIGN, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.WW_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.WW_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.HH_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.HH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.TH_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.TH_CHEST_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BB_BOAT, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.BB_CHEST_BOAT, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.POCKETWATCH, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.EMPTY_CUP, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.DRINK_ME, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.EAT_ME, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.EXTERIOR_KEY, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CAT_FUR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.CAT_GRIN, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.VINE_LASSO_ITEM, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(ModItems.PIPE_ITEM, ModelTemplates.FLAT_ITEM);

        itemModelGenerator.generateFlatItem(ModItems.MOCK_TURTLE_STEW, ModelTemplates.FLAT_ITEM);
    }


}
