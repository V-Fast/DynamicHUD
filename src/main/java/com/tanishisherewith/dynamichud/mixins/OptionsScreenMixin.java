package com.tanishisherewith.dynamichud.mixins;


import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.slider.ScaleSliderWidget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.option.OptionsScreen;
import net.minecraft.client.gui.widget.EmptyWidget;
import net.minecraft.client.gui.widget.GridWidget;
import net.minecraft.client.gui.widget.SimplePositioningWidget;
import net.minecraft.client.option.GameOptions;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.text.DecimalFormat;
@Mixin(OptionsScreen.class)
public class OptionsScreenMixin extends Screen {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");
    private final GameOptions settings = MinecraftClient.getInstance().options;


    protected OptionsScreenMixin(Text title) {
        super(title);
    }

    @Inject(at = @At("TAIL"), method = "init")
    public void addButtons(CallbackInfo ci) {
        int width = 100;
        int height = 20;
        int x = this.width - width - 10;
        int y = this.height - height- 5;
        String formattedValue = DECIMAL_FORMAT.format(Widget.getScale());
        ScaleSliderWidget scaleSlider = new ScaleSliderWidget(x, y, width, height, Text.of("Widgets Scale: " + formattedValue), Widget.getScale(), 0.5f, 3f);
        this.addDrawableChild(scaleSlider);
    }
}
