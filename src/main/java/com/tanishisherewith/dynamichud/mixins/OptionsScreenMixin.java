package com.tanishisherewith.dynamichud.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.tanishisherewith.dynamichud.config.GlobalConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.DirectionalLayoutWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Supplier;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    @Shadow protected abstract ButtonWidget createButton(Text message, Supplier<Screen> screenSupplier);

    protected OptionsScreenMixin(Text title) {
        super(title);
    }
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/widget/GridWidget$Adder;add(Lnet/minecraft/client/gui/widget/Widget;)Lnet/minecraft/client/gui/widget/Widget;", ordinal = 0))
    private void init(CallbackInfo ci, @Local(ordinal = 0) DirectionalLayoutWidget directionalLayoutWidget) {
        DirectionalLayoutWidget directionalLayoutWidget2 = directionalLayoutWidget.add(DirectionalLayoutWidget.horizontal());

        directionalLayoutWidget2.getMainPositioner().marginLeft(-26).marginY(-28);

        ButtonWidget widget = createButton(Text.of("DH"), () -> GlobalConfig.get().createYACLGUI());
        widget.setDimensions(20, 20);

        directionalLayoutWidget2.add(widget);
    }
}