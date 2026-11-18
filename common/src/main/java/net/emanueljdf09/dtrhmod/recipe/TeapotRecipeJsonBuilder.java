package net.emanueljdf09.dtrhmod.recipe;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRequirements;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TeapotRecipeJsonBuilder {
    private final Item output;
    private final int outputCount;
    private final List<Ingredient> ingredients = new ArrayList<>();
    private Ingredient fluid = Ingredient.of(Items.WATER_BUCKET);
    private int cupCount = 4;
    private final List<MobEffectInstance> effects = new ArrayList<>();
    private final Map<String, Criterion<?>> criteria = new LinkedHashMap<>();

    public TeapotRecipeJsonBuilder(Item output, int outputCount) {
        this.output = output;
        this.outputCount = outputCount;
    }

    public static TeapotRecipeJsonBuilder create(Item output) {
        return new TeapotRecipeJsonBuilder(output, 1);
    }

    public static TeapotRecipeJsonBuilder create(Item output, int count) {
        return new TeapotRecipeJsonBuilder(output, count);
    }

    public TeapotRecipeJsonBuilder addEffect(Holder<MobEffect> effectEntry, int duration, int amplifier) {
        this.effects.add(new MobEffectInstance(effectEntry, duration, amplifier));
        return this;
    }

    public TeapotRecipeJsonBuilder addEffect(MobEffect effect, int duration, int amplifier) {
        Holder<MobEffect> entry = BuiltInRegistries.MOB_EFFECT.wrapAsHolder(effect);
        return this.addEffect(entry, duration, amplifier);
    }

    public TeapotRecipeJsonBuilder fluid(Item fluidItem) {
        this.fluid = Ingredient.of(fluidItem);
        return this;
    }

    public TeapotRecipeJsonBuilder cupCount(int count) {
        this.cupCount = count;
        return this;
    }

    public TeapotRecipeJsonBuilder criterion(String name, Criterion<?> criterion) {
        this.criteria.put(name, criterion);
        return this;
    }

    public TeapotRecipeJsonBuilder addIngredient(Ingredient ingredient) {
        this.ingredients.add(ingredient);
        return this;
    }

    public TeapotRecipeJsonBuilder addIngredient(Item item) {
        this.ingredients.add(Ingredient.of(item));
        return this;
    }

    public void offerTo(RecipeOutput exporter, ResourceLocation id) {
        Advancement.Builder advancementBuilder = exporter.advancement()
                .addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
                .rewards(AdvancementRewards.Builder.recipe(id))
                .requirements(AdvancementRequirements.Strategy.OR);

        this.criteria.forEach(advancementBuilder::addCriterion);

        ResourceLocation advancementId = id.withPrefix("recipes/" + BuiltInRegistries.ITEM.getKey(this.output).getPath() + "/");

        NonNullList<Ingredient> defaultedIngredients = NonNullList.create();
        defaultedIngredients.addAll(this.ingredients);

        net.emanueljdf09.dtrhmod.recipe.TeapotRecipe recipe = new net.emanueljdf09.dtrhmod.recipe.TeapotRecipe(
                defaultedIngredients,
                fluid,
                cupCount,
                new ItemStack(output, outputCount),
                effects
        );

        exporter.accept(id, recipe, advancementBuilder.build(advancementId));
    }
}