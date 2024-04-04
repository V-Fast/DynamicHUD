package com.tanishisherewith.dynamichud.newTrial.screens;

import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
public abstract class AbstractMoveableScreen extends Screen {
    protected boolean ShouldPause = false; // To pause if the screen is opened or not
    public final WidgetRenderer widgetRenderer;


    /**
     * Constructs a AbstractMoveableScreen object.
     *
     */
    public AbstractMoveableScreen(Text title, WidgetRenderer renderer) {
        super(title);
        this.widgetRenderer = renderer;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        widgetRenderer.mouseDragged(mouseX,mouseY,button);
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        widgetRenderer.mouseClicked(mouseX,mouseY,button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        widgetRenderer.mouseReleased(mouseX,mouseY,button);
        return false;
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
        widgetRenderer.isInEditor = true;
        widgetRenderer.renderWidgets(drawContext);
    }

    @Override
    public void close() {
        super.close();
        widgetRenderer.isInEditor = false;
    }

    public void setShouldPause(boolean shouldPause) {
        this.ShouldPause = shouldPause;
    }


    @Override
    public void resize(MinecraftClient client, int width, int height) {
            super.resize(client, width, height);
    }

    @Override
    public boolean shouldPause() {
        return ShouldPause;
    }
}

