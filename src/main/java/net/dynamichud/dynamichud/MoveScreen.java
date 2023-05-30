package net.dynamichud.dynamichud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.Box;

public class MoveScreen extends Screen {
    private final DynamicUtil dynamicutil;
    private Widget selectedWidget = null;
    private int dragStartX = 0;
    private int dragStartY = 0;
    private boolean showMenu=false;
    private ContextMenu contextMenu = null;
    private ColorPicker colorPicker=null;
    private Widget rainbowWidget = null;
    private SliderWidget rainbowspeedslider = null;


    public MoveScreen(DynamicUtil dynamicutil) {
        super(Text.literal("Move Widgets"));
        this.dynamicutil = dynamicutil;
    }

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

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if the mouse is over any of the widgets
        for (Widget widget : dynamicutil.getWidgetManager().getWidgets()) {
            if (widget instanceof TextWidget textWidget) {
                TextWidget.setRainbowSpeed(rainbowspeedslider.getValue());
                int textWidth = client.textRenderer.getWidth(textWidget.getText());
                int textHeight = client.textRenderer.fontHeight;
                if (mouseX >= textWidget.getX() - textWidth / 2 && mouseX <= textWidget.getX() + textWidth / 2 && mouseY >= textWidget.getY() -textHeight / 2 && mouseY <= textWidget.getY() + textHeight / 2) {
                    if (button == 1) { // Right-click
                        selectedWidget = widget;
                        rainbowWidget=widget;
                        rainbowspeedslider = new SliderWidget(MinecraftClient.getInstance(), selectedWidget.getX(), selectedWidget.getY(), 120, 20, "Rainbow Speed",TextWidget.getRainbowSpeed(), 1f, 20.0f);
                        // Show context menu
                         contextMenu = new ContextMenu(MinecraftClient.getInstance(), selectedWidget.getX(),  selectedWidget.getY() + 21);
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
                                colorPicker = new ColorPicker(MinecraftClient.getInstance(), (int) mouseX, (int) mouseY + 66, textWidget.getColor(), textWidget::setColor);
                            });
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

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        // Stop dragging or scaling the widget
        if (selectedWidget != null) {
            selectedWidget = null;
            return true;
        }
        return contextMenu != null;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        // Update the position of the widget while dragging
        if (selectedWidget != null) {
            int newX = (int) (mouseX - dragStartX);
            int newY = (int) (mouseY - dragStartY);
            // Snap the position to a grid
            int gridSize = 2; // Change this value to adjust the size of the grid cells
            newX = gridSize * Math.round((float) newX / gridSize);
            newY = gridSize * Math.round((float) newY / gridSize);

            // Prevent the widget from moving beyond the borders of the screen
            int widgetWidth = 0;
            int widgetHeight = 0;
            if (selectedWidget instanceof TextWidget textWidget) {
                widgetWidth = textRenderer.getWidth(textWidget.getText());
                widgetHeight = textRenderer.fontHeight;
                newX = Math.max(newX, 0);
                newY = Math.max(newY, 0);
                newX = Math.min(newX, width - widgetWidth);
                newY = Math.min(newY, height - widgetHeight);
            } else if (selectedWidget instanceof ArmorWidget armorWidget) {
                widgetWidth = armorWidget.getWidth();
                widgetHeight = armorWidget.getHeight();
                newX = Math.max(newX, 0);
                newY = Math.max(newY, 0);
                newX = Math.min(newX, width - widgetWidth);
                newY = Math.min(newY, height - widgetHeight);
            }

            // Update the position of the widget
            if (selectedWidget instanceof TextWidget textWidgetItemStack) {
                textWidgetItemStack.setX(newX);
                textWidgetItemStack.setY(newY);
            } else if (selectedWidget instanceof ArmorWidget armorWidgetItemStack) {
                armorWidgetItemStack.setX(newX);
                armorWidgetItemStack.setY(newY);
            }
            if (rainbowWidget != null) {
                // Update the scale of the widget while scaling
                rainbowspeedslider.mouseDragged(mouseX,mouseY,button,deltaX,deltaY);
                TextWidget.setRainbowSpeed(rainbowspeedslider.getValue());
                return true;
            }
            return true;
        }
        return false;
    }
}

