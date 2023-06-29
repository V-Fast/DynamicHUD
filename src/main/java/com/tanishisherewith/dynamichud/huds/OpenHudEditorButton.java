package com.tanishisherewith.dynamichud.huds;

import com.tanishisherewith.dynamichud.DynamicHUD;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

public class OpenHudEditorButton extends ButtonWidget {
    public OpenHudEditorButton(int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text, button -> {},null);
    }

    @Override
    public void onPress() {
        // Open the HUD editor screen
        MinecraftClient.getInstance().setScreen(DynamicHUD.getScreen());
    }
}
