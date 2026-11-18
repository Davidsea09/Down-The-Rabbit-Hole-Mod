package net.emanueljdf09.dtrhmod.util;

import dev.architectury.networking.NetworkManager;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModPackets {
    public record OpenBookPayload(String triggerKey) implements CustomPacketPayload {
        public static final Type<OpenBookPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "open_custom_book"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenBookPayload> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OpenBookPayload::triggerKey, OpenBookPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public record LockedBiomeToastPayload() implements CustomPacketPayload {
        public static final Type<LockedBiomeToastPayload> TYPE =
                new Type<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "locked_biome_toast"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LockedBiomeToastPayload> CODEC =
                StreamCodec.unit(new LockedBiomeToastPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void sendOpenBookPacket(ServerPlayer player, String triggerKey) {
        NetworkManager.sendToPlayer(player, new OpenBookPayload(triggerKey));
    }

    public static void sendLockedBiomeToastPacket(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, new LockedBiomeToastPayload());
    }
}