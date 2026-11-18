package net.emanueljdf09.dtrhmod.util.effects;

import net.emanueljdf09.dtrhmod.fabric.util.ModEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import virtuoel.pehkui.api.ScaleTypes;

public class GrowStatusEffect extends MobEffect implements RemovalAwareEffect {
    public GrowStatusEffect(MobEffectCategory category, int color) {
        super(category, color);
    }


    @Override
    public void onEffectStarted(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {

            if (entity.hasEffect(ModEffects.SHRINK)) {
                entity.removeEffect(ModEffects.SHRINK);
            }

            ScaleTypes.BASE.getScaleData(entity).setTargetScale(2.5f);
            ScaleTypes.MOTION.getScaleData(entity).setTargetScale(0.65f);
            ScaleTypes.HEALTH.getScaleData(entity).setTargetScale(1.5f);
            ScaleTypes.ATTACK.getScaleData(entity).setTargetScale(1.5f);
            ScaleTypes.JUMP_HEIGHT.getScaleData(entity).setTargetScale(1.0f);

        }
        super.onEffectStarted(entity, amplifier);
    }

    @Override
    public void onRemovedFrom(LivingEntity entity) {
        if (!entity.level().isClientSide) {
            if (!entity.hasEffect(ModEffects.GROW)) {
                ScaleTypes.BASE.getScaleData(entity).setTargetScale(1.0f);
                ScaleTypes.MOTION.getScaleData(entity).setTargetScale(1.0f);
                ScaleTypes.HEALTH.getScaleData(entity).setTargetScale(1.0f);
                ScaleTypes.JUMP_HEIGHT.getScaleData(entity).setTargetScale(1.0f);
                ScaleTypes.ATTACK.getScaleData(entity).setTargetScale(1.0f);
            }
        }
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

}
