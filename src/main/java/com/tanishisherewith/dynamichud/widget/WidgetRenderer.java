package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WidgetRenderer {
    public final List<Class<? extends Screen>> allowedScreens = new CopyOnWriteArrayList<>();
    public boolean isInEditor = false;
    public Widget selectedWidget = null;
    List<Widget> widgets;
    private boolean renderInGameHud = true;

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

    public void renderWidgets(DrawContext context, int mouseX, int mouseY) {
        if (WidgetManager.getWidgets().isEmpty() || DynamicHUD.MC.getDebugHud().shouldShowDebugHud()) return;

        Screen currentScreen = DynamicHUD.MC.currentScreen;

        if (currentScreen == null && renderInGameHud) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context, 0, 0);
            }
            return;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.isInEditor = true;
                widget.renderInEditor(context, mouseX, mouseY);
            }
            return;
        }
        if (currentScreen != null && allowedScreens.contains(DynamicHUD.MC.currentScreen.getClass()) && !this.isInEditor) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context, 0, 0);
            }
        }
    }

    public void mouseClicked(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                // This essentially acts as a Z - layer where the widget first in the list is moved and dragged
                // if they are overlapped on each other.
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    selectedWidget = widget;
                    return;
                }
            }
        }
    }

    public void mouseDragged(double mouseX, double mouseY, int button, int snapSize) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                // This essentially acts as a Z - layer where the widget first in the list is moved and dragged
                // if they are overlapped on each other.
                if (widget.mouseDragged(mouseX, mouseY, button, snapSize)) {
                    selectedWidget = widget;
                    return;
                }
            }
        }
    }

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

    public void keyPressed(int keyCode) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen instanceof AbstractMoveableScreen && (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.shiftDown = true;
            }
        }
    }

    public void keyReleased(int keyCode) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen instanceof AbstractMoveableScreen && (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.shiftDown = false;
            }
        }
    }

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

    public void mouseReleased(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if (currentScreen == null) {
            return;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.mouseReleased(mouseX, mouseY, button);
            }
        }
    }
}
