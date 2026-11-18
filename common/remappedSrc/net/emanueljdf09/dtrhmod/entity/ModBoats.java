package net.emanueljdf09.dtrhmod.entity;

import com.terraformersmc.terraform.boat.api.TerraformBoatType;
import com.terraformersmc.terraform.boat.api.TerraformBoatTypeRegistry;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

public class ModBoats {
    public static final ResourceLocation TH_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "th_boat");
    public static final ResourceLocation TH_CHEST_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "th_chest_boat");

    public static final ResourceLocation BB_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "bb_boat");
    public static final ResourceLocation BB_CHEST_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "bb_chest_boat");

    public static final ResourceLocation WW_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "ww_boat");
    public static final ResourceLocation WW_CHEST_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "ww_chest_boat");

    public static final ResourceLocation HH_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hh_boat");
    public static final ResourceLocation HH_CHEST_BOAT_ID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hh_chest_boat");

    public static final ResourceKey<TerraformBoatType> TH_BOAT_KEY = TerraformBoatTypeRegistry.createKey(TH_BOAT_ID);
    public static final ResourceKey<TerraformBoatType> BB_BOAT_KEY = TerraformBoatTypeRegistry.createKey(BB_BOAT_ID);
    public static final ResourceKey<TerraformBoatType> WW_BOAT_KEY = TerraformBoatTypeRegistry.createKey(WW_BOAT_ID);
    public static final ResourceKey<TerraformBoatType> HH_BOAT_KEY = TerraformBoatTypeRegistry.createKey(HH_BOAT_ID);

    public static void registerBoats() {
        TerraformBoatType thBoat = new TerraformBoatType() {
            @Override
            public boolean isRaft() {
                return false;
            }

            @Override
            public Item getItem() {
                return ModItems.TH_BOAT;
            }

            @Override
            public Item getChestItem() {
                return ModItems.TH_CHEST_BOAT;
            }

            @Override
            public Item getPlanks() {
                return ModBlocks.TH_PLANKS.asItem();
            }
        };
        TerraformBoatType bbBoat = new TerraformBoatType() {
            @Override
            public boolean isRaft() {
                return false;
            }

            @Override
            public Item getItem() {
                return ModItems.BB_BOAT;
            }

            @Override
            public Item getChestItem() {
                return ModItems.BB_CHEST_BOAT;
            }

            @Override
            public Item getPlanks() {
                return ModBlocks.BB_PLANKS.asItem();
            }
        };
        TerraformBoatType wwBoat = new TerraformBoatType() {
            @Override
            public boolean isRaft() {
                return false;
            }

            @Override
            public Item getItem() {
                return ModItems.WW_BOAT;
            }

            @Override
            public Item getChestItem() {
                return ModItems.WW_CHEST_BOAT;
            }

            @Override
            public Item getPlanks() {
                return ModBlocks.WW_PLANKS.asItem();
            }
        };
        TerraformBoatType hhBoat = new TerraformBoatType() {
            @Override
            public boolean isRaft() {
                return false;
            }

            @Override
            public Item getItem() {
                return ModItems.HH_BOAT;
            }

            @Override
            public Item getChestItem() {
                return ModItems.HH_CHEST_BOAT;
            }

            @Override
            public Item getPlanks() {
                return ModBlocks.HH_PLANKS.asItem();
            }
        };

        Registry.register(TerraformBoatTypeRegistry.INSTANCE, TH_BOAT_KEY, thBoat);
        Registry.register(TerraformBoatTypeRegistry.INSTANCE, BB_BOAT_KEY, bbBoat);
        Registry.register(TerraformBoatTypeRegistry.INSTANCE, WW_BOAT_KEY, wwBoat);
        Registry.register(TerraformBoatTypeRegistry.INSTANCE, HH_BOAT_KEY, hhBoat);
    }
}
