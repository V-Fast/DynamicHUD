package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.BooleanPool;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * SubMenu option displays a sub menu beside a boolean-like button.
 * <p>
 * The {@link #getter} gets a boolean value to display/close the subMenu by default.
 * <p>
 * The {@link #setter} returns a boolean value depending on if the subMenu is visible or not
 */
public class SubMenuOption extends Option<Boolean> {
    private final ContextMenu subMenu;
    private final ContextMenu parentMenu;

    public SubMenuOption(String name, ContextMenu parentMenu, Supplier<Boolean> getter, Consumer<Boolean> setter, ContextMenuProperties properties) {
        super(name,getter, setter);
        Objects.requireNonNull(parentMenu, "Parent Menu cannot be null");
        this.parentMenu = parentMenu;
        this.subMenu = new ContextMenu(parentMenu.x + parentMenu.getWidth(), this.y, properties);
        this.subMenu.getProperties().setHeightOffset(0);
        this.subMenu.setVisible(get());
        this.renderer.init(this);
    }

    public SubMenuOption(String name, ContextMenu parentMenu, ContextMenuProperties properties) {
        this(name, parentMenu, () -> BooleanPool.get(name), value -> BooleanPool.put(name, value), properties);
    }

    public SubMenuOption(String name, ContextMenu parentMenu, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(name, parentMenu, getter, setter, parentMenu.getProperties().copyNewSkin());
    }

    public SubMenuOption(String name, ContextMenu parentMenu) {
        this(name, parentMenu, () -> BooleanPool.get(name), value -> BooleanPool.put(name, value), parentMenu.getProperties().copyNewSkin());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            subMenu.toggleDisplay();
            set(subMenu.isVisible());
            return true;
        }
        subMenu.mouseClicked(mouseX, mouseY, button);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        subMenu.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        subMenu.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    public SubMenuOption getOption() {
        return this;
    }

    public ContextMenu getSubMenu() {
        return subMenu;
    }

    public ContextMenu getParentMenu() {
        return parentMenu;
    }
}
