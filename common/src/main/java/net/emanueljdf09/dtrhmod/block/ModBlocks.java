package net.emanueljdf09.dtrhmod.block;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.custom.*;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadType;
import net.emanueljdf09.dtrhmod.block.custom.head.ModWallHeadBlock;
import net.emanueljdf09.dtrhmod.block.custom.mirror.MirrorBlock;
import net.emanueljdf09.dtrhmod.block.custom.signs.ModCeilingHangingSignBlock;
import net.emanueljdf09.dtrhmod.block.custom.signs.ModStandingSignBlock;
import net.emanueljdf09.dtrhmod.block.custom.signs.ModWallHangingSignBlock;
import net.emanueljdf09.dtrhmod.block.custom.signs.ModWallSignBlock;
import net.emanueljdf09.dtrhmod.block.custom.spawn.CaterpillarSpawnerBlock;
import net.emanueljdf09.dtrhmod.block.custom.spawn.CheshireCatSpawnerBlock;
import net.emanueljdf09.dtrhmod.block.custom.spawn.MadHatterSpawnerBlock;
import net.emanueljdf09.dtrhmod.block.custom.spawn.SingingFlowersSpawnerBlock;
import net.emanueljdf09.dtrhmod.world.features.ModConfiguredFeatures;
import net.emanueljdf09.dtrhmod.world.features.tree.sapling.ModSaplingsGenerators;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.BlockFamily;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.BLOCK);

    public static final RegistrySupplier<Block> LAWN_DAISY_PATCH = BLOCKS.register("lawn_daisy_patch", () ->
            new PinkPetalsBlock(BlockBehaviour.Properties.of().mapColor(MapColor.QUARTZ).noCollission().sound(SoundType.PINK_PETALS).pushReaction(PushReaction.DESTROY)));

    public static final RegistrySupplier<Block> BLUE_MUSHROOM_BLOCK = BLOCKS.register("blue_mushroom_block",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE)));
    public static final RegistrySupplier<Block> BLUE_MUSHROOM = BLOCKS.register("blue_mushroom",
            () -> new MushroomBlock(ModConfiguredFeatures.BIG_BLUE_MUSHROOM_SELECTOR, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BLUE).noCollission().randomTicks().instabreak()));

    public static final RegistrySupplier<Block> YELLOW_MUSHROOM_BLOCK = BLOCKS.register("yellow_mushroom_block",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW)));
    public static final RegistrySupplier<Block> YELLOW_MUSHROOM = BLOCKS.register("yellow_mushroom",
            () -> new MushroomBlock(ModConfiguredFeatures.BIG_YELLOW_MUSHROOM_SELECTOR, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).noCollission().randomTicks().instabreak()));

    public static final RegistrySupplier<Block> MAGENTA_MUSHROOM_BLOCK = BLOCKS.register("magenta_mushroom_block",
            () -> new HugeMushroomBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA)));
    public static final RegistrySupplier<Block> MAGENTA_MUSHROOM = BLOCKS.register("magenta_mushroom",
            () -> new MushroomBlock(ModConfiguredFeatures.BIG_MAGENTA_MUSHROOM_SELECTOR, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_MAGENTA).noCollission().randomTicks().instabreak()));

    public static final RegistrySupplier<Block> MAD_HATTER_HAT = BLOCKS.register("mad_hatter_hat",
            () -> new MadHatterHatBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)
                    .isSuffocating((state, world, pos) -> false)
                    .isViewBlocking((state, world, pos) -> false)
                    .noOcclusion().noCollission()));


    public static final RegistrySupplier<Block> RABBIT_HOLE = BLOCKS.register("rabbit_hole",
            () -> new RabbitHoleBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).noCollission()));

    public static final RegistrySupplier<Block> EXTERIOR_PORTAL = BLOCKS.register("exterior_portal",
            () -> new ExteriorPortal(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).noCollission()));

    public static final RegistrySupplier<Block> FLOWER_PORTAL = BLOCKS.register("flower_portal",
            () -> new FlowerPortal(MobEffects.DIG_SPEED, 30, BlockBehaviour.Properties.ofFullCopy(Blocks.ALLIUM)));

    public static final RegistrySupplier<Block> POTTED_FLOWER_PORTAL = BLOCKS.register("potted_flower_portal",
            () -> new FlowerPotBlock(FLOWER_PORTAL.get(), BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_ALLIUM)));


    public static final RegistrySupplier<Block> EXTERIOR_CHEST = BLOCKS.register("exterior_chest",
            () -> new ExteriorChest(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(-1.0f, 3600000.0f)));

    public static final RegistrySupplier<Block> EXTERIOR_DOOR = BLOCKS.register("exterior_door",
            () -> new ExteriorDoor(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final RegistrySupplier<Block> TEAPOT_BLOCK = BLOCKS.register("teapot_block",
            () -> new TeapotBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS)));

    public static final RegistrySupplier<Block> MIRROR_BLOCK = BLOCKS.register("mirror_block",
            () -> new MirrorBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK)
                    .strength(3f, 1200.0f)
                    .requiresCorrectToolForDrops()
                    .isSuffocating((state, world, pos) -> false)
                    .isViewBlocking((state, world, pos) -> false)));

    public static final RegistrySupplier<Block> WONDER_GRASS = BLOCKS.register("wonder_grass",
            () -> new WonderGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).forceSolidOn()));
    public static final RegistrySupplier<Block> WONDER_DIRT = BLOCKS.register("wonder_dirt",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));
    public static final RegistrySupplier<Block> WONDER_DIRT_PATH = BLOCKS.register("wonder_dirt_path",
            () -> new WonderDirtPathBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT_PATH)));

    public static final RegistrySupplier<Block> TH_SAPLING = BLOCKS.register("th_sapling",
            () -> new SaplingBlock(ModSaplingsGenerators.TH_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final RegistrySupplier<Block> TH_LOG = BLOCKS.register("th_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> TH_WOOD = BLOCKS.register("th_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_TH_LOG = BLOCKS.register("stripped_th_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_TH_WOOD = BLOCKS.register("stripped_th_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final RegistrySupplier<Block> TH_PLANKS = BLOCKS.register("th_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final RegistrySupplier<Block> TH_STAIRS = BLOCKS.register("th_stairs",
            () -> new StairBlock(ModBlocks.TH_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final RegistrySupplier<Block> TH_SLABS = BLOCKS.register("th_slabs",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final RegistrySupplier<Block> TH_BUTTON = BLOCKS.register("th_button",
            () -> new ButtonBlock(ModBlockCollections.TH_WOOD_SET, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final RegistrySupplier<Block> TH_PRESSURE_PLATE = BLOCKS.register("th_pressure_plate",
            () -> new PressurePlateBlock(ModBlockCollections.TH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final RegistrySupplier<Block> TH_FENCE = BLOCKS.register("th_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final RegistrySupplier<Block> TH_FENCE_GATE = BLOCKS.register("th_fence_gate",
            () -> new FenceGateBlock(ModBlockCollections.TH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final RegistrySupplier<Block> TH_SIGN = BLOCKS.register("th_sign",
            () -> new ModStandingSignBlock(ModBlockCollections.TH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
    public static final RegistrySupplier<Block> WALL_TH_SIGN = BLOCKS.register("wall_th_sign",
            () -> new ModWallSignBlock(ModBlockCollections.TH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(TH_SIGN.get())));
    public static final RegistrySupplier<Block> TH_HANGING_SIGN = BLOCKS.register("th_hanging_sign",
            () -> new ModCeilingHangingSignBlock(ModBlockCollections.TH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final RegistrySupplier<Block> WALL_TH_HANGING_SIGN = BLOCKS.register("wall_th_hanging_sign",
            () -> new ModWallHangingSignBlock(ModBlockCollections.TH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(TH_HANGING_SIGN.get())));

    public static final RegistrySupplier<Block> TH_DOOR = BLOCKS.register("th_door",
            () -> new DoorBlock(ModBlockCollections.TH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));
    public static final RegistrySupplier<Block> TH_TRAPDOOR = BLOCKS.register("th_trapdoor",
            () -> new TrapDoorBlock(ModBlockCollections.TH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final RegistrySupplier<Block> TH_LEAVES = BLOCKS.register("th_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final RegistrySupplier<Block> WW_SAPLING = BLOCKS.register("ww_sapling",
            () -> new SaplingBlock(ModSaplingsGenerators.WW_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final RegistrySupplier<Block> WW_LOG = BLOCKS.register("ww_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> WW_WOOD = BLOCKS.register("ww_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_WW_LOG = BLOCKS.register("stripped_ww_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_WW_WOOD = BLOCKS.register("stripped_ww_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final RegistrySupplier<Block> WW_PLANKS = BLOCKS.register("ww_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final RegistrySupplier<Block> WW_STAIRS = BLOCKS.register("ww_stairs",
            () -> new StairBlock(ModBlocks.WW_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final RegistrySupplier<Block> WW_SLABS = BLOCKS.register("ww_slabs",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final RegistrySupplier<Block> WW_BUTTON = BLOCKS.register("ww_button",
            () -> new ButtonBlock(ModBlockCollections.WW_WOOD_SET, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final RegistrySupplier<Block> WW_PRESSURE_PLATE = BLOCKS.register("ww_pressure_plate",
            () -> new PressurePlateBlock(ModBlockCollections.WW_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final RegistrySupplier<Block> WW_FENCE = BLOCKS.register("ww_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final RegistrySupplier<Block> WW_FENCE_GATE = BLOCKS.register("ww_fence_gate",
            () -> new FenceGateBlock(ModBlockCollections.WW_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final RegistrySupplier<Block> WW_SIGN = BLOCKS.register("ww_sign",
            () -> new ModStandingSignBlock(ModBlockCollections.WW_WOOD_TYPE,BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
    public static final RegistrySupplier<Block> WALL_WW_SIGN = BLOCKS.register("wall_ww_sign",
            () -> new ModWallSignBlock(ModBlockCollections.WW_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(WW_SIGN.get())));
    public static final RegistrySupplier<Block> WW_HANGING_SIGN = BLOCKS.register("ww_hanging_sign",
            () -> new ModCeilingHangingSignBlock(ModBlockCollections.WW_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final RegistrySupplier<Block> WALL_WW_HANGING_SIGN = BLOCKS.register("wall_ww_hanging_sign",
            () -> new ModWallHangingSignBlock(ModBlockCollections.WW_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(WW_HANGING_SIGN.get())));


    public static final RegistrySupplier<Block> WW_DOOR = BLOCKS.register("ww_door",
            () -> new DoorBlock(ModBlockCollections.WW_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));
    public static final RegistrySupplier<Block> WW_TRAPDOOR = BLOCKS.register("ww_trapdoor",
            () -> new TrapDoorBlock(ModBlockCollections.WW_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final RegistrySupplier<Block> WW_LEAVES = BLOCKS.register("ww_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));
    public static final RegistrySupplier<Block> WW_HANGING_LEAVES = BLOCKS.register("ww_hanging_leaves",
            () -> new WHangingLeavesHeadBlock(BlockBehaviour.Properties.of().sound(SoundType.VINE).noOcclusion().noCollission().randomTicks().instabreak().pushReaction(PushReaction.DESTROY)));
    public static final RegistrySupplier<Block> WW_HANGING_LEAVES_PLANT = BLOCKS.register("ww_hanging_leaves_plant",
            () -> new WHangingLeavesBodyBlock(BlockBehaviour.Properties.of().sound(SoundType.VINE).noOcclusion().noCollission().randomTicks().instabreak().pushReaction(PushReaction.DESTROY)));

    public static final RegistrySupplier<Block> BB_SAPLING = BLOCKS.register("bb_sapling",
            () -> new SaplingBlock(ModSaplingsGenerators.BB_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final RegistrySupplier<Block> BB_LOG = BLOCKS.register("bb_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> BB_WOOD = BLOCKS.register("bb_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_BB_LOG = BLOCKS.register("stripped_bb_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_BB_WOOD = BLOCKS.register("stripped_bb_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final RegistrySupplier<Block> BB_PLANKS = BLOCKS.register("bb_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final RegistrySupplier<Block> BB_STAIRS = BLOCKS.register("bb_stairs",
            () -> new StairBlock(ModBlocks.BB_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final RegistrySupplier<Block> BB_SLABS = BLOCKS.register("bb_slabs",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final RegistrySupplier<Block> BB_BUTTON = BLOCKS.register("bb_button",
            () -> new ButtonBlock(ModBlockCollections.BB_WOOD_SET, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final RegistrySupplier<Block> BB_PRESSURE_PLATE = BLOCKS.register("bb_pressure_plate",
            () -> new PressurePlateBlock( ModBlockCollections.BB_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final RegistrySupplier<Block> BB_FENCE = BLOCKS.register("bb_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final RegistrySupplier<Block> BB_FENCE_GATE = BLOCKS.register("bb_fence_gate",
            () -> new FenceGateBlock(ModBlockCollections.BB_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final RegistrySupplier<Block> BB_SIGN = BLOCKS.register("bb_sign",
            () -> new ModStandingSignBlock(ModBlockCollections.BB_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
    public static final RegistrySupplier<Block> WALL_BB_SIGN = BLOCKS.register("wall_bb_sign",
            () -> new ModWallSignBlock(ModBlockCollections.BB_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(BB_SIGN.get())));
    public static final RegistrySupplier<Block> BB_HANGING_SIGN = BLOCKS.register("bb_hanging_sign",
            () -> new ModCeilingHangingSignBlock(ModBlockCollections.BB_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final RegistrySupplier<Block> WALL_BB_HANGING_SIGN = BLOCKS.register("wall_bb_hanging_sign",
            () -> new ModWallHangingSignBlock(ModBlockCollections.BB_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(BB_HANGING_SIGN.get())));


    public static final RegistrySupplier<Block> BB_DOOR = BLOCKS.register("bb_door",
            () -> new DoorBlock(ModBlockCollections.BB_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));
    public static final RegistrySupplier<Block> BB_TRAPDOOR = BLOCKS.register("bb_trapdoor",
            () -> new TrapDoorBlock(ModBlockCollections.BB_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final RegistrySupplier<Block> BB_LEAVES = BLOCKS.register("bb_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final RegistrySupplier<Block> HH_SAPLING = BLOCKS.register("hh_sapling",
            () -> new SaplingBlock(ModSaplingsGenerators.HH_SAPLING, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SAPLING)));
    public static final RegistrySupplier<Block> HH_LOG = BLOCKS.register("hh_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> HH_WOOD = BLOCKS.register("hh_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WOOD).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_HH_LOG = BLOCKS.register("stripped_hh_log",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_LOG).strength(4f)));
    public static final RegistrySupplier<Block> STRIPPED_HH_WOOD = BLOCKS.register("stripped_hh_wood",
            () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.STRIPPED_OAK_WOOD).strength(4f)));

    public static final RegistrySupplier<Block> HH_PLANKS = BLOCKS.register("hh_planks",
            () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PLANKS).strength(4f)));
    public static final RegistrySupplier<Block> HH_STAIRS = BLOCKS.register("hh_stairs",
            () -> new StairBlock(ModBlocks.HH_PLANKS.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_STAIRS).strength(4f)));
    public static final RegistrySupplier<Block> HH_SLABS = BLOCKS.register("hh_slabs",
            () -> new SlabBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SLAB).strength(4f)));
    public static final RegistrySupplier<Block> HH_BUTTON = BLOCKS.register("hh_button",
            () -> new ButtonBlock(ModBlockCollections.HH_WOOD_SET, 10, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_BUTTON)));
    public static final RegistrySupplier<Block> HH_PRESSURE_PLATE = BLOCKS.register("hh_pressure_plate",
            () -> new PressurePlateBlock(ModBlockCollections.HH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_PRESSURE_PLATE)));

    public static final RegistrySupplier<Block> HH_FENCE = BLOCKS.register("hh_fence",
            () -> new FenceBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE)));
    public static final RegistrySupplier<Block> HH_FENCE_GATE = BLOCKS.register("hh_fence_gate",
            () -> new FenceGateBlock(ModBlockCollections.HH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_FENCE_GATE)));

    public static final RegistrySupplier<Block> HH_SIGN = BLOCKS.register("hh_sign",
            () -> new ModStandingSignBlock(ModBlockCollections.HH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_SIGN)));
    public static final RegistrySupplier<Block> WALL_HH_SIGN = BLOCKS.register("wall_hh_sign",
            () -> new ModWallSignBlock(ModBlockCollections.HH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_SIGN).dropsLike(HH_SIGN.get())));
    public static final RegistrySupplier<Block> HH_HANGING_SIGN = BLOCKS.register("hh_hanging_sign",
            () -> new ModCeilingHangingSignBlock(ModBlockCollections.HH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_HANGING_SIGN)));
    public static final RegistrySupplier<Block> WALL_HH_HANGING_SIGN = BLOCKS.register("wall_hh_hanging_sign",
            () -> new ModWallHangingSignBlock(ModBlockCollections.HH_WOOD_TYPE, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_WALL_HANGING_SIGN).dropsLike(HH_HANGING_SIGN.get())));


    public static final RegistrySupplier<Block> HH_DOOR = BLOCKS.register("hh_door",
            () -> new DoorBlock(ModBlockCollections.HH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_DOOR)));
    public static final RegistrySupplier<Block> HH_TRAPDOOR = BLOCKS.register("hh_trapdoor",
            () -> new TrapDoorBlock(ModBlockCollections.HH_WOOD_SET, BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_TRAPDOOR)));

    public static final RegistrySupplier<Block> HH_LEAVES = BLOCKS.register("hh_leaves",
            () -> new LeavesBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.OAK_LEAVES).strength(0.2f).noOcclusion()));

    public static final RegistrySupplier<Block> MAD_HATTER_HEAD = BLOCKS.register("mad_hatter_head",
            () -> new ModHeadBlock(ModHeadType.MAD_HATTER, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_HEAD)));

    public static final RegistrySupplier<Block> MAD_HATTER_WALL_HEAD = BLOCKS.register("mad_hatter_wall_head",
            () -> new ModWallHeadBlock(ModHeadType.MAD_HATTER, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_WALL_HEAD).dropsLike(MAD_HATTER_HEAD.get())));

    public static final RegistrySupplier<Block> CATERPILLAR_HEAD = BLOCKS.register("caterpillar_head",
            () -> new ModHeadBlock(ModHeadType.CATERPILLAR, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_HEAD)));

    public static final RegistrySupplier<Block> CATERPILLAR_WALL_HEAD = BLOCKS.register("caterpillar_wall_head",
            () -> new ModWallHeadBlock(ModHeadType.CATERPILLAR, BlockBehaviour.Properties.ofFullCopy(Blocks.PLAYER_WALL_HEAD).dropsLike(CATERPILLAR_HEAD.get())));

    public static final RegistrySupplier<Block> MAD_HATTER_SPAWNER_BLOCK = BLOCKS.register("mad_hatter_spawner_block",
            () -> new MadHatterSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL)));

    public static final RegistrySupplier<Block> CHESHIRE_CAT_SPAWNER_BLOCK = BLOCKS.register("cheshire_cat_spawner_block",
            () -> new CheshireCatSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL).noCollission().noLootTable()));

    public static final RegistrySupplier<Block> SINGING_FLOWERS_SPAWNER_BLOCK = BLOCKS.register("singing_flowers_spawner_block",
            () -> new SingingFlowersSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL).noLootTable()));

    public static final RegistrySupplier<Block> CATERPILLAR_SPAWNER_BLOCK = BLOCKS.register("caterpillar_spawner_block",
            () -> new CaterpillarSpawnerBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BLACK_WOOL).noLootTable()));


    public static BlockFamily getThFamily() {
        return new BlockFamily.Builder(ModBlocks.TH_PLANKS.get())
                .button(ModBlocks.TH_BUTTON.get())
                .door(ModBlocks.TH_DOOR.get())
                .fence(ModBlocks.TH_FENCE.get())
                .fenceGate(ModBlocks.TH_FENCE_GATE.get())
                .stairs(ModBlocks.TH_STAIRS.get())
                .slab(ModBlocks.TH_SLABS.get())
                .pressurePlate(ModBlocks.TH_PRESSURE_PLATE.get())
                .sign(ModBlocks.TH_SIGN.get(), ModBlocks.WALL_TH_SIGN.get())
                .trapdoor(ModBlocks.TH_TRAPDOOR.get())
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
    }

    public static BlockFamily getBbFamily() {
        return new BlockFamily.Builder(ModBlocks.BB_PLANKS.get())
                .button(ModBlocks.BB_BUTTON.get())
                .door(ModBlocks.BB_DOOR.get())
                .fence(ModBlocks.BB_FENCE.get())
                .fenceGate(ModBlocks.BB_FENCE_GATE.get())
                .stairs(ModBlocks.BB_STAIRS.get())
                .slab(ModBlocks.BB_SLABS.get())
                .pressurePlate(ModBlocks.BB_PRESSURE_PLATE.get())
                .sign(ModBlocks.BB_SIGN.get(), ModBlocks.WALL_BB_SIGN.get())
                .trapdoor(ModBlocks.BB_TRAPDOOR.get())
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
    }

    public static BlockFamily getWwFamily() {
        return new BlockFamily.Builder(ModBlocks.WW_PLANKS.get())
                .button(ModBlocks.WW_BUTTON.get())
                .door(ModBlocks.WW_DOOR.get())
                .fence(ModBlocks.WW_FENCE.get())
                .fenceGate(ModBlocks.WW_FENCE_GATE.get())
                .stairs(ModBlocks.WW_STAIRS.get())
                .slab(ModBlocks.WW_SLABS.get())
                .pressurePlate(ModBlocks.WW_PRESSURE_PLATE.get())
                .sign(ModBlocks.WW_SIGN.get(), ModBlocks.WALL_WW_SIGN.get())
                .trapdoor(ModBlocks.WW_TRAPDOOR.get())
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
    }

    public static BlockFamily getHhFamily() {
        return new BlockFamily.Builder(ModBlocks.HH_PLANKS.get())
                .button(ModBlocks.HH_BUTTON.get())
                .door(ModBlocks.HH_DOOR.get())
                .fence(ModBlocks.HH_FENCE.get())
                .fenceGate(ModBlocks.HH_FENCE_GATE.get())
                .stairs(ModBlocks.HH_STAIRS.get())
                .slab(ModBlocks.HH_SLABS.get())
                .pressurePlate(ModBlocks.HH_PRESSURE_PLATE.get())
                .sign(ModBlocks.HH_SIGN.get(), ModBlocks.WALL_HH_SIGN.get())
                .trapdoor(ModBlocks.HH_TRAPDOOR.get())
                .recipeGroupPrefix("wooden")
                .recipeUnlockedBy("has_planks")
                .getFamily();
    }

    public static void registerModBlock() {
        BLOCKS.register();
        DownTheRabbitHole.LOGGER.info("Registering ModBlocks for " + DownTheRabbitHole.MOD_ID);
    }
}
