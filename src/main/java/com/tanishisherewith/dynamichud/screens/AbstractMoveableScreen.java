package com.tanishisherewith.dynamichud.screens;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractMoveableScreen extends Screen {
    public final WidgetRenderer widgetRenderer;

    /**
     * Constructs a AbstractMoveableScreen object.
     */
    public AbstractMoveableScreen(Text title, WidgetRenderer renderer) {
        super(title);
        this.widgetRenderer = renderer;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        widgetRenderer.isInEditor = true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        widgetRenderer.mouseDragged(mouseX, mouseY, button,deltaX,deltaY, GlobalConfig.get().getSnapSize());
        ContextMenuManager.getInstance().mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button,deltaX,deltaY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (widgetRenderer.mouseClicked(mouseX, mouseY, button)) {
            handleClickOnWidget(widgetRenderer.selectedWidget, mouseX, mouseY, button);
        }
        ContextMenuManager.getInstance().mouseClicked(mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean charTyped(char chr, int modifiers) {
        widgetRenderer.charTyped(chr,modifiers);
        ContextMenuManager.getInstance().charTyped(chr,modifiers);
        return super.charTyped(chr, modifiers);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        widgetRenderer.mouseReleased(mouseX, mouseY, button);
        ContextMenuManager.getInstance().mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        widgetRenderer.keyPressed(keyCode, scanCode, modifiers);
        ContextMenuManager.getInstance().keyPressed(keyCode, scanCode, modifiers);
        if(widgetRenderer.selectedWidget != null && (keyCode == GLFW.GLFW_KEY_DELETE || keyCode == GLFW.GLFW_KEY_BACKSPACE)){
     //       trayWidget.minimizeWidget(widgetRenderer.selectedWidget);
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        widgetRenderer.keyReleased(keyCode, scanCode, modifiers);
        ContextMenuManager.getInstance().keyReleased(keyCode, scanCode, modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        widgetRenderer.mouseScrolled(mouseX, mouseY, verticalAmount, horizontalAmount);
        ContextMenuManager.getInstance().mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
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
        if (this.client.world == null) {
            renderInGameBackground(drawContext);
        }
        drawContext.drawText(client.textRenderer, title, client.getWindow().getScaledWidth() / 2 - client.textRenderer.getWidth(title.getString()) / 2, textRenderer.fontHeight / 2, -1, true);

        // Draw each widget
        widgetRenderer.renderWidgets(drawContext, mouseX, mouseY);

        ContextMenuManager.getInstance().renderAll(drawContext, mouseX, mouseY);

        if (GlobalConfig.get().shouldDisplayDescriptions()) {
            for (Widget widget : widgetRenderer.getWidgets()) {
                if (widget == null || widget.isShiftDown) continue;

                if (widget.getWidgetBox().isMouseOver(mouseX, mouseY)) {
                    drawContext.drawTooltip(client.textRenderer, widget.tooltipText, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public void handleClickOnWidget(Widget widget, double mouseX, double mouseY, int button) {}

    @Override
    public void close() {
        widgetRenderer.isInEditor = false;
        widgetRenderer.onCloseScreen();
        ContextMenuManager.getInstance().onClose();
        super.close();
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}

