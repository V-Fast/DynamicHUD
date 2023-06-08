package net.dynamichud.dynamichud.Util.ColorPicker;

import net.dynamichud.dynamichud.Widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;

import java.awt.*;
import java.util.function.Consumer;

public class ColorGradientPicker {
        private final MinecraftClient client; // The Minecraft client instance
        private final Consumer<Integer> onColorSelected; // The callback to call when a color is selected
        private GradientSlider gradientSlider;
        private GradientBox gradientBox;

        public ColorGradientPicker(MinecraftClient client, int x, int y, int initialColor, Consumer<Integer> onColorSelected, int BoxSize, int Colors, Widget selectedWidget) {
            this.client = client;
            this.onColorSelected = onColorSelected;
        float[] hsv = new float[3];
        Color.RGBtoHSB((initialColor >> 16) & 0xFF, (initialColor >> 8) & 0xFF, initialColor & 0xFF, hsv);

        if ((initialColor >> 24) != 0xFF) {
            // The initial color has an alpha component
            hsv[0] = 0.0f; // Set hue to default value
            hsv[1] = 1.0f; // Set saturation to default value
            hsv[2] = 1.0f; // Set value to default value
        }

        this.gradientSlider = new GradientSlider(x, y, Colors, 10,selectedWidget);
        this.gradientSlider.setHue(hsv[0]);

        this.gradientBox = new GradientBox(x, y+20, BoxSize,selectedWidget);
        this.gradientBox.setHue(hsv[0]);
        this.gradientBox.setSaturation(hsv[1]);
        this.gradientBox.setValue(hsv[2]);
       }

        public void tick() {
            gradientSlider.tick();
            gradientBox.tick();
        }

        public void render(MatrixStack matrices) {
            tick();
            gradientSlider.render(matrices);
            gradientBox.render(matrices);
        }

    public boolean mouseClicked(double mouseX,double mouseY,int button){
        if(gradientSlider.isMouseOver(mouseX,mouseY)){
            gradientSlider.onClick(mouseX,mouseY,button);
            gradientBox.setHue(gradientSlider.getHue());
        } else if(gradientBox.isMouseOver(mouseX,mouseY)){
            gradientBox.onClick(mouseX,mouseY,button);
        }
        onColorSelected.accept(gradientBox.getColor());
        return true;
    }

        public void mouseReleased(double mouseX,double mouseY,int button){
            gradientSlider.onRelease(mouseX,mouseY,button);
            gradientBox.onRelease(mouseX,mouseY,button);
        }

        public void mouseDragged(double mouseX,double mouseY,int button){
            gradientSlider.onDrag(mouseX,mouseY,button);
            gradientBox.setHue(gradientSlider.getHue());
            gradientBox.onDrag(mouseX,mouseY,button);
            onColorSelected.accept(gradientBox.getColor());
        }

}
