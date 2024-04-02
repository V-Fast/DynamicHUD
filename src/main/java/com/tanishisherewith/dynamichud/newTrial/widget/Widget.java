package com.tanishisherewith.dynamichud.newTrial.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.newTrial.utils.UID;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

import java.util.Set;

public abstract class Widget {

    protected MinecraftClient mc = MinecraftClient.getInstance();

    //This is the UID of the widget used to identify during loading and saving
    public UID uid = UID.generate();
    public boolean display = true; // Whether the widget is enabled
    public boolean isDraggable = true;
    protected float xPercent; // The x position of the widget as a percentage of the screen width
    protected float yPercent; // The y position of the widget as a percentage of the screen height
    public boolean shouldScale = false;
    protected WidgetBox widgetBox;
    public static WidgetData<?> DATA;

    public Widget(WidgetData<?> DATA){
        Widget.DATA = DATA;
        widgetBox = new WidgetBox(0,0,0,0,0);
        init();
    }

    public void init(){

    }
    /**
     * Returns the x position of the widget.
     *
     * @return The x position of the widget in pixels
     */
    public float getX() {
        return DynamicHUD.MC.getWindow().getScaledWidth() * xPercent;
    }

    /**
     * Returns the y position of the widget.
     *
     * @return The y position of the widget in pixels
     */
    public float getY() {
        return DynamicHUD.MC.getWindow().getScaledHeight() * yPercent;
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }
    public boolean isOverlapping(Set<Widget> other) {
        for (Widget widgetBox : other) {
            if ((this.getX() < widgetBox.getX() + widgetBox.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > widgetBox.getX() &&
                    this.getY() < widgetBox.getY() + widgetBox.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > widgetBox.getY())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverlapping(Widget other) {
        return this.getX() < other.getX() + other.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > other.getY();
    }

    /**
     * Renders the widget on the screen.
     */
    public void render(DrawContext drawContext){
        if(shouldScale) {
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), getX(), getY(),0, 1.0f);
        }

        renderWidget(drawContext);

        if(shouldScale){
            DrawHelper.stopScaling(drawContext.getMatrices());
        }
    }
    /**
     * Renders the widget on the editor screen.
     */
    public void renderInEditor(DrawContext drawContext){
        if(shouldScale) {
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), getX(), getY(),0, 1.0f);
        }

        renderWidgetInEditor(drawContext);

        if(shouldScale){
            DrawHelper.stopScaling(drawContext.getMatrices());
        }
    }


    /**
     * Renders the widget on the screen
     *
     * @param context
     */
    public abstract void renderWidget(DrawContext context);


    /**
     * Renders the widget in the editor screen with a background.
     * Override this method without super call to remove the background.
     * Could also be used to display placeholder values.
     *
     * @param context
     */
    public void renderWidgetInEditor(DrawContext context){
        displayBg(context);

        renderWidget(context);
    }

    /**
     * Displays a faint grayish background if enabled or faint reddish background if disabled.
     * Drawn with 2 pixel offset to all sides
     *
     * @param context
     */
    protected void displayBg(DrawContext context){
        int backgroundColor = this.shouldDisplay() ? ColorHelper.getColor(0, 0, 0, 128) : ColorHelper.getColor(255, 0, 0, 128);
        WidgetBox box = this.getWidgetBox();
        DrawHelper.fill(context, (int) (box.x1 - 2), (int) (box.y1 - 2), (int) (box.x2 + 2), (int) (box.y2 + 2), backgroundColor);
    }


    public void readFromTag(NbtCompound tag) {
        uid = new UID(tag.getString("UID"));
        xPercent = tag.getFloat("xPercent");
        yPercent = tag.getFloat("yPercent");
        display = tag.getBoolean("Display");
        isDraggable = tag.getBoolean("isDraggable");
    }

    /**
     * Writes the state of this widget to the given tag.
     *
     * @param tag The tag to write to
     */
    public void writeToTag(NbtCompound tag) {
        tag.putString("UID", uid.getUniqueID());
        tag.putBoolean("isDraggable", isDraggable);
        tag.putFloat("xPercent", xPercent);
        tag.putFloat("yPercent", yPercent);
        tag.putBoolean("Display", display);
    }

    public boolean shouldDisplay() {
        return display;
    }

    public WidgetBox getWidgetBox() {
        return widgetBox;
    }

    public void setxPercent(float xPercent) {
        this.xPercent = xPercent;
    }

    public void setyPercent(float yPercent) {
        this.yPercent = yPercent;
    }

    public void setUid(UID uid) {
        this.uid = uid;
    }

    public void setShouldScale(boolean shouldScale) {
        this.shouldScale = shouldScale;
    }

    @Override
    public String toString() {
        return "Widget{" +
                "uniqueId='" + uid.getUniqueID() + '\'' +
                ", xPercent=" + xPercent +
                ", yPercent=" + yPercent +
                ", display=" + display +
                '}';
    }

    public abstract static class WidgetBuilder<T,S> {
        protected float xPercent;
        protected float yPercent;
        protected boolean display = true;
        protected boolean isDraggable = true;
        protected boolean shouldScale = true;

        /**
         * X Position of the widget relative to the screen.
         * Should be between 0f - 1f
         *
         * @param xPercent
         * @return
         */
        public T setX(float xPercent) {
            this.xPercent = xPercent;
            return self();
        }

        /**
         * Y Position of the widget relative to the screen.
         * Should be between 0f - 1f
         *
         * @param yPercent
         * @return
         */
        public T setY(float yPercent) {
            this.yPercent = yPercent;
            return self();
        }

        public T setDisplay(boolean display) {
            this.display = display;
            return self();
        }

        public T setDraggable(boolean isDraggable) {
            this.isDraggable = isDraggable;
            return self();
        }

        public T shouldScale(boolean shouldScale) {
            this.shouldScale = shouldScale;
            return self();
        }

        // Method to be overridden in subclasses to return "this" correctly
        protected abstract T self();

        // Method to construct a Widget object
        public abstract S build();
    }
}
