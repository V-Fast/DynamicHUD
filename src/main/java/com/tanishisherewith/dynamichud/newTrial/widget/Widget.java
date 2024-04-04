package com.tanishisherewith.dynamichud.newTrial.widget;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.newTrial.config.GlobalConfig;
import com.tanishisherewith.dynamichud.newTrial.utils.UID;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.glfw.GLFW;

import java.util.Set;

public abstract class Widget {

    protected MinecraftClient mc = MinecraftClient.getInstance();

    //This is the UID of the widget used to identify during loading and saving
    public UID uid = UID.generate();
    public boolean isInEditor = false;
    // Whether the widget is enabled and should be displayed.
    public boolean display = true;
    public boolean isDraggable = true;

    //Boolean to check if the widget is being dragged
    public boolean dragging;

    //To enable/disable snapping
    public boolean shiftDown = false;

    // Used for dragging and snapping
    int startX, startY, snapSize = 120;

    // Absolute position of the widget on screen in pixels.
    public int x,y;

    // The x position of the widget as a percentage of the screen width, i.e. the relative x position of the widget for resizing and scaling
    protected float xPercent;
    // The y position of the widget as a percentage of the screen height, i.e. the relative y position of the widget for resizing and scaling
    protected float yPercent;
    public boolean shouldScale = true;
    public String modId = "unknown";

    //Dimensions of the widget
    protected WidgetBox widgetBox;
    public static WidgetData<?> DATA;

    public Widget(WidgetData<?> DATA, String modId){
        Widget.DATA = DATA;
        widgetBox = new WidgetBox(0,0,0,0,1);
        this.modId = modId;
        init();
    }

    /**
     * This method is called at the end of the {@link Widget#Widget(WidgetData)} constructor.
     */
    public void init(){

    }
    /**
     * Returns the x position of the widget.
     *
     * @return The x position of the widget in pixels
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y position of the widget.
     *
     * @return The y position of the widget in pixels
     */
    public int getY() {
        return y;
    }

    public float getWidth(){
        return widgetBox.getWidth();
    }
    public float getHeight(){
        return widgetBox.getHeight();
    }

    public void setPosition(int x, int y){
        this.x = x;
        this.y = y;
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
        if(!shouldDisplay())return;

        if(shouldScale) {
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), getX(), getY(), GlobalConfig.get().scale);
            scale(GlobalConfig.get().scale);
        }
        renderWidget(drawContext);

        if(shouldScale){
            reverseScale(GlobalConfig.get().scale);
            DrawHelper.stopScaling(drawContext.getMatrices());
        }
    }
    /**
     * Renders the widget on the editor screen.
     */
    public void renderInEditor(DrawContext drawContext){
        if(shouldScale) {
            DrawHelper.scaleAndPosition(drawContext.getMatrices(), getX(), getY(), GlobalConfig.get().scale);
            scale(GlobalConfig.get().scale);
        }

        renderWidgetInEditor(drawContext);

        if(shouldScale){
            reverseScale(GlobalConfig.get().scale);
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
    private void renderWidgetInEditor(DrawContext context){
        displayBg(context);

        renderWidget(context);
    }

    public void mouseClicked(double mouseX, double mouseY, int button){
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT){
            toggle();
        }
    }
    public void mouseDragged(double mouseX, double mouseY, int button){
        if(this.isDraggable && button == GLFW.GLFW_MOUSE_BUTTON_LEFT){
            //Start dragging
            startX = (int) (mouseX - x);
            startY = (int) (mouseY - y);
            dragging = true;
            this.x = startX;
            this.y = startY;

            // Divides the screen into several "grid boxes" which the elements snap to. Higher the snapSize, more the grid boxes
            if (this.shiftDown) {
                // Calculate the size of each snap box
                int snapBoxWidth = (int) (widgetBox.getWidth() / this.snapSize);
                int snapBoxHeight = (int) (widgetBox.getHeight() / this.snapSize);

                // Calculate the index of the snap box that the mouse is currently in
                int snapBoxX = (int) (mouseX / snapBoxWidth);
                int snapBoxY = (int) (mouseY / snapBoxHeight);

                // Snap the element to the top-left corner of the snap box
                this.x = snapBoxX * snapBoxWidth;
                this.y = snapBoxY * snapBoxHeight;
            }
        }
    }
    public void mouseReleased(double mouseX, double mouseY, int button) {
        dragging = false;
    }


    public boolean toggle(){
       return this.display = !this.display;
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
        modId = tag.getString("modId");
        uid = new UID(tag.getString("UID"));
        x = tag.getInt("x");
        y = tag.getInt("y");
        display = tag.getBoolean("Display");
        isDraggable = tag.getBoolean("isDraggable");
        shouldScale = tag.getBoolean("shouldScale");
    }

    /**
     * Writes the state of this widget to the given tag.
     *
     * @param tag The tag to write to
     */
    public void writeToTag(NbtCompound tag) {
        tag.putString("name", DATA.name());
        tag.putString("modId",modId);
        tag.putString("UID", uid.getUniqueID());
        tag.putBoolean("isDraggable", isDraggable);
        tag.putBoolean("shouldScale", shouldScale);
        tag.putInt("x", x);
        tag.putInt("y", y);
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

    public String getModId() {
        return modId;
    }
    public void scale(float scale) {
        this.xPercent *= scale;
        this.yPercent *= scale;
    }
    public void reverseScale(float scale) {
        this.xPercent /= scale;
        this.yPercent /= scale;
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
        protected int x;
        protected int y;
        protected boolean display = true;
        protected boolean isDraggable = true;
        protected boolean shouldScale = true;
        protected String modID = "unknown";


        /**
         * X Position of the widget relative to the screen.
         * Should be between 0f - 1f
         *
         * @param x
         * @return Builder
         */
        public T setX(int x) {
            this.x = x;
            return self();
        }

        /**
         * Y Position of the widget relative to the screen.
         * Should be between 0f - 1f
         *
         * @param y
         * @return
         */
        public T setY(int y) {
            this.y = y;
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
        public T setModID(String modID) {
            this.modID = modID;
            return self();
        }

        /**
         * Method to be overridden in subclasses to return "this" correctly
        */
        protected abstract T self();

        /**
         * Method to construct a Widget object
         */
        public abstract S build();
    }
}
