package net.emanueljdf09.dtrhmod.util.components;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import net.emanueljdf09.dtrhmod.DownTheRabbitHole;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public class ProgressionSync {
    public record Payload(CompoundTag data) implements CustomPacketPayload {
        public static final CustomPacketPayload.Type<Payload> TYPE = new CustomPacketPayload.Type<>(
                ResourceLocation.fromNamespaceAndPath(DownTheRabbitHole.MOD_ID, "progression_sync"));
        public static final StreamCodec<ByteBuf, Payload> CODEC =
                ByteBufCodecs.COMPOUND_TAG.map(Payload::new, Payload::data);

        @Override
        public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    private ProgressionSync() { }

    public static void register() {
        NetworkManager.registerS2CPayloadType(Payload.TYPE, Payload.CODEC);
        PlayerEvent.PLAYER_JOIN.register(ProgressionSync::send);
    }

    public static void registerClientReceiver() {
        NetworkManager.registerReceiver(NetworkManager.s2c(), Payload.TYPE, Payload.CODEC,
                (payload, context) -> context.queue(() -> ClientProgression.receive(payload.data())));
    }

    public static void send(ServerPlayer player) {
        if (player.getServer() == null) return;
        send(player, PlayerProgressionManager.get(player.getServer()).get(player.getUUID()));
    }

    public static void send(ServerPlayer player, ProgressionComponentImpl data) {
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        NetworkManager.sendToPlayer(player, new Payload(tag));
    }
}
