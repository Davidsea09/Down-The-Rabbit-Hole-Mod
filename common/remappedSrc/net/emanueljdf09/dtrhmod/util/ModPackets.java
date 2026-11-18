package net.emanueljdf09.dtrhmod.util;

import net.emanueljdf09.dtrhmod.fabric.DownTheRabbitHole;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ModPackets {
    public record OpenBookPayload(String triggerKey) implements CustomPacketPayload {
        public static final Type<OpenBookPayload> ID =
                new Type<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "open_custom_book"));
        public static final StreamCodec<RegistryFriendlyByteBuf, OpenBookPayload> CODEC =
                StreamCodec.composite(ByteBufCodecs.STRING_UTF8, OpenBookPayload::triggerKey, OpenBookPayload::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public record LockedBiomeToastPayload() implements CustomPacketPayload {
        public static final Type<LockedBiomeToastPayload> ID =
                new Type<>(ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "locked_biome_toast"));
        public static final StreamCodec<RegistryFriendlyByteBuf, LockedBiomeToastPayload> CODEC =
                StreamCodec.unit(new LockedBiomeToastPayload());

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return ID;
        }
    }

    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(OpenBookPayload.ID, OpenBookPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LockedBiomeToastPayload.ID, LockedBiomeToastPayload.CODEC);
    }

    public static void sendOpenBookPacket(ServerPlayer player, String triggerKey) {
        ServerPlayNetworking.send(player, new OpenBookPayload(triggerKey));
    }

    public static void sendLockedBiomeToastPacket(ServerPlayer player) {
        ServerPlayNetworking.send(player, new LockedBiomeToastPayload());
    }
}
