package com.tanishisherewith.dynamichud.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LayoutSettings;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow
    protected abstract Button openScreenButton(Component component, Supplier<Screen> supplier);

    @Shadow
    @Final
    private HeaderAndFooterLayout layout;

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 1 // this is where the FOV row is added
            )
    )
    private void injectDHLayout(CallbackInfo ci, @Local(ordinal = 0) LinearLayout header) {
        LinearLayout dhLayout = header.addChild(LinearLayout.vertical(), layoutSettings -> layoutSettings.paddingLeft(-20));

        Button dhButton = openScreenButton(Component.literal("DH"), () -> GlobalConfig.get().createYACLGUI());
        dhButton.setWidth(20);

        dhLayout.addChild(dhButton);
    }
}
