package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options.coloroption;

import com.tanishisherewith.dynamichud.newTrial.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.newTrial.helpers.DrawHelper;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.MathHelper;

import java.awt.*;

public class AlphaSlider {
    private final int width;
    private final int height;
    private int x;
    private int y;
    private boolean isDragging = false;
    private Color color;
    private int alphaHandleY = 0;
    private float alpha;
    public AlphaSlider(int x,int y,int width, int height, Color color) {
        this.width = width;
        this.height = height;
        this.color = color;
        this.x = x;
        this.y = y;
        alpha = color.getAlpha() / 255f;
    }

    public void setColor(Color color) {
        this.color = color;
    }
    public void render(DrawContext drawContext, int x, int y) {
        this.x = x;
        this.y = y;

        DrawHelper.drawOutlinedBox(drawContext,x - 2,y - 2,x + width + 2,y + height + 2,Color.WHITE.getRGB());
        DrawHelper.drawGradient(drawContext.getMatrices().peek().getPositionMatrix(),x,y,width,height,color.getRGB(), ColorHelper.changeAlpha(color,0).getRGB(), DrawHelper.Direction.TOP_BOTTOM);
        drawContext.fill(x-2, y + alphaHandleY-1,x+width+2, y +alphaHandleY+1,Color.WHITE.getRGB());
    }

    public Color getColor() {
        return ColorHelper.changeAlpha(color, (int) (alpha*255f));
    }

    public void onClick(double mouseX, double mouseY, int button) {
        if (button == 0 && isMouseOver(mouseX, mouseY)) {
            alphaHandleY = (int) mouseY-y;
            alpha = 1.0f - (alphaHandleY / (float) height);
            if(alpha < 0.0f){
                alpha = 0.0f;
            }else if(alpha > 1.0f){
                alpha = 1.0f;
            }
            this.isDragging = true;
        }
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void onRelease(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
        }
    }

    public void onDrag(double mouseX, double mouseY, int button) {
        if (isDragging && isMouseOver(mouseX, mouseY)) {
            alphaHandleY = (int) mouseY-y;
            alpha = 1.0f - (alphaHandleY / (float) height);
            if(alpha < 0.0f){
                alpha = 0.0f;
            }else if(alpha > 1.0f){
                alpha = 1.0f;
            }
        }
    }
}
