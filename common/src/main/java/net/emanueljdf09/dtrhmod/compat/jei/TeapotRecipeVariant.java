package net.emanueljdf09.dtrhmod.compat.jei;

import net.emanueljdf09.dtrhmod.recipe.TeapotRecipe;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.effect.MobEffectInstance;
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
        ItemStack resultCup = recipe.getResultItem(registries).copy();
        CustomData customData = resultCup.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY);
        CompoundTag nbt = customData.copyTag();
        ListTag effectsList = new ListTag();

        int totalR = 0;
        int totalG = 0;
        int totalB = 0;
        int colorCount = 0;

        var ops = registries.createSerializationContext(NbtOps.INSTANCE);

        for (MobEffectInstance baseEffect : recipe.getEffects()) {
            int finalDuration = baseEffect.getDuration();
            int finalAmplifier = baseEffect.getAmplifier();

            if (liquidType == LiquidType.LAVA) {
                finalAmplifier += 1;
            } else if (liquidType == LiquidType.MILK) {
                finalDuration = 400; // 20 seconds
            }

            MobEffectInstance modifiedEffect = new MobEffectInstance(
                    baseEffect.getEffect(),
                    finalDuration,
                    finalAmplifier,
                    baseEffect.isAmbient(),
                    baseEffect.isVisible()
            );

            MobEffectInstance.CODEC.encodeStart(ops, modifiedEffect)
                    .result()
                    .ifPresent(effectsList::add);

            int effectColor = baseEffect.getEffect().value().getColor();
            totalR += (effectColor >> 16) & 0xFF;
            totalG += (effectColor >> 8) & 0xFF;
            totalB += effectColor & 0xFF;
            colorCount++;
        }

        if (!effectsList.isEmpty()) {
            int r = colorCount > 0 ? (totalR / colorCount) : 0x33;
            int g = colorCount > 0 ? (totalG / colorCount) : 0x99;
            int b = colorCount > 0 ? (totalB / colorCount) : 0xFF;

            if (liquidType == LiquidType.LAVA) {
                r = (int) (r * 0.6 + 0xFF * 0.4);
                g = (int) (g * 0.6 + 0x45 * 0.4);
                b = (int) (b * 0.6 + 0x00 * 0.4);
            } else if (liquidType == LiquidType.MILK) {
                r = (int) (r * 0.5 + 255 * 0.5);
                g = (int) (g * 0.5 + 255 * 0.5);
                b = (int) (b * 0.5 + 255 * 0.5);
            }

            int finalArgbColor = 0xFF000000 | (r << 16) | (g << 8) | b;

            nbt.put("Effects", effectsList);
            nbt.putString("FluidUsed", liquidType.getFluidId());
            nbt.putInt("Color", finalArgbColor);
        }

        resultCup.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        return resultCup;
    }
}