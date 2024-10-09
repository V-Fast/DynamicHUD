package com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import net.minecraft.client.gui.screen.Screen;

/**
 * We will use this interface to provide the context menu with the screen required by its skins.
 * Some skins like {@link com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin} require a separate screen to render its contents.
 * This can also be used for developers to provide a new custom screen by them or for a custom skin.
 */
public interface ContextMenuScreenFactory {
    Screen create(ContextMenu contextMenu, ContextMenuProperties properties);
}
