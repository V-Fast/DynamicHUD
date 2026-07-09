package com.tanishisherewith.dynamichud.widget;

import com.mojang.blaze3d.platform.cursor.CursorType;
import com.mojang.blaze3d.platform.cursor.CursorTypes;
import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.internal.System;
import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.ContextMenuScreenRegistry;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class WidgetRenderer implements Input {
    private Predicate<Screen> allowedScreens;
    public boolean isInEditor = false;
    public Widget selectedWidget = null;
    List<Widget> widgets;
    private boolean renderInGameHud = true;
    //private int Z_Index = -1;

    private float snapLineX = -1;
    private float snapLineY = -1;
    private float screenCenterX = -1;
    private float screenCenterY = -1;

    private Widget hoveredWidget = null;
    private float hoverOverlayAlpha = 0f;
    private boolean scaleDragging = false;
    private float scaleDragStartY = 0;
    private float scaleDragStartScale = 1.0f;
    private Widget scaleDragWidget = null;

    private boolean isSelecting = false;
    private double selectionStartX = 0;
    private double selectionStartY = 0;
    private double selectionEndX = 0;
    private double selectionEndY = 0;
    private final List<Widget> selectedWidgets = new ArrayList<>();
    private boolean selectedViaDragWindow = false;

    private static final float OVERLAY_FADE_SPEED = 0.8f;

    public static final CursorType NSWE_CURSOR = CursorType.createStandardCursor(GLFW.GLFW_RESIZE_NWSE_CURSOR,"nwse",CursorType.DEFAULT);

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

    public void renderWidgets(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (WidgetManager.getWidgets().isEmpty() || !renderInDebugScreen()) return;

        Screen currentScreen = DynamicHUD.MC.screen;

        if (currentScreen instanceof AbstractMoveableScreen) {
            Widget currentlyHovered = null;
            for (Widget widget : widgets) {
                widget.isInEditor = true;
                widget.renderInEditor(graphics, mouseX, mouseY);
                if (widget.isMouseOverWidget(mouseX, mouseY)) {
                    currentlyHovered = widget;
                }
            }

            if (currentlyHovered != hoveredWidget) {
                hoveredWidget = currentlyHovered;
                if (hoveredWidget == null) {
                    hoverOverlayAlpha = 0f;
                }
            }

            // Draw dashed outlines for all selected widgets
            int selectColor = GlobalConfig.get().getDashedOutlineColor().getRGB();
            float thickness = GlobalConfig.get().getDashedOutlineThickness();
            for (Widget widget : selectedWidgets) {
                WidgetBox box = widget.getWidgetBox();
                DrawHelper.drawDashedOutlineBox(graphics, box.x - 1, box.y - 1,
                        box.getWidth() + 2, box.getHeight() + 2,
                        thickness, 4, 3, selectColor);
            }

            // Draw outlines for all groups
            for (WidgetGroup group : WidgetManager.getGroups().values()) {
                if (group.getMembers().size() > 1) {
                    float minX = Float.MAX_VALUE;
                    float minY = Float.MAX_VALUE;
                    float maxX = -Float.MAX_VALUE;
                    float maxY = -Float.MAX_VALUE;
                    for (Widget member : group.getMembers()) {
                        WidgetBox box = member.getWidgetBox();
                        if (box.x < minX) minX = box.x;
                        if (box.y < minY) minY = box.y;
                        if (box.x + box.getWidth() > maxX) maxX = box.x + box.getWidth();
                        if (box.y + box.getHeight() > maxY) maxY = box.y + box.getHeight();
                    }
                    if (minX != Float.MAX_VALUE) {
                        boolean isHovered = (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY);
                        if (isHovered) {
                            int oliveColor = new Color(107, 142, 35, 180).getRGB();
                            DrawHelper.drawDashedOutlineBox(graphics, minX - 3, minY - 3,
                                    (maxX - minX) + 6, (maxY - minY) + 6,
                                    1.5f, 6, 4, oliveColor);
                        }
                    }
                }
            }

            // Draw outlines for selection group if selected via drag window
            if (selectedViaDragWindow && selectedWidgets.size() > 1) {
                float minX = Float.MAX_VALUE;
                float minY = Float.MAX_VALUE;
                float maxX = -Float.MAX_VALUE;
                float maxY = -Float.MAX_VALUE;
                for (Widget member : selectedWidgets) {
                    WidgetBox box = member.getWidgetBox();
                    if (box.x < minX) minX = box.x;
                    if (box.y < minY) minY = box.y;
                    if (box.x + box.getWidth() > maxX) maxX = box.x + box.getWidth();
                    if (box.y + box.getHeight() > maxY) maxY = box.y + box.getHeight();
                }
                if (minX != Float.MAX_VALUE) {
                    boolean isHovered = (mouseX >= minX && mouseX <= maxX && mouseY >= minY && mouseY <= maxY);
                    if (isHovered) {
                        int oliveColor = new Color(107, 142, 35, 180).getRGB();
                        DrawHelper.drawDashedOutlineBox(graphics, minX - 3, minY - 3,
                                (maxX - minX) + 6, (maxY - minY) + 6,
                                1.5f, 6, 4, oliveColor);
                    }
                }
            }

            // Draw editor overlay (lock and scale dot) after outlines so they draw on top of dashed borders
            if (hoveredWidget != null) {
                hoverOverlayAlpha = Math.min(1.0f, hoverOverlayAlpha + OVERLAY_FADE_SPEED);
                renderEditorOverlay(graphics, hoveredWidget, mouseX, mouseY, hoverOverlayAlpha);
            } else {
                hoverOverlayAlpha = Math.max(0f, hoverOverlayAlpha - OVERLAY_FADE_SPEED);
            }

            // Draw drag selection box
            if (isSelecting) {
                float x1 = (float) Math.min(selectionStartX, selectionEndX);
                float y1 = (float) Math.min(selectionStartY, selectionEndY);
                float x2 = (float) Math.max(selectionStartX, selectionEndX);
                float y2 = (float) Math.max(selectionStartY, selectionEndY);

                int fillColor = new Color(0, 120, 255, 50).getRGB();
                int borderColor = new Color(0, 120, 255, 200).getRGB();
                DrawHelper.drawRectangle(graphics, x1, y1, x2 - x1, y2 - y1, fillColor);
                DrawHelper.drawOutlineBox(graphics, x1, y1, x2 - x1, y2 - y1, 1.0f, borderColor);
            }

            // Update GLFW Mouse cursor on scale hover
            boolean requestCursor = shouldChangeCursor(mouseX, mouseY);
            if(requestCursor) graphics.requestCursor(NSWE_CURSOR);

            if(GlobalConfig.get().doSmartSnapping()) {
                drawSnapGuides(graphics);
            }
            return;
        }
        if ((currentScreen == null && renderInGameHud) || allowedScreens.test(currentScreen)) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                if (!widget.isVisible()) continue;
                widget.render(graphics, 0, 0);
            }
        }
    }

    private boolean shouldChangeCursor(int mouseX, int mouseY) {
        boolean hoveringAnyScaleDot = false;
        for (Widget widget : widgets) {
            if (widget.canScale && widget.isMouseOverWidget(mouseX, mouseY)) {
                WidgetBox box = widget.getWidgetBox();
                int dotSize = GlobalConfig.get().getScaleDotSize();
                float dotX = box.x + box.getWidth() - dotSize / 2f;
                float dotY = box.y + box.getHeight() - dotSize / 2f;
                if (mouseX >= dotX - 2 && mouseX <= dotX + dotSize
                        && mouseY >= dotY - 2 && mouseY <= dotY + dotSize) {
                    hoveringAnyScaleDot = true;
                    break;
                }
            }
        }

        return hoveringAnyScaleDot || scaleDragging;
    }

    private void renderEditorOverlay(GuiGraphicsExtractor graphics, Widget widget, int mouseX, int mouseY, float alpha) {
        WidgetBox box = widget.getWidgetBox();
        Color outlineCol = GlobalConfig.get().getDashedOutlineColor();
        int dashColor = new Color(outlineCol.getRed(), outlineCol.getGreen(), outlineCol.getBlue(), (int)(255 * alpha)).getRGB();
        float thickness = GlobalConfig.get().getDashedOutlineThickness();

        DrawHelper.drawDashedOutlineBox(graphics, box.x - 1, box.y - 1,
                box.getWidth() + 2, box.getHeight() + 2,
                thickness, 4, 3, dashColor);

        int lockSize = GlobalConfig.get().getLockButtonSize();
        if (GlobalConfig.get().showLockButton() && box.getWidth() >= lockSize * 3 && box.getHeight() >= lockSize * 2) {
            renderLockIcon(graphics, widget, mouseX, mouseY, alpha);
        }

        if (widget.canScale) {
            renderScaleDot(graphics, widget, mouseX, mouseY, alpha);
        }
    }

    private void renderLockIcon(GuiGraphicsExtractor graphics, Widget widget, int mouseX, int mouseY, float alpha) {
        WidgetBox box = widget.getWidgetBox();
        int lockSize = GlobalConfig.get().getLockButtonSize();
        float iconX = Math.clamp(box.x + box.getWidth() - lockSize - 3, box.x + 2, box.x + box.getWidth() - lockSize - 2);
        float iconY = Math.clamp(box.y + 3, box.y + 2, box.y + box.getHeight() - lockSize - 2);

        boolean canToggle = widget.canToggleLock();
        boolean locked = widget.isLocked();
        boolean hoveringIcon = mouseX >= iconX - 2 && mouseX <= iconX + lockSize + 2
                && mouseY >= iconY - 2 && mouseY <= iconY + lockSize + 2;

        Color bgColor;
        if (!canToggle) {
            bgColor = new Color(80, 80, 80, (int)(200 * alpha));
        } else if (hoveringIcon) {
            bgColor = locked
                    ? new Color(220, 60, 60, (int)(255 * alpha))
                    : new Color(60, 220, 60, (int)(255 * alpha));
        } else {
            bgColor = locked
                    ? new Color(180, 50, 50, (int)(255 * alpha))
                    : new Color(50, 180, 50, (int)(255 * alpha));
        }

        DrawHelper.drawRoundedRectangle(graphics, iconX, iconY, lockSize, lockSize, 2, bgColor.getRGB());

        String icon = locked ? "\uD83D\uDD12" : "\uD83D\uDD13";
        int textColor = new Color(255, 255, 255, (int)(255 * alpha)).getRGB();
        DrawHelper.scaleAndPosition(graphics.pose(), iconX + lockSize / 2f, iconY + lockSize / 2f, 0.5f);
        graphics.text(DynamicHUD.MC.font, icon,
                Math.round(iconX + lockSize / 2f - DynamicHUD.MC.font.width(icon) / 2f),
                Math.round(iconY + lockSize / 2f - DynamicHUD.MC.font.lineHeight / 2f),
                textColor, false);
        DrawHelper.stopScaling(graphics.pose());
    }

    private void renderScaleDot(GuiGraphicsExtractor graphics, Widget widget, int mouseX, int mouseY, float alpha) {
        if (!widget.canScale) return;

        WidgetBox box = widget.getWidgetBox();
        int dotSize = GlobalConfig.get().getScaleDotSize();
        float dotX = box.x + box.getWidth() - dotSize / 2f;
        float dotY = box.y + box.getHeight() - dotSize / 2f;

        boolean hoveringDot = mouseX >= dotX - 2 && mouseX <= dotX + dotSize
                && mouseY >= dotY - 2 && mouseY <= dotY + dotSize;

        Color dotColor = hoveringDot
                ? new Color(100, 180, 255, (int)(255 * alpha))
                : new Color(180, 180, 180, (int)(255 * alpha));

        DrawHelper.drawFilledCircle(graphics, dotX + dotSize / 2f, dotY + dotSize / 2f,
                dotSize / 2f, dotColor.getRGB());
    }

    public boolean handleLockIconClick(Widget widget, double mouseX, double mouseY) {
        if (!widget.canToggleLock()) return false;
        WidgetBox box = widget.getWidgetBox();
        int lockSize = GlobalConfig.get().getLockButtonSize();
        if (box.getWidth() < lockSize * 3 || box.getHeight() < lockSize * 2) return false;
        float iconX = Math.clamp(box.x + box.getWidth() - lockSize - 3, box.x + 2, box.x + box.getWidth() - lockSize - 2);
        float iconY = Math.clamp(box.y + 3, box.y + 2, box.y + box.getHeight() - lockSize - 2);
        if (mouseX >= iconX - 2 && mouseX <= iconX + lockSize + 2
                && mouseY >= iconY - 2 && mouseY <= iconY + lockSize + 2) {
            widget.setLocked(!widget.isLocked());
            return true;
        }
        return false;
    }

    public boolean handleScaleDotPress(Widget widget, double mouseX, double mouseY) {
        if (!widget.canScale) return false;
        WidgetBox box = widget.getWidgetBox();
        int dotSize = GlobalConfig.get().getScaleDotSize();
        float dotX = box.x + box.getWidth() - dotSize / 2f;
        float dotY = box.y + box.getHeight() - dotSize / 2f;
        if (mouseX >= dotX - 2 && mouseX <= dotX + dotSize
                && mouseY >= dotY - 2 && mouseY <= dotY + dotSize) {
            scaleDragging = true;
            scaleDragStartY = (float) mouseY;
            scaleDragStartScale = widget.getWidgetBox().getScale();
            scaleDragWidget = widget;
            return true;
        }
        return false;
    }

    public boolean handleScaleDotDrag(double mouseX, double mouseY) {
        if (!scaleDragging || scaleDragWidget == null) return false;
        float baseHeight = scaleDragWidget.getWidgetBox().getHeight() / scaleDragWidget.getWidgetBox().getScale();
        if (baseHeight <= 0) return false;

        float newScale = (float)(mouseY - scaleDragWidget.getY()) / baseHeight;
        float sensitivity = GlobalConfig.get().getScaleSensitivity();
        if (sensitivity != 1.0f) {
            newScale = scaleDragStartScale + (newScale - scaleDragStartScale) * sensitivity;
        }

        newScale = Math.clamp(newScale, scaleDragWidget.getMinScale(), scaleDragWidget.getMaxScale());
        scaleDragWidget.getWidgetBox().setScale(newScale);
        scaleDragWidget.clampPosition();
        return true;
    }

    public void handleScaleDotRelease() {
        scaleDragging = false;
        scaleDragWidget = null;
    }

    public boolean isScaleDragging() {
        return scaleDragging;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen == null) {
            return false;
        }
        if (currentScreen instanceof AbstractMoveableScreen) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (hoveredWidget != null) {
                    if (handleLockIconClick(hoveredWidget, mouseX, mouseY)) {
                        return true;
                    }
                    if (handleScaleDotPress(hoveredWidget, mouseX, mouseY)) {
                        return true;
                    }
                }
            }

            Widget clickedWidget = null;
            for (Widget widget : widgets) {
                if (widget.mouseClicked(mouseX, mouseY, button)) {
                    selectedWidget = widget;
                    clickedWidget = widget;
                    break;
                }
            }

            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                if (clickedWidget != null) {
                    long handle = DynamicHUD.MC.getWindow().handle();
                    boolean ctrlDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                                       GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
                    if (ctrlDown) {
                        selectedViaDragWindow = false;
                        if (selectedWidgets.contains(clickedWidget)) {
                            selectedWidgets.remove(clickedWidget);
                        } else {
                            selectedWidgets.add(clickedWidget);
                        }
                    } else {
                        if (!selectedWidgets.contains(clickedWidget)) {
                            selectedWidgets.clear();
                            selectedWidgets.add(clickedWidget);
                            selectedViaDragWindow = false;
                        }
                    }
                    return true;
                } else if (hoveredWidget == null) {
                    if (GlobalConfig.get().isDragSelectionEnabled()) {
                        isSelecting = true;
                        selectedViaDragWindow = true;
                        selectionStartX = mouseX;
                        selectionStartY = mouseY;
                        selectionEndX = mouseX;
                        selectionEndY = mouseY;
                        selectedWidgets.clear();
                        return true;
                    }
                }
            }
            if (clickedWidget == null) {
                selectedWidgets.clear();
                selectedViaDragWindow = false;
            }
            selectedWidget = clickedWidget;
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
        selectedViaDragWindow = false;
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
        if (isSelecting) {
            isSelecting = false;
            return true;
        }
        selectedWidget = null;
        clearSnapLines();
        handleScaleDotRelease();

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
            if (isSelecting) {
                selectionEndX = mouseX;
                selectionEndY = mouseY;
                updateSelection();
                return true;
            }
            if (isScaleDragging()) {
                handleScaleDotDrag(mouseX, mouseY);
                return true;
            }
            for (Widget widget : widgets) {
                if (widget.dragging) {
                    int prevX = widget.getX();
                    int prevY = widget.getY();
                    if (widget.mouseDragged(mouseX, mouseY, button, deltaX, deltaY, snapSize)) {
                        selectedWidget = widget;
                        int shiftX = widget.getX() - prevX;
                        int shiftY = widget.getY() - prevY;
                        if (shiftX != 0 || shiftY != 0) {
                            if (selectedWidgets.contains(widget)) {
                                for (Widget selected : selectedWidgets) {
                                    if (selected != widget && !selected.isLocked()) {
                                        selected.setPosition(selected.getX() + shiftX, selected.getY() + shiftY);
                                        selected.clampPosition();
                                    }
                                }
                            }
                        }
                        if(GlobalConfig.get().doSmartSnapping()) {
                            applySnappingAndGuides(selectedWidget);
                        }
                        return true;
                    }
                }
            }
            for (Widget widget : widgets) {
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

    private void updateSelection() {
        selectedWidgets.clear();
        double x1 = Math.min(selectionStartX, selectionEndX);
        double y1 = Math.min(selectionStartY, selectionEndY);
        double x2 = Math.max(selectionStartX, selectionEndX);
        double y2 = Math.max(selectionStartY, selectionEndY);

        for (Widget widget : widgets) {
            WidgetBox box = widget.getWidgetBox();
            if (box.x < x2 && box.x + box.getWidth() > x1 &&
                box.y < y2 && box.y + box.getHeight() > y1) {
                selectedWidgets.add(widget);
            }
        }
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        Screen currentScreen = DynamicHUD.MC.screen;
        if (currentScreen instanceof AbstractMoveableScreen) {
            if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) {
                for (Widget widget : widgets) {
                    widget.isShiftDown = true;
                }
            }
            long handle = DynamicHUD.MC.getWindow().handle();
            boolean isCtrlDown = GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_LEFT_CONTROL) == GLFW.GLFW_PRESS ||
                                 GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_RIGHT_CONTROL) == GLFW.GLFW_PRESS;
            if (key == GLFW.GLFW_KEY_G && isCtrlDown) {
                boolean anyInGroup = false;
                for (Widget widget : selectedWidgets) {
                    if (widget.getGroup() != null) {
                        anyInGroup = true;
                        break;
                    }
                }

                if (anyInGroup) {
                    for (Widget widget : selectedWidgets) {
                        if (widget.getGroup() != null) {
                            widget.getGroup().removeMember(widget);
                        }
                    }
                } else if (selectedWidgets.size() > 1) {
                    String name = "Group " + (WidgetManager.getGroups().size() + 1);
                    WidgetGroup group = new WidgetGroup(name);
                    WidgetManager.getGroups().put(group.getId(), group);
                    for (Widget widget : selectedWidgets) {
                        group.addMember(widget);
                    }
                }
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
    private void drawSnapGuides(GuiGraphicsExtractor graphics) {
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

        int preSnapX = dragged.getX();
        int preSnapY = dragged.getY();

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

        int postSnapX = dragged.getX();
        int postSnapY = dragged.getY();
        int snapDeltaX = postSnapX - preSnapX;
        int snapDeltaY = postSnapY - preSnapY;
        if (snapDeltaX != 0 || snapDeltaY != 0) {
            if (dragged.getGroup() != null) {
                for (Widget member : dragged.getGroup().getMembers()) {
                    if (member != dragged && !member.isLocked()) {
                        member.setPosition(member.getX() + snapDeltaX, member.getY() + snapDeltaY);
                    }
                }
            }
            if (selectedWidgets.contains(dragged)) {
                for (Widget selected : selectedWidgets) {
                    if (selected != dragged && !selected.isLocked()) {
                        selected.setPosition(selected.getX() + snapDeltaX, selected.getY() + snapDeltaY);
                    }
                }
            }
        }
    }



    //  public WidgetRenderer withZIndex(int z_Index) {
     //   this.Z_Index = z_Index;
   //     return this;
   // }
}
