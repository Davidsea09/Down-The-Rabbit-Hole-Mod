package net.emanueljdf09.dtrhmod.recipe;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

public record TeapotRecipeInput(List<ItemStack> items) implements RecipeInput {
    public static TeapotRecipeInput of(Container inventory) {
        List<ItemStack> list = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            list.add(inventory.getItem(i));
        }
        return new TeapotRecipeInput(list);
    }


    @Override
    public ItemStack getItem(int slot) {
        return slot >= 0 && slot < items.size() ? items.get(slot) : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return items.size();
    }
}
