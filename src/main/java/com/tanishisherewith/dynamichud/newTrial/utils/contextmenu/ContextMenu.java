package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ContextMenu {
    public int x,y;
    protected float scale = 0.0f;
    private final List<Option<?>> options = new ArrayList<>(); // The list of options in the context menu
    private Widget selectedWidget; // The widget that this context menu is associated with
    public int width = 0;
    public int height = 0;
    public int backgroundColor = new Color(107, 112, 126, 124).getRGB();// Semi-transparent light grey color
    public int padding = 5; // The amount of padding around the rectangle
    public int heightOffset = 4; // Height offset from the widget
    public boolean shouldDisplay = false;

    public ContextMenu(int x,int y){
        this.x = x;
        this.y = y + heightOffset;
        this.selectedWidget = selectedWidget;
    }

    public void addOption(Option<?> option){
        options.add(option);
    }

    public void render(DrawContext drawContext, int x, int y, int height){
        this.x = x;
        this.y = y + heightOffset + height;
        if(!shouldDisplay) return;

        update();
        DrawHelper.scaleAndPosition(drawContext.getMatrices(),x,y,scale);

        int x1 = this.x - 1;
        int y1 = this.y;
        int x2 = this.x + width;
        int y2 = this.y + this.height;

        // Draw the background
        DrawHelper.drawCutRectangle(drawContext, x1, y1, x2, y2, 0, backgroundColor, 1);

        int yOffset = y1 + 3;
        this.width = 10;
        for(Option<?> option: options){
            option.render(drawContext,x + 2,yOffset);
            this.width = Math.max(this.width, option.width + padding);
            yOffset += option.height + 1;
        }
        this.width = this.width + 3;
        this.height = (yOffset - y1);

        DrawHelper.stopScaling(drawContext.getMatrices());
    }
    public void update() {
        // Update the scale
        float scaleSpeed = 0.1f;
        scale += scaleSpeed;
        if (scale > 1.0f) {
            scale = 1.0f;
        }
    }

    public void close(){
        shouldDisplay = false;
        scale = 0.0f;
    }
    public void open(){
        shouldDisplay = true;
        update();
    }
    public void toggleDisplay(){
        if(shouldDisplay){
            close();
        }else{
            open();
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button){
        if(!shouldDisplay) return;
        for(Option<?> option: options){
            option.mouseClicked(mouseX, mouseY, button);
        }
    }
    public void mouseReleased(double mouseX, double mouseY, int button){
        if(!shouldDisplay) return;
        for(Option<?> option: options){
            option.mouseReleased(mouseX, mouseY, button);
        }
    }
    public void mouseDragged(double mouseX, double mouseY, int button){
        if(!shouldDisplay) return;
        for(Option<?> option: options){
            option.mouseDragged(mouseX, mouseY, button);
        }
    }
    public void keyPressed(int key){
        if(!shouldDisplay) return;
        for(Option<?> option: options){
            option.keyPressed(key);
        }
    }
    public void keyReleased(int key){
        if(!shouldDisplay) return;
        for(Option<?> option: options){
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