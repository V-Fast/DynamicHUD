package com.tanishisherewith.dynamichud.utils.contextmenu;

import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContextMenuManager {
    private static final ContextMenuManager INSTANCE = new ContextMenuManager();
    private final List<ContextMenuProvider> providers = new ArrayList<>();

    private ContextMenuManager() {}

    public static ContextMenuManager getInstance() {
        return INSTANCE;
    }

    public void registerProvider(ContextMenuProvider provider) {
        providers.add(provider);
    }

    public void renderAll(DrawContext drawContext, int mouseX, int mouseY) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.render(drawContext, contextMenu.getX(), contextMenu.getY(), mouseX, mouseY);
            }
        }
    }

    public void handleMouseClicked(double mouseX, double mouseY, int button) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseClicked(mouseX, mouseY, button);
            }
        }
    }

    public void handleMouseReleased(double mouseX, double mouseY, int button) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseReleased(mouseX, mouseY, button);
            }
        }
    }

    public void handleMouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
    }

    public void handleKeyPressed(int key, int scanCode, int modifiers) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.keyPressed(key, scanCode, modifiers);
            }
        }
    }

    public void handleKeyReleased(int key, int scanCode, int modifiers) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.keyReleased(key, scanCode, modifiers);
            }
        }
    }

    public void handleMouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        }
    }
    public void onClose(){
        for(ContextMenuProvider provider: providers){
            provider.getContextMenu().close();
        }
    }
}
