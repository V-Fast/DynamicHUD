package com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.OptionGroup;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.GroupableSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public abstract class Skin {
    protected static final Minecraft mc = Minecraft.getInstance();
    protected ContextMenu<?> contextMenu;
    protected Map<Class<? extends Option<?>>, Supplier<SkinRenderer<? extends Option<?>>>> renderers = new HashMap<>();
    private boolean createNewScreen;

    public Skin(ContextMenu<?> menu) {
        this();
        this.contextMenu = menu;
    }

    public Skin() {
        addRenderer(OptionGroup.class, OptionGroup.OptionGroupRenderer::new);
    }

    public <T extends Option<?>> void addRenderer(Class<T> optionClass, Supplier<SkinRenderer<?>> renderer) {
        renderers.put(optionClass, renderer);
    }

    @SuppressWarnings("unchecked")
    public <T extends Option<?>> SkinRenderer<T> getRenderer(Class<T> optionClass) {
        Class<?> current = optionClass;
        while (current != null && Option.class.isAssignableFrom(current)) {
            Supplier<SkinRenderer<? extends Option<?>>> supplier = renderers.get(current);
            if (supplier != null) {
                return (SkinRenderer<T>) supplier.get();
            }
            current = current.getSuperclass();
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
    public static List<Option<?>> flattenOptions(List<Option<?>> options) {
        List<Option<?>> flattened = new ArrayList<>();

        for (Option<?> option : options) {
            if (option instanceof OptionGroup group) {
                // Create a new list with type List<Option<?>>
                ArrayList<Option<?>> groupOptions = new ArrayList<>(group.getGroupOptions());
                flattened.addAll(flattenOptions(groupOptions));
            } else {
                flattened.add(option);
            }
        }

        return flattened;
    }

    public List<Option<?>> getOptions(ContextMenu<?> menu) {
        return supportsGroups() ? menu.getOptions() : flattenOptions(menu.getOptions());
    }

    public void setContextMenu(ContextMenu<?> contextMenu) {
        this.contextMenu = contextMenu;
    }

    public void setRenderers(Map<Class<? extends Option<?>>, Supplier<SkinRenderer<? extends Option<?>>>> renderers) {
        this.renderers = renderers;
    }

    public abstract void renderContextMenu(GuiGraphicsExtractor graphics, ContextMenu<?> contextMenu, int mouseX, int mouseY);

    public boolean mouseClicked(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseReleased(ContextMenu<?> menu, double mouseX, double mouseY, int button) {
        return false;
    }

    public boolean mouseDragged(ContextMenu<?> menu, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public void keyPressed(ContextMenu<?> menu, int key, int scanCode, int modifiers) {
    }

    public void keyReleased(ContextMenu<?> menu, int key, int scanCode, int modifiers) {
    }

    public void charTyped(ContextMenu<?> menu, char c, int modifiers) {
    }

    public void mouseScrolled(ContextMenu<?> menu, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
    }

    public boolean shouldCreateNewScreen() {
        return createNewScreen;
    }

    public boolean showHoverTooltips() {
        return true;
    }

    public void renderTooltipIfHovered(GuiGraphicsExtractor graphics, Option<?> option, int x, int y, int mouseX, int mouseY, int maxTextWidth) {
        if (!showHoverTooltips()) return;

        if (isMouseOver(mouseX, mouseY, x, y, option.getWidth(), option.getHeight())) {
            List<FormattedCharSequence> tooltipLines = new ArrayList<>();
            boolean isTruncated = mc.font.width(option.name.getString()) > maxTextWidth;

            if (isTruncated) {
                tooltipLines.add(option.name.copy().withStyle(style -> style.withColor(0xFFFFAA00).withBold(true)).getVisualOrderText());
            }

            if (option.description != null && !option.description.getString().isEmpty()) {
                tooltipLines.addAll(mc.font.split(option.description, 200));
            }

            if (!tooltipLines.isEmpty()) {
                graphics.setTooltipForNextFrame(mc.font, tooltipLines, mouseX, mouseY);
            }
        }
    }

    public void setCreateNewScreen(boolean createNewScreen) {
        this.createNewScreen = createNewScreen;
    }

    public static boolean isMouseOver(double mouseX, double mouseY, double x, double y, double width, double height) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    /**
     * Create a new Skin object with the same parameters as this current screen. This is must for SubMenu options.
     * If an object of same skin type is not returned then SubMenu options will not share the same skin with parent menu.
     * @return new instance of this skin that need to be cloned to sub-menu option.
     */
    public abstract Skin clone();
}
