package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.huds.MoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.newTrial.widgets.TextWidget;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.text.Text;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicHudTest implements DynamicHudIntegration {
    TextWidget textWidget;
    DynamicValueRegistry registry = new DynamicValueRegistry(DynamicHUD.MOD_ID);
    WidgetRenderer renderer;
    @Override
    public void init() {
        DynamicValueRegistry.registerGlobal("FPS",() -> "FPS: "+ DynamicHUD.MC.getCurrentFps());
        registry.registerLocal("FPS",()-> "FPS: "+ DynamicHUD.MC.getCurrentFps());

        textWidget  = new TextWidget.Builder()
                .setX(300)
                .setY(60)
                .setDraggable(true)
                .rainbow(false)
                .setDRKey("FPS")
                .setModID(DynamicHUD.MOD_ID)
                .shouldScale(false)
                .build();
    }

    @Override
    public void addWidgets() {
        WidgetManager.addWidget(textWidget);
    }

    @Override
    public AbstractMoveableScreen getMovableScreen() {
        return new MoveableScreen(Text.of("Editor"),new DynamicUtil(DynamicHUD.MC));
    }

    @Override
    public WidgetRenderer getWidgetRenderer() {
        // Get the widgets for this mod
        List<Widget> widgets = WidgetManager.getWidgetsForMod(DynamicHUD.MOD_ID);

        renderer = new WidgetRenderer(widgets);
        renderer.shouldRenderInGameHud(true);
        renderer.addScreen(TitleScreen.class);
        renderer.addScreen(MultiplayerScreen.class);
        return renderer;
    }

}
