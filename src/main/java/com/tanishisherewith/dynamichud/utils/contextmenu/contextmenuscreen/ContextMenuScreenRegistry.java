package com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen;

import net.minecraft.client.gui.screen.Screen;

public class ContextMenuScreenRegistry{
    public Class<? extends Screen> screenKlass;

    public ContextMenuScreenRegistry(Class<? extends Screen> screenKlass){
        this.screenKlass = screenKlass;
    }
}