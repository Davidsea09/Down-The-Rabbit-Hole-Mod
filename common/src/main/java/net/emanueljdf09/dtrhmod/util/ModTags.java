package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public class ModTags {

    public static class Biomes {

        public static final TagKey<Biome> WONDERLAND_BIOMES = tag("wonderland_biomes");
        public static final TagKey<Biome> IS_CHESSBOARD = tag("is_chessboard");
        public static final TagKey<Biome> IS_EXTERIOR = tag("is_exterior");
        public static final TagKey<Biome> HAS_MIRROR = tag("has_structure/mirror_room");
        public static final TagKey<Biome> HAS_RABBITHOLE = tag("has_structure/rabbithole");


        private static TagKey<Biome> tag(String name) {
            return TagKey.create(Registries.BIOME, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
        }
    }


    public static class Blocks {
        public static final TagKey<Block> TH_LOGS = tag("th_logs");
        public static final TagKey<Block> WW_LOGS = tag("ww_logs");
        public static final TagKey<Block> BB_LOGS = tag("bb_logs");
        public static final TagKey<Block> HH_LOGS = tag("hh_logs");
        public static final TagKey<Block> WONDERLAND_LOGS = tag("wonderland_logs");
        public static final TagKey<Block> WONDERLAND_PLANKS = tag("wonderland_planks");
        public static final TagKey<Block> HEAT_SOURCE = tag("heat_source");


        private static TagKey<Block> tag(String name) {
            return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
        }
    }

    public static class Items {
        public static final TagKey<Item> TH_LOGS = tag("th_logs");
        public static final TagKey<Item> WW_LOGS = tag("ww_logs");
        public static final TagKey<Item> BB_LOGS = tag("bb_logs");
        public static final TagKey<Item> HH_LOGS = tag("hh_logs");
        public static final TagKey<Item> WONDERLAND_LOGS = tag("wonderland_logs");
        public static final TagKey<Item> WONDERLAND_PLANKS = tag("wonderland_planks");

        private static TagKey<Item> tag(String name) {
            return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
        }
    }

    public static class Fluids {
        public static final TagKey<Fluid> TEA = tag("tea");


        private static TagKey<Fluid> tag(String name) {
            return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name));
        }
    }
}
