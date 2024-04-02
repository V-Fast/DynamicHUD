package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.DynamicHudIntegration;
import com.tanishisherewith.dynamichud.huds.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.huds.MoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
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

public class DynamicHudTest implements DynamicHudIntegration {
    TextWidget textWidget;

    DynamicValueRegistry registry = new DynamicValueRegistry();
    WidgetRenderer renderer;
    @Override
    public void init() {
        DynamicValueRegistry.registerGlobal("FPS",() -> String.valueOf(DynamicHUD.MC.getCurrentFps()));
        registry.registerLocal("FPS",()->String.valueOf(DynamicHUD.MC.getCurrentFps()));
    }

    @Override
    public void addWidgets() {
        textWidget  = new TextWidget.Builder()
                .setX(0.6f)
                .setY(0.6f)
                .setDraggable(true)
                .shouldScale(false)
                .rainbow(false)
                .setDHKey("FPS")
                .build();


        WidgetManager.addWidget(textWidget);
    }

    @Override
    public AbstractMoveableScreen getMovableScreen() {
        return new MoveableScreen(Text.of("Editor LOL"),new DynamicUtil(DynamicHUD.MC));
    }

    @Override
    public WidgetRenderer getWidgetRenderer() {
        renderer = new WidgetRenderer(Collections.singletonList(textWidget));
        renderer.shouldRenderInGameHud(true);
        renderer.addScreen(TitleScreen.class);
        renderer.addScreen(MultiplayerScreen.class);
        return renderer;
    }

}
