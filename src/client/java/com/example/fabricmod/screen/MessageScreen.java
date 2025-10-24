package com.example.fabricmod.screen;

import com.example.fabricmod.network.MessagePacket;
import com.example.fabricmod.proto.MessageProto;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MessageScreen extends Screen {

    private EditBox messageField;
    private Button sendButton;

    public MessageScreen() {
        super(Component.literal("Send Message"));
    }

    @Override
    protected void init() {
        super.init();

        this.messageField = new EditBox(
                this.font,
                this.width / 2 - 100,
                this.height / 2 - 10,
                200,
                20,
                Component.literal("Message")
        );
        this.messageField.setMaxLength(256);
        this.addRenderableWidget(this.messageField);

        this.sendButton = Button.builder(
                        Component.literal("Send"),
                        button -> this.sendMessage()
                )
                .bounds(this.width / 2 - 50, this.height / 2 + 20, 100, 20)
                .build();
        this.addRenderableWidget(this.sendButton);
    }

    private void sendMessage() {
        String text = this.messageField.getValue();

        if (text.isEmpty()) {
            return;
        }

        MessageProto.Message message = MessageProto.Message.newBuilder()
                .setText(text)
                .build();
        ClientPlayNetworking.send(MessagePacket.fromProto(message));

        this.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);

        guiGraphics.drawCenteredString(
                this.font,
                this.title,
                this.width / 2,
                this.height / 2 - 40,
                0xFFFFFF
        );
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
