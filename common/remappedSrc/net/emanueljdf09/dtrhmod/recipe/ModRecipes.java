package net.emanueljdf09.dtrhmod.recipe;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class ModRecipes {
    public static void registerRecipes() {
        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot"),
                TeapotRecipe.Serializer.INSTANCE);

        Registry.register(BuiltInRegistries.RECIPE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "teapot"),
                TeapotRecipe.Type.INSTANCE);

        Registry.register(BuiltInRegistries.RECIPE_SERIALIZER,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hat_ritual"),
                HatRitualRecipe.Serializer.INSTANCE);

        Registry.register(BuiltInRegistries.RECIPE_TYPE,
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "hat_ritual"),
                HatRitualRecipe.Type.INSTANCE);

        DownTheRabbitHole.LOGGER.info("Registering Recipes for " + DownTheRabbitHole.MOD_ID);

    }
}
