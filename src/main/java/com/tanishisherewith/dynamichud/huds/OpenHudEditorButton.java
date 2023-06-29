package com.tanishisherewith.dynamichud.huds;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.interfaces.IWigdets;
import com.tanishisherewith.dynamichud.util.DynamicUtil;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;

import static com.tanishisherewith.dynamichud.DynamicHUD.WIDGETS_FILE;


public class OpenHudEditorButton extends ButtonWidget {
    public OpenHudEditorButton(int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text, button -> {},textSupplier ->  getNarrationMessage(Text.of("HudEditorButton")));
    }

    @Override
    public void onPress() {
        // Open the HUD editor screen
        MinecraftClient.getInstance().setScreen(DynamicHUD.getScreen());
    }
}
