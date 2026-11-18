package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.util.ModPackets;
import net.emanueljdf09.dtrhmod.fabric.util.components.ProgressionComponent;
import net.emanueljdf09.dtrhmod.fabric.util.config.ModConfigData;
import net.emanueljdf09.dtrhmod.fabric.world.dimension.ModDimensions;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class WonderlandProgressionUtil {
    private static final Map<UUID, Long> TOAST_COOLDOWNS = new HashMap<>();

    public static void register() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                if (player.getWorld().getRegistryKey() == ModDimensions.WONDERLAND_LEVEL_KEY) {
                    evaluateBiomeProgression(player);
                } else {
                    TOAST_COOLDOWNS.remove(player.getUuid());
                }
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            TOAST_COOLDOWNS.remove(handler.getPlayer().getUuid());
        });
    }

    public static void triggerMilestone(ServerPlayer player, int nextStage, String bookTriggerKey) {
        ProgressionComponent story = ModComponents.PROGRESSION_COMPONENT.get(player);

        if (nextStage > story.getCompletedStages()) {
            story.setCompletedStages(nextStage);

            ModComponents.PROGRESSION_COMPONENT.sync(player);

            player.serverLevel().playSound(
                    null, player.blockPosition(),
                    SoundEvents.UI_TOAST_CHALLENGE_COMPLETE,
                    SoundSource.PLAYERS, 1.0f, 1.0f
            );

            if (bookTriggerKey != null) {
                net.emanueljdf09.dtrhmod.util.ModPackets.sendOpenBookPacket(player, bookTriggerKey);
            }
        }
    }

    private static void evaluateBiomeProgression(ServerPlayer player) {
        if (!ModConfigData.isBiomeLockingEnabled()) {
            return;
        }

        if (player.isCreative() || player.isSpectator()) {
            return;
        }

        BlockPos pos = player.blockPosition();
        Optional<ResourceKey<Biome>> currentBiomeKey = player.level().getBiome(pos).unwrapKey();

        if (currentBiomeKey.isEmpty()) return;
        ResourceLocation biomeId = currentBiomeKey.get().location();

        ProgressionComponent story = ModComponents.PROGRESSION_COMPONENT.get(player);
        int stage = story.getCompletedStages();

        boolean isLocked = false;
        String warningMessage = "";

        if (biomeId.getPath().equals("vale_of_tears") && stage < 1) {
            isLocked = true;
            warningMessage = "You're not supposed to be here yet";
        } else if (biomeId.getPath().equals("chessboard_fields") && stage < 2) {
            isLocked = true;
            warningMessage = "You're not supposed to be here yet";
        }

        if (isLocked) {
            UUID uuid = player.getUUID();
            long currentTime = player.level().getGameTime();

            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 0, true, false, false));

            if (player.tickCount % 100 == 0) {
                player.hurt(player.damageSources().generic(), 1.0f);
                player.serverLevel().playSound(
                        null, player.blockPosition(),
                        SoundEvents.PLAYER_HURT,
                        SoundSource.PLAYERS, 0.5f, 1.0f
                );
                player.displayClientMessage(Component.literal("§c" + warningMessage), true);
            }

            if (!TOAST_COOLDOWNS.containsKey(uuid) || currentTime - TOAST_COOLDOWNS.get(uuid) > 100) {
                TOAST_COOLDOWNS.put(uuid, currentTime);

                net.emanueljdf09.dtrhmod.util.ModPackets.sendLockedBiomeToastPacket(player);
            }

        }
    }

    public record LockedBiomeToast(ItemStack item) implements Toast {

        private static final ResourceLocation TEXTURE = ResourceLocation.parse("textures/gui/toasts.png");

        private static final Component TITLE = Component.translatable("misc.dtrhmod.biome_locked.title");
        private static final Component DESCRIPTION = Component.translatable("misc.dtrhmod.biome_locked.description");

        @Override
        public Visibility render(GuiGraphics context, ToastComponent manager, long startTime) {
            context.blit(TEXTURE, 0, 0, 0, 0, this.width(), this.height());
            context.renderItem(this.item(), 6, 8);
            context.drawString(manager.getMinecraft().font, TITLE, 25,7, -256, false);
            context.drawString(manager.getMinecraft().font, DESCRIPTION, 25, 18, 16777215, false);


            return startTime >= 10000L ? Visibility.HIDE : Visibility.SHOW;
        }
    }

}