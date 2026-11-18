package net.emanueljdf09.dtrhmod.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import java.util.ArrayList;
import java.util.List;

public class TeapotRecipe implements Recipe<TeapotRecipeInput> {
    private final List<Ingredient> recipeItems;
    private final Ingredient fluidIngredient;
    private final int cupCount;
    private final ItemStack output;
    private final List<MobEffectInstance> effects;

    public TeapotRecipe(List<Ingredient> recipeItems, Ingredient fluidIngredient, int cupCount, ItemStack output, List<MobEffectInstance> effects) {
        this.recipeItems = recipeItems;
        this.fluidIngredient = fluidIngredient;
        this.cupCount = cupCount;
        this.output = output;
        this.effects = effects;
    }

    public List<MobEffectInstance> getEffects() {
        return this.effects;
    }

    public int getCupCount() {
        return this.cupCount;
    }

    @Override
    public boolean matches(TeapotRecipeInput input, Level world) {
        List<ItemStack> inputs = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                inputs.add(stack);
            }
        }

        if (inputs.size() != this.recipeItems.size()) {
            return false;
        }

        for (Ingredient ingredient : this.recipeItems) {
            boolean matched = false;
            for (int i = 0; i < inputs.size(); i++) {
                if (ingredient.test(inputs.get(i))) {
                    inputs.remove(i);
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                return false;
            }
        }

        if (input.size() <= 3) {
            return true;
        }

        if (!this.fluidIngredient.test(input.getItem(3)) && !input.getItem(3).isEmpty()) {
            return false;
        }

        List<Item> uniqueLeaves = new ArrayList<>();
        int totalItems = 0;

        int[] leafSlots = new int[]{2, 3, 4};
        for (int slot : leafSlots) {
            ItemStack stack = input.getItem(slot);
            if (!stack.isEmpty()) {
                totalItems++;
                if (!uniqueLeaves.contains(stack.getItem())) {
                    uniqueLeaves.add(stack.getItem());
                }
            }
        }

        return uniqueLeaves.size() == totalItems;
    }

    @Override
    public ItemStack craft(TeapotRecipeInput input, HolderLookup.Provider lookup) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider lookup) {
        ItemStack displayStack = this.output.copy();

        if (this.effects != null && !this.effects.isEmpty()) {
            CompoundTag nbt = new CompoundTag();
            ListTag effectsList = new ListTag();

            for (MobEffectInstance effect : this.effects) {
                MobEffectInstance.CODEC.encodeStart(NbtOps.INSTANCE, effect).result().ifPresent(effectsList::add);
            }
            nbt.put("Effects", effectsList);

            if (this.fluidIngredient.test(new ItemStack(Items.LAVA_BUCKET))) {
                nbt.putString("FluidUsed", "minecraft:lava_bucket");
            } else if (this.fluidIngredient.test(new ItemStack(Items.MILK_BUCKET))) {
                nbt.putString("FluidUsed", "minecraft:milk_bucket");
            } else {
                nbt.putString("FluidUsed", "minecraft:water_bucket");
            }

            displayStack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        }

        return displayStack;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> allIngredients = NonNullList.withSize(this.recipeItems.size(), Ingredient.EMPTY);
        for (int i = 0; i < this.recipeItems.size(); i++) {
            allIngredients.set(i, this.recipeItems.get(i));
        }
        return allIngredients;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<TeapotRecipe> {
        public static final Type INSTANCE = new Type();
        public static final String ID = "teapot";
    }

    public static class Serializer implements RecipeSerializer<TeapotRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final String ID = "teapot";

        private static final MapCodec<TeapotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.recipeItems),
                Ingredient.CODEC_NONEMPTY.optionalFieldOf("fluid", Ingredient.of(Items.WATER_BUCKET)).forGetter(recipe -> recipe.fluidIngredient),
                Codec.INT.optionalFieldOf("cup_count", 4).forGetter(recipe -> recipe.cupCount),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.output),
                MobEffectInstance.CODEC.listOf().optionalFieldOf("effects", List.of()).forGetter(recipe -> recipe.effects)
        ).apply(instance, TeapotRecipe::new));

        private static final StreamCodec<RegistryFriendlyByteBuf, TeapotRecipe> PACKET_CODEC = StreamCodec.composite(
                Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.recipeItems,
                Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.fluidIngredient,
                ByteBufCodecs.VAR_INT, recipe -> recipe.cupCount,
                ItemStack.STREAM_CODEC, recipe -> recipe.output,
                MobEffectInstance.STREAM_CODEC.apply(ByteBufCodecs.list()), recipe -> recipe.effects,
                TeapotRecipe::new
        );

        @Override
        public MapCodec<TeapotRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, TeapotRecipe> streamCodec() {
            return PACKET_CODEC;
        }
    }

}
