package net.emanueljdf09.dtrhmod.item;

import com.terraformersmc.terraform.boat.api.item.TerraformBoatItemHelper;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.block.custom.head.ModHeadType;
import net.emanueljdf09.dtrhmod.fabric.entity.ModBoats;
import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.item.ModFoodComponent;
import net.emanueljdf09.dtrhmod.fabric.item.block.ExteriorChestItem;
import net.emanueljdf09.dtrhmod.fabric.item.block.ExteriorDoorItem;
import net.emanueljdf09.dtrhmod.fabric.item.block.MadHatItem;
import net.emanueljdf09.dtrhmod.fabric.item.block.ModHeadBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.item.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.HangingSignItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SignItem;
import net.minecraft.world.item.SpawnEggItem;
import java.util.List;

public class ModItems {

    public static final Item POCKETWATCH = registerItem("pocketwatch", new Item(new Item.Properties()));

    public static final Item MIRROR = registerItem("mirror", new
            BlockItem(ModBlocks.MIRROR_BLOCK, new Item.Properties()));

    public static final Item MAD_HAT_ITEM = registerItem("mad_hat",
            new MadHatItem(ModBlocks.MAD_HATTER_HAT, new Item.Properties().stacksTo(1)));

    public static final Item EXTERIOR_DOOR_ITEM = registerItem("exterior_door",
            new ExteriorDoorItem(ModBlocks.EXTERIOR_DOOR, new Item.Properties()));

    public static final Item WW_HANGING_LEAVES = registerItem("ww_hanging_leaves", new
            BlockItem(ModBlocks.WW_HANGING_LEAVES, new Item.Properties()));

    public static final Item EXTERIOR_KEY = registerItem("exterior_key", new Item(new Item.Properties()));

    public static BlockItem EXTERIOR_CHEST_ITEM = registerItem("exterior_chest",
            new ExteriorChestItem(ModBlocks.EXTERIOR_CHEST, new Item.Properties()));

