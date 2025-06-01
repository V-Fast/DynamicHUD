package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.utils.Input;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ContextMenuManager implements Input {
    private static final ContextMenuManager INSTANCE = new ContextMenuManager();
    private final List<ContextMenuProvider> providers = new ArrayList<>();

    private ContextMenuManager() {
    }

    public static ContextMenuManager getInstance() {
        return INSTANCE;
    }

    public void registerProvider(ContextMenuProvider provider) {
        providers.add(provider);
    }

    public void renderAll(DrawContext drawContext, int mouseX, int mouseY) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.render(drawContext, contextMenu.getX(), contextMenu.getY(), mouseX, mouseY);
            }
        }
    }

    public void onClose() {
        for (ContextMenuProvider provider : providers) {
            provider.getContextMenu().close();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseClicked(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
            }
        }
        return false;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.keyPressed(key, scanCode, modifiers);
            }
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.keyReleased(key, scanCode, modifiers);
            }
        }
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        }
    }

    @Override
    public void charTyped(char c, int modifiers) {
        for (ContextMenuProvider provider : providers) {
            ContextMenu<?> contextMenu = provider.getContextMenu();
            if (contextMenu != null) {
                contextMenu.charTyped(c, modifiers);
            }
        }
    }
}
