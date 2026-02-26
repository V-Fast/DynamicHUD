package com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ContextMenuScreen extends Screen {
    ContextMenu<?> contextMenu;
    ContextMenuProperties properties;

    protected ContextMenuScreen(ContextMenu<?> menu, ContextMenuProperties properties) {
        super(Component.literal("ContextMenu screen"));
        this.contextMenu = menu;
        this.properties = properties;
    }

    @Override
    public void added() {
        super.added();
        contextMenu.setVisible(true);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        contextMenu.update();
        DrawHelper.scaleAndPosition(graphics.pose(), (float) width / 2, (float) height / 2, contextMenu.getScale());

        properties.getSkin().setContextMenu(contextMenu);
        properties.getSkin().renderContextMenu(graphics, contextMenu, mouseX, mouseY);

        DrawHelper.stopScaling(graphics.pose());

        if (contextMenu.getScale() <= 0 && !contextMenu.isVisible()) {
            contextMenu.close();
        }
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int i, int j, float f) {

    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean bl) {
        contextMenu.mouseClicked(event.x(), event.y(), event.button());
        return super.mouseClicked(event, bl);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        contextMenu.mouseDragged(event.x(), event.y(), event.button(), dx, dy);
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseScrolled(double x, double y, double horizontalAmount, double verticalAmount) {
        contextMenu.mouseScrolled(x, y, horizontalAmount, verticalAmount);
        return super.mouseScrolled(x, y, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        contextMenu.mouseReleased(event.x(), event.y(), event.button());
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyReleased(KeyEvent event) {
        contextMenu.keyReleased(event.key(), event.scancode(), event.modifiers());
        return super.keyReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        contextMenu.keyPressed(event.key(), event.scancode(), event.modifiers());
        return super.keyPressed(event);
    }

    @Override
    public void onClose() {
        contextMenu.close();
        contextMenu.setVisible(false);
    }
}
