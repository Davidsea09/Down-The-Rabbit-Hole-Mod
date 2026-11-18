package net.emanueljdf09.dtrhmod.block;

import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockSetType;
import net.minecraft.world.level.block.state.properties.WoodType;

public class ModBlockCollections {
    public static final BlockSetType TH_WOOD_SET = new BlockSetType(DownTheRabbitHole.MOD_ID + "th");
    public static final BlockSetType BB_WOOD_SET = new BlockSetType(DownTheRabbitHole.MOD_ID + "bb");
    public static final BlockSetType WW_WOOD_SET = new BlockSetType(DownTheRabbitHole.MOD_ID + "ww");
    public static final BlockSetType HH_WOOD_SET = new BlockSetType(DownTheRabbitHole.MOD_ID + "hh");

    public static final WoodType TH_WOOD_TYPE = register(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "th"), TH_WOOD_SET);
    public static final WoodType BB_WOOD_TYPE = register(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "bb"), BB_WOOD_SET);
    public static final WoodType WW_WOOD_TYPE = register(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "ww"), WW_WOOD_SET);
    public static final WoodType HH_WOOD_TYPE = register(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hh"), HH_WOOD_SET);

    private static WoodType register(ResourceLocation id, BlockSetType setType) {
        return new WoodType(id.toString(), setType);
    }

    public static void registerModBlockCollections() {
        DownTheRabbitHole.LOGGER.info("Registering Mod Blocks Collections for " + DownTheRabbitHole.MOD_ID);
    }
}
