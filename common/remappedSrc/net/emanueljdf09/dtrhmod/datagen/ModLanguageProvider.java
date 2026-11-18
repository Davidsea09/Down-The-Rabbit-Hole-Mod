package net.emanueljdf09.dtrhmod.datagen;

import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.entity.ModEntities;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;

public class ModLanguageProvider extends FabricLanguageProvider {
    public ModLanguageProvider(FabricDataOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generateTranslations(HolderLookup.Provider registryLookup, TranslationBuilder builder) {

        builder.add("itemgroup.dtrh", "Down the Rabbit Hole");

        // Storybooks

        builder.add(ModItems.AURORA_STORYBOOK, "The Sleeping Beauty");
        builder.add("storybook.aurora.title", "The Sleeping Beauty");
        builder.add("storybook.aurora.ogTitle", "Dornröschen");
        builder.add("storybook.aurora.author", "Jacob & Wilhelm Grimm");
        builder.add("storybook.aurora.text", """
        Rework""");

        builder.add(ModItems.CINDERELLA_STORYBOOK, "Cinderella");
        builder.add("storybook.cinderella.title", "Cinderella");
        builder.add("storybook.cinderella.ogTitle", "Aschenputtel");
        builder.add("storybook.cinderella.author", "Jacob & Wilhelm Grimm");
        builder.add("storybook.cinderella.text", """
       Rework""");

        builder.add(ModItems.RAPUNZEL_STORYBOOK, "Rapunzel");
        builder.add("storybook.rapunzel.title", "Rapunzel");
        builder.add("storybook.rapunzel.ogTitle", "Rapunzel");
        builder.add("storybook.rapunzel.author", "Jacob & Wilhelm Grimm");
        builder.add("storybook.rapunzel.text", """
               Rework""");

        builder.add(ModItems.RED_RIDING_HOOD_STORYBOOK, "Little Red Riding Hood");
        builder.add("storybook.red_riding_hood.title", "Little Red Riding Hood");
        builder.add("storybook.red_riding_hood.ogTitle", "Rotkäppchen");
        builder.add("storybook.red_riding_hood.author", "Jacob & Wilhelm Grimm");
        builder.add("storybook.red_riding_hood.text", """
               Rework""");

        builder.add(ModItems.SNOW_WHITE_STORYBOOK, "Snow White");
        builder.add("storybook.snow_white.title", "Snow White");
        builder.add("storybook.snow_white.ogTitle", "Schneewittchen");
        builder.add("storybook.snow_white.author", "Jacob & Wilhelm Grimm");
        builder.add("storybook.snow_white.text", """
               Rework""");

        builder.add(ModItems.JACK_AND_THE_BEANSTALK_STORYBOOK, "Jack and the Beanstalk");
        builder.add("storybook.jack_and_the_beanstalk.title", "Jack and the Beanstalk");
        builder.add("storybook.jack_and_the_beanstalk.ogTitle", "Jack and the Beanstalk");
        builder.add("storybook.jack_and_the_beanstalk.author", "Traditional English");
        builder.add("storybook.jack_and_the_beanstalk.text", """
                Rework""");

        builder.add(ModItems.THE_LITTLE_MERMAID_STORYBOOK, "The Little Mermaid");
        builder.add("storybook.the_little_mermaid.title", "The Little Mermaid");
        builder.add("storybook.the_little_mermaid.ogTitle", "Den lille Havfrue");
        builder.add("storybook.the_little_mermaid.author", "Hans Christian Andersen");
        builder.add("storybook.the_little_mermaid.text", """
                Rework""");

        builder.add(ModItems.THREE_LITTLE_PIGS_STORYBOOK, "Three Little Pigs");
        builder.add("storybook.three_little_pigs.title", "The Three Little Pigs");
        builder.add("storybook.three_little_pigs.ogTitle", "Die drei kleinen Schweinchen");
        builder.add("storybook.three_little_pigs.author", "Traditional English");
        builder.add("storybook.three_little_pigs.text", """
               Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, "and what is the use of a book," thought Alice, "without pictures or conversations?" So she was considering in her own mind, (as well as she could, for the hot day made her feel very sleepy and stupid,) whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a white rabbit with pink eyes ran close by her.

        There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, "Oh dear! Oh dear! I shall be too late!" (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural;) but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and was just in time to see it pop down a large rabbit-hole under the hedge.

        In another moment down went Alice after it, never once considering how in the world she was to get out again.

        The rabbit-hole went straight on like a tunnel for some way, and then dipped suddenly down, so suddenly that Alice had not a moment to think about stopping herself before she found herself falling down what seemed to be a very deep well.
        """);

        builder.add("cover.title.open", "Once upon a time...");
        builder.add("storybook.wonderland.intro.text", """
        Alice was beginning to get very tired of sitting by her sister on the bank, and of having nothing to do: once or twice she had peeped into the book her sister was reading, but it had no pictures or conversations in it, "and what is the use of a book," thought Alice, "without pictures or conversations?" So she was considering in her own mind, (as well as she could, for the hot day made her feel very sleepy and stupid,) whether the pleasure of making a daisy-chain would be worth the trouble of getting up and picking the daisies, when suddenly a white rabbit with pink eyes ran close by her.

        There was nothing so very remarkable in that; nor did Alice think it so very much out of the way to hear the Rabbit say to itself, "Oh dear! Oh dear! I shall be too late!" (when she thought it over afterwards, it occurred to her that she ought to have wondered at this, but at the time it all seemed quite natural;) but when the Rabbit actually took a watch out of its waistcoat-pocket, and looked at it, and then hurried on, Alice started to her feet, for it flashed across her mind that she had never before seen a rabbit with either a waistcoat-pocket, or a watch to take out of it, and burning with curiosity, she ran across the field after it, and was just in time to see it pop down a large rabbit-hole under the hedge.

        In another moment down went Alice after it, never once considering how in the world she was to get out again.

        The rabbit-hole went straight on like a tunnel for some way, and then dipped suddenly down, so suddenly that Alice had not a moment to think about stopping herself before she found herself falling down what seemed to be a very deep well.
        """);

        builder.add("storybook.mad_hatter.intro.text",
                "Oh, what a delightful child! Welcome! This is an unbirthday party!");


        // Entities

        // Random
        builder.add(ModEntities.SKY_SEED, "Flower Bomb");
        builder.add(ModEntities.VINE_ANCHOR, "Vine");
        builder.add(ModEntities.VINE_LASSO_PROJECTILE, "Vine");
        builder.add(ModEntities.TEAPOT_PROJECTILE, "Tea Cup");

        // White Rabbit
        builder.add(ModItems.WHITE_RABBIT_SPAWN_EGG, "White Rabbit Spawn Egg (WIP)");
        builder.add(ModEntities.WHITE_RABBIT, "White Rabbit");
        builder.add("entity.dtrhmod.white_rabbit.greet_msg", "I'm late, I'm late, for a very important date! ");
        builder.add("entity.dtrhmod.white_rabbit.busy_msg", "No time to say 'hello, goodbye,' I'm late, I'm late, I'm late!");
        builder.add("entity.dtrhmod.white_rabbit.wonderland_remember_msg", "Oh hey, i know who you are; you followed me here.");
        builder.add("entity.dtrhmod.white_rabbit.wonderland_stranger_msg", "Who are you?");

        // Weeping Players
        builder.add(ModItems.WEEPING_PLAYERS_SPAWN_EGG, "Weeping Player Spawn Egg (WIP)");
        builder.add(ModEntities.WEEPING_PLAYER, "Weeping Player");

        // Mad Hatter
        builder.add(ModItems.MAD_HATTER_SPAWN_EGG, "Mad Hatter Spawn Egg (WIP)");
        builder.add(ModItems.HARE_SPAWN_EGG, "March Hare Spawn Egg (WIP)");
        builder.add(ModEntities.MAD_HATTER, "Mad Hatter");
        builder.add(ModEntities.HARE, "March Hare");
        builder.add(ModBlocks.MAD_HATTER_HEAD, "Mad Hatter's Head");
        builder.add(ModBlocks.MAD_HATTER_SPAWNER_BLOCK, "Mad Hatter Spawner Block");
        builder.add("entity.dtrhmod.mad_hatter.trivia_fail", "I haven’t the slightest idea");
        builder.add("entity.dtrhmod.mad_hatter.trivia_start", "Mad Hatter: Let us see how your mind works! Answer my riddles...");
        builder.add("entity.dtrhmod.mad_hatter.trivia_pass", "Mad Hatter: Splendid! You actually passed my tea party test. Off with you!");
        builder.add("entity.dtrhmod.mad_hatter.not_playing", "Mad Hatter: You aren't playing my game right now!");
        builder.add("entity.dtrhmod.mad_hatter.feedback_prefix", "Mad Hatter: ");
        builder.add("entity.dtrhmod.mad_hatter.riddle_header", "Riddle %s/3: %s");

        // Riddle 1: Raven
        builder.add("riddle.dtrhmod.raven", "Why is a raven like a writing-desk?");
        builder.add("riddle.dtrhmod.raven.success", "Splendid!");
        builder.add("riddle.dtrhmod.raven.failure", "People who don't think shouldn't talk.");
        builder.add("riddle.dtrhmod.raven.opt1", "Because it can produce a few notes.");
        builder.add("riddle.dtrhmod.raven.opt2", "Nobody knows!");
        builder.add("riddle.dtrhmod.raven.opt3", "It has wings.");

        // Riddle 2
        builder.add("riddle.dtrhmod.tea", "What color is a tea party without tea?");
        builder.add("riddle.dtrhmod.tea.success", "Brilliant!");
        builder.add("riddle.dtrhmod.tea.failure", "Two days wrong!");
        builder.add("riddle.dtrhmod.tea.opt1", "Transparent");
        builder.add("riddle.dtrhmod.tea.opt2", "White");
        builder.add("riddle.dtrhmod.tea.opt3", "Invisible");

        // Riddle 3
        builder.add("riddle.dtrhmod.hand", "Which hand is best for stirring tea?");
        builder.add("riddle.dtrhmod.hand.success", "Correct!");
        builder.add("riddle.dtrhmod.hand.failure", "You can always take more than nothing");
        builder.add("riddle.dtrhmod.hand.opt1", "Your right hand.");
        builder.add("riddle.dtrhmod.hand.opt2", "Your left hand.");
        builder.add("riddle.dtrhmod.hand.opt3", "A spoon!");

        // Cheshire Cat
        builder.add(ModItems.CHESHIRE_CAT_SPAWN_EGG, "Cheshire Cat Spawn Egg (WIP)");
        builder.add(ModEntities.CHESHIRE_CAT, "Cheshire Cat");
        builder.add(ModItems.CAT_SCARF_ITEM, "Cat Scarf");
        builder.add(ModItems.CAT_FUR, "Cat Fur");
        builder.add(ModBlocks.CHESHIRE_CAT_SPAWNER_BLOCK, "Cheshire Cat Spawner Block");
        builder.add("entity.dtrhmod.cheshire_cat.confused1", "Oh, you can't help that, We're all mad here.");
        builder.add("entity.dtrhmod.cheshire_cat.confused2", "If you don't know where you want to go, then it doesn't matter which path you take.");
        builder.add("entity.dtrhmod.cheshire_cat.confused3", "What is sought is most often found, if it is truly sought.");
        builder.add("entity.dtrhmod.cheshire_cat.confused4", "Goodbye, sweet hat.");
        builder.add("entity.dtrhmod.cheshire_cat.confused5", "That depends a good deal on where you want to get to");


        builder.add("entity.dtrhmod.cheshire_cat.tea_party_clue", "Oh, by the way, if you'd really like to know, The mad hatter is roughly %1$d blocks to the %2$s... Or maybe not. Who Knows?'");

        // Singing FLowers
        builder.add(ModItems.SINGING_FLOWER_SPAWN_EGG, "Singing Flower Spawn Egg (WIP)");
        builder.add(ModEntities.SINGING_FLOWER, "Singing Flower");
        builder.add("entity.dtrhmod.singing_flowers", "The Flower Garden");
        builder.add(ModBlocks.SINGING_FLOWERS_SPAWNER_BLOCK, "Singing Flower Spawner Block");
        builder.add(ModItems.VINE_LASSO_ITEM, "Vine Lasso");

        // Caterpillar
        builder.add(ModItems.CATERPILLAR_SPAWN_EGG, "Caterpillar Spawn Egg (WIP)");
        builder.add(ModEntities.CATERPILLAR, "The Caterpillar");
        builder.add(ModBlocks.CATERPILLAR_HEAD, "Caterpillar's Head");
        builder.add(ModBlocks.CATERPILLAR_SPAWNER_BLOCK, "Caterpillar Spawner Block");
        builder.add(ModItems.PIPE_ITEM, "Caterpillar's Pipe");
        builder.add("item.dtrhmod.pipe.missing_ingredient", "Missing required ingredient: %s");
        builder.add("item.dtrhmod.pipe.requires", "Requires: §d%s");
        builder.add("item.dtrhmod.pipe.selected", "Selected Puff: %s");
        builder.add("item.dtrhmod.pipe.hint", "Sneak + Right-Click to change puff letter");
        builder.add("item.dtrhmod.pipe.letter.o", "O (Instant Damage)");
        builder.add("item.dtrhmod.pipe.letter.r", "R (Slowness)");
        builder.add("item.dtrhmod.pipe.letter.u", "U (Wither)");

        //Mock Turtle
        builder.add(ModItems.MOCK_TURTLE_SPAWN_EGG, "Mock Turtle Spawn Egg (WIP)");
        builder.add(ModEntities.MOCK_TURTLE, "Mock Turtle");
        builder.add(ModItems.MOCK_TURTLE_STEW, "Mock Turtle Stew");

        // Items
        builder.add(ModItems.POCKETWATCH, "Pocketwatch");
        builder.add(ModItems.EXTERIOR_KEY, "Door Key");
        builder.add(ModItems.EAT_ME, "§d§o§l\"Eat me\"");
        builder.add(ModItems.DRINK_ME, "§d§o§l\"Drink me\"");


        builder.add(ModItems.EMPTY_CUP, "Empty Cup");
        builder.add(ModItems.FILLED_TEA_CUP, "Tea Cup");

        // Blocks

        // Special Blocks
        builder.add(ModBlocks.RABBIT_HOLE, "Rabbit Hole");
        builder.add(ModBlocks.EXTERIOR_PORTAL, "Exterior Gateway");
        builder.add(ModBlocks.FLOWER_PORTAL, "A Flower...?");
        builder.add(ModBlocks.MIRROR_BLOCK, "Mirror (WIP)");
        builder.add(ModBlocks.MAD_HATTER_HAT, "Mad Hatter´s Hat (WIP)");

        // Exterior Chest
        builder.add(ModBlocks.EXTERIOR_CHEST, "A Tiny Chest");
        builder.add("exterior.player.openchest", "§d§o§l\"Eat me\"§r What is this?");
        builder.add("exterior.player.opengrownchest", "§d§o§l\"Drink me\"§r What will this one do?");
        builder.add("exterior.player.failchestGrow", "The chest is empty...");
        builder.add("exterior.player.failchest", "There's nothing here");

        // Exterior Door
        builder.add(ModBlocks.EXTERIOR_DOOR, "A Tiny Door");
        builder.add("exterior.player.faileddoorgrowth", "You're too big to fit in here!");
        builder.add("exterior.player.faileddoor", "I'm locked, did you grab the key?");
        builder.add("exterior.player.opendoor", "Now we're talking");

        // Teapot Block
        builder.add("container.dtrhmod.teapot", "Teapot");
        builder.add("block.dtrhmod.teapot.tooltip", "You must put it on top of a heat source!");
        builder.add(ModBlocks.TEAPOT_BLOCK, "Teapot");

        // Wood Types
        builder.add(ModBlocks.BB_BUTTON, "Tulgey Oak Button");
        builder.add(ModBlocks.BB_DOOR, "Tulgey Oak Door");
        builder.add(ModBlocks.BB_FENCE, "Tulgey Oak Fence");
        builder.add(ModBlocks.BB_FENCE_GATE, "Tulgey Oak Fence Gate");
        builder.add(ModBlocks.BB_LEAVES, "Tulgey Oak Leaves");
        builder.add(ModBlocks.BB_LOG, "Tulgey Oak Log");
        builder.add(ModBlocks.BB_PLANKS, "Tulgey Oak Planks");
        builder.add(ModBlocks.BB_PRESSURE_PLATE, "Tulgey Oak Pressure Plate");
        builder.add(ModBlocks.BB_SAPLING, "Tulgey Oak Sapling");
        builder.add(ModBlocks.BB_SLABS, "Tulgey Oak Slabs");
        builder.add(ModBlocks.BB_STAIRS, "Tulgey Oak Stairs");
        builder.add(ModBlocks.BB_TRAPDOOR, "Tulgey Oak Trapdoor");
        builder.add(ModBlocks.BB_WOOD, "Tulgey Oak Wood");
        builder.add(ModBlocks.STRIPPED_BB_WOOD, "Stripped Tulgey Oak Wood");
        builder.add(ModBlocks.STRIPPED_BB_LOG, "Stripped Tulgey Oak Log");
        builder.add(ModBlocks.BB_SIGN, "Tulgey Oak Sign");
        builder.add(ModBlocks.BB_HANGING_SIGN, "Tulgey Oak Hanging Sign");
        builder.add(ModItems.BB_BOAT, "Tulgey Oak Boat");
        builder.add(ModItems.BB_CHEST_BOAT, "Tulgey Oak Chest Boat");

        builder.add(ModBlocks.TH_BUTTON, "Dark Tulgey Oak Button");
        builder.add(ModBlocks.TH_DOOR, "Dark Tulgey Oak Door");
        builder.add(ModBlocks.TH_FENCE, "Dark Tulgey Oak Fence");
        builder.add(ModBlocks.TH_FENCE_GATE, "Dark Tulgey Oak Fence Gate");
        builder.add(ModBlocks.TH_LEAVES, "Dark Tulgey Oak Leaves");
        builder.add(ModBlocks.TH_LOG, "Dark Tulgey Oak Log");
        builder.add(ModBlocks.TH_PLANKS, "Dark Tulgey Oak Planks");
        builder.add(ModBlocks.TH_PRESSURE_PLATE, "Dark Tulgey Oak Pressure Plate");
        builder.add(ModBlocks.TH_SAPLING, "Dark Tulgey Oak Sapling");
        builder.add(ModBlocks.TH_SLABS, "Dark Tulgey Oak Slabs");
        builder.add(ModBlocks.TH_STAIRS, "Dark Tulgey Oak Stairs");
        builder.add(ModBlocks.TH_TRAPDOOR, "Dark Tulgey Oak Trapdoor");
        builder.add(ModBlocks.TH_WOOD, "Dark Tulgey Oak Wood");
        builder.add(ModBlocks.STRIPPED_TH_WOOD, "Stripped Dark Tulgey Oak Wood");
        builder.add(ModBlocks.STRIPPED_TH_LOG, "Stripped Dark Tulgey Oak Log");
        builder.add(ModBlocks.TH_SIGN, "Dark Tulgey Oak Sign");
        builder.add(ModBlocks.TH_HANGING_SIGN, "Dark Tulgey Oak Hanging Sign");
        builder.add(ModItems.TH_BOAT, "Dark Tulgey Oak Boat");
        builder.add(ModItems.TH_CHEST_BOAT, "Dark Tulgey Oak Chest Boat");

        builder.add(ModBlocks.WW_BUTTON, "Twisted Willow Button");
        builder.add(ModBlocks.WW_DOOR, "Twisted Willow Door");
        builder.add(ModBlocks.WW_FENCE, "Twisted Willow Fence");
        builder.add(ModBlocks.WW_FENCE_GATE, "Twisted Willow Fence Gate");
        builder.add(ModBlocks.WW_LEAVES, "Twisted Willow Leaves");
        builder.add(ModBlocks.WW_HANGING_LEAVES, "Twisted Willow Hanging Leaves");
        builder.add(ModBlocks.WW_HANGING_LEAVES_PLANT, "Twisted Willow Hanging Leaves");
        builder.add(ModBlocks.WW_LOG, "Twisted Willow Log");
        builder.add(ModBlocks.WW_PLANKS, "Twisted Willow Planks");
        builder.add(ModBlocks.WW_PRESSURE_PLATE, "Twisted Willow Pressure Plate");
        builder.add(ModBlocks.WW_SAPLING, "Twisted Willow Sapling");
        builder.add(ModBlocks.WW_SLABS, "Twisted Willow Slabs");
        builder.add(ModBlocks.WW_STAIRS, "Twisted Willow Stairs");
        builder.add(ModBlocks.WW_TRAPDOOR, "Twisted Willow Trapdoor");
        builder.add(ModBlocks.WW_WOOD, "Twisted Willow Wood");
        builder.add(ModBlocks.STRIPPED_WW_WOOD, "Stripped Twisted Willow Wood");
        builder.add(ModBlocks.STRIPPED_WW_LOG, "Stripped Twisted Willow Log");
        builder.add(ModBlocks.WW_SIGN, "Twisted Willow Sign");
        builder.add(ModBlocks.WW_HANGING_SIGN, "Twisted Willow Hanging Sign");
        builder.add(ModItems.WW_BOAT, "Twisted Willow Boat");
        builder.add(ModItems.WW_CHEST_BOAT, "Twisted Willow Chest Boat");

        builder.add(ModBlocks.HH_BUTTON, "Heart Birch Button");
        builder.add(ModBlocks.HH_DOOR, "Heart Birch Door");
        builder.add(ModBlocks.HH_FENCE, "Heart Birch Fence");
        builder.add(ModBlocks.HH_FENCE_GATE, "Heart Birch Fence Gate");
        builder.add(ModBlocks.HH_LEAVES, "Heart Birch Leaves");
        builder.add(ModBlocks.HH_LOG, "Heart Birch Log");
        builder.add(ModBlocks.HH_PLANKS, "Heart Birch Planks");
        builder.add(ModBlocks.HH_PRESSURE_PLATE, "Heart Birch Pressure Plate");
        builder.add(ModBlocks.HH_SAPLING, "Heart Birch Sapling");
        builder.add(ModBlocks.HH_SLABS, "Heart Birch Slabs");
        builder.add(ModBlocks.HH_STAIRS, "Heart Birch Stairs");
        builder.add(ModBlocks.HH_TRAPDOOR, "Heart Birch Trapdoor");
        builder.add(ModBlocks.HH_WOOD, "Heart Birch Wood");
        builder.add(ModBlocks.STRIPPED_HH_WOOD, "Stripped Heart Birch Wood");
        builder.add(ModBlocks.STRIPPED_HH_LOG, "Stripped Heart Birch Log");
        builder.add(ModBlocks.HH_SIGN, "Heart Birch Sign");
        builder.add(ModBlocks.HH_HANGING_SIGN, "Heart Birch Hanging Sign");
        builder.add(ModItems.HH_BOAT, "Heart Birch Boat");
        builder.add(ModItems.HH_CHEST_BOAT, "Heart Birch Chest Boat");

        // Mushrooms
        builder.add(ModBlocks.BLUE_MUSHROOM_BLOCK, "Blue Mushroom Block");
        builder.add(ModBlocks.BLUE_MUSHROOM, "Blue Mushroom");
        builder.add(ModBlocks.YELLOW_MUSHROOM_BLOCK, "Yellow Mushroom Block");
        builder.add(ModBlocks.YELLOW_MUSHROOM, "Yellow Mushroom");
        builder.add(ModBlocks.MAGENTA_MUSHROOM_BLOCK, "Magenta Mushroom Block");
        builder.add(ModBlocks.MAGENTA_MUSHROOM, "Magenta Mushroom");

        // Foliage
        builder.add(ModBlocks.WONDER_DIRT, "Under Dirt");
        builder.add(ModBlocks.WONDER_GRASS, "Under Grass");
        builder.add(ModBlocks.WONDER_DIRT_PATH, "Under Dirt Path");
        builder.add(ModBlocks.LAWN_DAISY_PATCH, "Lawn Daisy Patch");

        // Extras

        // Advancements

        builder.add("advancement.dtrhmod.root.title", "Down the Rabbit hole");
        builder.add("advancement.dtrhmod.root.description", "A Very Fairy Tale focused mod!");

        builder.add("advancement.dtrhmod.boss_root.title", "Bosses");
        builder.add("advancement.dtrhmod.boss_root.description", "All the best people are crazy!");

        builder.add("advancement.dtrhmod.cheshire_cat_snitch.title", "I think we're all MAD");
        builder.add("advancement.dtrhmod.cheshire_cat_snitch.description", "Make the Cheshire Cat snitch.");

        builder.add("advancement.dtrhmod.flowers_kill.title", "All in the Golden Afternoon");
        builder.add("advancement.dtrhmod.flowers_kill.description", "Silence the garden that mistook you for a weed.");

        builder.add("advancement.dtrhmod.secret_root.title", "Secrets");
        builder.add("advancement.dtrhmod.secret_root.description", "I didn't know this was here...");

        builder.add("advancement.dtrhmod.portal.title", "Follow the tunnel...");
        builder.add("advancement.dtrhmod.portal.description", "...into the portal");

        builder.add("misc.dtrhmod.biome_locked.title", "This Biome is Locked.");
        builder.add("misc.dtrhmod.biome_locked.description", "Come back later!");

        builder.add("advancement.dtrhmod.mad_hatter_trivia.title", "A Mad Tea Party");
        builder.add("advancement.dtrhmod.mad_hatter_trivia.description", "Successfully answer the Mad Hatter's riddles.");
        builder.add( "advancement.dtrhmod.mad_hatter_kill.title", "Off With His Head!");
        builder.add( "advancement.dtrhmod.mad_hatter_kill.description", "Defeat the Mad Hatter in combat.");

        builder.add( "advancement.dtrhmod.teapot_craft_adv.title", "Anyone want some tea?");
        builder.add( "advancement.dtrhmod.teapot_craft_adv.description", "Successfully brew tea in a teapot.");

        builder.add( "advancement.dtrhmod.caterpillar_kill.title", "whO aRe yoU?");
        builder.add( "advancement.dtrhmod.caterpillar_kill.description", "Defeat the Caterpillar in all three of his forms");



        // World Gen

        // Dimensions
        builder.add("travelerstitles.dtrhmod.exterior", "The Exterior");
        builder.add("travelerstitles.dtrhmod.wonderland", "Wonderland");
        builder.add("travelerstitles.dtrhmod.storybook", "Once Upon A Time in..");

        // Biomes
        builder.add("biome.dtrhmod.chessboard_fields", "Chessboard Fields");
        builder.add("biome.dtrhmod.enchanted_forest", "The Enchanted Forest");
        builder.add("biome.dtrhmod.the_exterior", "The Exterior");
        builder.add("biome.dtrhmod.tulgey_wood", "Tulgey Woods");
        builder.add("biome.dtrhmod.vale_of_tears", "Vale of Tears");

        // Effects
        builder.add("effect.dtrhmod.shrink", "Shrink");
        builder.add("effect.dtrhmod.grow", "Grow");
        builder.add("item.minecraft.potion.effect.shrink_potion", "Potion of Shrinking");
        builder.add("item.minecraft.potion.effect.grow_potion", "Potion of Growing");
        builder.add("item.minecraft.splash_potion.effect.shrink_potion", "Splash Potion of Shrinking");
        builder.add("item.minecraft.splash_potion.effect.grow_potion", "Splash Potion of Growing");
        builder.add("item.minecraft.lingering_potion.effect.shrink_potion", "Lingering Potion of Shrinking");
        builder.add("item.minecraft.lingering_potion.effect.grow_potion", "Lingering Potion of Growing");

    }
}