    public static final Item CINDERELLA_STORYBOOK = registerItem("cinderella_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "cinderella"));

    public static final Item RED_RIDING_HOOD_STORYBOOK = registerItem("red_riding_hood_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "red_riding_hood"));

    public static final Item SNOW_WHITE_STORYBOOK = registerItem("snow_white_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "snow_white"));

    public static final Item THREE_LITTLE_PIGS_STORYBOOK = registerItem("three_little_pigs_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "three_little_pigs"));

    public static final Item JACK_AND_THE_BEANSTALK_STORYBOOK = registerItem("jack_and_the_beanstalk_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "jack_and_the_beanstalk"));

    public static final Item THE_LITTLE_MERMAID_STORYBOOK = registerItem("the_little_mermaid_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "the_little_mermaid"));

    public static final Item AURORA_STORYBOOK = registerItem("aurora_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "aurora"));

    public static final Item RAPUNZEL_STORYBOOK = registerItem("rapunzel_storybook",
            new StoryBook(new Item.Properties().stacksTo(1), "rapunzel"));

    public static final Item DRINK_ME = registerItem("drink_me",
            new Item(new Item.Properties().food(net.emanueljdf09.dtrhmod.item.ModFoodComponent.DRINK_ME)));
    public static final Item EAT_ME = registerItem("eat_me",
            new Item(new Item.Properties().food(net.emanueljdf09.dtrhmod.item.ModFoodComponent.EAT_ME)));

    public static final Item EMPTY_CUP = registerItem("empty_cup",
            new Item(new Item.Properties()));
    public static final Item FILLED_TEA_CUP = registerItem("filled_tea_cup",
            new DynamicTeaCupItem(new Item.Properties().stacksTo(16)));

    public static final Item WHITE_RABBIT_SPAWN_EGG = registerItem("white_rabbit_spawn_egg",
            new SpawnEggItem(ModEntities.WHITE_RABBIT, 0xc23232, 0xe8e3e3,
                    new Item.Properties()));

    public static final Item CHESHIRE_CAT_SPAWN_EGG = registerItem("cheshire_cat_spawn_egg",
            new SpawnEggItem(ModEntities.CHESHIRE_CAT, 0x857b97, 0x375c76,
                    new Item.Properties()));

    public static final Item SINGING_FLOWER_SPAWN_EGG = registerItem("singing_flower_spawn_egg",
            new SpawnEggItem(ModEntities.SINGING_FLOWER, 0xf76b8f, 0xffae34,
                    new Item.Properties()));

    public static final Item MOCK_TURTLE_SPAWN_EGG = registerItem("mock_turtle_spawn_egg",
            new SpawnEggItem(ModEntities.MOCK_TURTLE, 0x484637, 0x66a090,
                    new Item.Properties()));

    public static final Item CATERPILLAR_SPAWN_EGG = registerItem("caterpillar_spawn_egg",
            new SpawnEggItem(ModEntities.CATERPILLAR, 0x1a79a0, 0x198994,
                    new Item.Properties()));

    public static final Item HARE_SPAWN_EGG = registerItem("hare_spawn_egg",
            new SpawnEggItem(ModEntities.HARE, 0x7b8595, 0xb7892b,
                    new Item.Properties()));

    public static final Item MOCK_TURTLE_STEW = registerItem("mock_turtle_stew",
            new Item(new Item.Properties().stacksTo(1).food(new FoodProperties.Builder()
                    .nutrition(4)
                    .saturationModifier(0.8f)
                    .alwaysEdible()
                    .usingConvertsTo(Items.BOWL)
                    .effect(new MobEffectInstance(MobEffects.ABSORPTION, 300, 1), 1.0f)
                    .effect(new MobEffectInstance(MobEffects.WATER_BREATHING, 300, 1), 1.0f)
                    .build())));



    public static final Item VINE_LASSO_ITEM = registerItem("vine_lasso",
            new VineLassoItem(new Item.Properties().stacksTo(1)));

    public static final Item PIPE_ITEM = registerItem("pipe_item",
            new PipeItem(new Item.Properties().stacksTo(1)));

    public static final Item CAT_SCARF_ITEM = registerItem("cat_scarf",
            new CatScarfItem(new Item.Properties().stacksTo(1)));

    public static final Item CAT_FUR = registerItem("cat_fur",
            new Item(new Item.Properties().stacksTo(16)));

    public static final Item CAT_GRIN = registerItem("cat_grin",
            new Item(new Item.Properties().stacksTo(16)));



    public static final Item WEEPING_PLAYERS_SPAWN_EGG = registerItem("weeping_players_spawn_egg",
            new SpawnEggItem(ModEntities.WEEPING_PLAYER, 0x727070, 0x211e1e,
                    new Item.Properties()));

    public static final Item MAD_HATTER_SPAWN_EGG = registerItem("mad_hatter_spawn_egg",
            new SpawnEggItem(ModEntities.MAD_HATTER,0x628239,  0x55456d,
                    new Item.Properties()));

    public static final Item MAD_HATTER_HEAD_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "mad_hatter_head"),
            new ModHeadBlockItem(ModHeadType.MAD_HATTER, ModBlocks.MAD_HATTER_HEAD, ModBlocks.MAD_HATTER_WALL_HEAD, new Item.Properties().stacksTo(64))
    );

    public static final Item CATERPILLAR_HEAD_ITEM = Registry.register(
            BuiltInRegistries.ITEM,
            ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "caterpillar_head"),
            new ModHeadBlockItem(ModHeadType.CATERPILLAR, ModBlocks.CATERPILLAR_HEAD, ModBlocks.CATERPILLAR_WALL_HEAD, new Item.Properties().stacksTo(64))
    );

    public static final Item WW_SIGN = registerItem("ww_sign",
            new SignItem(new Item.Properties().stacksTo(16), ModBlocks.WW_SIGN, ModBlocks.WALL_WW_SIGN));

    public static final Item HANGING_WW_SIGN = registerItem("ww_hanging_sign",
            new HangingSignItem(ModBlocks.WW_HANGING_SIGN, ModBlocks.WALL_WW_HANGING_SIGN, new Item.Properties().stacksTo(16)));

    public static final Item TH_SIGN = registerItem("th_sign",
            new SignItem(new Item.Properties().stacksTo(16), ModBlocks.TH_SIGN, ModBlocks.WALL_TH_SIGN));

    public static final Item HANGING_TH_SIGN = registerItem("th_hanging_sign",
            new HangingSignItem(ModBlocks.TH_HANGING_SIGN, ModBlocks.WALL_TH_HANGING_SIGN, new Item.Properties().stacksTo(16)));

    public static final Item BB_SIGN = registerItem("bb_sign",
            new SignItem(new Item.Properties().stacksTo(16), ModBlocks.BB_SIGN, ModBlocks.WALL_BB_SIGN));

    public static final Item HANGING_BB_SIGN = registerItem("bb_hanging_sign",
            new HangingSignItem(ModBlocks.BB_HANGING_SIGN, ModBlocks.WALL_BB_HANGING_SIGN, new Item.Properties().stacksTo(16)));

    public static final Item HH_SIGN = registerItem("hh_sign",
            new SignItem(new Item.Properties().stacksTo(16), ModBlocks.HH_SIGN, ModBlocks.WALL_HH_SIGN));

    public static final Item HANGING_HH_SIGN = registerItem("hh_hanging_sign",
            new HangingSignItem(ModBlocks.HH_HANGING_SIGN, ModBlocks.WALL_HH_HANGING_SIGN, new Item.Properties().stacksTo(16)));

    public static final Item TH_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.TH_BOAT_ID, ModBoats.TH_BOAT_KEY, false);
    public static final Item TH_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.TH_CHEST_BOAT_ID, ModBoats.TH_BOAT_KEY, true);

    public static final Item BB_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.BB_BOAT_ID, ModBoats.BB_BOAT_KEY, false);
    public static final Item BB_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.BB_CHEST_BOAT_ID, ModBoats.BB_BOAT_KEY, true);

    public static final Item WW_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.WW_BOAT_ID, ModBoats.WW_BOAT_KEY, false);
    public static final Item WW_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.WW_CHEST_BOAT_ID, ModBoats.WW_BOAT_KEY, true);

    public static final Item HH_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.HH_BOAT_ID, ModBoats.HH_BOAT_KEY, false);
    public static final Item HH_CHEST_BOAT = TerraformBoatItemHelper.registerBoatItem(ModBoats.HH_CHEST_BOAT_ID, ModBoats.HH_BOAT_KEY, true);

    public static final List<Item> STORYBOOK_ITEMS = List.of(
            CINDERELLA_STORYBOOK,
            RED_RIDING_HOOD_STORYBOOK,
            SNOW_WHITE_STORYBOOK,
            RAPUNZEL_STORYBOOK,
            THE_LITTLE_MERMAID_STORYBOOK,
            THREE_LITTLE_PIGS_STORYBOOK,
            AURORA_STORYBOOK,
            JACK_AND_THE_BEANSTALK_STORYBOOK
    );

    private static <T extends Item> T registerItem(String name, T item) {
        return Registry.register(BuiltInRegistries.ITEM, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name), item);
    }



    public static void registerModItems() {
        DownTheRabbitHole.LOGGER.info("Registerings Mod Items for " + DownTheRabbitHole.MOD_ID);
    }
}
