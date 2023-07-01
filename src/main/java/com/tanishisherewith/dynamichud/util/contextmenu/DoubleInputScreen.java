package com.tanishisherewith.dynamichud.util.contextmenu;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class DoubleInputScreen extends Screen {
    private final Consumer<Double> consumer;
    private TextFieldWidget textField;
    private int x,y;


    public DoubleInputScreen(Consumer<Double> consumer,int x,int y) {
        super(Text.of("Double Input"));
        this.consumer = consumer;
        this.x=x;
        this.y=y;
    }
    @Override
    protected void init() {
        // Create a text field for the player to input data
        this.textField = new TextFieldWidget(this.textRenderer, x, y, 100, 14, Text.of(""));
        this.addDrawableChild(this.textField);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            // Close the screen and pass the entered data to the consumer when the Enter key is pressed
            try {
                double value = Double.parseDouble(this.textField.getText());
                this.consumer.accept(value);
                this.close();
            } catch (NumberFormatException e) {
                // Handle invalid input
                this.textField.setText("Not double value");
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
    @Override
    public boolean shouldPause() {
        return false;
    }
}
