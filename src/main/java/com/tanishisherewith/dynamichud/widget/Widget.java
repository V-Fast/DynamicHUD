package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.internal.UID;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.widgets.GraphWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

/**
 * This is the base Widget class that handles the rendering, scaling, dragging, anchoring and positioning of the Widget.
 * <p>
 * Default fields are made to help with all the basic functions of a widget.
 * Main fields include: {@link #uid},{@link #isVisible},{@link #isDraggable},{@link #canScale},{@link #isInEditor},{@link #widgetBox},{@link #DATA}
 */
public abstract class Widget implements Input {
    public static Minecraft mc = Minecraft.getInstance();
    public WidgetData<?> DATA;
    /**
     * This is the UID of the widget used to identify during loading and saving.
     * <p>
     * It's different from modID because this is unique to each widget.
     *
     * @see #modId
     */
    public UID uid = UID.generate();
    // Whether the widget is enabled and should be displayed.
    protected boolean isVisible = true;
    protected boolean isDraggable = true;
    //Boolean to check if the widget is being dragged
    public boolean dragging;
    private boolean wasDragged = false;

    //To enable/disable snapping
    public boolean isShiftDown = false;
    /**
     * An identifier for widgets to group them under one ID.
     * <p>
     * Doesn't necessarily have to be the mod ID of mod, but it's preferred to use mod ID if you are only grouping widgets under one ID.
     * Can be any string if wanted.
     *
     * @see #uid
     */
    public String modId = "unknown";

    public Component tooltipText;

    // Boolean to know if the widget is currently being displayed in an instance of AbstractMoveableScreen
    protected boolean isInEditor = false;

    // Absolute position of the widget on screen in pixels.
    protected int x, y;

    protected boolean canScale = true;

    protected Anchor anchor;         // The chosen anchor point

    //Dimensions of the widget
    protected final WidgetBox widgetBox;

    private int startX, startY;
    protected int offsetX, offsetY;  // Offset from the anchor point

    public Widget(WidgetData<?> DATA, String modId) {
        this(DATA, modId, Anchor._default());
    }

    public Widget(WidgetData<?> DATA, String modId, Anchor anchor) {
        this.DATA = DATA;
        this.widgetBox = new WidgetBox(0, 0, 0, 0);
        this.modId = modId;
        this.anchor = anchor;
        this.tooltipText = Component.literal(DATA.description());
        init();
    }

