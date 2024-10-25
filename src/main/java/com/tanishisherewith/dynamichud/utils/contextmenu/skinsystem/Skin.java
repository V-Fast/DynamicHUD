package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Skin {
    protected static final MinecraftClient mc = MinecraftClient.getInstance();
    protected ContextMenu contextMenu;
    protected Map<Class<? extends Option<?>>, Supplier<SkinRenderer<? extends Option<?>>>> renderers = new HashMap<>();
    private boolean createNewScreen;

    public Skin(ContextMenu menu) {
        this.contextMenu = menu;
    }

    public Skin() {
    }

    public <T extends Option<?>> void addRenderer(Class<T> optionClass, Supplier<SkinRenderer<?>> renderer) {
        renderers.put(optionClass, renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Option<?>> SkinRenderer<T> getRenderer(Class<T> optionClass) {
        Supplier<SkinRenderer<? extends Option<?>>> supplier = renderers.get(optionClass);
        if (supplier != null) {
            return (SkinRenderer<T>) supplier.get();
        }
        return null;
    }

    /**
     * Whether this skin supports rendering option groups.
     * If false, groups should be flattened before rendering.
     */
    public boolean supportsGroups() {
        return this instanceof GroupableSkin; // Check if the skin supports groups
    }

    /**
     * Flatten a list of options, expanding any groups into their constituent options.
     * Used by skins that don't support group rendering.
     */
    protected List<Option<?>> flattenOptions(List<Option<?>> options) {
        List<Option<?>> flattened = new ArrayList<>();

        for (Option<?> option : options) {
            if (option instanceof OptionGroup group) {
                // Create a new list with type List<Option<?>>
                ArrayList groupOptions = new ArrayList<>(group.getGroupOptions());
                flattened.addAll(flattenOptions(groupOptions));
            } else {
                flattened.add(option);
            }
        }

        return flattened;
    }

    protected List<Option<?>> getOptions(ContextMenu menu){
        return supportsGroups() ? menu.getOptions() : flattenOptions(menu.getOptions());
    }

    public void setContextMenu(ContextMenu contextMenu) {
        this.contextMenu = contextMenu;
    }

    public void setRenderers(Map<Class<? extends Option<?>>, Supplier<SkinRenderer<? extends Option<?>>>> renderers) {
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

    protected boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
}
