package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    private final List<Option<?>> options = new ArrayList<>(); // The list of options in the context menu
    public int x, y;
    public int width = 0, finalWidth = 0;
    public int height = 0;
    public Color backgroundColor = new Color(107, 112, 126, 124);
    private Color darkerBorderColor = backgroundColor.darker().darker().darker().darker().darker().darker();
    //Todo: Add padding around the rectangle instead of just one side.
    public int padding = 5; // The amount of padding around the rectangle
    public int heightOffset = 4; // Height offset from the widget
    public boolean shouldDisplay = false;
    public static boolean drawBorder = true;
    protected float scale = 0.0f;

    public ContextMenu(int x, int y) {
        this.x = x;
        this.y = y + heightOffset;
    }

    public void addOption(Option<?> option) {
        options.add(option);
    }

    public void render(DrawContext drawContext, int x, int y, int height, int mouseX, int mouseY) {
        this.x = x;
        this.y = y + heightOffset + height;
        if (!shouldDisplay) return;

        update();
        DrawHelper.scaleAndPosition(drawContext.getMatrices(), x, y, scale);

        // Draw the background
        DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(),  this.x - 1, this.y, this.width, this.height, 2,backgroundColor.getRGB());
        if(drawBorder){
            DrawHelper.drawOutlineRoundedBox(drawContext.getMatrices().peek().getPositionMatrix(), this.x - 1,this.y,this.width,this.height,2,0.7f,darkerBorderColor.getRGB());
        }

        int yOffset = this.y + 3;
        this.width = 10;
        for (Option<?> option : options) {
            if (!option.shouldRender()) continue;
            if(isMouseOver(mouseX,mouseY, this.x +1,yOffset-1,this.finalWidth - 2,option.height)){
               DrawHelper.drawRoundedRectangle(drawContext.getMatrices().peek().getPositionMatrix(), this.x,yOffset - 1.24f,this.finalWidth - 2,option.height + 0.48f,2,backgroundColor.darker().darker().getRGB());
            }
            option.render(drawContext, x + 2, yOffset,mouseX,mouseY);
            this.width = Math.max(this.width, option.width);
            yOffset += option.height + 1;
        }
        this.width = this.width + padding;
        this.finalWidth = this.width;
        this.height = (yOffset - this.y);

        DrawHelper.stopScaling(drawContext.getMatrices());
    }
    public boolean isMouseOver(int mouseX, int mouseY, int x, int y, int width, int height){
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public void update() {
        // Update the scale
        float scaleSpeed = 0.1f;
        scale += scaleSpeed;
        if (scale > 1.0f) {
            scale = 1.0f;
        }
    }

    public void close() {
        shouldDisplay = false;
        scale = 0.0f;
    }

    public void open() {
        shouldDisplay = true;
        update();
    }

    public void toggleDisplay() {
        if (shouldDisplay) {
            close();
        } else {
            open();
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            option.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void mouseReleased(double mouseX, double mouseY, int button) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            option.mouseReleased(mouseX, mouseY, button);
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            option.mouseDragged(mouseX, mouseY, button);
        }
    }

    public void keyPressed(int key) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            option.keyPressed(key);
        }
    }

    public void keyReleased(int key) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            option.keyReleased(key);
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public List<Option<?>> getOptions() {
        return options;
    }

    public int getHeight() {
        return height;
    }
}
