package com.tanishisherewith.dynamichud.newTrial;

import com.tanishisherewith.dynamichud.newTrial.screens.AbstractMoveableScreen;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.Option;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetManager;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetRenderer;
import com.tanishisherewith.dynamichud.newTrial.widgets.TextWidget;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.util.List;

public class DynamicHudTestTWO implements DynamicHudIntegration {
    TextWidget textWidget;
    WidgetRenderer renderer;
    @Override
    public void init() {
        //Global registry
        DynamicValueRegistry.registerGlobal("CPS",() -> "NOT FPS");


        textWidget  = new TextWidget.Builder()
                .setX(150)
                .setY(100)
                .setDraggable(true)
                .rainbow(true)
                .setDRKey("CPS")
                .setModID("CustomMod")
                .shouldScale(true)
                .build();
    }

    @Override
    public KeyBinding getKeyBind() {
        return KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "DynamicHUD editor screenn",
                INPUT_TYPE,
                GLFW.GLFW_KEY_RIGHT_CONTROL,
                "CATEGORY 2"
        ));
    }

    @Override
    public File getWidgetsFile() {
        return new File(FILE_DIRECTORY,"widgets_new.nbt");
    }

    @Override
    public void addWidgets() {
        WidgetManager.addWidget(textWidget);
    }
    public void initAfter(){
        List<Widget> widgets = WidgetManager.getWidgetsForMod("CustomMod");

        renderer = new WidgetRenderer(widgets);
        renderer.shouldRenderInGameHud(true);
        renderer.addScreen(OptionsScreen.class);
        renderer.addScreen(TitleScreen.class);
    }

    @Override
    public AbstractMoveableScreen getMovableScreen() {
        return new AbstractMoveableScreen(Text.literal("Editor 2"), renderer) {};
    }

    @Override
    public WidgetRenderer getWidgetRenderer() {
        return renderer;
    }

}
