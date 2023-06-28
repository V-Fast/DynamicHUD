package com.tanishisherewith.dynamichud.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

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
     */
    public abstract void render(DrawContext drawContext);

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
     * Sets the x position of the widget.
     *
     * @param x The new x position of the widget in pixels
     */
    public void setX(int x) {
        int screenWidth = client.getWindow().getScaledWidth();
        if (x < 0) {
            x = 0;
        } else if (x + getWidgetBox().getWidth() > screenWidth) {
            x = screenWidth - getWidgetBox().getWidth()+(getWidgetBox().getWidth()/2);
        }
        this.xPercent = (float) x / screenWidth;
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
     * Sets the y position of the widget.
     *
     * @param y The new y position of the widget in pixels
     */
    public void setY(int y) {
        int screenHeight = client.getWindow().getScaledHeight();
        if (y < 0) {
            y = 0;
        } else if (y + getWidgetBox().getHeight() > screenHeight) {
            y = screenHeight - getWidgetBox().getHeight()+(getWidgetBox().getHeight()/2);
        }
        this.yPercent = (float) y / screenHeight;
    }

    public int getHeight()
    {
        return client.textRenderer.fontHeight;
    }
    public void readFromTag(NbtCompound tag) {
    }

    /**
     * Writes the state of this widget to the given tag.
     *
     * @param tag The tag to write to
     */
    public void writeToTag(NbtCompound tag) {
        tag.putString("class", getClass().getName());

        for (Field field : getClass().getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);

            try {
                Object value = field.get(this);

                if (value instanceof Boolean) {
                    tag.putBoolean(field.getName(), (Boolean) value);
                } else if (value instanceof Byte) {
                    tag.putByte(field.getName(), (Byte) value);
                } else if (value instanceof Short) {
                    tag.putShort(field.getName(), (Short) value);
                } else if (value instanceof Integer) {
                    tag.putInt(field.getName(), (Integer) value);
                } else if (value instanceof Long) {
                    tag.putLong(field.getName(), (Long) value);
                } else if (value instanceof Float) {
                    tag.putFloat(field.getName(), (Float) value);
                } else if (value instanceof Double) {
                    tag.putDouble(field.getName(), (Double) value);
                } else if (value instanceof String) {
                    tag.putString(field.getName(), (String) value);
                } // Add more cases here for other data types
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
