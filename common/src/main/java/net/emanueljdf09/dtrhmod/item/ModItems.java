package net.emanueljdf09.dtrhmod.item;
import dev.architectury.core.item.ArchitecturySpawnEggItem;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.block.ModBlocks;
import net.emanueljdf09.dtrhmod.block.custom.head.ModHeadType;
import net.emanueljdf09.dtrhmod.entity.ModEntities;
import net.emanueljdf09.dtrhmod.item.block.ExteriorChestItem;
import net.emanueljdf09.dtrhmod.item.block.ExteriorDoorItem;
import net.emanueljdf09.dtrhmod.item.block.MadHatItem;
import net.emanueljdf09.dtrhmod.item.block.ModHeadBlockItem;
import net.emanueljdf09.dtrhmod.item.custom.*;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;

import java.util.List;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.ITEM);

    private static Item.Properties baseProperties() {
        return new Item.Properties().arch$tab(DownTheRabbitHole.CREATIVE_TAB);
    }

    public static final RegistrySupplier<Item> POCKETWATCH = ITEMS.register("pocketwatch",
            () -> new Item(baseProperties()));

    public static final RegistrySupplier<Item> MIRROR = ITEMS.register("mirror",
            () -> new BlockItem(ModBlocks.MIRROR_BLOCK.get(), baseProperties()));

    public static final RegistrySupplier<Item> MAD_HAT_ITEM = ITEMS.register("mad_hat",
            () -> new MadHatItem(ModBlocks.MAD_HATTER_HAT.get(), baseProperties().stacksTo(1)));

    public static final RegistrySupplier<Item> EXTERIOR_DOOR_ITEM = ITEMS.register("exterior_door",
            () -> new ExteriorDoorItem(ModBlocks.EXTERIOR_DOOR.get(), baseProperties()));

    public static final RegistrySupplier<Item> WW_HANGING_LEAVES = ITEMS.register("ww_hanging_leaves",
            () -> new BlockItem(ModBlocks.WW_HANGING_LEAVES.get(), baseProperties()));

    public static final RegistrySupplier<Item> EXTERIOR_KEY = ITEMS.register("exterior_key",
            () -> new Item(baseProperties()));

    public static RegistrySupplier<Item> EXTERIOR_CHEST_ITEM = ITEMS.register("exterior_chest",
            () -> new ExteriorChestItem(ModBlocks.EXTERIOR_CHEST.get(), baseProperties()));

    public static final RegistrySupplier<Item> CINDERELLA_STORYBOOK = ITEMS.register("cinderella_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "cinderella"));

    public static final RegistrySupplier<Item> RED_RIDING_HOOD_STORYBOOK = ITEMS.register("red_riding_hood_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "red_riding_hood"));

    public static final RegistrySupplier<Item> SNOW_WHITE_STORYBOOK = ITEMS.register("snow_white_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "snow_white"));

    public static final RegistrySupplier<Item> THREE_LITTLE_PIGS_STORYBOOK = ITEMS.register("three_little_pigs_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "three_little_pigs"));

    public static final RegistrySupplier<Item> JACK_AND_THE_BEANSTALK_STORYBOOK = ITEMS.register("jack_and_the_beanstalk_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "jack_and_the_beanstalk"));

    public static final RegistrySupplier<Item> THE_LITTLE_MERMAID_STORYBOOK = ITEMS.register("the_little_mermaid_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "the_little_mermaid"));

    public static final RegistrySupplier<Item> AURORA_STORYBOOK = ITEMS.register("aurora_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "aurora"));

    public static final RegistrySupplier<Item> RAPUNZEL_STORYBOOK = ITEMS.register("rapunzel_storybook",
            () -> new StoryBook(baseProperties().stacksTo(1), "rapunzel"));

    public static final RegistrySupplier<Item> DRINK_ME = ITEMS.register("drink_me",
            () -> new Item(baseProperties().food(ModFoodComponent.DRINK_ME)));
    public static final RegistrySupplier<Item> EAT_ME = ITEMS.register("eat_me",
            () -> new Item(baseProperties().food(ModFoodComponent.EAT_ME)));

    public static final RegistrySupplier<Item> EMPTY_CUP = ITEMS.register("empty_cup",
            () -> new Item(baseProperties().stacksTo(64)));
    public static final RegistrySupplier<Item> FILLED_TEA_CUP = ITEMS.register("filled_tea_cup",
            () -> new DynamicTeaCupItem(new Item.Properties().stacksTo(16)));

    public static final RegistrySupplier<Item> WHITE_RABBIT_SPAWN_EGG = registerSpawnEgg(ModEntities.WHITE_RABBIT, 0xc23232, 0xe8e3e3);

    public static final RegistrySupplier<Item> CHESHIRE_CAT_SPAWN_EGG = registerSpawnEgg(ModEntities.CHESHIRE_CAT, 0x857b97, 0x375c76);

    public static final RegistrySupplier<Item> SINGING_FLOWER_SPAWN_EGG = registerSpawnEgg(ModEntities.SINGING_FLOWER, 0xf76b8f, 0xffae34);

    public static final RegistrySupplier<Item> MOCK_TURTLE_SPAWN_EGG = registerSpawnEgg(ModEntities.MOCK_TURTLE, 0x484637, 0x66a090);

    public static final RegistrySupplier<Item> CATERPILLAR_SPAWN_EGG = registerSpawnEgg(ModEntities.CATERPILLAR, 0x1a79a0, 0x198994);

    public static final RegistrySupplier<Item> HARE_SPAWN_EGG = registerSpawnEgg(ModEntities.HARE, 0x7b8595, 0xb7892b);

    public static final RegistrySupplier<Item> MOCK_TURTLE_STEW = ITEMS.register("mock_turtle_stew",
            () -> new Item(baseProperties().stacksTo(1).food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.8f)
                    .alwaysEdible()
                    .usingConvertsTo(Items.BOWL)
                    .effect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1), 1.0f)
                    .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 1), 1.0f)
                    .build())));



    public static final RegistrySupplier<Item> VINE_LASSO_ITEM = ITEMS.register("vine_lasso",
            () -> new VineLassoItem(baseProperties().stacksTo(1)));

    public static final RegistrySupplier<Item> PIPE_ITEM = ITEMS.register("pipe_item",
            () -> new PipeItem(baseProperties().stacksTo(1)));

    public static final RegistrySupplier<Item> CAT_SCARF_ITEM = ITEMS.register("cat_scarf",
            () -> new CatScarfItem(baseProperties().stacksTo(1)));

    public static final RegistrySupplier<Item> CAT_FUR = ITEMS.register("cat_fur",
            () -> new Item(baseProperties().stacksTo(16)));

    public static final RegistrySupplier<Item> CAT_GRIN = ITEMS.register("cat_grin",
            () -> new Item(baseProperties().stacksTo(16)));



    public static final RegistrySupplier<Item> WEEPING_PLAYERS_SPAWN_EGG = registerSpawnEgg(ModEntities.WEEPING_PLAYER, 0x727070, 0x211e1e);

    public static final RegistrySupplier<Item> MAD_HATTER_SPAWN_EGG = registerSpawnEgg(ModEntities.MAD_HATTER,0x628239,  0x55456d);

    public static final RegistrySupplier<Item> MAD_HATTER_HEAD_ITEM = ITEMS.register("mad_hatter_head",
            () -> new ModHeadBlockItem(ModHeadType.MAD_HATTER, ModBlocks.MAD_HATTER_HEAD.get(), ModBlocks.MAD_HATTER_WALL_HEAD.get(), baseProperties().stacksTo(64))
    );

    public static final RegistrySupplier<Item> CATERPILLAR_HEAD_ITEM = ITEMS.register("caterpillar_head",
            () -> new ModHeadBlockItem(ModHeadType.CATERPILLAR, ModBlocks.CATERPILLAR_HEAD.get(), ModBlocks.CATERPILLAR_WALL_HEAD.get(), baseProperties().stacksTo(64))
    );

    public static final RegistrySupplier<Item> WW_SIGN = ITEMS.register("ww_sign",
            () -> new SignItem(baseProperties().stacksTo(16), ModBlocks.WW_SIGN.get(), ModBlocks.WALL_WW_SIGN.get()));

    public static final RegistrySupplier<Item> HANGING_WW_SIGN = ITEMS.register("ww_hanging_sign",
            () -> new HangingSignItem(ModBlocks.WW_HANGING_SIGN.get(), ModBlocks.WALL_WW_HANGING_SIGN.get(), baseProperties().stacksTo(16)));

    public static final RegistrySupplier<Item> TH_SIGN = ITEMS.register("th_sign",
            () -> new SignItem(baseProperties().stacksTo(16), ModBlocks.TH_SIGN.get(), ModBlocks.WALL_TH_SIGN.get()));

    public static final RegistrySupplier<Item> HANGING_TH_SIGN = ITEMS.register("th_hanging_sign",
            () -> new HangingSignItem(ModBlocks.TH_HANGING_SIGN.get(), ModBlocks.WALL_TH_HANGING_SIGN.get(), baseProperties().stacksTo(16)));

    public static final RegistrySupplier<Item> BB_SIGN = ITEMS.register("bb_sign",
            () -> new SignItem(baseProperties().stacksTo(16), ModBlocks.BB_SIGN.get(), ModBlocks.WALL_BB_SIGN.get()));

    public static final RegistrySupplier<Item> HANGING_BB_SIGN = ITEMS.register("bb_hanging_sign",
            () -> new HangingSignItem(ModBlocks.BB_HANGING_SIGN.get(), ModBlocks.WALL_BB_HANGING_SIGN.get(), baseProperties().stacksTo(16)));

    public static final RegistrySupplier<Item> HH_SIGN = ITEMS.register("hh_sign",
            () -> new SignItem(baseProperties().stacksTo(16), ModBlocks.HH_SIGN.get(), ModBlocks.WALL_HH_SIGN.get()));

    public static final RegistrySupplier<Item> HANGING_HH_SIGN = ITEMS.register("hh_hanging_sign",
            () -> new HangingSignItem(ModBlocks.HH_HANGING_SIGN.get(), ModBlocks.WALL_HH_HANGING_SIGN.get(), baseProperties().stacksTo(16)));

    /*public static final RegistrySupplier<Item> TH_BOAT = ITEMS.register("th_boat",
            () -> new BoatItem(false, ModEntities.TH_BOAT.get(), baseProperties().stacksTo(1)));
    public static final RegistrySupplier<Item> TH_CHEST_BOAT = ITEMS.register("th_chest_boat",
            () -> new BoatItem(true, ModEntities.TH_CHEST_BOAT, baseProperties().stacksTo(1)));

    // Blue Mushroom Wood (BB) Boats
    public static final RegistrySupplier<Item> BB_BOAT = ITEMS.register("bb_boat",
            () -> new BoatItem(false, ModEntities.BB_BOAT, baseProperties().stacksTo(1)));
    public static final RegistrySupplier<Item> BB_CHEST_BOAT = ITEMS.register("bb_chest_boat",
            () -> new BoatItem(true, ModEntities.BB_CHEST_BOAT, baseProperties().stacksTo(1)));

    // Wonder Wood (WW) Boats
    public static final RegistrySupplier<Item> WW_BOAT = ITEMS.register("ww_boat",
            () -> new BoatItem(false, ModEntities.WW_BOAT, baseProperties().stacksTo(1)));
    public static final RegistrySupplier<Item> WW_CHEST_BOAT = ITEMS.register("ww_chest_boat",
            () -> new BoatItem(true, ModEntities.WW_CHEST_BOAT, baseProperties().stacksTo(1)));

    // Hatter Wood (HH) Boats
    public static final RegistrySupplier<Item> HH_BOAT = ITEMS.register("hh_boat",
            () -> new BoatItem(false, ModEntities.HH_BOAT, baseProperties().stacksTo(1)));
    public static final RegistrySupplier<Item> HH_CHEST_BOAT = ITEMS.register("hh_chest_boat",
            () -> new BoatItem(true, ModEntities.HH_CHEST_BOAT, baseProperties().stacksTo(1)));*/

    public static final RegistrySupplier<Item> LAWN_DAISY_PATCH_ITEM = ITEMS.register("lawn_daisy_patch", () -> new BlockItem(ModBlocks.LAWN_DAISY_PATCH.get(), baseProperties()));
    public static final RegistrySupplier<Item> BLUE_MUSHROOM_BLOCK_ITEM = ITEMS.register("blue_mushroom_block", () -> new BlockItem(ModBlocks.BLUE_MUSHROOM_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> BLUE_MUSHROOM_ITEM = ITEMS.register("blue_mushroom", () -> new BlockItem(ModBlocks.BLUE_MUSHROOM.get(), baseProperties()));
    public static final RegistrySupplier<Item> YELLOW_MUSHROOM_BLOCK_ITEM = ITEMS.register("yellow_mushroom_block", () -> new BlockItem(ModBlocks.YELLOW_MUSHROOM_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> YELLOW_MUSHROOM_ITEM = ITEMS.register("yellow_mushroom", () -> new BlockItem(ModBlocks.YELLOW_MUSHROOM.get(), baseProperties()));
    public static final RegistrySupplier<Item> MAGENTA_MUSHROOM_BLOCK_ITEM = ITEMS.register("magenta_mushroom_block", () -> new BlockItem(ModBlocks.MAGENTA_MUSHROOM_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> MAGENTA_MUSHROOM_ITEM = ITEMS.register("magenta_mushroom", () -> new BlockItem(ModBlocks.MAGENTA_MUSHROOM.get(), baseProperties()));

    public static final RegistrySupplier<Item> RABBIT_HOLE_ITEM = ITEMS.register("rabbit_hole", () -> new BlockItem(ModBlocks.RABBIT_HOLE.get(), baseProperties()));
    public static final RegistrySupplier<Item> EXTERIOR_PORTAL_ITEM = ITEMS.register("exterior_portal", () -> new BlockItem(ModBlocks.EXTERIOR_PORTAL.get(), baseProperties()));
    public static final RegistrySupplier<Item> FLOWER_PORTAL_ITEM = ITEMS.register("flower_portal", () -> new BlockItem(ModBlocks.FLOWER_PORTAL.get(), baseProperties()));
    public static final RegistrySupplier<Item> POTTED_FLOWER_PORTAL_ITEM = ITEMS.register("potted_flower_portal", () -> new BlockItem(ModBlocks.POTTED_FLOWER_PORTAL.get(), baseProperties()));
    public static final RegistrySupplier<Item> TEAPOT_BLOCK_ITEM = ITEMS.register("teapot_block", () -> new BlockItem(ModBlocks.TEAPOT_BLOCK.get(), baseProperties()));

    public static final RegistrySupplier<Item> WONDER_GRASS_ITEM = ITEMS.register("wonder_grass", () -> new BlockItem(ModBlocks.WONDER_GRASS.get(), baseProperties()));
    public static final RegistrySupplier<Item> WONDER_DIRT_ITEM = ITEMS.register("wonder_dirt", () -> new BlockItem(ModBlocks.WONDER_DIRT.get(), baseProperties()));
    public static final RegistrySupplier<Item> WONDER_DIRT_PATH_ITEM = ITEMS.register("wonder_dirt_path", () -> new BlockItem(ModBlocks.WONDER_DIRT_PATH.get(), baseProperties()));

    public static final RegistrySupplier<Item> MAD_HATTER_SPAWNER_ITEM = ITEMS.register("mad_hatter_spawner_block", () -> new BlockItem(ModBlocks.MAD_HATTER_SPAWNER_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> CHESHIRE_CAT_SPAWNER_ITEM = ITEMS.register("cheshire_cat_spawner_block", () -> new BlockItem(ModBlocks.CHESHIRE_CAT_SPAWNER_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> SINGING_FLOWERS_SPAWNER_ITEM = ITEMS.register("singing_flowers_spawner_block", () -> new BlockItem(ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK.get(), baseProperties()));
    public static final RegistrySupplier<Item> CATERPILLAR_SPAWNER_ITEM = ITEMS.register("caterpillar_spawner_block", () -> new BlockItem(ModBlocks.CATERPILLAR_SPAWNER_BLOCK.get(), baseProperties()));

    public static final RegistrySupplier<Item> TH_SAPLING_ITEM = ITEMS.register("th_sapling", () -> new BlockItem(ModBlocks.TH_SAPLING.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_LOG_ITEM = ITEMS.register("th_log", () -> new BlockItem(ModBlocks.TH_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_WOOD_ITEM = ITEMS.register("th_wood", () -> new BlockItem(ModBlocks.TH_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_TH_LOG_ITEM = ITEMS.register("stripped_th_log", () -> new BlockItem(ModBlocks.STRIPPED_TH_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_TH_WOOD_ITEM = ITEMS.register("stripped_th_wood", () -> new BlockItem(ModBlocks.STRIPPED_TH_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_PLANKS_ITEM = ITEMS.register("th_planks", () -> new BlockItem(ModBlocks.TH_PLANKS.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_STAIRS_ITEM = ITEMS.register("th_stairs", () -> new BlockItem(ModBlocks.TH_STAIRS.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_SLABS_ITEM = ITEMS.register("th_slabs", () -> new BlockItem(ModBlocks.TH_SLABS.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_BUTTON_ITEM = ITEMS.register("th_button", () -> new BlockItem(ModBlocks.TH_BUTTON.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_PRESSURE_PLATE_ITEM = ITEMS.register("th_pressure_plate", () -> new BlockItem(ModBlocks.TH_PRESSURE_PLATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_FENCE_ITEM = ITEMS.register("th_fence", () -> new BlockItem(ModBlocks.TH_FENCE.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_FENCE_GATE_ITEM = ITEMS.register("th_fence_gate", () -> new BlockItem(ModBlocks.TH_FENCE_GATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_DOOR_ITEM = ITEMS.register("th_door", () -> new DoubleHighBlockItem(ModBlocks.TH_DOOR.get(), baseProperties())); // Or standard BlockItem / custom door item if applicable
    public static final RegistrySupplier<Item> TH_TRAPDOOR_ITEM = ITEMS.register("th_trapdoor", () -> new BlockItem(ModBlocks.TH_TRAPDOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> TH_LEAVES_ITEM = ITEMS.register("th_leaves", () -> new BlockItem(ModBlocks.TH_LEAVES.get(), baseProperties()));

    public static final RegistrySupplier<Item> WW_SAPLING_ITEM = ITEMS.register("ww_sapling", () -> new BlockItem(ModBlocks.WW_SAPLING.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_LOG_ITEM = ITEMS.register("ww_log", () -> new BlockItem(ModBlocks.WW_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_WOOD_ITEM = ITEMS.register("ww_wood", () -> new BlockItem(ModBlocks.WW_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_WW_LOG_ITEM = ITEMS.register("stripped_ww_log", () -> new BlockItem(ModBlocks.STRIPPED_WW_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_WW_WOOD_ITEM = ITEMS.register("stripped_ww_wood", () -> new BlockItem(ModBlocks.STRIPPED_WW_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_PLANKS_ITEM = ITEMS.register("ww_planks", () -> new BlockItem(ModBlocks.WW_PLANKS.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_STAIRS_ITEM = ITEMS.register("ww_stairs", () -> new BlockItem(ModBlocks.WW_STAIRS.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_SLABS_ITEM = ITEMS.register("ww_slabs", () -> new BlockItem(ModBlocks.WW_SLABS.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_BUTTON_ITEM = ITEMS.register("ww_button", () -> new BlockItem(ModBlocks.WW_BUTTON.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_PRESSURE_PLATE_ITEM = ITEMS.register("ww_pressure_plate", () -> new BlockItem(ModBlocks.WW_PRESSURE_PLATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_FENCE_ITEM = ITEMS.register("ww_fence", () -> new BlockItem(ModBlocks.WW_FENCE.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_FENCE_GATE_ITEM = ITEMS.register("ww_fence_gate", () -> new BlockItem(ModBlocks.WW_FENCE_GATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_DOOR_ITEM = ITEMS.register("ww_door", () -> new DoubleHighBlockItem(ModBlocks.WW_DOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_TRAPDOOR_ITEM = ITEMS.register("ww_trapdoor", () -> new BlockItem(ModBlocks.WW_TRAPDOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> WW_LEAVES_ITEM = ITEMS.register("ww_leaves", () -> new StandingAndWallBlockItem(ModBlocks.WW_LEAVES.get(), ModBlocks.WW_HANGING_LEAVES_PLANT.get(), baseProperties(), Direction.DOWN));

    public static final RegistrySupplier<Item> BB_SAPLING_ITEM = ITEMS.register("bb_sapling", () -> new BlockItem(ModBlocks.BB_SAPLING.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_LOG_ITEM = ITEMS.register("bb_log", () -> new BlockItem(ModBlocks.BB_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_WOOD_ITEM = ITEMS.register("bb_wood", () -> new BlockItem(ModBlocks.BB_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_BB_LOG_ITEM = ITEMS.register("stripped_bb_log", () -> new BlockItem(ModBlocks.STRIPPED_BB_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_BB_WOOD_ITEM = ITEMS.register("stripped_bb_wood", () -> new BlockItem(ModBlocks.STRIPPED_BB_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_PLANKS_ITEM = ITEMS.register("bb_planks", () -> new BlockItem(ModBlocks.BB_PLANKS.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_STAIRS_ITEM = ITEMS.register("bb_stairs", () -> new BlockItem(ModBlocks.BB_STAIRS.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_SLABS_ITEM = ITEMS.register("bb_slabs", () -> new BlockItem(ModBlocks.BB_SLABS.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_BUTTON_ITEM = ITEMS.register("bb_button", () -> new BlockItem(ModBlocks.BB_BUTTON.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_PRESSURE_PLATE_ITEM = ITEMS.register("bb_pressure_plate", () -> new BlockItem(ModBlocks.BB_PRESSURE_PLATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_FENCE_ITEM = ITEMS.register("bb_fence", () -> new BlockItem(ModBlocks.BB_FENCE.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_FENCE_GATE_ITEM = ITEMS.register("bb_fence_gate", () -> new BlockItem(ModBlocks.BB_FENCE_GATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_DOOR_ITEM = ITEMS.register("bb_door", () -> new DoubleHighBlockItem(ModBlocks.BB_DOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_TRAPDOOR_ITEM = ITEMS.register("bb_trapdoor", () -> new BlockItem(ModBlocks.BB_TRAPDOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> BB_LEAVES_ITEM = ITEMS.register("bb_leaves", () -> new BlockItem(ModBlocks.BB_LEAVES.get(), baseProperties()));

    public static final RegistrySupplier<Item> HH_SAPLING_ITEM = ITEMS.register("hh_sapling", () -> new BlockItem(ModBlocks.HH_SAPLING.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_LOG_ITEM = ITEMS.register("hh_log", () -> new BlockItem(ModBlocks.HH_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_WOOD_ITEM = ITEMS.register("hh_wood", () -> new BlockItem(ModBlocks.HH_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_HH_LOG_ITEM = ITEMS.register("stripped_hh_log", () -> new BlockItem(ModBlocks.STRIPPED_HH_LOG.get(), baseProperties()));
    public static final RegistrySupplier<Item> STRIPPED_HH_WOOD_ITEM = ITEMS.register("stripped_hh_wood", () -> new BlockItem(ModBlocks.STRIPPED_HH_WOOD.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_PLANKS_ITEM = ITEMS.register("hh_planks", () -> new BlockItem(ModBlocks.HH_PLANKS.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_STAIRS_ITEM = ITEMS.register("hh_stairs", () -> new BlockItem(ModBlocks.HH_STAIRS.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_SLABS_ITEM = ITEMS.register("hh_slabs", () -> new BlockItem(ModBlocks.HH_SLABS.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_BUTTON_ITEM = ITEMS.register("hh_button", () -> new BlockItem(ModBlocks.HH_BUTTON.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_PRESSURE_PLATE_ITEM = ITEMS.register("hh_pressure_plate", () -> new BlockItem(ModBlocks.HH_PRESSURE_PLATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_FENCE_ITEM = ITEMS.register("hh_fence", () -> new BlockItem(ModBlocks.HH_FENCE.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_FENCE_GATE_ITEM = ITEMS.register("hh_fence_gate", () -> new BlockItem(ModBlocks.HH_FENCE_GATE.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_DOOR_ITEM = ITEMS.register("hh_door", () -> new DoubleHighBlockItem(ModBlocks.HH_DOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_TRAPDOOR_ITEM = ITEMS.register("hh_trapdoor", () -> new BlockItem(ModBlocks.HH_TRAPDOOR.get(), baseProperties()));
    public static final RegistrySupplier<Item> HH_LEAVES_ITEM = ITEMS.register("hh_leaves", () -> new BlockItem(ModBlocks.HH_LEAVES.get(), baseProperties()));


     public static final List<RegistrySupplier<Item>> STORYBOOK_ITEMS = List.of(
            CINDERELLA_STORYBOOK,
            RED_RIDING_HOOD_STORYBOOK,
            SNOW_WHITE_STORYBOOK,
            RAPUNZEL_STORYBOOK,
            THE_LITTLE_MERMAID_STORYBOOK,
            THREE_LITTLE_PIGS_STORYBOOK,
            AURORA_STORYBOOK,
            JACK_AND_THE_BEANSTALK_STORYBOOK
    );

    private static <T extends Mob> RegistrySupplier<Item> registerSpawnEgg(RegistrySupplier<EntityType<T>> entity, int primary, int secondary) {
        return ITEMS.register(entity.getKey().location().getPath() + "_spawn_egg", () ->
                new ArchitecturySpawnEggItem(
                        entity,
                        primary,
                        secondary,
                        baseProperties()
                )
        );
    }

    /*public static void registerCompostables() {
        ComposterBlock.add(0.3F, ModItems.FLOWER_PORTAL.get());
    }*/


    public static void registerModItems() {
        ITEMS.register();
        DownTheRabbitHole.LOGGER.info("Registerings Mod Items for " + DownTheRabbitHole.MOD_ID);
    }
}
