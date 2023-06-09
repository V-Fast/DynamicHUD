package com.tanishisherewith.dynamichud.util.contextmenu;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class DoubleInputScreen extends Screen {
    private final Consumer<Double> consumer;
    private TextWidgetButtonExt textField;
    private int x,y;
    private final Screen parentScreen;
    private final ContextMenu.DoubleInputOption doubleInputOption;

    public DoubleInputScreen(Consumer<Double> consumer, int x, int y, Screen parentScreen, ContextMenu.DoubleInputOption doubleInputOption) {
        super(Text.of("Double Input"));
        this.consumer = consumer;
        this.x=x;
        this.y=y;
        this.parentScreen=parentScreen;
        this.doubleInputOption=doubleInputOption;
        if(this.x>MinecraftClient.getInstance().getWindow().getScaledWidth())this.x=MinecraftClient.getInstance().getWindow().getScaledWidth()-110;
        if(this.y>MinecraftClient.getInstance().getWindow().getScaledWidth())this.y=MinecraftClient.getInstance().getWindow().getScaledHeight()-14;
        if(this.x<0)this.x=MinecraftClient.getInstance().getWindow().getScaledWidth()+110;
        if(this.y<0)this.y=MinecraftClient.getInstance().getWindow().getScaledHeight()+14;
    }
    @Override
    protected void init() {
        // Create a text field for the player to input data
        this.textField = new TextWidgetButtonExt(this.textRenderer, x, y, 100, 14, Text.of(""));
        this.addDrawableChild(this.textField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            // Close the screen and pass the entered data to the consumer when the Enter key is pressed
            try {
                double value = Double.parseDouble(this.textField.getText());
                this.consumer.accept(value);
                MinecraftClient.getInstance().setScreen(parentScreen);
                doubleInputOption.getLabelSetter().accept(value);
            } catch (NumberFormatException e) {
                // Handle invalid input
                this.textField.setText("");
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean shouldPause() {
        return false;
    }
}
