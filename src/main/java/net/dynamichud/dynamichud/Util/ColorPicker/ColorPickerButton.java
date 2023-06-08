package net.dynamichud.dynamichud.Util.ColorPicker;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;

public class ColorPickerButton {
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean isPicking = false;

    public ColorPickerButton(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void render(MatrixStack matrices) {
        // Draw the button
        DrawableHelper.fill(matrices, x, y, x + width, y + height, 0xFFAAAAAA);
        DrawableHelper.drawCenteredTextWithShadow(matrices, MinecraftClient.getInstance().textRenderer, "Pick", x + width / 2, y + (height - 8) / 2, 0xFFFFFFFF);
    }

    public boolean onClick(double mouseX,double mouseY,int button){
        if(button==0){
            if(mouseX>=x&&mouseX<=x+width&&mouseY>=y&&mouseY<=y+height){
                isPicking=true;
                return true;
            }
        }
        return false;
    }

    public boolean isPicking() {
        return isPicking;
    }

    public void setPicking(boolean picking) {
        isPicking = picking;
    }
}