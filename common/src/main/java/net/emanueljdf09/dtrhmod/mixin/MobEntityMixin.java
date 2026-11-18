package net.emanueljdf09.dtrhmod.mixin;

import io.wispforest.accessories.api.AccessoriesCapability;
import net.emanueljdf09.dtrhmod.item.ModItems;
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
        if (target instanceof Player player && hasCatScarf(player) && player.isShiftKeyDown()) {
            ci.cancel();
        }
    }

    private boolean hasCatScarf(Player player) {
        return AccessoriesCapability.getOptionally(player)
                .map(capability -> capability.isEquipped(ModItems.CAT_SCARF_ITEM.get()))
                .orElse(false);
    }
}
