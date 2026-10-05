package net.emanueljdf09.dtrhmod.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.emanueljdf09.dtrhmod.item.ModItems;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MobEntity.class)
public class MobEntityMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventScarfTargeting(LivingEntity target, CallbackInfo ci) {
        if (target instanceof PlayerEntity player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    ci.cancel();
                }
            });
        }
    }

}