package net.emanueljdf09.dtrhmod.block;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.BlockFamilies;
import net.minecraft.data.BlockFamily;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ButtonBlock;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.FenceBlock;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.FlowerPotBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.PinkPetalsBlock;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {

    public static final Block LAWN_DAISY_PATCH = registerBlockWithItem("lawn_daisy_patch",
            new PinkPetalsBlock(Block.Settings.of().mapColor(MapColor.QUARTZ).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY)));

    public static final Block BLUE_MUSHROOM_BLOCK = registerBlockWithItem("blue_mushroom_block",
            new HugeMushroomBlock(Block.Settings.of().mapColor(MapColor.COLOR_BLUE)));
    public static final Block BLUE_MUSHROOM = registerBlockWithItem("blue_mushroom",
            new MushroomBlock(ModConfiguredFeatures.BIG_BLUE_MUSHROOM_SELECTOR, Block.Settings.of().mapColor(MapColor.COLOR_BLUE).noCollission().randomTicks().instabreak()));

    public static final Block YELLOW_MUSHROOM_BLOCK = registerBlockWithItem("yellow_mushroom_block",
            new HugeMushroomBlock(Block.Settings.of().mapColor(MapColor.COLOR_YELLOW)));
    public static final Block YELLOW_MUSHROOM = registerBlockWithItem("yellow_mushroom",
            new MushroomBlock(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM_SELECTOR, Block.Settings.of().mapColor(MapColor.COLOR_YELLOW).noCollission().randomTicks().instabreak()));

    public static final Block MAGENTA_MUSHROOM_BLOCK = registerBlockWithItem("magenta_mushroom_block",
            new HugeMushroomBlock(Block.Settings.of().mapColor(MapColor.COLOR_MAGENTA)));
    public static final Block MAGENTA_MUSHROOM = registerBlockWithItem("magenta_mushroom",
            new MushroomBlock(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM_SELECTOR, Block.Settings.of().mapColor(MapColor.COLOR_MAGENTA).noCollission().randomTicks().instabreak()));

    public static final Block MAD_HATTER_HAT = registerBlock("mad_hatter_hat",
            new MadHatterHatBlock(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS)
                    .isSuffocating((state, world, pos) -> false)
                    .isViewBlocking((state, world, pos) -> false)
                    .noOcclusion().noCollission()));


    public static final Block RABBIT_HOLE = registerBlockWithItem("rabbit_hole",
            new RabbitHoleBlock(Block.Settings.ofFullCopy(Blocks.MOSS_BLOCK).noCollission()));

    public static final Block EXTERIOR_PORTAL = registerBlockWithItem("exterior_portal",
            new ExteriorPortal(Block.Settings.ofFullCopy(Blocks.MOSS_BLOCK).noCollission()));

    public static final Block FLOWER_PORTAL = registerBlockWithItem("flower_portal",
            new FlowerPortal(MobEffects.DIG_SPEED, 30, Block.Settings.ofFullCopy(Blocks.ALLIUM)));

    public static final Block POTTED_FLOWER_PORTAL = registerBlock("potted_flower_portal",
            new FlowerPotBlock(FLOWER_PORTAL, Block.Settings.ofFullCopy(Blocks.POTTED_ALLIUM)));


    public static final Block EXTERIOR_CHEST = registerBlock("exterior_chest",
            new ExteriorChest(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS).strength(-1.0f, 3600000.0f)));

    public static final Block EXTERIOR_DOOR = registerBlock("exterior_door",
            new ExteriorDoor(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final Block TEAPOT_BLOCK = registerBlockWithItem("teapot_block",
            new TeapotBlock(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final Block MIRROR_BLOCK = registerBlock("mirror_block",
            new MirrorBlock(Block.Settings.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(3f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .isSuffocating((state, world, pos) -> false)
                    .isViewBlocking((state, world, pos) -> false)));

    public static final Block WONDER_GRASS = registerBlockWithItem("wonder_grass",
            new WonderGrassBlock(Block.Settings.ofFullCopy(Blocks.GRASS_BLOCK).forceSolidOn()));
    public static final Block WONDER_DIRT = registerBlockWithItem("wonder_dirt",
            new Block(Block.Settings.ofFullCopy(Blocks.DIRT)));
    public static final Block WONDER_DIRT_PATH = registerBlockWithItem("wonder_dirt_path",
            new WonderDirtPathBlock(Block.Settings.ofFullCopy(Blocks.DIRT_PATH)));

    public static final Block TH_SAPLING = registerBlockWithItem("th_sapling",
            new SaplingBlock(ModSaplingsGenerators.TH_SAPLING, Block.Settings.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final Block TH_LOG = registerBlockWithItem("th_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final Block TH_WOOD = registerBlockWithItem("th_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final Block STRIPPED_TH_LOG = registerBlockWithItem("stripped_th_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final Block STRIPPED_TH_WOOD = registerBlockWithItem("stripped_th_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final Block TH_PLANKS = registerBlockWithItem("th_planks",
            new Block(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final Block TH_STAIRS = registerBlockWithItem("th_stairs",
            new StairBlock(ModBlocks.TH_PLANKS.defaultBlockState(), Block.Settings.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final Block TH_SLABS = registerBlockWithItem("th_slabs",
            new SlabBlock(Block.Settings.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final Block TH_BUTTON = registerBlockWithItem("th_button",
            new ButtonBlock(ModBlockCollections.TH_WOOD_SET, 10, Block.Settings.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final Block TH_PRESSURE_PLATE = registerBlockWithItem("th_pressure_plate",
            new PressurePlateBlock(ModBlockCollections.TH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final Block TH_FENCE = registerBlockWithItem("th_fence",
            new FenceBlock(Block.Settings.ofFullCopy(Blocks.OAK_FENCE)));
    public static final Block TH_FENCE_GATE = registerBlockWithItem("th_fence_gate",
            new FenceGateBlock(ModBlockCollections.TH_WOOD_TYPE, Block.Settings.ofFullCopy(Blocks.OAK_FENCE_GATE)));



    public static final ResourceLocation TH_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/th");
    public static final ResourceLocation HANGING_TH_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/hanging/th");
    public static final ResourceLocation HANGING_TH_SIGN_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/hanging_signs/th");


    public static final Block TH_SIGN = registerBlock("th_sign",
            new TerraformSignBlock(TH_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_SIGN)));
    public static final Block WALL_TH_SIGN = registerBlock("wall_th_sign",
            new TerraformWallSignBlock(TH_SIGN_TEXTURE,Block.Settings.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(TH_SIGN)));
    public static final Block TH_HANGING_SIGN = registerBlock("th_hanging_sign",
            new TerraformHangingSignBlock(HANGING_TH_SIGN_TEXTURE, HANGING_TH_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final Block WALL_TH_HANGING_SIGN = registerBlock("wall_th_hanging_sign",
            new TerraformWallHangingSignBlock(HANGING_TH_SIGN_TEXTURE, HANGING_TH_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(TH_HANGING_SIGN)));

    public static final Block TH_DOOR = registerBlockWithItem("th_door",
            new DoorBlock(ModBlockCollections.TH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_DOOR)));
    public static final Block TH_TRAPDOOR = registerBlockWithItem("th_trapdoor",
            new TrapDoorBlock(ModBlockCollections.TH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final Block TH_LEAVES = registerBlockWithItem("th_leaves",
            new LeavesBlock(Block.Settings.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final Block WW_SAPLING = registerBlockWithItem("ww_sapling",
            new SaplingBlock(ModSaplingsGenerators.WW_SAPLING, Block.Settings.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final Block WW_LOG = registerBlockWithItem("ww_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final Block WW_WOOD = registerBlockWithItem("ww_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final Block STRIPPED_WW_LOG = registerBlockWithItem("stripped_ww_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final Block STRIPPED_WW_WOOD = registerBlockWithItem("stripped_ww_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final Block WW_PLANKS = registerBlockWithItem("ww_planks",
            new Block(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final Block WW_STAIRS = registerBlockWithItem("ww_stairs",
            new StairBlock(ModBlocks.WW_PLANKS.defaultBlockState(), Block.Settings.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final Block WW_SLABS = registerBlockWithItem("ww_slabs",
            new SlabBlock(Block.Settings.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final Block WW_BUTTON = registerBlockWithItem("ww_button",
            new ButtonBlock(ModBlockCollections.WW_WOOD_SET, 10, Block.Settings.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final Block WW_PRESSURE_PLATE = registerBlockWithItem("ww_pressure_plate",
            new PressurePlateBlock(ModBlockCollections.WW_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final Block WW_FENCE = registerBlockWithItem("ww_fence",
            new FenceBlock(Block.Settings.ofFullCopy(Blocks.OAK_FENCE)));
    public static final Block WW_FENCE_GATE = registerBlockWithItem("ww_fence_gate",
            new FenceGateBlock(ModBlockCollections.WW_WOOD_TYPE, Block.Settings.ofFullCopy(Blocks.OAK_FENCE_GATE)));


    public static final ResourceLocation WW_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/ww");
    public static final ResourceLocation HANGING_WW_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/hanging/ww");
    public static final ResourceLocation HANGING_WW_SIGN_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/hanging_signs/ww");


    public static final Block WW_SIGN = registerBlock("ww_sign",
            new TerraformSignBlock(WW_SIGN_TEXTURE,Block.Settings.ofFullCopy(Blocks.OAK_SIGN)));
    public static final Block WALL_WW_SIGN = registerBlock("wall_ww_sign",
            new TerraformWallSignBlock(WW_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(WW_SIGN)));
    public static final Block WW_HANGING_SIGN = registerBlock("ww_hanging_sign",
            new TerraformHangingSignBlock(HANGING_WW_SIGN_TEXTURE, HANGING_WW_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final Block WALL_WW_HANGING_SIGN = registerBlock("wall_ww_hanging_sign",
            new TerraformWallHangingSignBlock(HANGING_WW_SIGN_TEXTURE, HANGING_WW_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(WW_HANGING_SIGN)));


    public static final Block WW_DOOR = registerBlockWithItem("ww_door",
            new DoorBlock(ModBlockCollections.WW_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_DOOR)));
    public static final Block WW_TRAPDOOR = registerBlockWithItem("ww_trapdoor",
            new TrapDoorBlock(ModBlockCollections.WW_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final Block WW_LEAVES = registerBlockWithItem("ww_leaves",
            new LeavesBlock(Block.Settings.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));
    public static final Block WW_HANGING_LEAVES = registerBlock("ww_hanging_leaves",
            new WHangingLeavesHeadBlock(Block.Settings.of().sound(SoundType.VINE).noOcclusion().noCollission().randomTicks().instabreak().pushReaction(PushReaction.DESTROY)));
    public static final Block WW_HANGING_LEAVES_PLANT = registerBlock("ww_hanging_leaves_plant",
            new WHangingLeavesBodyBlock(Block.Settings.of().sound(SoundType.VINE).noOcclusion().noCollission().randomTicks().instabreak().pushReaction(PushReaction.DESTROY)));

    public static final Block BB_SAPLING = registerBlockWithItem("bb_sapling",
            new SaplingBlock(ModSaplingsGenerators.BB_SAPLING, Block.Settings.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final Block BB_LOG = registerBlockWithItem("bb_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final Block BB_WOOD = registerBlockWithItem("bb_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final Block STRIPPED_BB_LOG = registerBlockWithItem("stripped_bb_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final Block STRIPPED_BB_WOOD = registerBlockWithItem("stripped_bb_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final Block BB_PLANKS = registerBlockWithItem("bb_planks",
            new Block(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final Block BB_STAIRS = registerBlockWithItem("bb_stairs",
            new StairBlock(ModBlocks.BB_PLANKS.defaultBlockState(), Block.Settings.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final Block BB_SLABS = registerBlockWithItem("bb_slabs",
            new SlabBlock(Block.Settings.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final Block BB_BUTTON = registerBlockWithItem("bb_button",
            new ButtonBlock(ModBlockCollections.BB_WOOD_SET, 10, Block.Settings.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final Block BB_PRESSURE_PLATE = registerBlockWithItem("bb_pressure_plate",
            new PressurePlateBlock( ModBlockCollections.BB_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final Block BB_FENCE = registerBlockWithItem("bb_fence",
            new FenceBlock(Block.Settings.ofFullCopy(Blocks.OAK_FENCE)));
    public static final Block BB_FENCE_GATE = registerBlockWithItem("bb_fence_gate",
            new FenceGateBlock(ModBlockCollections.BB_WOOD_TYPE, Block.Settings.ofFullCopy(Blocks.OAK_FENCE_GATE)));


    public static final ResourceLocation BB_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/bb");
    public static final ResourceLocation HANGING_BB_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/hanging/bb");
    public static final ResourceLocation HANGING_BB_SIGN_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/hanging_signs/bb");


    public static final Block BB_SIGN = registerBlock("bb_sign",
            new TerraformSignBlock(BB_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_SIGN)));
    public static final Block WALL_BB_SIGN = registerBlock("wall_bb_sign",
            new TerraformWallSignBlock(BB_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(BB_SIGN)));
    public static final Block BB_HANGING_SIGN = registerBlock("bb_hanging_sign",
            new TerraformHangingSignBlock(HANGING_BB_SIGN_TEXTURE, HANGING_BB_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final Block WALL_BB_HANGING_SIGN = registerBlock("wall_bb_hanging_sign",
            new TerraformWallHangingSignBlock(HANGING_BB_SIGN_TEXTURE, HANGING_BB_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(BB_HANGING_SIGN)));


    public static final Block BB_DOOR = registerBlockWithItem("bb_door",
            new DoorBlock(ModBlockCollections.BB_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_DOOR)));
    public static final Block BB_TRAPDOOR = registerBlockWithItem("bb_trapdoor",
            new TrapDoorBlock(ModBlockCollections.BB_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final Block BB_LEAVES = registerBlockWithItem("bb_leaves",
            new LeavesBlock(Block.Settings.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final Block HH_SAPLING = registerBlockWithItem("hh_sapling",
            new SaplingBlock(ModSaplingsGenerators.HH_SAPLING, Block.Settings.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final Block HH_LOG = registerBlockWithItem("hh_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final Block HH_WOOD = registerBlockWithItem("hh_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final Block STRIPPED_HH_LOG = registerBlockWithItem("stripped_hh_log",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final Block STRIPPED_HH_WOOD = registerBlockWithItem("stripped_hh_wood",
            new RotatedPillarBlock(Block.Settings.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final Block HH_PLANKS = registerBlockWithItem("hh_planks",
            new Block(Block.Settings.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final Block HH_STAIRS = registerBlockWithItem("hh_stairs",
            new StairBlock(ModBlocks.HH_PLANKS.defaultBlockState(), Block.Settings.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final Block HH_SLABS = registerBlockWithItem("hh_slabs",
            new SlabBlock(Block.Settings.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final Block HH_BUTTON = registerBlockWithItem("hh_button",
            new ButtonBlock(ModBlockCollections.HH_WOOD_SET, 10, Block.Settings.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final Block HH_PRESSURE_PLATE = registerBlockWithItem("hh_pressure_plate",
            new PressurePlateBlock(ModBlockCollections.HH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final Block HH_FENCE = registerBlockWithItem("hh_fence",
            new FenceBlock(Block.Settings.ofFullCopy(Blocks.OAK_FENCE)));
    public static final Block HH_FENCE_GATE = registerBlockWithItem("hh_fence_gate",
            new FenceGateBlock(ModBlockCollections.HH_WOOD_TYPE, Block.Settings.ofFullCopy(Blocks.OAK_FENCE_GATE)));


    public static final ResourceLocation HH_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/hh");
    public static final ResourceLocation HANGING_HH_SIGN_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "entity/signs/hanging/hh");
    public static final ResourceLocation HANGING_HH_SIGN_GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/hanging_signs/hh");


    public static final Block HH_SIGN = registerBlock("hh_sign",
            new TerraformSignBlock(HH_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_SIGN)));
    public static final Block WALL_HH_SIGN = registerBlock("wall_hh_sign",
            new TerraformWallSignBlock(HH_SIGN_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(HH_SIGN)));
    public static final Block HH_HANGING_SIGN = registerBlock("hh_hanging_sign",
            new TerraformHangingSignBlock(HANGING_HH_SIGN_TEXTURE, HANGING_HH_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final Block WALL_HH_HANGING_SIGN = registerBlock("wall_hh_hanging_sign",
            new TerraformWallHangingSignBlock(HANGING_HH_SIGN_TEXTURE, HANGING_HH_SIGN_GUI_TEXTURE, Block.Settings.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(HH_HANGING_SIGN)));


    public static final Block HH_DOOR = registerBlockWithItem("hh_door",
            new DoorBlock(ModBlockCollections.HH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_DOOR)));
    public static final Block HH_TRAPDOOR = registerBlockWithItem("hh_trapdoor",
            new TrapDoorBlock(ModBlockCollections.HH_WOOD_SET, Block.Settings.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final Block HH_LEAVES = registerBlockWithItem("hh_leaves",
            new LeavesBlock(Block.Settings.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final Block MAD_HATTER_HEAD = registerBlock("mad_hatter_head",
            new ModHeadBlock(ModHeadType.MAD_HATTER, Block.Settings.ofFullCopy(Blocks.PLAYER_HEAD)));

    public static final Block MAD_HATTER_WALL_HEAD = registerBlock("mad_hatter_wall_head",
            new ModWallHeadBlock(ModHeadType.MAD_HATTER, Block.Settings.ofFullCopy(Blocks.PLAYER_WALL_HEAD).dropsLike(MAD_HATTER_HEAD)));

    public static final Block CATERPILLAR_HEAD = registerBlock("caterpillar_head",
            new ModHeadBlock(ModHeadType.CATERPILLAR, Block.Settings.ofFullCopy(Blocks.PLAYER_HEAD)));

    public static final Block CATERPILLAR_WALL_HEAD = registerBlock("caterpillar_wall_head",
            new ModWallHeadBlock(ModHeadType.CATERPILLAR, Block.Settings.ofFullCopy(Blocks.PLAYER_WALL_HEAD).dropsLike(MAD_HATTER_HEAD)));

    public static final Block MAD_HATTER_SPAWNER_BLOCK = registerBlockWithItem("mad_hatter_spawner_block",
            new MadHatterSpawnerBlock(Block.Settings.ofFullCopy(Blocks.BLACK_WOOL)));

    public static final Block CHESHIRE_CAT_SPAWNER_BLOCK = registerBlockWithItem("cheshire_cat_spawner_block",
            new CheshireCatSpawnerBlock(Block.Settings.ofFullCopy(Blocks.BLACK_WOOL).noCollission().noLootTable()));

    public static final Block SINGING_FLOWERS_SPAWNER_BLOCK = registerBlockWithItem("singing_flowers_spawner_block",
            new SingingFlowersSpawnerBlock(Block.Settings.ofFullCopy(Blocks.BLACK_WOOL).noLootTable()));

    public static final Block CATERPILLAR_SPAWNER_BLOCK = registerBlockWithItem("caterpillar_spawner_block",
            new CaterpillarSpawnerBlock(Block.Settings.ofFullCopy(Blocks.BLACK_WOOL).noLootTable()));


    public static BlockFamily TH_FAMILY = BlockFamilies.familyBuilder(ModBlocks.TH_PLANKS)
            .button(ModBlocks.TH_BUTTON)
            .door(ModBlocks.TH_DOOR)
            .fence(ModBlocks.TH_FENCE)
            .fenceGate(ModBlocks.TH_FENCE_GATE)
            .stairs(ModBlocks.TH_STAIRS)
            .slab(ModBlocks.TH_SLABS)
            .pressurePlate(ModBlocks.TH_PRESSURE_PLATE)
            .sign(ModBlocks.TH_SIGN, ModBlocks.WALL_TH_SIGN)
            .trapdoor(ModBlocks.TH_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static BlockFamily BB_FAMILY = BlockFamilies.familyBuilder(ModBlocks.BB_PLANKS)
            .button(ModBlocks.BB_BUTTON)
            .door(ModBlocks.BB_DOOR)
            .fence(ModBlocks.BB_FENCE)
            .fenceGate(ModBlocks.BB_FENCE_GATE)
            .stairs(ModBlocks.BB_STAIRS)
            .slab(ModBlocks.BB_SLABS)
            .pressurePlate(ModBlocks.BB_PRESSURE_PLATE)
            .sign(ModBlocks.BB_SIGN, ModBlocks.WALL_BB_SIGN)
            .trapdoor(ModBlocks.BB_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static BlockFamily WW_FAMILY = BlockFamilies.familyBuilder(ModBlocks.WW_PLANKS)
            .button(ModBlocks.WW_BUTTON)
            .door(ModBlocks.WW_DOOR)
            .fence(ModBlocks.WW_FENCE)
            .fenceGate(ModBlocks.WW_FENCE_GATE)
            .stairs(ModBlocks.WW_STAIRS)
            .slab(ModBlocks.WW_SLABS)
            .pressurePlate(ModBlocks.WW_PRESSURE_PLATE)
            .sign(ModBlocks.WW_SIGN, ModBlocks.WALL_WW_SIGN)
            .trapdoor(ModBlocks.WW_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();

    public static BlockFamily HH_FAMILY = BlockFamilies.familyBuilder(ModBlocks.HH_PLANKS)
            .button(ModBlocks.HH_BUTTON)
            .door(ModBlocks.HH_DOOR)
            .fence(ModBlocks.HH_FENCE)
            .fenceGate(ModBlocks.HH_FENCE_GATE)
            .stairs(ModBlocks.HH_STAIRS)
            .slab(ModBlocks.HH_SLABS)
            .pressurePlate(ModBlocks.HH_PRESSURE_PLATE)
            .sign(ModBlocks.HH_SIGN, ModBlocks.WALL_HH_SIGN)
            .trapdoor(ModBlocks.HH_TRAPDOOR)
            .recipeGroupPrefix("wooden")
            .recipeUnlockedBy("has_planks")
            .getFamily();


    private static Block registerBlock(String name, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name), block);
    }

    private static Block registerBlockWithItem(String name, Block block) {
        registerBlockItem(name, block);
        return Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name), block);
    }

    private static Item registerBlockItem(String name, Block block) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name),
                new BlockItem(block, new Item.Properties()));
    }

    public static void registerModBlock() {
        DownTheRabbitHole.LOGGER.info("Registering ModBlocks for " + DownTheRabbitHole.MOD_ID);
    }
}
