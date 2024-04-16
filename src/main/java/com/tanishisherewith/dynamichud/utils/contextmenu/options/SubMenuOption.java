package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.Option;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
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
    public String name = "Empty";


    public SubMenuOption(String name, @NotNull ContextMenu parentMenu, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        super(getter, setter);
        Objects.requireNonNull(parentMenu, "Parent Menu cannot be null");
        this.name = name;
        this.parentMenu = parentMenu;
        this.subMenu = new ContextMenu(parentMenu.x + parentMenu.finalWidth, this.y);
        this.subMenu.heightOffset = 0;
        this.subMenu.shouldDisplay = get();
    }

    @Override
    public void render(DrawContext drawContext, int x, int y,int mouseX,int mouseY) {
        this.x = x;
        this.y = y;

        int color = value ? Color.GREEN.getRGB() : Color.RED.getRGB();
        drawContext.drawText(mc.textRenderer, Text.of(name), x, y + 1, color, false);
        this.height = mc.textRenderer.fontHeight + 2;
        this.width = mc.textRenderer.getWidth(name) + 1;

        subMenu.render(drawContext, this.x + parentMenu.finalWidth, this.y, 0,mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            subMenu.toggleDisplay();
            set(subMenu.shouldDisplay);
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
    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        subMenu.mouseDragged(mouseX, mouseY, button);
        return super.mouseDragged(mouseX, mouseY, button);
    }

    public SubMenuOption getOption() {
        return this;
    }

    public ContextMenu getSubMenu() {
        return subMenu;
    }
}
