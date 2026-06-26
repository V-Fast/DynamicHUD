package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import net.minecraft.util.Mth;

public class WidgetBox {
    public float x, y;
    private float width, rawWidth;
    private float height, rawHeight;
    protected float scale;

    public WidgetBox(float x, float y, float width, float height, float scale) {
        this.x = x;
        this.y = y;
        this.width = width * scale;
        this.height = height * scale;
        this.rawWidth = width;
        this.rawHeight = height;
        this.scale = scale;
    }

    public WidgetBox(float x, float y, float width, float height) {
        this(x,y,width,height,1.0f);
    }

    /**
     * Checks if the mouse is over the box, accounting for its scale factor.
     */
    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + this.width && mouseY >= y && mouseY <= y + this.height;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public boolean intersects(WidgetBox other, float myScale, float otherScale) {
        float myWidth = this.width * myScale;
        float myHeight = this.height * myScale;
        float oWidth = other.width * otherScale;
        float oHeight = other.height * otherScale;

        return this.x < other.x + oWidth && this.x + myWidth > other.x &&
                this.y < other.y + oHeight && this.y + myHeight > other.y;
    }

    public void setScale(float scale) {
        this.scale = Mth.clamp(scale, 0.2f, 10.0f);
    }

    public float getScale() {
        return scale * DynamicHUD.getGlobalScale();
    }

    public float getRawWidth() {
        return rawWidth;
    }

    public float getRawHeight() {
        return rawHeight;
    }

    private void setDimensions(float x, float y, float width, float height, boolean shouldScale, float scale) {
        this.x = x;
        this.y = y;
        this.height = height * (shouldScale ? scale : 1.0f);
        this.width = width * (shouldScale ? scale : 1.0f);
        this.rawWidth = width;
        this.rawHeight = height;
    }

    private void setSize(float width, float height, boolean shouldScale, float scale) {
        if (width >= 0) {
            this.width = (float) Math.ceil(width * (shouldScale ? scale : 1.0f));
            this.rawWidth = width;
        }
        if (height >= 0) {
            this.height = (float) Math.ceil(height * (shouldScale ? scale : 1.0f));
            this.rawHeight = height;
        }
    }

    public void setDimensions(float x, float y, float width, float height, boolean canScale) {
        this.setDimensions(x,y,width,height,canScale,getScale());
    }

    public void setSize(float width, float height, boolean canScale) {
        this.setSize(width,height,canScale,getScale());
    }
}
