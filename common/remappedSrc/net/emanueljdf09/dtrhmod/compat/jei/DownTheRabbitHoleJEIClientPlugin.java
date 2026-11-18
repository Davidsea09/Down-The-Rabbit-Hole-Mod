package net.emanueljdf09.dtrhmod.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.compat.jei.TeapotRecipeVariant;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.menu.screen.TeapotScreen;
import net.emanueljdf09.dtrhmod.fabric.recipe.TeapotRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeHolder;
import java.util.ArrayList;
import java.util.List;

public class DownTheRabbitHoleJEIClientPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TeapotCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.FILLED_TEA_CUP, (stack, context) -> {
            NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
            return customData != null ? customData.toString() : "";
        });
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        Minecraft client = Minecraft.getInstance();
        if (client.level == null) return;

        List<TeapotRecipe> baseRecipes = client.level.getRecipeManager()
                .getAllRecipesFor(TeapotRecipe.Type.INSTANCE)
                .stream()
                .map(RecipeHolder::value)
                .toList();

        List<net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant> expandedVariants = new ArrayList<>();
        for (TeapotRecipe recipe : baseRecipes) {
            expandedVariants.add(new net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant(recipe, net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant.LiquidType.WATER));
            expandedVariants.add(new net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant(recipe, net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant.LiquidType.LAVA));
            expandedVariants.add(new net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant(recipe, net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant.LiquidType.MILK));
        }

        registration.addRecipes(TeapotCategory.TEAPOT_RECIPE_TYPE, expandedVariants);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TeapotScreen.class, 91, 35, 24, 17,
                TeapotCategory.TEAPOT_RECIPE_TYPE);
    }
}