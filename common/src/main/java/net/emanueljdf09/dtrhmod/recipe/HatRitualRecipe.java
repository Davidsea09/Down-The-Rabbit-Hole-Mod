package net.emanueljdf09.dtrhmod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HatRitualRecipe implements Recipe<HatRitualRecipeInput> {
    private final NonNullList<Ingredient> ingredients;
    private final ResourceLocation targetDimension;
    private final boolean isInstanceDimension;
    private final Optional<ResourceLocation> structureId;

    public HatRitualRecipe(NonNullList<Ingredient> ingredients, ResourceLocation targetDimension, boolean isInstanceDimension, Optional<ResourceLocation> structureId) {
        this.ingredients = ingredients;
        this.targetDimension = targetDimension;
        this.isInstanceDimension = isInstanceDimension;
        this.structureId = structureId;
    }

    public ResourceLocation getStructureId() {
        return structureId.orElse(null);
    }

    public ResourceLocation getTargetDimension() {
        return targetDimension;
    }

    public boolean isInstanceDimension() {
        return isInstanceDimension;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(HatRitualRecipeInput input, Level world) {
        if (world.isClientSide) return false;

        List<ItemStack> nonEmptyInputs = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) nonEmptyInputs.add(stack);
        }

        if (nonEmptyInputs.size() != ingredients.size()) return false;

        RecipeMatcher matcher = new RecipeMatcher();
        return matcher.match(nonEmptyInputs, ingredients);
    }

    @Override
    public ItemStack assemble(HatRitualRecipeInput recipeInput, HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registriesLookup) {
        return ItemStack.EMPTY;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<HatRitualRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "hat_ritual";
    }

    private static class RecipeMatcher {
        public boolean match(List<ItemStack> inputs, NonNullList<Ingredient> ingredients) {
            boolean[] used = new boolean[inputs.size()];
            for (Ingredient ingredient : ingredients) {
                boolean matched = false;
                for (int i = 0; i < inputs.size(); i++) {
                    if (!used[i] && ingredient.test(inputs.get(i))) {
                        used[i] = true;
                        matched = true;
                        break;
                    }
                }
                if (!matched) return false;
            }
            return true;
        }
    }

    public static class Serializer implements RecipeSerializer<HatRitualRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "hat_ritual";

        public static final MapCodec<HatRitualRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        Ingredient.CODEC.listOf().fieldOf("ingredients").xmap(
                                list -> {
                                    NonNullList<Ingredient> defaultedList = NonNullList.create();
                                    defaultedList.addAll(list);
                                    return defaultedList;
                                },
                                defaultedList -> defaultedList
                        ).forGetter(recipe -> recipe.ingredients),
                        ResourceLocation.CODEC.fieldOf("target_dimension").forGetter(recipe -> recipe.targetDimension),
                        Codec.BOOL.fieldOf("is_instance_dimension").forGetter(recipe -> recipe.isInstanceDimension),
                        ResourceLocation.CODEC.optionalFieldOf("structure_id").forGetter(recipe -> recipe.structureId)
                ).apply(instance, HatRitualRecipe::new)
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, HatRitualRecipe> PACKET_CODEC = StreamCodec.of(
                (buf, recipe) -> {
                    buf.writeVarInt(recipe.ingredients.size());
                    for (Ingredient ingredient : recipe.ingredients) {
                        Ingredient.CONTENTS_STREAM_CODEC.encode(buf, ingredient);
                    }
                    buf.writeResourceLocation(recipe.targetDimension);
                    buf.writeBoolean(recipe.isInstanceDimension);

                    boolean hasStructure = recipe.structureId.isPresent();
                    buf.writeBoolean(hasStructure);
                    recipe.structureId.ifPresent(buf::writeResourceLocation);
                },
                buf -> {
                    int size = buf.readVarInt();
                    NonNullList<Ingredient> ingredients = NonNullList.withSize(size, Ingredient.EMPTY);
                    for (int i = 0; i < size; i++) {
                        ingredients.set(i, Ingredient.CONTENTS_STREAM_CODEC.decode(buf));
                    }
                    ResourceLocation targetDimension = buf.readResourceLocation();
                    boolean isInstance = buf.readBoolean();
                    Optional<ResourceLocation> structureId = buf.readBoolean() ? Optional.of(buf.readResourceLocation()) : Optional.empty();
                    return new HatRitualRecipe(ingredients, targetDimension, isInstance, structureId);
                }
        );

        @Override
        public MapCodec<HatRitualRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, HatRitualRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }
}