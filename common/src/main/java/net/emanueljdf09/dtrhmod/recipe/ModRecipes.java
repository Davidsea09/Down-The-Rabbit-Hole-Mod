package net.emanueljdf09.dtrhmod.recipe;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.RECIPE_SERIALIZER);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.RECIPE_TYPE);

    public static final RegistrySupplier<RecipeSerializer<TeapotRecipe>> TEAPOT_SERIALIZER =
            SERIALIZERS.register("teapot", () -> TeapotRecipe.Serializer.INSTANCE);

    public static final RegistrySupplier<RecipeType<TeapotRecipe>> TEAPOT_TYPE =
            TYPES.register("teapot", () -> TeapotRecipe.Type.INSTANCE);

    public static final RegistrySupplier<RecipeSerializer<HatRitualRecipe>> HAT_RITUAL_SERIALIZER =
            SERIALIZERS.register("hat_ritual", () -> HatRitualRecipe.Serializer.INSTANCE);

    public static final RegistrySupplier<RecipeType<HatRitualRecipe>> HAT_RITUAL_TYPE =
            TYPES.register("hat_ritual", () -> HatRitualRecipe.Type.INSTANCE);

    public static void registerRecipes() {
        SERIALIZERS.register();
        TYPES.register();
        DownTheRabbitHole.LOGGER.info("Registering Recipes for " + DownTheRabbitHole.MOD_ID);
    }
}