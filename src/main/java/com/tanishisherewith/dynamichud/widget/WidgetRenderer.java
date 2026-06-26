package com.tanishisherewith.dynamichud.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.internal.System;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.ContextMenuScreenRegistry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.List;
import java.util.function.Predicate;

public class WidgetRenderer implements Input {
    private Predicate<Screen> allowedScreens;
    public boolean isInEditor = false;
    public Widget selectedWidget = null;
    List<Widget> widgets;
    private boolean renderInGameHud = true;
    //private int Z_Index = -1;

    // Snapping Guideline Coordinates
    private float snapLineX = -1;
    private float snapLineY = -1;
    private float screenCenterX = -1;
    private float screenCenterY = -1;

    /**
     * Add the list of widgets the widgetRenderer should render
     * <p>
     * By default, it adds the {@link PauseScreen} to allow rendering of the widgets in the pause/main menu screen.
     *
     * @param widgets List of widgets to render
     */
    public WidgetRenderer(List<Widget> widgets) {
        this.widgets = widgets;
        // Render in GameMenuScreen
        this.allowedScreens = screen -> screen.getClass() == PauseScreen.class ||
                System.getInstances(ContextMenuScreenRegistry.class, DynamicHUD.MOD_ID).stream().anyMatch(registry -> registry.screenKlass == screen.getClass());
    }

    public WidgetRenderer(String modID) {
        this(WidgetManager.getWidgetsForMod(modID));
    }

    public void addWidget(Widget widget) {
        this.widgets.add(widget);
    }

    public void removeWidget(Widget widget) {
        this.widgets.remove(widget);
    }

    public void clearAndAdd(List<Widget> widgets) {
        this.widgets.clear();
        this.widgets.addAll(widgets);
    }

    /**
     * Use this when you want to simply add more screens
     */
    public void addScreen(Class<? extends Screen> screen) {
        this.allowedScreens = allowedScreens.or(screen1 -> screen1.getClass() == screen);
    }

    /**
     * Use this when you want a more complex approach to rendering your widgets
     */
    public Predicate<Screen> getAllowedScreens() {
        return this.allowedScreens;
    }

    public void updateAllowedScreens(Predicate<Screen> newAllowedScreens) {
        this.allowedScreens = newAllowedScreens;
    }

    public void negateAllowedScreens() {
        allowedScreens = allowedScreens.negate();
    }

    public boolean isScreenAllowed(Screen screen) {
        return allowedScreens.test(screen);
    }

    public void shouldRenderInGameHud(boolean renderInGameHud) {
        this.renderInGameHud = renderInGameHud;
    }

    private boolean renderInDebugScreen() {
        if (GlobalConfig.get().renderInDebugScreen()) {
            return true;
        }
        return !DynamicHUD.MC.getDebugOverlay().showDebugScreen();
    }

    public void renderWidgets(GuiGraphics graphics, int mouseX, int mouseY) {
        if (WidgetManager.getWidgets().isEmpty() || !renderInDebugScreen()) return;

        Screen currentScreen = DynamicHUD.MC.screen;

       // graphics.pose().pushMatrix();
       // graphics.pose().translate(0, 0,Z_Index);

        //Render in editing screen
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                widget.isInEditor = true;
                widget.renderInEditor(graphics, mouseX, mouseY);
            }

