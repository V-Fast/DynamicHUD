package com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import net.minecraft.client.gui.screen.Screen;

/**
 * Default implementation of the {@link ContextMenuScreenFactory} providing a {@link ContextMenuScreen}
 */
public class DefaultContextMenuScreenFactory implements ContextMenuScreenFactory {
    @Override
    public Screen create(ContextMenu<?> contextMenu, ContextMenuProperties properties) {
        return new ContextMenuScreen(contextMenu, properties);
    }
}
