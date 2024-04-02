package com.tanishisherewith.dynamichud.newTrial.widgets;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;

import java.awt.*;
import java.util.function.Supplier;

public class TextWidget extends Widget {
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget","Display Text on screen",TextWidget::new);
    Supplier<String> textSupplier;
    String dynamicRegistryKey;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text

    public TextWidget() {
       this(null,false,false);
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#globalRegistry} using the given registryKey
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(String dynamicRegistryKey, boolean shadow, boolean rainbow) {
        super(DATA);
        this.dynamicRegistryKey = dynamicRegistryKey;
        textSupplier = (Supplier<String>) DynamicValueRegistry.getGlobal(dynamicRegistryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#localRegistry} using the given registryKey and registryValue
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(DynamicValueRegistry dynamicValueRegistry,String dynamicRegistryKey, boolean shadow, boolean rainbow) {
        super(DATA);
        this.dynamicRegistryKey = dynamicRegistryKey;
        textSupplier = (Supplier<String>) dynamicValueRegistry.get(dynamicRegistryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
    }

    @Override
    public void renderWidget(DrawContext drawContext) {
        drawContext.getMatrices().push();
        drawContext.getMatrices().translate(0, 0, 420);
        int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % 10000) / (1 * 400f)) : Color.WHITE.getRGB();
        if(textSupplier != null) {
            String text = textSupplier.get();
            drawContext.drawText(mc.textRenderer, text, (int) getX(), (int) getY(), color, shadow);
            widgetBox.setSize(getX(),getY(),mc.textRenderer.getWidth(text),mc.textRenderer.fontHeight);
        }
        drawContext.getMatrices().pop();
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("DRKey",dynamicRegistryKey);
        tag.putBoolean("shadow",shadow);
        tag.putBoolean("rainbow",rainbow);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        shadow = tag.getBoolean("shadow");
        rainbow = tag.getBoolean("rainbow");
        dynamicRegistryKey = tag.getString("DRKey");
    }
    public static class Builder extends WidgetBuilder<Builder,TextWidget> {

        protected boolean shadow = false;
        protected boolean rainbow = false;
        protected String dynamicRegistryKey = "";
        DynamicValueRegistry dynamicValueRegistry = null;

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return self();
        }

        public Builder rainbow(boolean rainbow) {
            this.rainbow = rainbow;
            return self();
        }
        public Builder setDHKey(String dynamicRegistryKey) {
            this.dynamicRegistryKey = dynamicRegistryKey;
            return self();
        }
        public Builder setDH(DynamicValueRegistry dynamicValueRegistry) {
            this.dynamicValueRegistry = dynamicValueRegistry;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public TextWidget build() {
            TextWidget widget;
            if(dynamicValueRegistry == null) {
                widget = new TextWidget(dynamicRegistryKey, shadow, rainbow);
            }else{
                widget = new TextWidget(dynamicValueRegistry,dynamicRegistryKey, shadow, rainbow);
            }
            widget.setxPercent(xPercent);
            widget.setyPercent(yPercent);
            widget.setDraggable(isDraggable);
            widget.setShouldScale(shouldScale);
            return widget;
        }
    }
}
