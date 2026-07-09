package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.Input;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.UUID;

/**
 * This is the base Widget class that handles the rendering, scaling, dragging, anchoring and positioning of the Widget.
 * <p>
 * Default fields are made to help with all the basic functions of a widget.
 * Main fields include: {@link #uid},{@link #isVisible},{@link #isLocked},{@link #canScale},{@link #isInEditor},{@link #widgetBox},{@link #DATA}
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
    public UUID uid = UUID.randomUUID();
    // Whether the widget is enabled and should be displayed.
    protected boolean isVisible = true;
    protected boolean isLocked = false;
    protected float minScale = 0.3f;
    protected float maxScale = 3.0f;
    public boolean dragging;
    private boolean wasDragged = false;
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

    protected WidgetGroup group;

    public WidgetGroup getGroup() {
        return group;
    }

    public void setGroup(WidgetGroup group) {
        this.group = group;
    }

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

    private Anchor determineBestAnchor(int x, int y, int width, int height, int screenWidth, int screenHeight) {
        if (screenWidth <= 0 || screenHeight <= 0) return Anchor.TOP_LEFT;

        int centerX = x + width / 2;
        int centerY = y + height / 2;

        int xZone = 0; // 0 = Left, 1 = Center, 2 = Right
        if (centerX < screenWidth / 3) {
            xZone = 0;
        } else if (centerX < 2 * screenWidth / 3) {
            xZone = 1;
        } else {
            xZone = 2;
        }

        int yZone = 0; // 0 = Top, 1 = Center, 2 = Bottom
        if (centerY < screenHeight / 3) {
            yZone = 0;
        } else if (centerY < 2 * screenHeight / 3) {
            yZone = 1;
        } else {
            yZone = 2;
        }

        if (xZone == 0) {
            if (yZone == 0) return Anchor.TOP_LEFT;
            if (yZone == 1) return Anchor.CENTER_LEFT;
            return Anchor.BOTTOM_LEFT;
        } else if (xZone == 1) {
            if (yZone == 0) return Anchor.TOP_CENTER;
            if (yZone == 1) return Anchor.CENTER;
            return Anchor.BOTTOM_CENTER;
        } else {
            if (yZone == 0) return Anchor.TOP_RIGHT;
            if (yZone == 1) return Anchor.CENTER_RIGHT;
            return Anchor.BOTTOM_RIGHT;
        }
    }

    private void calculateOffset(int initialX, int initialY, int screenWidth, int screenHeight) {
        if (screenWidth > 0 && screenHeight > 0) {
            this.anchor = determineBestAnchor(initialX, initialY, (int) getWidth(), (int) getHeight(), screenWidth, screenHeight);
        }
        int anchorX = anchor.getBaseX(screenWidth);
        int anchorY = anchor.getBaseY(screenHeight);
        this.offsetX = initialX - anchorX;
        this.offsetY = initialY - anchorY;
    }

    // Update position based on anchor and offset
    void updatePosition(int screenWidth, int screenHeight) {
        int anchorX = anchor.getBaseX(screenWidth);
        int anchorY = anchor.getBaseY(screenHeight);
        this.x = anchorX + offsetX;
        this.y = anchorY + offsetY;
        clampPosition();
        calculateOffset(x, y, screenWidth, screenHeight);
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
        if(mc.getWindow() != null) {
            calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
            updatePosition(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
        }
    }



    public boolean isOverlapping(Widget other) {
        return this.getX() < other.getX() + other.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > other.getY();
    }

    /**
     * Renders the widget on the screen.
     */
    public final void render(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
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
    public final void renderInEditor(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
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
     * @param graphics GuiGraphicsExtractor Object
     * @param mouseX  X position of mouse.
     * @param mouseY  Y position of mouse
     */
    public abstract void renderWidget(GuiGraphicsExtractor graphics, int mouseX, int mouseY);

    /**
     * Renders the widget in the editor screen with a background.
     * Could also be used to display placeholder values.
     */
    private void renderWidgetInEditor(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        //drawWidgetBackground(graphics);

        renderWidget(graphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (widgetBox.isMouseOver(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            wasDragged = false;
            if (isLocked) {
                toggle(); // Static widgets toggle immediately
            } else {
                startX = (int) (mouseX - x);
                startY = (int) (mouseY - y);
                dragging = true;
                if (group != null) {
                    for (Widget member : group.getMembers()) {
                        if (member != this) {
                            member.wasDragged = false;
                            member.dragging = false;
                        }
                    }
                }
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
        if (isLocked) return false;

        if (dragging && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            wasDragged = true;

            int newX = (int) (mouseX - startX);
            int newY = (int) (mouseY - startY);

            if (this.isShiftDown) {
                int snapBoxWidth = mc.getWindow().getGuiScaledWidth() / snapSize;
                int snapBoxHeight = mc.getWindow().getGuiScaledHeight() / snapSize;

                newX = (newX / snapBoxWidth) * snapBoxWidth;
                newY = (newY / snapBoxHeight) * snapBoxHeight;
            }

            newX = (int) Mth.clamp(newX, 0, mc.getWindow().getGuiScaledWidth() - getWidth());
            newY = (int) Mth.clamp(newY, 0, mc.getWindow().getGuiScaledHeight() - getHeight());

            int deltaXMove = newX - this.x;
            int deltaYMove = newY - this.y;

            if (deltaXMove != 0 || deltaYMove != 0) {
                if (group != null) {
                    for (Widget member : group.getMembers()) {
                        if (member.isLocked()) continue;
                        member.x += deltaXMove;
                        member.y += deltaYMove;
                        member.clampPosition();
                        member.calculateOffset(member.x, member.y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                    }
                } else {
                    this.x = newX;
                    this.y = newY;
                    calculateOffset(x, y, mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
                }
            }

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
            float newScale = widgetBox.getScale() + (float) vAmount * 0.05f;
            widgetBox.setScale(Mth.clamp(newScale, minScale, maxScale));

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

    public boolean isMouseOverWidget(double mouseX, double mouseY) {
        return this.widgetBox.isMouseOver(mouseX, mouseY);
    }

    /**
     * Displays a faint grayish background if enabled or faint reddish background if disabled.
     */
    protected void drawWidgetBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
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
        uid = tag.contains("UID") ? UUID.fromString(tag.getString("UID").get()) : UUID.randomUUID();
        anchor = Anchor.valueOf(tag.getString("anchor").orElse("TOP_LEFT"));
        offsetX = tag.getIntOr("offsetX", 0);
        offsetY = tag.getIntOr("offsetY",0);
        isVisible = tag.getBoolean("isVisible").orElse(true);
        isLocked = tag.getBoolean("isLocked").orElse(tag.getBoolean("isDraggable").orElse(false));
        canScale = tag.getBoolean("canScale").orElse(true);
        widgetBox.setScale(tag.getFloat("widgetScale").orElse(1.0f));
        if (tag.contains("groupId")) {
            UUID groupId = UUID.fromString(tag.getString("groupId").get());
            String groupName = tag.getString("groupName").orElse("Group");
            WidgetGroup g = WidgetManager.getOrCreateGroup(groupId, groupName);
            g.addMember(this);
        }

        updatePosition(mc.getWindow().getGuiScaledWidth(), mc.getWindow().getGuiScaledHeight());
    }

    public void writeToTag(CompoundTag tag) {
        tag.putString("name", DATA.name());
        tag.putString("modId", modId);
        tag.putString("UID", uid.toString());
        tag.putBoolean("isLocked", isLocked);
        tag.putBoolean("canScale", canScale);
        tag.putFloat("widgetScale", widgetBox.getScale());
        tag.putString("anchor", anchor.name());
        tag.putInt("offsetX", offsetX);
        tag.putInt("offsetY", offsetY);
        tag.putBoolean("isVisible", isVisible);
        if (group != null) {
            tag.putString("groupId", group.getId().toString());
            tag.putString("groupName", group.getName());
        }
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

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        this.isLocked = locked;
    }

    public boolean canToggleLock() {
        return true;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public String getModId() {
        return modId;
    }

    @Override
    public String toString() {
        return this.getClass().getName() + "{" +
                "uniqueId='" + uid.toString() + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", offsetX=" + offsetX +
                ", offsetY=" + offsetY +
                ", isVisible=" + isVisible +
                ", isLocked=" + isLocked +
                ", shiftDown=" + isShiftDown +
                ", canScale=" + canScale +
                '}';
    }

    public enum Anchor {
        TOP_LEFT(0.0f, 0.0f), TOP_CENTER(0.5f, 0.0f), TOP_RIGHT(1.0f, 0.0f),
        CENTER_LEFT(0.0f, 0.5f), CENTER(0.5f, 0.5f), CENTER_RIGHT(1.0f, 0.5f),
        BOTTOM_LEFT(0.0f, 1.0f), BOTTOM_CENTER(0.5f, 1.0f), BOTTOM_RIGHT(1.0f, 1.0f);

        private final float xRatio;
        private final float yRatio;

        Anchor(float xRatio, float yRatio) {
            this.xRatio = xRatio;
            this.yRatio = yRatio;
        }

        public int getBaseX(int screenWidth) { return (int) (screenWidth * xRatio); }
        public int getBaseY(int screenHeight) { return (int) (screenHeight * yRatio); }

        public static Anchor _default(){
            return TOP_LEFT;
        }
    }

    public abstract static class WidgetBuilder<T, S> {
        protected int x;
        protected int y;
        protected boolean isVisible = true;
        protected boolean isLocked = false;
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

        public T setLocked(boolean isLocked) {
            this.isLocked = isLocked;
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
