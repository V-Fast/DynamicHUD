package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A group is just another type of Option that contains other options
public class OptionGroup extends Option<OptionGroup> {
    private final List<Option<?>> groupOptions = new ArrayList<>();
    protected boolean expanded; // Skins can choose to use this or ignore it

    public OptionGroup(Text name) {
        super(name, () -> null, (v) -> {}, () -> true);
        this.expanded = false;
    }

    public void addOption(Option<?> option) {
        groupOptions.add(option);
    }

    public List<Option<?>> getGroupOptions() {
        return Collections.unmodifiableList(groupOptions);
    }

    @Override
    public void updateProperties(ContextMenuProperties properties) {
        super.updateProperties(properties);
        if (groupOptions == null) return;

        for (Option<?> option : groupOptions) {
            option.updateProperties(properties);
        }
    }

    @Override
    public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        super.render(drawContext,x,y,mouseX,mouseY);
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getHeightOfOptions() {
        int height = 0;
        for (Option<?> option : getGroupOptions()) {
            height += option.getHeight() + 1;
        }
        return height;
    }

    @Override
    public int getHeight() {
        return super.getHeight();
    }

    public static class OptionGroupRenderer implements SkinRenderer<Option<OptionGroup>> {
        @Override
        public void render(DrawContext drawContext, Option<OptionGroup> option, int x, int y, int mouseX, int mouseY) {}

        @Override
        public boolean mouseClicked(Option<OptionGroup> option2, double mouseX, double mouseY, int button) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().mouseClicked(subOption, mouseX, mouseY, button);
            }
            return SkinRenderer.super.mouseClicked(option, mouseX, mouseY, button);
        }

        @Override
        public boolean mouseReleased(Option<OptionGroup> option2, double mouseX, double mouseY, int button) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().mouseReleased(subOption, mouseX, mouseY, button);
            }
            return SkinRenderer.super.mouseReleased(option, mouseX, mouseY, button);
        }

        @Override
        public boolean mouseDragged(Option<OptionGroup> option2, double mouseX, double mouseY, int button, double deltaX, double deltaY) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().mouseDragged(subOption, mouseX, mouseY, button, deltaX, deltaY);
            }
            return SkinRenderer.super.mouseDragged(option, mouseX, mouseY, button, deltaX, deltaY);
        }

        @Override
        public void keyPressed(Option<OptionGroup> option2, int key, int scanCode, int modifiers) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().keyPressed(subOption, key, scanCode, modifiers);
            }
            SkinRenderer.super.keyPressed(option, key, scanCode, modifiers);
        }

        @Override
        public void keyReleased(Option<OptionGroup> option2, int key, int scanCode, int modifiers) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().keyReleased(subOption, key, scanCode, modifiers);
            }
            SkinRenderer.super.keyReleased(option, key, scanCode, modifiers);
        }

        @Override
        public void mouseScrolled(Option<OptionGroup> option2, double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
            OptionGroup option = (OptionGroup) option2;

            for (Option subOption : option.getGroupOptions()) {
                subOption.getRenderer().mouseScrolled(subOption, mouseX, mouseY, horizontalAmount, verticalAmount);
            }
            SkinRenderer.super.mouseScrolled(option, mouseX, mouseY, horizontalAmount, verticalAmount);
        }
    }
}