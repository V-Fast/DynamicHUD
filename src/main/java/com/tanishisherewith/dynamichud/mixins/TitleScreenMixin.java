package com.tanishisherewith.dynamichud.mixins;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.handlers.DefaultDragHandler;
import com.tanishisherewith.dynamichud.handlers.DefaultMouseHandler;
import com.tanishisherewith.dynamichud.handlers.DragHandler;
import com.tanishisherewith.dynamichud.handlers.MouseHandler;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {
    protected MouseHandler mouseHandler = new DefaultMouseHandler(null, null, null);
    protected DragHandler dragHandler = new DefaultDragHandler();
    protected Widget selectedWidget;
    protected int dragStartX = 0, dragStartY = 0; // The starting position of a drag operation
    protected int gridSize = 1; // The size of each grid cell in pixels
    DynamicUtil dynamicUtil = DynamicHUD.getDynamicUtil();
    protected TitleScreenMixin(Text title) {
        super(title);
    }

    public void setGridSize(int gridSize) {
        this.gridSize = gridSize;
    }

    @Inject(at = @At("TAIL"), method = "render")
    private void render(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        // Draw custom text on the title screen
        if (dynamicUtil != null && (dynamicUtil.MainMenuWidgetAdded || dynamicUtil.WidgetLoaded)) {
            dynamicUtil.render(context, delta);
        }
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseHandler.mouseClicked(mouseX, mouseY, button)) {
            return true;
        }

        for (Widget widget : dynamicUtil.getWidgetManager().getMainMenuWidgets()) {
            if (widget.getWidgetBox().contains(widget, mouseX, mouseY)) {
                if (button == 1) { // Right-click
                    handleRightClickOnWidget(widget);
                } else if (button == 0) {
                    widget.enabled = !widget.enabled;
                }
                if (dragHandler.startDragging(widget, mouseX, mouseY) && button == 0 && widget.isDraggable) {
                    selectedWidget = widget;
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }


    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (mouseHandler.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (selectedWidget != null && selectedWidget.isDraggable) {
            // Update the position of the widget while dragging
            int newX = (int) (mouseX - dragStartX);
            int newY = (int) (mouseY - dragStartY);

            // Snap the widget to the grid
            newX = (newX / gridSize) * gridSize;
            newY = (newY / gridSize) * gridSize;

            selectedWidget.setX(newX);
            selectedWidget.setY(newY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (mouseHandler.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        if (selectedWidget != null) {
            selectedWidget = null;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    protected void handleRightClickOnWidget(Widget widget) {

    }

}
