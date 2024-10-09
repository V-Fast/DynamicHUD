package com.tanishisherewith.dynamichud;

import com.tanishisherewith.dynamichud.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetManager;
import com.tanishisherewith.dynamichud.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.widgets.TextWidget;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.text.Text;

import java.util.List;

public class DynamicHudTest implements DynamicHudIntegration {
    TextWidget FPSWidget;
    TextWidget HelloWidget;
    TextWidget DynamicHUDWidget;
    DynamicValueRegistry registry;
    WidgetRenderer renderer;

    @Override
    public void init() {
        //Global registry
        DynamicValueRegistry.registerGlobal("FPS", () -> "FPS: " + DynamicHUD.MC.getCurrentFps());

        //Local registry
        registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
        registry.registerLocal("Hello", () -> "Hello " + DynamicHUD.MC.getSession().getUsername() + "!");
        registry.registerLocal("DynamicHUD", () -> "DynamicHUD");


        FPSWidget = new TextWidget.Builder()
                .setX(250)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .setDRKey("FPS")
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();

        HelloWidget = new TextWidget.Builder()
                .setX(200)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .setDRKey("Hello")
                .setDVR(registry)
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .build();

        DynamicHUDWidget = new TextWidget.Builder()
                .setX(0)
                .setY(0)
                .setDraggable(false)
                .rainbow(true)
                .setDRKey("DynamicHUD")
                .setDVR(registry)
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .build();

    }

    @Override
    public void addWidgets() {
        WidgetManager.addWidget(FPSWidget);
        WidgetManager.addWidget(HelloWidget);
        WidgetManager.addWidget(DynamicHUDWidget);
    }

    @Override
    public void registerCustomWidgets() {
        //WidgetManager.addWidgetData(MyWidget.DATA);
    }

    public void initAfter() {
        List<Widget> widgets = WidgetManager.getWidgetsForMod(DynamicHUD.MOD_ID);

        renderer = new WidgetRenderer(widgets);
        renderer.shouldRenderInGameHud(true);

        //This will make widgets render in the titlescreen as well.
        renderer.addScreen(TitleScreen.class);
    }

    @Override
    public AbstractMoveableScreen getMovableScreen() {
        return new AbstractMoveableScreen(Text.literal("Editor Screen"), renderer) {};
    }

    @Override
    public WidgetRenderer getWidgetRenderer() {
        return renderer;
    }

}
