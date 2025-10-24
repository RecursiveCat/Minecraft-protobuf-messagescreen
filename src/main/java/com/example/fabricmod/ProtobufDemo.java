package com.example.fabricmod;

import com.example.fabricmod.database.DatabaseManager;
import com.example.fabricmod.network.MessagePacket;
import com.example.fabricmod.proto.MessageProto;
import com.google.protobuf.InvalidProtocolBufferException;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufDemo implements ModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("protobuf-demo");

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Protobuf Demo Mod");

        DatabaseManager.initialize(
                "jdbc:postgresql://localhost:5436/minecraft",
                "minecraft_user",
                "minecraft_pass"
        );

        PayloadTypeRegistry.playC2S().register(MessagePacket.TYPE, MessagePacket.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(MessagePacket.TYPE, (payload, context) -> {
            try {
                MessageProto.Message message = payload.toProto();

                context.server().execute(() -> {
                    DatabaseManager.saveMessage(
                            context.player().getUUID(),
                            message.getText()
                    );

                    LOGGER.info("Received message from {}: {}",
                            context.player().getName().getString(),
                            message.getText());

                    MessageProto.Message response = MessageProto.Message.newBuilder()
                            .setText("Message received!")
                            .build();
                    ServerPlayNetworking.send(context.player(), MessagePacket.fromProto(response));
                });
            } catch (InvalidProtocolBufferException e) {
                LOGGER.error("Failed to decode protobuf message", e);
            }
        });

        LOGGER.info("Protobuf Demo Mod initialized");
    }
}
