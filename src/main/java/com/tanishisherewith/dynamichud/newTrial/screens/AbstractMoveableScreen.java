package com.tanishisherewith.dynamichud.newTrial.screens;

import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public abstract class AbstractMoveableScreen extends Screen {
    public final WidgetRenderer widgetRenderer;
    public int snapSize = 100;
    protected boolean shouldPause = false; // To pause if the screen is opened or not

    /**
     * Constructs a AbstractMoveableScreen object.
     */
    public AbstractMoveableScreen(Text title, WidgetRenderer renderer) {
        super(title);
        this.widgetRenderer = renderer;
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        widgetRenderer.isInEditor = true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        widgetRenderer.mouseDragged(mouseX, mouseY, button, snapSize);
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        widgetRenderer.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        widgetRenderer.mouseReleased(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        widgetRenderer.keyPressed(keyCode);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        widgetRenderer.keyReleased(keyCode);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        widgetRenderer.mouseScrolled(mouseX, mouseY, verticalAmount, horizontalAmount);
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
            this.renderBackgroundTexture(drawContext);
        }
        // Draw each widget
        widgetRenderer.renderWidgets(drawContext, mouseX, mouseY);
    }

    @Override
    public void close() {
        widgetRenderer.isInEditor = false;
        widgetRenderer.onCloseScreen();
        super.close();
    }

    public void setShouldPause(boolean shouldPause) {
        this.shouldPause = shouldPause;
    }

    @Override
    public boolean shouldPause() {
        return shouldPause;
    }

    public void setSnapSize(int size) {
        this.snapSize = size;
    }
}

