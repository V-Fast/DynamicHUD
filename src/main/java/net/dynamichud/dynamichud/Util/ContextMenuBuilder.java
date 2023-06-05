package net.dynamichud.dynamichud.Util;

import net.dynamichud.dynamichud.Widget.Widget;
import net.minecraft.client.MinecraftClient;

import java.io.Serializable;

public class ContextMenuBuilder {
    private final MinecraftClient client;
    private int x;
    private int y;
    private Widget selectedWidget;
    private int backgroundColor = 0x80C0C0C0;
    private int cornerRadius = 5;
    private int padding = 5;
    private int heightfromwidget = 5;

    public ContextMenuBuilder(MinecraftClient client) {
        this.client = client;
    }

    public ContextMenuBuilder setX(int x) {
        this.x = x;
        return this;
    }

    public ContextMenuBuilder setY(int y) {
        this.y = y;
        return this;
    }

    public ContextMenuBuilder setSelectedWidget(Widget selectedWidget) {
        this.selectedWidget = selectedWidget;
        return this;
    }

    public ContextMenuBuilder setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public ContextMenuBuilder setPadding(int padding) {
        this.padding = padding;
        return this;
    }

    public ContextMenuBuilder setHeightFromWidget(int heightfromwidget) {
        this.heightfromwidget = heightfromwidget;
        return this;
    }

    public ContextMenu build() {
        ContextMenu contextMenu = new ContextMenu(client, x, y, selectedWidget);
        contextMenu.setBackgroundColor(backgroundColor);
        contextMenu.setPadding(padding);
        contextMenu.setHeightfromwidget(heightfromwidget);
        return contextMenu;
    }
}

