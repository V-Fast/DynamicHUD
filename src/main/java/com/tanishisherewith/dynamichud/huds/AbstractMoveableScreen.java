package com.tanishisherewith.dynamichud.huds;

import com.tanishisherewith.dynamichud.handlers.DefaultDragHandler;
import com.tanishisherewith.dynamichud.handlers.DefaultMouseHandler;
import com.tanishisherewith.dynamichud.handlers.DragHandler;
import com.tanishisherewith.dynamichud.handlers.MouseHandler;
import com.tanishisherewith.dynamichud.widget.slider.SliderWidget;
import com.tanishisherewith.dynamichud.util.colorpicker.ColorGradientPicker;
import com.tanishisherewith.dynamichud.util.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class AbstractMoveableScreen extends Screen {
    protected final DynamicUtil dynamicutil; // The DynamicUtil instance used by this screen
    protected MinecraftClient mc = MinecraftClient.getInstance();
    protected Widget selectedWidget = null; // The currently selected widget
    protected int dragStartX = 0, dragStartY = 0; // The starting position of a drag operation
    protected ContextMenu contextMenu = null; // The context menu that is currently displayed
    protected ColorGradientPicker colorPicker = null; // The color picker that is currently displayed
    protected Widget sliderWigdet = null; // The widget that is currently being edited by the slider
    protected SliderWidget Slider = null; // The rainbow speed slider
    protected MouseHandler mouseHandler;
    protected DragHandler dragHandler;
    protected int gridSize = 3; // The size of each grid cell in pixels
    protected boolean ShouldPause = false; // To pause if the screen is opened or not
    protected boolean ShouldBeAffectedByResize = false; // If the stuff drawn on screen to be affected by screen resize or not
    protected int widgetX;
    protected int widgetY;


    /**
     * Constructs a AbstractMoveableScreen object.
     *
     * @param dynamicutil The DynamicUtil instance used by this screen
     */
    public AbstractMoveableScreen(Text title, DynamicUtil dynamicutil) {
        super(title);
        this.dynamicutil = dynamicutil;
        updateMouseHandler(this.colorPicker, this.contextMenu, this.Slider);
        dragHandler = new DefaultDragHandler();
    }

    /**
     * Handles mouse dragging on this screen.
     *
     * @param mouseX - Current X position of mouse cursor.
     * @param mouseY - Current Y position of mouse cursor.
     * @param button - Mouse button being dragged.
     * @param deltaX - Change in X position since last call to this method.
     * @param deltaY - Change in Y position since last call to this method.
     * @return true if mouse dragging was handled by this screen.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (mouseHandler.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        // Update the position of the widget while dragging
        if (selectedWidget != null) {
            // Update the position of the context menu
            int newX = (int) (mouseX - dragStartX);
            int newY = (int) (mouseY - dragStartY);

            // Snap the widget to the grid
            newX = (newX / gridSize) * gridSize;
            newY = (newY / gridSize) * gridSize;

            selectedWidget.setX(newX);
            selectedWidget.setY(newY);
            return true;
        }
        return false;
    }
    /**
     * Handles mouse clicks on this screen.
     *
     * @param mouseX - X position of mouse cursor.
     * @param mouseY - Y position of mouse cursor.
     * @param button - Mouse button that was clicked.
     * @return true if mouse click was handled by this screen, false otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseHandler.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }
        // Check if the context menu is visible and if the mouse click occurred outside its bounds
        // Check if the Slider is visible and if the mouse click occurred outside its bounds
        if ((contextMenu != null && !contextMenu.contains(mouseX, mouseY) || Slider != null && !Slider.contains(mouseX, mouseY))) {
            // Close the context menu
            contextMenu = null;
            Slider = null;
            return true;
        }

        for (Widget widget : dynamicutil.getWidgetManager().getWidgets()) {
            if (widget.getWidgetBox().contains(widget, mouseX, mouseY)) {
                // Start dragging the widget
                colorPicker = null;
                contextMenu=null;
                Slider=null;
                if (button == 1) { // Right-click
                    handleRightClickOnWidget(widget);
                }else if(button == 0) {
                    widget.enabled = !widget.enabled;
                }
                if (dragHandler.startDragging(widget, mouseX, mouseY) && button==0) {
                    selectedWidget = widget;
                    if (contextMenu!=null) {
                        contextMenu.updatePosition();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Handles mouse release events on this screen.
     *
     * @param mouseX The current x position of the mouse cursor
     * @param mouseY The current y position of the mouse cursor
     * @param button The mouse button that was released
     * @return True if the mouse release event was handled by this screen, false otherwise
     */
    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Stop dragging or scaling the widget
        if (mouseHandler.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (selectedWidget != null) {
            selectedWidget = null;
            return true;
        }
        return contextMenu != null;
    }

    /**
     * Renders this screen and its widgets on the screen.
     *
     * @param drawContext The matrix stack used for rendering
     * @param mouseX      The current x position of the mouse cursor
     * @param mouseY      The current y position of the mouse cursor
     * @param delta       The time elapsed since the last frame in seconds
     */
    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        super.render(drawContext, mouseX, mouseY, delta);

        // Draw each widget
        for (Widget widget : dynamicutil.getWidgetManager().getWidgets()) {
            widget.render(drawContext);
        }

        // Draw the slider and other stuff
        if (Slider != null) {
            Slider.render(drawContext);
        }
        if (contextMenu != null) {
            contextMenu.render(drawContext);
        }
        if (colorPicker != null) {
            colorPicker.render(drawContext);
        }
        if (selectedWidget!=null)
        {
            widgetX=selectedWidget.getX();
            widgetY=selectedWidget.getY();
        }
        updateMouseHandler(colorPicker, contextMenu, Slider);
    }

    // Pass null if you dont want certain stuff
    private void updateMouseHandler(ColorGradientPicker colorPicker, ContextMenu contextMenu, SliderWidget Slider) {
        this.colorPicker = colorPicker;
        this.contextMenu = contextMenu;
        this.Slider = Slider;
        mouseHandler = new DefaultMouseHandler(colorPicker, contextMenu, Slider);
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    public void setShouldPause(boolean shouldpause) {
        this.ShouldPause = shouldpause;
    }

    public void setShouldBeAffectedByResize(boolean shouldBeAffectedByResize) {
        this.ShouldBeAffectedByResize = shouldBeAffectedByResize;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        if (ShouldBeAffectedByResize)
            super.resize(client, width, height);
        else {
            return;
        }
    }

    @Override
    public boolean shouldPause() {
        return ShouldPause;
    }

    protected abstract boolean handleRightClickOnWidget(Widget widget);

    protected abstract void menu(Widget widget, int x, int y);
}

