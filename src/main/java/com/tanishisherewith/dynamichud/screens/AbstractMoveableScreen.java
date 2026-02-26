package com.tanishisherewith.dynamichud.screens;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.utils.Util;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import org.jspecify.annotations.NonNull;
import org.lwjgl.glfw.GLFW;

public abstract class AbstractMoveableScreen extends Screen {
    public final WidgetRenderer widgetRenderer;

    /**
     * Constructs a AbstractMoveableScreen object.
     */
    public AbstractMoveableScreen(Component title, WidgetRenderer renderer) {
        super(title);
        this.widgetRenderer = renderer;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void added() {
        super.added();
        widgetRenderer.isInEditor = true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        widgetRenderer.mouseDragged(event.x(), event.y(), event.button(),
                dx, dy,
                GlobalConfig.get().getSnapSize());
        ContextMenuManager.getInstance().mouseDragged(event.x(), event.y(), event.button(),
                dx, dy);
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        if (widgetRenderer.mouseClicked(event.x(), event.y(), event.button())) {
            handleClickOnWidget(widgetRenderer.selectedWidget, event.x(), event.y(), event.button());
        }
        ContextMenuManager.getInstance().mouseClicked(event.x(), event.y(), event.button());
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        widgetRenderer.charTyped((char) event.codepoint(), event.modifiers());
        ContextMenuManager.getInstance().charTyped((char) event.codepoint(), event.modifiers());
        return super.charTyped(event);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        widgetRenderer.mouseReleased(event.x(), event.y(), event.button());
        ContextMenuManager.getInstance().mouseReleased(event.x(), event.y(), event.button());
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        widgetRenderer.keyPressed(event.key(), event.scancode(), event.modifiers());
        ContextMenuManager.getInstance().keyPressed(event.key(), event.scancode(), event.modifiers());
        if (widgetRenderer.selectedWidget != null &&
                (event.key() == GLFW.GLFW_KEY_DELETE || event.key() == GLFW.GLFW_KEY_BACKSPACE)) {
            // trayWidget.minimizeWidget(widgetRenderer.selectedWidget);
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        widgetRenderer.keyReleased(event.key(), event.scancode(), event.modifiers());
        ContextMenuManager.getInstance().keyReleased(event.key(), event.scancode(), event.modifiers());
        return super.keyReleased(event);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontalAmount, double verticalAmount) {
        widgetRenderer.mouseScrolled(x, y, verticalAmount, horizontalAmount);
        ContextMenuManager.getInstance().mouseScrolled(x, y, horizontalAmount, verticalAmount);
        return super.mouseScrolled(x, y, horizontalAmount, verticalAmount);
    }

    /**
     * Renders this screen and its widgets on the screen.
     *
     * @param graphics The matrix stack used for rendering
     * @param mouseX      The current x position of the mouse cursor
     * @param mouseY      The current y position of the mouse cursor
     * @param delta       The time elapsed since the last frame in seconds
     */
    @Override
    public void render(@NonNull GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft.level == null) {
            renderBackground(graphics,mouseX,mouseY,delta);
        }
        graphics.drawCenteredString(this.font, this.title, this.width / 2, this.font.lineHeight / 2,-1);

        // Draw each widget
        widgetRenderer.renderWidgets(graphics, mouseX, mouseY);

        ContextMenuManager.getInstance().renderAll(graphics, mouseX, mouseY);

        if (GlobalConfig.get().shouldDisplayDescriptions()) {
            for (Widget widget : widgetRenderer.getWidgets()) {
                if (widget == null || widget.isShiftDown) continue;

                if (widget.getWidgetBox().isMouseOver(mouseX, mouseY)) {
                    graphics.setTooltipForNextFrame(this.font, widget.tooltipText, mouseX, mouseY);
                    break;
                }
            }
        }
    }

    public void handleClickOnWidget(Widget widget, double mouseX, double mouseY, int button) {
    }

    @Override
    public void onClose() {
        widgetRenderer.isInEditor = false;
        widgetRenderer.onCloseScreen();
        ContextMenuManager.getInstance().onClose();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
