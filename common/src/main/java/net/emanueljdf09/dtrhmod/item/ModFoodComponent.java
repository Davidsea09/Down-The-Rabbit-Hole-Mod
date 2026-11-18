package net.emanueljdf09.dtrhmod.item;

import net.emanueljdf09.dtrhmod.util.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;


public class ModFoodComponent {
    public static final FoodProperties EAT_ME = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.1f)
            .fast()
            .alwaysEdible()
            .effect(new MobEffectInstance(ModEffects.GROW, 1800, 0), 1.0f)
            .build();

    public static final FoodProperties DRINK_ME = new FoodProperties.Builder()
            .nutrition(1)
            .saturationModifier(0.1f)
            .fast()
            .alwaysEdible()
            .effect(new MobEffectInstance(ModEffects.SHRINK, 1800, 0), 1.0f)
            .build();
}
