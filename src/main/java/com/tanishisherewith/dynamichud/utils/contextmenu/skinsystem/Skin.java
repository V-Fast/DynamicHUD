package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;

import java.util.HashMap;
import java.util.Map;

public abstract class Skin {
    protected ContextMenu contextMenu;
    protected Map<Class<? extends Option<?>>, SkinRenderer<? extends Option<?>>> renderers = new HashMap<>();
    private boolean createNewScreen;

    public Skin(ContextMenu menu) {
        this.contextMenu = menu;
    }

    public Skin() {
    }

    public <T extends Option<?>> void addRenderer(Class<T> optionClass, SkinRenderer<? super T> renderer) {
        renderers.put(optionClass, renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Option<?>> SkinRenderer<T> getRenderer(Class<T> optionClass) {
        return (SkinRenderer<T>) renderers.get(optionClass);
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public void setRenderers(Map<Class<? extends Option<?>>, SkinRenderer<? extends Option<?>>> renderers) {
        this.renderers = renderers;
    }

    public abstract void renderContextMenu(DrawContext drawContext, ContextMenu contextMenu, int mouseX, int mouseY);

    public boolean mouseClicked(ContextMenu menu, double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(ContextMenu menu, double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(ContextMenu menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public void keyPressed(ContextMenu menu, int key, int scanCode, int modifiers) {
    }

    public void keyReleased(ContextMenu menu, int key, int scanCode, int modifiers) {
    }

    public void mouseScrolled(ContextMenu menu, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }

    public boolean shouldCreateNewScreen() {
        return createNewScreen;
    }

    public void setCreateNewScreen(boolean createNewScreen) {
        this.createNewScreen = createNewScreen;
    }
}
