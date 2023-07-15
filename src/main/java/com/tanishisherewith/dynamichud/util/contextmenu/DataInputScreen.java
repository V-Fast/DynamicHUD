package com.tanishisherewith.dynamichud.util.contextmenu;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class DataInputScreen extends Screen {
    private final Consumer<String> consumer;
    private final Screen parentScreen;
    private final ContextMenu.DataInputOption DataInputOption;
    private TextWidgetButtonExt textField;
    private int x, y;


    public DataInputScreen(Consumer<String> consumer, int x, int y, Screen parentScreen, ContextMenu.DataInputOption dataInputOption) {
        super(Text.of("Data Input"));
        this.consumer = consumer;
        this.x = x;
        this.y = y;
        this.parentScreen = parentScreen;
        this.DataInputOption = dataInputOption;
        if (this.x > MinecraftClient.getInstance().getWindow().getScaledWidth())
            this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() - 110;
        if (this.y > MinecraftClient.getInstance().getWindow().getScaledWidth())
            this.y = MinecraftClient.getInstance().getWindow().getScaledHeight() - 14;
        if (this.x < 0) this.x = MinecraftClient.getInstance().getWindow().getScaledWidth() + 110;
        if (this.y < 0) this.y = MinecraftClient.getInstance().getWindow().getScaledHeight() + 14;
    }

    @Override
    protected void init() {
        // Create a text field for the player to input data
        this.textField = new TextWidgetButtonExt(this.textRenderer, x, y, 100, 14, Text.of(""));
        this.addDrawableChild(this.textField);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            // Close the screen and pass the entered data to the consumer when the Enter key is pressed
            String text = this.textField.getText();
            this.consumer.accept(text);
            MinecraftClient.getInstance().setScreen(parentScreen);
            DataInputOption.getLabelSetter().accept(text);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}

