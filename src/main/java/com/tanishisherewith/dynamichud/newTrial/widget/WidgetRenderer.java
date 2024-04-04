package com.tanishisherewith.dynamichud.newTrial.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WidgetRenderer {
    public final List<Class<? extends Screen>> allowedScreens = new CopyOnWriteArrayList<>();
    private boolean renderInGameHud = true;
    public boolean isInEditor = false;
    List<Widget> widgets;
    public WidgetRenderer(List<Widget> widgets){
        this.widgets = widgets;
    }
    public void addWidget(Widget widget){
        widgets.add(widget);
    }

    public void addScreen(Class<? extends Screen> screen){
        allowedScreens.add(screen);
    }

    public void shouldRenderInGameHud(boolean renderInGameHud) {
        this.renderInGameHud = renderInGameHud;
    }

    public void renderWidgets(DrawContext context) {
        if(WidgetManager.getWidgets().isEmpty()) return;

        Screen currentScreen = DynamicHUD.MC.currentScreen;

        if(currentScreen == null && renderInGameHud){
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context);
            }
            return;
        }
        if(currentScreen instanceof AbstractMoveableScreen){
            for (Widget widget : widgets) {
                widget.isInEditor = true;
                widget.renderInEditor(context);
            }
            return;
        }
        if (currentScreen != null && allowedScreens.contains(DynamicHUD.MC.currentScreen.getClass()) && !this.isInEditor) {
            for (Widget widget : widgets) {
                widget.isInEditor = false;
                widget.render(context);
            }
        }
    }
    public void mouseClicked(double mouseX, double mouseY, int button){
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if(currentScreen == null) {
            return;
        }
        if(currentScreen instanceof AbstractMoveableScreen){
            for (Widget widget : widgets) {
                widget.mouseClicked(mouseX,mouseY,button);
            }
        }
    }
    public void mouseDragged(double mouseX, double mouseY, int button){
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if(currentScreen == null) {
            return;
        }
        if(currentScreen instanceof AbstractMoveableScreen){
            for (Widget widget : widgets) {
                widget.mouseDragged(mouseX,mouseY,button);
            }
        }
    }

    public void keyPressed(int keyCode){
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if(currentScreen instanceof AbstractMoveableScreen && (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.shiftDown = true;
            }
        }
    }
    public void keyReleased(int keyCode){
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if(currentScreen instanceof AbstractMoveableScreen && (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT)) {
            for (Widget widget : widgets) {
                widget.shiftDown = false;
            }
        }
    }
    public void onCloseScreen(){
        if(DynamicHUD.MC.currentScreen instanceof AbstractMoveableScreen){
            for (Widget widget : widgets) {
                widget.shiftDown = false;
            }
        }
    }

    public List<Widget> getWidgets() {
        return widgets;
    }

    public void mouseReleased(double mouseX, double mouseY, int button){
        Screen currentScreen = DynamicHUD.MC.currentScreen;
        if(currentScreen == null) {
            return;
        }
        if(currentScreen instanceof AbstractMoveableScreen){
            for (Widget widget : widgets) {
                widget.mouseReleased(mouseX,mouseY,button);
            }
        }
    }
}
