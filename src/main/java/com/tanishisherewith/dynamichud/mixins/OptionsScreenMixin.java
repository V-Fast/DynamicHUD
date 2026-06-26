package com.tanishisherewith.dynamichud.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.gui.screens.options.OptionsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow
    protected abstract Button openScreenButton(Component component, Supplier<Screen> supplier);

    protected OptionsScreenMixin(Component title) {
        super(title);
    }

    @Unique
    private LinearLayout header;

    @Unique
    private Button dhButton;

    @Inject(
            method = "init",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/layouts/LinearLayout;addChild(Lnet/minecraft/client/gui/layouts/LayoutElement;)Lnet/minecraft/client/gui/layouts/LayoutElement;",
                    ordinal = 1, // this is where the FOV row is added
                    shift = At.Shift.AFTER
            )
    )
    private void injectDHLayout(CallbackInfo ci, @Local(ordinal = 0) LinearLayout header) {
        this.header = header;
        dhButton = openScreenButton(Component.literal("DH"), () -> GlobalConfig.get().createYACLGUI());
        dhButton.setPosition(this.width / 2 - 150 - 24 - 8, header.getY() + header.getHeight() - 20);
        dhButton.setSize(20,20);
        this.addRenderableWidget(dhButton);
    }


    @Inject(
            method = "repositionElements",
            at = @At(value = "TAIL")
    )
    private void repositionElements(CallbackInfo ci) {
        if(dhButton != null && header != null) {
            dhButton.setPosition(this.width / 2 - 150 - 24 - 8, header.getY() + header.getHeight() - 20);
        }
    }
}
