package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.interfaces.TextGenerator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * This class represents a widget that can be displayed on the screen.
 */
public abstract class Widget {
    protected static Map<String, TextGenerator> textGenerators = new HashMap<>();

    protected final MinecraftClient client; // The Minecraft client instance
    public boolean enabled = true; // Whether the widget is enabled
    public boolean isDraggable = true;
    protected float xPercent; // The x position of the widget as a percentage of the screen width
    protected float yPercent; // The y position of the widget as a percentage of the screen height
    protected String label;
    protected static float scale = 1f; // The scaling factor of the widget

    /**
     * Constructs a Widget object.
     *
     * @param client The Minecraft client instance
     */
    public Widget(MinecraftClient client, String label) {
        this.client = client;
        this.label = label;
    }

    public static void addTextGenerator(String label, TextGenerator textGenerator) {
        textGenerators.put(label, textGenerator);
    }

    public abstract void setTextGeneratorFromLabel();

    /**
     * Gets the box around the widget for some purpose
     */
    public abstract WidgetBox getWidgetBox();

    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }


    public boolean isOverlapping(Set<Widget> other) {
        for (Widget widget : other) {
            if ((this.getX() < widget.getX() + widget.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > widget.getX() &&
                    this.getY() < widget.getY() + widget.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > widget.getY())) {
                return true;
            }
        }
        return false;
    }

    public boolean isOverlapping(Widget other) {
        return this.getX() < other.getX() + other.getWidgetBox().getWidth() && this.getX() + this.getWidgetBox().getWidth() > other.getX() &&
                this.getY() < other.getY() + other.getWidgetBox().getHeight() && this.getY() + this.getWidgetBox().getHeight() > other.getY();
    }

    /**
     * Renders the widget on the screen.
     */
    public abstract void render(DrawContext drawContext);

    public void updatePosition() {
        int screenWidth = client.getWindow().getScaledWidth();
        int screenHeight = client.getWindow().getScaledHeight();
    }

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
     * Returns the scaling factor of the widget.
     *
     * @return The scaling factor of the widget
     */
    public static float getScale() {
        return scale;
    }

    /**
     * Sets the scaling factor of the widget.
     *
     * @param scale The new scaling factor of the widget
     */
    public static void setScale(float scale) {
        Widget.scale = scale;
    }
    /**
     * Sets the x position of the widget.
     *
     * @param x The new x position of the widget in pixels
     */
    public void setX(float x) {
        int screenWidth = client.getWindow().getScaledWidth();
        if (x < 0) {
            x = (0);
        } else if (x + getWidgetBox().getWidth() > screenWidth) {
            x = screenWidth - getWidgetBox().getWidth();
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
    public void setY(float y) {
        int screenHeight = client.getWindow().getScaledHeight();
        if (y < 0) {
            y = 0;
        } else if (y + getWidgetBox().getHeight() > screenHeight) {
            y = (screenHeight) - getWidgetBox().getHeight();
        }
        this.yPercent = (float) y / screenHeight;
    }

    /**
     * Returns the fontheight
     *
     * @return fontHeight from TextRenderer
     */
    public int getHeight() {
        return client.textRenderer.fontHeight;
    }

    public void readFromTag(NbtCompound tag) {
        xPercent = tag.getFloat("xPercent");
        yPercent = tag.getFloat("yPercent");
        enabled = tag.getBoolean("Enabled");
        isDraggable = tag.getBoolean("isDraggable");
        label = tag.getString("label");
        scale = tag.getFloat("scale");

        setTextGeneratorFromLabel();
    }

    /**
     * Writes the state of this widget to the given tag.
     *
     * @param tag The tag to write to
     */
    public void writeToTag(NbtCompound tag) {
        tag.putString("class", getClass().getName());
        tag.putBoolean("isDraggable", isDraggable);
        tag.putFloat("xPercent", xPercent);
        tag.putFloat("yPercent", yPercent);
        tag.putBoolean("Enabled", enabled);
        tag.putString("label", label);
        tag.putFloat("scale", scale);


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
                } else if (value instanceof Color colorvalue) {
                    tag.putInt(field.getName(), colorvalue.getRGB());
                }// Add more cases here for other data types
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
