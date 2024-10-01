package com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen;

import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ContextMenuScreen extends Screen {
    ContextMenu contextMenu;
    ContextMenuProperties properties;

    protected ContextMenuScreen(ContextMenu menu,ContextMenuProperties properties) {
        super(Text.of("ContextMenu screen"));
        this.contextMenu = menu;
        this.properties = properties;
    }

    @Override
    public void onDisplayed() {
        super.onDisplayed();
        contextMenu.setVisible(true);
    }

    @Override
    public void render(DrawContext drawContext, int mouseX, int mouseY, float delta) {
        contextMenu.update();
        DrawHelper.scaleAndPosition(drawContext.getMatrices(), (float) width /2, (float) height /2,contextMenu.getScale());

        properties.getSkin().setContextMenu(contextMenu);
        properties.getSkin().renderContextMenu(drawContext,contextMenu,mouseX,mouseY);

        DrawHelper.stopScaling(drawContext.getMatrices());

        if(contextMenu.getScale() <= 0 && !contextMenu.isVisible()){
            contextMenu.close();
        }
    }

    @Override
    protected void renderDarkening(DrawContext context) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        contextMenu.mouseClicked(mouseX,mouseY,button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        contextMenu.mouseDragged(mouseX,mouseY,button,deltaX,deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        contextMenu.mouseScrolled(mouseX,mouseY,horizontalAmount,verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        contextMenu.mouseReleased(mouseX,mouseY,button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        contextMenu.keyReleased(keyCode,scanCode,modifiers);
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        contextMenu.keyPressed(keyCode,scanCode,modifiers);
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void close() {
        contextMenu.close();
    }
}
