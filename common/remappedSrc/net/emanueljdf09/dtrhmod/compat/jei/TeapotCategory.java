package net.emanueljdf09.dtrhmod.compat.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.drawable.IDrawableStatic;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.block.ModBlocks;
import net.emanueljdf09.dtrhmod.fabric.compat.jei.TeapotRecipeVariant;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.recipe.TeapotRecipe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeapotCategory implements IRecipeCategory<net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant> {
    public static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot");
    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "textures/gui/teapot_jei_screen.png");

    public static final RecipeType<net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant> TEAPOT_RECIPE_TYPE =
            new RecipeType<>(UID, net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;

    public TeapotCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 85);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocks.TEAPOT_BLOCK));

        IDrawableStatic staticArrow = helper.createDrawable(TEXTURE, 176, 14, 24, 17);

        this.arrow = helper.createAnimatedDrawable(staticArrow, 80, IDrawableAnimated.StartDirection.LEFT, false);

    }

    @Override
    public RecipeType<net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant> getRecipeType() {
        return TEAPOT_RECIPE_TYPE;
    }

    @Override
    public Component getTitle() {
        return ModBlocks.TEAPOT_BLOCK.getName();
    }

    @Override
    public int getWidth() {
        return 176;
    }

    @Override
    public int getHeight() {
        return 87;
    }

    @Override
    public void draw(net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant recipeVariant, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        this.background.draw(guiGraphics, 0, 0);

        this.arrow.draw(guiGraphics, 91, 35);
    }

    @Override
    public @Nullable IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, net.emanueljdf09.dtrhmod.compat.jei.TeapotRecipeVariant variant, IFocusGroup focuses) {
        TeapotRecipe recipe = variant.getRecipe();

        List<Ingredient> leafIngredients = recipe.getIngredients();
        if (!leafIngredients.isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 27, 34).addIngredients(leafIngredients.get(0));
        }

        ItemStack emptyCupDisplay = new ItemStack(ModItems.EMPTY_CUP, 1);
        builder.addSlot(RecipeIngredientRole.INPUT, 63, 56).addItemStack(emptyCupDisplay);

        builder.addSlot(RecipeIngredientRole.INPUT, 27, 12).addItemStack(variant.getLiquidType().getStack());

        RegistryAccess rm = Minecraft.getInstance().level.registryAccess();
        ItemStack specificOutput = variant.getOutput(rm);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 130, 35).addItemStack(specificOutput);
    }
}