    /**
     * This method is called at the end of the {@link Widget#Widget(WidgetData, String)} constructor.
     */
    public void init() {
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

    public float getScale() {
        return canScale ? widgetBox.getScale() * DynamicHUD.getGlobalScale() : 1.0f;
    }

    public float getWidth() { return widgetBox.getWidth(); }
    public float getHeight() { return widgetBox.getHeight(); }

    private void calculateOffset(int initialX, int initialY, int screenWidth, int screenHeight) {
        int anchorX = getAnchorX(screenWidth);
        int anchorY = getAnchorY(screenHeight);
        this.offsetX = initialX - anchorX;
        this.offsetY = initialY - anchorY;
    }

    private int getAnchorX(int screenWidth) {
        return switch (anchor) {
            case TOP_RIGHT, BOTTOM_RIGHT -> screenWidth;
            case CENTER -> screenWidth / 2;
            default -> 0; // TOP_LEFT and BOTTOM_LEFT
        };
    }

    private int getAnchorY(int screenHeight) {
        return switch (anchor) {
            case BOTTOM_LEFT, BOTTOM_RIGHT -> screenHeight;
            case CENTER -> screenHeight / 2;
            default -> 0; // TOP_LEFT and TOP_RIGHT
        };
    }

    // Update position based on anchor and offset
    void updatePosition(int screenWidth, int screenHeight) {
        if (offsetX == 0 || offsetY == 0) {
            calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }

        int anchorX = getAnchorX(screenWidth);
        int anchorY = getAnchorY(screenHeight);
        this.x = anchorX + offsetX;
        this.y = anchorY + offsetY;
        clampPosition();
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if(mc.getWindow() != null) {
            calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            updatePosition(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }

    public boolean isOverlapping(Widget other) {
        return this.getX() < other.getX() + other.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > other.getY();
    }

    /**
     * Renders the widget on the screen.
     */
    public final void render(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isVisible()) return;


        if (canScale) {
            DrawHelper.scaleAndPosition(graphics.pose(), getX(), getY(), getScale());
        }
        renderWidget(graphics, mouseX, mouseY);

        if (canScale) {
            DrawHelper.stopScaling(graphics.pose());
        }
        clampPosition();
    }

    /**
     * Renders the widget on the editor screen.
     */
    public final void renderInEditor(GuiGraphics graphics, int mouseX, int mouseY) {
        if (!isInEditor) return;

        drawWidgetBackground(graphics,mouseX,mouseY);

        if (canScale) {
            DrawHelper.scaleAndPosition(graphics.pose(), getX(), getY(), getScale());
        }
        renderWidgetInEditor(graphics, mouseX, mouseY);

        if (canScale) {
            DrawHelper.stopScaling(graphics.pose());
        }
        clampPosition();
    }

    /**
     * Renders the widget on the screen
     * <p>
     * The mouse position values are only passed when in a {@link com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen} screen.
     * </p>
     *
     * @param graphics GuiGraphics Object
     * @param mouseX  X position of mouse.
     * @param mouseY  Y position of mouse
     */
    public abstract void renderWidget(GuiGraphics graphics, int mouseX, int mouseY);

    /**
     * Renders the widget in the editor screen with a background.
     * Could also be used to display placeholder values.
     */
    private void renderWidgetInEditor(GuiGraphics graphics, int mouseX, int mouseY) {
        //drawWidgetBackground(graphics);

        renderWidget(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (widgetBox.isMouseOver(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            wasDragged = false;
            if (isDraggable) {
                startX = (int) (mouseX - x);
                startY = (int) (mouseY - y);
                dragging = true;
            } else {
                toggle(); // Static widgets toggle immediately
            }
            return true;
        }
        return false;
    }

    public void clampPosition() {
        this.x = (int) Mth.clamp(this.x, 0, mc.getWindow().getGuiScaledWidth() - getWidth());
        this.y = (int) Mth.clamp(this.y, 0, mc.getWindow().getGuiScaledHeight() - getHeight());
    }

    /** Input related methods. Override with **super call** to add your own input-based code like contextMenu **/

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, int snapSize) {
        if (!isDraggable) return false;

        if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            wasDragged = true;

            int newX = (int) (mouseX - startX);
            int newY = (int) (mouseY - startY);

            // Divides the screen into several "grid boxes" which the elements snap to.
            // Higher the snapSize, more the grid boxes
            if (this.isShiftDown) {
                // Calculate the size of each snap box
                int snapBoxWidth = mc.getWindow().getGuiScaledWidth() / snapSize;
                int snapBoxHeight = mc.getWindow().getGuiScaledHeight() / snapSize;

                // Calculate the index of the snap box that the new position would be in and
                // snap the new position to the top-left corner of the snap box
                newX = (newX / snapBoxWidth) * snapBoxWidth;
                newY = (newY / snapBoxHeight) * snapBoxHeight;
            }

            this.x = (int) Mth.clamp(newX, 0, mc.getWindow().getGuiScaledWidth() - getWidth());
            this.y = (int) Mth.clamp(newY, 0, mc.getWindow().getGuiScaledHeight() - getHeight());

            calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());  // Set initial offset

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging && widgetBox.isMouseOver(mouseX,mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            if (!wasDragged) {
                toggle();
                dragging = false;
                wasDragged = false;
                return true;
            }
        }
        dragging = false;
        wasDragged = false;
        return false;
    }

    /**
     * MouseScrolled event
     *
     * @param vAmount vertical amount of scrolling
     * @param hAmount horizontal amount of scrolling
     */
    @Override
    public void mouseScrolled(double mouseX, double mouseY, double vAmount, double hAmount) {
        if (canScale && widgetBox.isMouseOver(mouseX,mouseY) && GLFW.glfwGetKey(mc.getWindow().handle(),GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS) {
            widgetBox.setScale(widgetBox.getScale() + (float) vAmount * 0.05f);

            clampPosition();
            calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
    }

    @Override
    public void charTyped(char c, int modifiers) {
    }

    public boolean toggle() {
        return this.isVisible = !this.isVisible;
    }

    public void onClose() {
        this.isShiftDown = false;
    }

    /**
     * Displays a faint grayish background if enabled or faint reddish background if disabled.
     */
    protected void drawWidgetBackground(GuiGraphics graphics, int mouseX, int mouseY) {
        boolean isHovered = widgetBox.isMouseOver(mouseX, mouseY);
        Color backgroundColor = this.isVisible() ? GlobalConfig.get().getHudActiveColor() : GlobalConfig.get().getHudInactiveColor();
        WidgetBox box = this.getWidgetBox();


        DrawHelper.drawRectangle(graphics,
                box.x,
                box.y,
                box.getWidth(),
                box.getHeight(),
                isHovered ? backgroundColor.darker().darker().getRGB() : backgroundColor.getRGB());
    }

    /**
     * Set the tooltip Component of the widget
     */
    protected void setTooltipText(Component Component) {
        this.tooltipText = Component;
    }

    public void setWidgetScale(float widgetScale) {
        widgetBox.setScale(widgetScale);
    }

    public void readFromTag(CompoundTag tag) {
        modId = tag.getString("modId").orElse("unknown");
        uid = tag.contains("UID") ? new UID(tag.getString("UID").get()) : UID.generate();
        //     x = tag.getInt("x");
        //     y = tag.getInt("y");
        anchor = Anchor.valueOf(tag.getString("anchor").orElse("TOP_LEFT"));
        offsetX = tag.getIntOr("offsetX", 0);
        offsetY = tag.getIntOr("offsetY",0);
        isVisible = tag.getBoolean("isVisible").orElse(true);
        isDraggable = tag.getBoolean("isDraggable").orElse(true);
        canScale = tag.getBoolean("canScale").orElse(true);
        widgetBox.setScale(tag.getFloat("widgetScale").orElse(1.0f));
    }

    /**
     * Writes the state of this widget to the given tag.
     *
     * @param tag The tag to write to
     */
    public void writeToTag(CompoundTag tag) {
        tag.putString("name", DATA.name());
        tag.putString("modId", modId);
        tag.putString("UID", uid.getUniqueID());
        tag.putBoolean("isDraggable", isDraggable);
        tag.putBoolean("canScale", canScale);
        tag.putFloat("widgetScale", widgetBox.getScale());
        //    tag.putInt("x", x);
        //     tag.putInt("y", y);
        tag.putString("anchor", anchor.name());
        tag.putInt("offsetX", offsetX);
        tag.putInt("offsetY", offsetY);
        tag.putBoolean("isVisible", isVisible);
    }

    public boolean isVisible() {
        return isVisible;
    }

    public WidgetBox getWidgetBox() {
        return widgetBox;
    }

    public void setCanScale(boolean canScale) {
        this.canScale = canScale;
    }

    public String getModId() {
        return modId;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" +
                "uniqueId='" + uid.getUniqueID() + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", isVisible=" + isVisible +
                ", isDraggable=" + isDraggable +
                ", shiftDown=" + isShiftDown +
                ", canScale=" + canScale +
                '}';
    }

    public enum Anchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT,
        CENTER;

        public static Anchor _default(){
            return TOP_LEFT;
        }
    }

    public abstract static class WidgetBuilder<T, S> {
        protected int x;
        protected int y;
        protected boolean isVisible = true;
        protected boolean isDraggable = true;
        protected boolean shouldScale = true;
        protected String modID = "unknown";
        protected Anchor anchor = Anchor._default();

        /**
         * X Position of the widget of the scaled screen.
         */
        public T setX(int x) {
            this.x = x;
            return self();
        }

        /**
         * Y Position of the widget of the scaled screen.
         */
        public T setY(int y) {
            this.y = y;
            return self();
        }

        public T setIsVisible(boolean isVisible) {
            this.isVisible = isVisible;
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

        public T anchor(Anchor anchor) {
            this.anchor = anchor;
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
