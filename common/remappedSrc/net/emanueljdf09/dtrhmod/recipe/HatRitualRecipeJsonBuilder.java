package net.emanueljdf09.dtrhmod.recipe;

import net.emanueljdf09.dtrhmod.fabric.recipe.HatRitualRecipe;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.NonNullList;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class HatRitualRecipeJsonBuilder {
    private final List<Ingredient> ingredients = new ArrayList<>();
    private final ResourceLocation targetDimension;
    private final boolean isInstanceDimension;
    @Nullable
    private ResourceLocation structureId;
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public HatRitualRecipeJsonBuilder(ResourceLocation targetDimension, boolean isInstanceDimension) {
        this.targetDimension = targetDimension;
        this.isInstanceDimension = isInstanceDimension;
    }

    public static HatRitualRecipeJsonBuilder create(ResourceLocation targetDimension, boolean isInstanceDimension) {
        return new HatRitualRecipeJsonBuilder(targetDimension, isInstanceDimension);
    }

    public HatRitualRecipeJsonBuilder structureId(ResourceLocation structureId) {
        this.structureId = structureId;
        return this;
    }

    public HatRitualRecipeJsonBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public HatRitualRecipeJsonBuilder addIngredient(Item item) {
        this.ingredients.add(Ingredient.of(item));
        return this;
    }

    public HatRitualRecipeJsonBuilder criterion(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation id) {
        Advancement.Builder advancementBuilder = exporter.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation advancementId = id.withPrefix("recipes/hat_ritual/");

        NonNullList<Ingredient> defaultedIngredients = NonNullList.create();
        defaultedIngredients.addAll(this.ingredients);

        net.emanueljdf09.dtrhmod.recipe.HatRitualRecipe recipe = new net.emanueljdf09.dtrhmod.recipe.HatRitualRecipe(
                defaultedIngredients,
                this.targetDimension,
                this.isInstanceDimension,
                Optional.ofNullable(this.structureId)
        );

        exporter.accept(id, recipe, advancementBuilder.build(advancementId));
    }
}