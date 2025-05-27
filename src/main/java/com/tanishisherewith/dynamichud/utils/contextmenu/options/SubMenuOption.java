package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.BooleanPool;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import net.minecraft.text.Text;

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
    private final ContextMenu<?> subMenu;

    public <T extends ContextMenuProperties> SubMenuOption(Text name, ContextMenu<?> parentMenu, Supplier<Boolean> getter, Consumer<Boolean> setter, T properties) {
        super(name, getter, setter);
        Objects.requireNonNull(parentMenu, "Parent Context Menu cannot be null in [" + name + "] SubMenu option");
        this.subMenu = parentMenu.createSubMenu(parentMenu.x + parentMenu.getWidth(), this.y, properties);
        this.subMenu.getProperties().setHeightOffset(0);
        this.subMenu.setVisible(get());
        this.renderer.init(this);
    }

    public <T extends ContextMenuProperties> SubMenuOption(Text name, ContextMenu<?> parentMenu, T properties) {
        this(name, parentMenu, () -> BooleanPool.get(name.getString()), value -> BooleanPool.put(name.getString(), value), properties);
    }

    public <T extends ContextMenuProperties> SubMenuOption(Text name, ContextMenu<?> parentMenu, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(name, parentMenu, getter, setter, parentMenu.getProperties().cloneWithSkin());
    }

    public <T extends ContextMenuProperties> SubMenuOption(Text name, ContextMenu<?> parentMenu) {
        this(name, parentMenu, () -> BooleanPool.get(name.getString()), value -> BooleanPool.put(name.getString(), value), parentMenu.getProperties().cloneWithSkin());
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

    public ContextMenu<?> getSubMenu() {
        return subMenu;
    }

    public ContextMenu<?> getParentMenu() {
        return subMenu.getParentMenu();
    }
}
