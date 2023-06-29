package com.tanishisherewith.dynamichud.mixins;

import com.tanishisherewith.dynamichud.huds.OpenHudEditorButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class TitleScreenMixin {
    @Inject(method = "init", at = @At("TAIL"))
    protected void onInit(CallbackInfo info) {
        // Add the "Open HUD Editor" button to the main menu

        this.addDrawableChild(Text.literal(" s"),button -> MinecraftClient.getInstance().setScreen(new OpenHudEditorButton(width / 2 - 100, height / 4 + 48 + 24 * 3, 200, 20, "Open HUD Editor")));
    }
}
