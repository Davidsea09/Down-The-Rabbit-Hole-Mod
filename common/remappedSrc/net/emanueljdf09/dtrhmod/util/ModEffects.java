package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.emanueljdf09.dtrhmod.fabric.util.effects.GrowStatusEffect;
import net.emanueljdf09.dtrhmod.fabric.util.effects.ShrinkStatusEffect;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class ModEffects {

    public static final Holder<MobEffect> GROW = registerStatusEffect("grow",
            new GrowStatusEffect(MobEffectCategory.NEUTRAL, 0x90329f));

    public static final Holder<MobEffect> SHRINK = registerStatusEffect("shrink",
            new ShrinkStatusEffect(MobEffectCategory.NEUTRAL, 0x3399FF));

    public static final Holder<Potion> SHRINK_POTION = registerPotions("shrink_potion",
            new Potion(new MobEffectInstance(SHRINK, 3600, 0)));

    public static final Holder<Potion> GROW_POTION = registerPotions("grow_potion",
            new Potion(new MobEffectInstance(GROW, 3600, 0)));

    public static Holder<Potion> registerPotions(String name, Potion potion) {
        return Registry.registerForHolder(BuiltInRegistries.POTION, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name), potion);
    }

    public static Holder<MobEffect> registerStatusEffect(String name, MobEffect statusEffect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, name), statusEffect);
    }

    public static void registerBrewingRecipes() {
        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, ModItems.POCKETWATCH, SHRINK_POTION);
            builder.registerPotionRecipe(Potions.AWKWARD, ModItems.AURORA_STORYBOOK, GROW_POTION);
        });
    }

    public static void registerModEffects() {
        DownTheRabbitHole.LOGGER.info("Registering ModEffects for " + DownTheRabbitHole.MOD_ID);
    }
}
