package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.util.components.ClientProgression;
import net.emanueljdf09.dtrhmod.util.components.ProgressionComponent;
import net.minecraft.client.Minecraft;
import software.bernie.geckolib.animation.AnimationState;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;

public class ClientAnimationHelper {
    public static PlayState handleDoorAnimation(AnimationState<?> state, RawAnimation idle, RawAnimation openIdle) {
        Minecraft client = Minecraft.getInstance();

        if (client.player != null) {
            ProgressionComponent component = ClientProgression.get();

            if (component.hasOpenedExtDoor()) {
                return state.setAndContinue(openIdle);
            }
        }
        return state.setAndContinue(idle);
    }

    public static PlayState handleChestAnimation(AnimationState<?> state, RawAnimation closed, RawAnimation openNormal, RawAnimation takeGrownItems, RawAnimation openGrown) {
        Minecraft client = Minecraft.getInstance();

        if (client.player != null) {
            ProgressionComponent component = ClientProgression.get();

            if (component.hasOpenedExtGrownChest()) {
                return state.setAndContinue(takeGrownItems);
            }

            if (component.hasOpenedExtChest() && client.player.hasEffect(ModEffects.GROW)) {
                return state.setAndContinue(openGrown);
            }
            if (component.hasOpenedExtChest()) {
                return state.setAndContinue(openNormal);
            }
        }

        return state.setAndContinue(closed);
    }
}
