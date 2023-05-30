package net.dynamichud.dynamichud.hudscreen;

import net.dynamichud.dynamichud.Util.ColorPicker;
import net.dynamichud.dynamichud.Util.ContextMenu;
import net.dynamichud.dynamichud.Util.DynamicUtil;
import net.dynamichud.dynamichud.Widget.ArmorWidget;
import net.dynamichud.dynamichud.Widget.SliderWidget;
import net.dynamichud.dynamichud.Widget.TextWidget;
import net.dynamichud.dynamichud.Widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

/**
 * This class represents a screen that allows the user to move widgets around.
 */
public class MoveScreen extends Screen {
    private final DynamicUtil dynamicutil; // The DynamicUtil instance used by this screen
    MinecraftClient mc = MinecraftClient.getInstance();
    private Widget selectedWidget = null; // The currently selected widget
    private int dragStartX = 0,dragStartY = 0; // The starting position of a drag operation
    private ContextMenu contextMenu = null; // The context menu that is currently displayed
    private ColorPicker colorPicker = null; // The color picker that is currently displayed
    private Widget rainbowWidget = null; // The widget that is currently being edited by the rainbow speed slider
    private SliderWidget rainbowspeedslider = null; // The rainbow speed slider

    /**
     * Constructs a MoveScreen object.
     *
     * @param dynamicutil The DynamicUtil instance used by this screen
     */
    public MoveScreen(DynamicUtil dynamicutil) {
        super(Text.literal("Move Widgets"));
        this.dynamicutil = dynamicutil;
    }

    /**
     * Renders this screen and its widgets on the screen.
     *
     * @param matrices The matrix stack used for rendering
     * @param mouseX   The current x position of the mouse cursor
     * @param mouseY   The current y position of the mouse cursor
     * @param delta    The time elapsed since the last frame in seconds
     */
    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        super.render(matrices, mouseX, mouseY, delta);

        // Draw each widget
        for (Widget widget : dynamicutil.getWidgetManager().getWidgets()) {
            widget.render(matrices);
        }

