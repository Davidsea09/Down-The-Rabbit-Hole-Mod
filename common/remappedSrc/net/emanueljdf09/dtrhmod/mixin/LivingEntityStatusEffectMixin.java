package net.emanueljdf09.dtrhmod.mixin;

import net.emanueljdf09.dtrhmod.fabric.util.effects.RemovalAwareEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityStatusEffectMixin {

    @Inject(method = "onEffectRemoved", at = @At("TAIL"))
    private void dtrhmod$afterEffectRemoved(MobEffectInstance effect, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (self.level().isClientSide()) return;

        if (effect.getEffect().value() instanceof RemovalAwareEffect aware) {
            aware.onRemovedFrom(self);
        }
    }
}