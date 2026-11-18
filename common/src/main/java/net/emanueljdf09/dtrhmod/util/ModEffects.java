package net.emanueljdf09.dtrhmod.util;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.emanueljdf09.dtrhmod.util.effects.GrowStatusEffect;
import net.emanueljdf09.dtrhmod.util.effects.ShrinkStatusEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.Potion;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.MOB_EFFECT);

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(DownTheRabbitHole.MOD_ID, Registries.POTION);

    public static final RegistrySupplier<MobEffect> GROW = MOB_EFFECTS.register("grow",
            () -> new GrowStatusEffect(MobEffectCategory.NEUTRAL, 0x90329f));

    public static final RegistrySupplier<MobEffect> SHRINK = MOB_EFFECTS.register("shrink",
            () -> new ShrinkStatusEffect(MobEffectCategory.NEUTRAL, 0x3399FF));

    public static final RegistrySupplier<Potion> SHRINK_POTION = POTIONS.register("shrink_potion",
            () -> new Potion(new MobEffectInstance(SHRINK, 3600, 0)));

    public static final RegistrySupplier<Potion> GROW_POTION = POTIONS.register("grow_potion",
            () -> new Potion(new MobEffectInstance(GROW, 3600, 0)));

    public static void registerBrewingRecipes() {
        // Nota: Las recetas de pociones en multi-loader se manejan idealmente mediante
        // archivos JSON en tus datapacks o mediante los registros específicos de cada plataforma.
    }

    public static void registerModEffects() {
        MOB_EFFECTS.register();
        POTIONS.register();
        DownTheRabbitHole.LOGGER.info("Registering ModEffects for " + DownTheRabbitHole.MOD_ID);
    }
}