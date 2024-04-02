package com.tanishisherewith.dynamichud.newTrial.widget;

import com.tanishisherewith.dynamichud.DynamicHUD;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WidgetRenderer {
    public final List<Class<? extends Screen>> allowedScreens = new CopyOnWriteArrayList<>();
    private boolean renderInGameHud = true;
    List<Widget> widgets;
    public WidgetRenderer(List<Widget> widgets){
        this.widgets = widgets;
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
                widget.render(context);
            }
            return;
        }
        if (currentScreen!= null && allowedScreens.contains(DynamicHUD.MC.currentScreen.getClass())) {
            for (Widget widget : widgets) {
               // System.out.println("Rendering Widget: " + widget);
                widget.render(context);
            }
        }
    }
}
