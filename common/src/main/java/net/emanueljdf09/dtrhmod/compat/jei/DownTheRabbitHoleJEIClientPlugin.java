package net.emanueljdf09.dtrhmod.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.emanueljdf09.dtrhmod.menu.screen.TeapotScreen;
import net.emanueljdf09.dtrhmod.recipe.TeapotRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
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
        registration.registerSubtypeInterpreter(VanillaTypes.ITEM_STACK, ModItems.FILLED_TEA_CUP.get(), (stack, context) -> {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            return customData != null ? customData.copyTag().toString() : "";
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

        List<TeapotRecipeVariant> expandedVariants = new ArrayList<>();
        for (TeapotRecipe recipe : baseRecipes) {
            expandedVariants.add(new TeapotRecipeVariant(recipe, TeapotRecipeVariant.LiquidType.WATER));
            expandedVariants.add(new TeapotRecipeVariant(recipe, TeapotRecipeVariant.LiquidType.LAVA));
            expandedVariants.add(new TeapotRecipeVariant(recipe, TeapotRecipeVariant.LiquidType.MILK));
        }

        registration.addRecipes(TeapotCategory.TEAPOT_RECIPE_TYPE, expandedVariants);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(TeapotScreen.class, 91, 35, 24, 17,
                TeapotCategory.TEAPOT_RECIPE_TYPE);
    }
}