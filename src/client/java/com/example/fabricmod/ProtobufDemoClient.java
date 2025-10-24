package com.example.fabricmod;

import com.example.fabricmod.network.MessagePacket;
import com.example.fabricmod.screen.MessageScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProtobufDemoClient implements ClientModInitializer {

    private static final Logger LOGGER = LoggerFactory.getLogger("protobuf-demo-client");
    private static KeyMapping openScreenKey;

    @Override
    public void onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(MessagePacket.TYPE, MessagePacket.CODEC);

        ClientPlayNetworking.registerGlobalReceiver(MessagePacket.TYPE, (payload, context) -> {
            context.client().execute(() -> {
                try {
                    var message = payload.toProto();
                    LOGGER.info("Received message from server: {}", message.getText());
                } catch (Exception e) {
                    LOGGER.error("Failed to process server message", e);
                }
            });
        });

        openScreenKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.protobuf-demo.open_screen",
                GLFW.GLFW_KEY_M,
                "category.protobuf-demo"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openScreenKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new MessageScreen());
                }
            }
        });
    }
}
