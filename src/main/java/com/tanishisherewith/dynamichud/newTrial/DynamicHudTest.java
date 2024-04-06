package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.huds.MoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.newTrial.widgets.TextWidget;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;

import java.util.List;

public class DynamicHudTest implements DynamicHudIntegration {
    TextWidget textWidget;
    TextWidget Example2Widget;
    DynamicValueRegistry registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
    WidgetRenderer renderer;
    @Override
    public void init() {
        DynamicValueRegistry.registerGlobal("FPS",() -> "FPS: "+ DynamicHUD.MC.getCurrentFps());
        registry.registerLocal("FPS",()-> "FPS C-DVR: "+ DynamicHUD.MC.getCurrentFps());

        textWidget  = new TextWidget.Builder()
                .setX(300)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .setDRKey("FPS")
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();

        Example2Widget  = new TextWidget.Builder()
                .setX(200)
                .setY(100)
                .setDraggable(true)
                .rainbow(false)
                .setDRKey("FPS")
                .setDVR(registry)
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(true)
                .build();
    }

    @Override
    public void addWidgets() {
        WidgetManager.addWidget(textWidget);
        WidgetManager.addWidget(Example2Widget);
    }
    public void initAfter(){
        List<Widget> widgets = WidgetManager.getWidgetsForMod(DynamicHUD.MOD_ID);

        renderer = new WidgetRenderer(widgets);
        renderer.shouldRenderInGameHud(true);
        renderer.addScreen(TitleScreen.class);
        renderer.addScreen(MultiplayerScreen.class);
    }

    @Override
    public AbstractMoveableScreen getMovableScreen() {
        return new AbstractMoveableScreen(Text.literal("Editor"), renderer) {};
    }

    @Override
    public WidgetRenderer getWidgetRenderer() {
        return renderer;
    }

}
