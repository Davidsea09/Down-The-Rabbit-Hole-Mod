package net.emanueljdf09.dtrhmod.mixin;

import dev.emi.trinkets.api.TrinketsApi;
import net.emanueljdf09.dtrhmod.fabric.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public class MobEntityMixin {
    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void preventScarfTargeting(LivingEntity target, CallbackInfo ci) {
        if (target instanceof Player player) {
            TrinketsApi.getTrinketComponent(player).ifPresent(component -> {
                if (component.isEquipped(ModItems.CAT_SCARF_ITEM) && player.isSneaking()) {
                    ci.cancel();
                }
            });
        }
    }
}