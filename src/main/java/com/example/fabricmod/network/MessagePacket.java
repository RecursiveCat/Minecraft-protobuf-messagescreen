package com.example.fabricmod.network;

import com.example.fabricmod.proto.MessageProto;
import com.google.protobuf.InvalidProtocolBufferException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MessagePacket(byte[] data) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessagePacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("protobuf-demo", "message"));

    public static final StreamCodec<FriendlyByteBuf, MessagePacket> CODEC =
            StreamCodec.of(MessagePacket::write, MessagePacket::read);

    public static MessagePacket fromProto(MessageProto.Message message) {
        return new MessagePacket(message.toByteArray());
    }

    public MessageProto.Message toProto() throws InvalidProtocolBufferException {
        return MessageProto.Message.parseFrom(data);
    }

    private static void write(FriendlyByteBuf buf, MessagePacket packet) {
        buf.writeByteArray(packet.data);
    }

    private static MessagePacket read(FriendlyByteBuf buf) {
        return new MessagePacket(buf.readByteArray());
    }

    @Override
    public @NotNull CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
