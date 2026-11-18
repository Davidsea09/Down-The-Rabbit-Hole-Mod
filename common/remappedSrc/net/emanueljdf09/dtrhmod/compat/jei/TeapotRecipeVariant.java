package net.emanueljdf09.dtrhmod.compat.jei;

import net.emanueljdf09.dtrhmod.fabric.recipe.TeapotRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;

public class TeapotRecipeVariant {
    public enum LiquidType {
        WATER(new ItemStack(Items.WATER_BUCKET), "minecraft:water_bucket"),
        LAVA(new ItemStack(Items.LAVA_BUCKET), "minecraft:lava_bucket"),
        MILK(new ItemStack(Items.MILK_BUCKET), "minecraft:milk_bucket");

        private final ItemStack stack;
        private final String fluidId;

        LiquidType(ItemStack stack, String fluidId) {
            this.stack = stack;
            this.fluidId = fluidId;
        }

        public ItemStack getStack() { return stack; }
        public String getFluidId() { return fluidId; }
    }

    private final TeapotRecipe recipe;
    private final LiquidType liquidType;

    public TeapotRecipeVariant(TeapotRecipe recipe, LiquidType liquidType) {
        this.recipe = recipe;
        this.liquidType = liquidType;
    }

    public TeapotRecipe getRecipe() {
        return recipe;
    }

    public LiquidType getLiquidType() {
        return liquidType;
    }

    public ItemStack getOutput(HolderLookup.Provider registries) {
        ItemStack output = recipe.getResult(registries).copy();

        CustomData customData = output.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag();

        nbt.putString("FluidUsed", liquidType.getFluidId());

        int baseColor = nbt.contains("Color") ? nbt.getInt("Color") : 0x3399FF;
        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;

        if (liquidType == LiquidType.LAVA) {
            r = (int) (r * 0.6 + 0xFF * 0.4);
            g = (int) (g * 0.6 + 0x45 * 0.4);
            b = (int) (b * 0.6 + 0x00 * 0.4);

            if (nbt.contains("Effects", 9)) {
                ListTag list = nbt.getList("Effects", 10);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag effectNbt = list.getCompound(i);
                    int currentAmp = effectNbt.getInt("Amplifier");
                    effectNbt.putInt("Amplifier", Math.max(currentAmp, 1));
                }
            }
        } else if (liquidType == LiquidType.MILK) {
            r = (int) (r * 0.5 + 255 * 0.5);
            g = (int) (g * 0.5 + 255 * 0.5);
            b = (int) (b * 0.5 + 255 * 0.5);

            if (nbt.contains("Effects", 9)) {
                ListTag list = nbt.getList("Effects", 10);
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag effectNbt = list.getCompound(i);
                    effectNbt.putInt("Duration", 400);
                }
            }
        }

        int finalArgbColor = 0xFF000000 | (r << 16) | (g << 8) | b;
        nbt.putInt("Color", finalArgbColor);

        output.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return output;
    }
}