        // Draw the slider and other stuff
        if (rainbowspeedslider != null) {
            rainbowspeedslider.render(matrices);
        }
        if (contextMenu != null) {
            contextMenu.render(matrices);
        }
        if (colorPicker != null) {
            colorPicker.render(matrices);
        }
    }

    /**
     * Handles mouse clicks on this screen.
     *@param mouseX - X position of mouse cursor.
     *@param mouseY - Y position of mouse cursor.
     *@param button - Mouse button that was clicked.
     *@return true if mouse click was handled by this screen, false otherwise.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the mouse is over any of the widgets
        for (Widget widget : dynamicutil.getWidgetManager().getWidgets()) {
            if (widget instanceof TextWidget textWidget) {
                int textWidth = client.textRenderer.getWidth(textWidget.getText());
                int textHeight = client.textRenderer.fontHeight;
                if (mouseX >= textWidget.getX() - textWidth / 2 && mouseX <= textWidget.getX() + textWidth / 2 && mouseY >= textWidget.getY() - textHeight / 2 && mouseY <= textWidget.getY() + textHeight / 2) {
                    if (button == 1) { // Right-click
                        selectedWidget = widget;
                        rainbowWidget = widget;
                        int x = selectedWidget.getX();
                        int y = selectedWidget.getY();
                        // Show context menu
                        contextMenu = new ContextMenu(mc, x, y + textRenderer.fontHeight + 2, textWidget);
                        contextMenu.addOption("Shadow", () -> {
                            // Toggle shadow
                            textWidget.setShadow(!textWidget.hasShadow());
                        });
                        contextMenu.addOption("Rainbow", () -> {
                            // Toggle rainbow
                            textWidget.setRainbow(!textWidget.hasRainbow());
                        });
                        contextMenu.addOption("Vertical Rainbow", () -> {
                            // Toggle vertical rainbow
                            textWidget.setVerticalRainbow(!textWidget.hasVerticalRainbow());
                        });
                        contextMenu.addOption("Color", () -> {
                            // Show color picker
                            // Set the color of the text
                            textWidget.toggleColorOption();
                            if (textWidget.isColorOptionEnabled()) {
                                // Set the color of the text widget
                                colorPicker = new ColorPicker(mc, mc.getWindow().getScaledWidth() / 2, mc.getWindow().getScaledHeight() / 2, textWidget.getColor(), textWidget::setColor);
                            } else colorPicker = null;
                        });

                        rainbowspeedslider = new SliderWidget(mc, x, y + 70, 105, 20, "Rainbow Speed", TextWidget.getRainbowSpeed(), 5f, 25.0f);
                        TextWidget.setRainbowSpeed(rainbowspeedslider.getValue());
                        return true;
                    } else {
                        // Start dragging the widget
                        selectedWidget = widget;
                        dragStartX = (int) (mouseX - textWidget.getX());
                        dragStartY = (int) (mouseY - textWidget.getY());

                        if (button == 0) widget.enabled = !widget.enabled;

                        return true;
                    }
                }
            } else if (widget instanceof ArmorWidget armorWidget) {
                if (mouseX >= armorWidget.getX() - armorWidget.getWidth() / 2 && mouseX <= armorWidget.getX() + armorWidget.getWidth() / 2 && mouseY >= armorWidget.getY() - armorWidget.getHeight() / 2 && mouseY <= armorWidget.getY() + armorWidget.getHeight() / 2) {
                    // Start dragging the widget
                    selectedWidget = widget;
                    dragStartX = (int) (mouseX - armorWidget.getX());
                    dragStartY = (int) (mouseY - armorWidget.getY());

                    if (button == 0) widget.enabled = !widget.enabled;
                    return true;
                }
            }
            if (contextMenu != null && contextMenu.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            // Check if the mouse is over the color picker
            if (colorPicker != null && colorPicker.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
            if (rainbowspeedslider != null && rainbowspeedslider.mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether this screen should pause the game when it is displayed.
     *
     * @return False to indicate that the game should not be paused
     */
    @Override
    public boolean shouldPause() {
        return false;
    }

    /**
     * Handles mouse movement on this screen.
     *
     * @param mouseX The current x position of the mouse cursor
     * @param mouseY The current y position of the mouse cursor
     */
    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // Call the mouseMoved method of the ColorPicker object
        ColorPicker.mouseMoved(mouseX, mouseY);
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
        if (selectedWidget != null) {
            selectedWidget = null;
            return true;
        }
        return contextMenu != null;
    }

    /**
     * Called when this screen is resized.
     *
     * @param client The Minecraft client instance
     * @param width  The new width of this screen in pixels
     * @param height The new height of this screen in pixels
     */
    @Override
    public void resize(MinecraftClient client, int width, int height) {
    }


    /**
     * Handles mouse dragging on this screen.
     *@param mouseX - Current X position of mouse cursor.
     *@param mouseY - Current Y position of mouse cursor.
     *@param button - Mouse button being dragged.
     *@param deltaX - Change in X position since last call to this method.
     *@param deltaY - Change in Y position since last call to this method.
     *@return true if mouse dragging was handled by this screen.
     */
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (rainbowWidget != null && rainbowspeedslider.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            // Update the scale of the widget while scaling
            TextWidget.setRainbowSpeed(rainbowspeedslider.getValue());
        }
        // Update the position of the widget while dragging
        if (selectedWidget != null) {
            // Update the position of the context menu
            if (this.contextMenu != null) {
                if (selectedWidget instanceof TextWidget) {
                    int textHeight = client.textRenderer.fontHeight;
                    contextMenu.setPosition(selectedWidget.getX(), selectedWidget.getY() + textHeight + 4);
                    rainbowspeedslider.setPosition(selectedWidget.getX(), selectedWidget.getY() + textHeight + 67);
                } else {
                    contextMenu.setPosition(selectedWidget.getX(), selectedWidget.getY());
                }
            }

            int newX = (int) (mouseX - dragStartX);
            int newY = (int) (mouseY - dragStartY);
            selectedWidget.setX(newX);
            selectedWidget.setY(newY);
            return true;
        }
        return false;
    }
}