            if(GlobalConfig.get().doSmartSnapping()) {
                drawSnapGuides(graphics);
            }
            return;
        }
        //Render in any other screen and the inGameHud
        if ((currentScreen == null && renderInGameHud) || allowedScreens.test(currentScreen)) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(graphics, 0, 0);
            }
        }
        //graphics.pose().popMatrix();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.screen;
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
        Screen currentScreen = DynamicHUD.MC.screen;
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
    public void charTyped(char c, int modifiers) {
    }

    public void onCloseScreen() {
        clearSnapLines();
        if (DynamicHUD.MC.screen instanceof AbstractMoveableScreen) {
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
        selectedWidget = null;
        clearSnapLines();

        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                if(widget.mouseReleased(mouseX, mouseY, button)){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public final boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY, int snapSize) {
        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            for (Widget widget : widgets) {
                // This essentially acts as a Z - layer where the widget first in the list is moved and dragged
                // if they are overlapped on each other.
                if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, snapSize)) {
                    selectedWidget = widget;
                    if(GlobalConfig.get().doSmartSnapping()) {
                        applySnappingAndGuides(selectedWidget);
                    }
                    return true;
                }
            }
            selectedWidget = null;
            clearSnapLines();
        }
        return false;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen instanceof AbstractMoveableScreen && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.isShiftDown = true;
            }
        }
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen instanceof AbstractMoveableScreen && (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.isShiftDown = false;
            }
        }
    }

    /**
     * Renders alignment and screen axis guidelines if snapping conditions are met.
     */
    private void drawSnapGuides(GuiGraphics graphics) {
        int screenWidth = DynamicHUD.MC.getWindow().getGuiScaledWidth();
        int screenHeight = DynamicHUD.MC.getWindow().getGuiScaledHeight();

        int screenCenterColor = new Color(255, 80, 80, 180).getRGB(); // Light red for screen axes
        int widgetSnapColor = new Color(0, 220, 255, 180).getRGB();   // Bright cyan for widget alignments

        // Screen Vertical Center guideline
        if (screenCenterX != -1) {
            DrawHelper.drawVerticalLine(graphics, screenCenterX, 0, screenHeight, 1.0f, screenCenterColor);
        }

        // Screen Horizontal Center guideline
        if (screenCenterY != -1) {
            DrawHelper.drawHorizontalLine(graphics, 0, screenWidth, screenCenterY, 1.0f, screenCenterColor);
        }

        // Neighboring Widget Vertical alignment guideline
        if (snapLineX != -1) {
            DrawHelper.drawVerticalLine(graphics, snapLineX, 0, screenHeight, 1.0f, widgetSnapColor);
        }

        // Neighboring Widget Horizontal alignment guideline
        if (snapLineY != -1) {
            DrawHelper.drawHorizontalLine(graphics, 0, screenWidth, snapLineY, 1.0f, widgetSnapColor);
        }
    }

    /**
     * Resets active alignment line parameters.
     */
    private void clearSnapLines() {
        snapLineX = -1;
        snapLineY = -1;
        screenCenterX = -1;
        screenCenterY = -1;
    }

    /**
     * Evaluates alignment thresholds and snaps the dragged widget to relevant lines or centers.
     */
    private void applySnappingAndGuides(Widget dragged) {
        clearSnapLines();

        if (dragged == null) return;

        int screenWidth = DynamicHUD.MC.getWindow().getGuiScaledWidth();
        int screenHeight = DynamicHUD.MC.getWindow().getGuiScaledHeight();
        float threshold = 4f; // Snap tolerance threshold in pixels

        float dw = dragged.getWidth();
        float dh = dragged.getHeight();

        float dl = dragged.getX();
        float dr = dl + dw;
        float dcx = dl + dw / 2.0f;

        float dt = dragged.getY();
        float db = dt + dh;
        float dcy = dt + dh / 2.0f;

        boolean snappedX = false;
        boolean snappedY = false;

        float screenMidX = screenWidth / 2.0f;
        float screenMidY = screenHeight / 2.0f;

        if (Math.abs(dcx - screenMidX) < threshold) {
            dragged.setPosition((int) (screenMidX - dw / 2.0f), dragged.getY());
            screenCenterX = screenMidX;
            snappedX = true;
            // Refresh coordinates
            dl = dragged.getX();
            dr = dl + dw;
            dcx = dl + dw / 2.0f;
        }

        if (Math.abs(dcy - screenMidY) < threshold) {
            dragged.setPosition(dragged.getX(), (int) (screenMidY - dh / 2.0f));
            screenCenterY = screenMidY;
            snappedY = true;
            // Refresh coordinates
            dt = dragged.getY();
            db = dt + dh;
            dcy = dt + dh / 2.0f;
        }

        for (Widget other : widgets) {
            if (other == dragged || !other.isVisible()) continue;

            float ow = other.getWidth();
            float oh = other.getHeight();

            float ol = other.getX();
            float or = ol + ow;
            float ocx = ol + ow / 2.0f;

            float ot = other.getY();
            float ob = ot + oh;
            float ocy = ot + oh / 2.0f;

            // X-Axis Snap Checks
            if (!snappedX) {
                if (Math.abs(dl - ol) < threshold) { // Left to Left
                    dragged.setPosition((int) ol, dragged.getY());
                    snapLineX = ol;
                    snappedX = true;
                } else if (Math.abs(dl - or) < threshold) { // Left to Right
                    dragged.setPosition((int) or, dragged.getY());
                    snapLineX = or;
                    snappedX = true;
                } else if (Math.abs(dr - ol) < threshold) { // Right to Left
                    dragged.setPosition((int) (ol - dw), dragged.getY());
                    snapLineX = ol;
                    snappedX = true;
                } else if (Math.abs(dr - or) < threshold) { // Right to Right
                    dragged.setPosition((int) (or - dw), dragged.getY());
                    snapLineX = or;
                    snappedX = true;
                } else if (Math.abs(dcx - ocx) < threshold) { // Center to Center
                    dragged.setPosition((int) (ocx - dw / 2.0f), dragged.getY());
                    snapLineX = ocx;
                    snappedX = true;
                }
            }

            // Y-Axis Snap Checks
            if (!snappedY) {
                if (Math.abs(dt - ot) < threshold) { // Top to Top
                    dragged.setPosition(dragged.getX(), (int) ot);
                    snapLineY = ot;
                    snappedY = true;
                } else if (Math.abs(dt - ob) < threshold) { // Top to Bottom
                    dragged.setPosition(dragged.getX(), (int) ob);
                    snapLineY = ob;
                    snappedY = true;
                } else if (Math.abs(db - ot) < threshold) { // Bottom to Top
                    dragged.setPosition(dragged.getX(), (int) (ot - dh));
                    snapLineY = ot;
                    snappedY = true;
                } else if (Math.abs(db - ob) < threshold) { // Bottom to Bottom
                    dragged.setPosition(dragged.getX(), (int) (ob - dh));
                    snapLineY = ob;
                    snappedY = true;
                } else if (Math.abs(dcy - ocy) < threshold) { // Center to Center
                    dragged.setPosition(dragged.getX(), (int) (ocy - dh / 2.0f));
                    snapLineY = ocy;
                    snappedY = true;
                }
            }

            if (snappedX && snappedY) break;
        }
    }



    //  public WidgetRenderer withZIndex(int z_Index) {
     //   this.Z_Index = z_Index;
   //     return this;
   // }
}
