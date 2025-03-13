package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.utils.contextmenu.contextmenuscreen.ContextMenuScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WidgetRenderer implements Input {
    public final List<Class<? extends Screen>> allowedScreens = new CopyOnWriteArrayList<>();
    public boolean isInEditor = false;
    public Widget selectedWidget = null;
    List<Widget> widgets;
    private boolean renderInGameHud = true;
    private int Z_Index = 100;

    /**
     * Add the list of widgets the widgetRenderer should render
     * <p>
     * By default, it adds the {@link GameMenuScreen} to allow rendering of the widgets in the pause/main menu screen.
     *
     * @param widgets List of widgets to render
     */
    public WidgetRenderer(List<Widget> widgets) {
        this.widgets = widgets;
        addScreen(GameMenuScreen.class);
        addScreen(ContextMenuScreen.class);
    }

    public WidgetRenderer(String modID) {
        this(WidgetManager.getWidgetsForMod(modID));
    }

    public void addWidget(Widget widget) {
        widgets.add(widget);
    }

    public void addScreen(Class<? extends Screen> screen) {
        allowedScreens.add(screen);
    }

    public void shouldRenderInGameHud(boolean renderInGameHud) {
        this.renderInGameHud = renderInGameHud;
    }

    private boolean renderInDebugScreen(){
        if(GlobalConfig.get().renderInDebugScreen()){
            return true;
        }
        return !DynamicHUD.MC.getDebugHud().shouldShowDebugHud();
    }

    public void renderWidgets(DrawContext context, int mouseX, int mouseY) {
        if (WidgetManager.getWidgets().isEmpty() || !renderInDebugScreen()) return;

        Screen currentScreen = DynamicHUD.MC.currentScreen;

        context.getMatrices().push();
        context.getMatrices().translate(0,0, Z_Index);

        //Render in game hud
        if (currentScreen == null && renderInGameHud) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context, 0, 0);
            }
            return;
        }

        //Render in editing screen
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.isInEditor = true;
                widget.renderInEditor(context, mouseX, mouseY);
            }
            return;
        }
        //Render in any other screen
        if (currentScreen != null && allowedScreens.contains(DynamicHUD.MC.currentScreen.getClass())) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context, 0, 0);
            }
        }
        context.getMatrices().pop();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                // This essentially acts as a Z - layer where the widget first in the list is moved and dragged
                // if they are overlapped on each other.
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    selectedWidget = widget;
                    return true;
                }
            }
            selectedWidget = null;
        }
        return false;
    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double vAmount, double hAmount) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.mouseScrolled(mouseX, mouseY, vAmount, hAmount);
            }
        }
    }

    @Override
    public void charTyped(char c, int modifiers) {}

    public void onCloseScreen() {
        if (DynamicHUD.MC.currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.onClose();
            }
        }
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.mouseReleased(mouseX, mouseY, button);
            }
        }
        return false;
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, int snapSize) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                // This essentially acts as a Z - layer where the widget first in the list is moved and dragged
                // if they are overlapped on each other.
                if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, snapSize)) {
                    selectedWidget = widget;
                    return true;
                }
            }
            selectedWidget = null;
        }
        return false;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen instanceof AbstractMoveableScreen && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.isShiftDown = true;
            }
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen instanceof AbstractMoveableScreen && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.isShiftDown = false;
            }
        }
    }

    public WidgetRenderer withZIndex(int z_Index){
        this.Z_Index = z_Index;
        return this;
    }
}
