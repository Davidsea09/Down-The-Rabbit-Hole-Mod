package net.emanueljdf09.dtrhmod.util;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class AdvancementUtil {

    public  static  void grant(ServerPlayer player, ResourceLocation id) {

        AdvancementHolder advancement = player.getServer()
                .getAdvancements()
                .get(id);

        if (advancement == null) return;

        AdvancementProgress progress =
                player.getAdvancements().getOrStartProgress(advancement);

        for (String criterion : progress.getRemainingCriteria()) {
            player.getAdvancements().award(advancement, criterion);

        }
    }
}
