package net.dynamichud.dynamichud.Widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Box;

/**
 * This class represents a widget that can be displayed on the screen.
 */
public abstract class Widget {
    protected final MinecraftClient client; // The Minecraft client instance
    public boolean enabled = true; // Whether the widget is enabled
    protected float xPercent; // The x position of the widget as a percentage of the screen width
    protected float yPercent; // The y position of the widget as a percentage of the screen height

    /**
     * Constructs a Widget object.
     *
     * @param client The Minecraft client instance
     */
    public Widget(MinecraftClient client) {
        this.client = client;
    }

    /**
     * Gets the box around the widget for collison purpose
     */
    public abstract WidgetBox getWidgetBox();


    /**
     * Renders the widget on the screen.
     *
     * @param matrices The matrix stack used for rendering
     */
    public abstract void render(MatrixStack matrices);

    /**
     * Returns whether the widget is enabled.
     *
     * @return True if the widget is enabled, false otherwise
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Returns the x position of the widget.
     *
     * @return The x position of the widget in pixels
     */
    public int getX() {
        return (int) (client.getWindow().getScaledWidth() * xPercent);
    }

    /**
     * Returns the y position of the widget.
     *
     * @return The y position of the widget in pixels
     */
    public int getY() {
        return (int) (client.getWindow().getScaledHeight() * yPercent);
    }

    /**
     * Sets the x position of the widget.
     *
     * @param x The new x position of the widget in pixels
     */
    public void setX(int x) {
        this.xPercent = (float) x / client.getWindow().getScaledWidth();
    }

    /**
     * Sets the y position of the widget.
     *
     * @param y The new y position of the widget in pixels
     */
    public void setY(int y) {
        this.yPercent = (float) y / client.getWindow().getScaledHeight();
    }

    /**
     * Returns height of this widget.
     *@return height of this widget.
     */
    public int getHeight() {return client.textRenderer.fontHeight;}
}
