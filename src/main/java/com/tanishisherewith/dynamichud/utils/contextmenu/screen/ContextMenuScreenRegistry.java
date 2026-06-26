package com.tanishisherewith.dynamichud.utils.contextmenu.screen;

import net.minecraft.client.gui.screens.Screen;

public class ContextMenuScreenRegistry {
    public Class<? extends Screen> screenKlass;

    public ContextMenuScreenRegistry(Class<? extends Screen> screenKlass) {
        this.screenKlass = screenKlass;
    }
}