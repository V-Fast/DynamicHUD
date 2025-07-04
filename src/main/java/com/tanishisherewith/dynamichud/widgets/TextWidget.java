package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.awt.Color;

public class TextWidget extends DynamicValueWidget implements ContextMenuProvider {
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget", "Display Text on screen", TextWidget::new);

    private ContextMenu<?> menu;
    public Color textColor;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text
    protected int rainbowSpeed = 2; //Speed of the rainbow effect
    protected float rainbowSpread = 0.01f, rainbowSaturation = 1.0f, rainbowBrightness = 1.0f;

    public TextWidget() {
        this(DynamicValueRegistry.GLOBAL_ID, "unknown", false, false, Color.WHITE, "unknown");
    }

    public TextWidget(DynamicValueRegistry valueRegistry, String registryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        this(valueRegistry.getId(), registryKey, shadow, rainbow, color, modID);
    }

    public TextWidget(String registryID, String registryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        super(DATA, modID, registryID, registryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
        this.textColor = color;
        createMenu();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    public void createMenu() {
        menu = new ContextMenu<>(getX(), getY(),ContextMenuProperties.createGenericSimplified());

        menu.addOption(new BooleanOption(Text.of("Shadow"),
                () -> this.shadow, value -> this.shadow = value,
                BooleanOption.BooleanType.ON_OFF)
                .description(Text.of("Adds shadow to your text"))
        );
        menu.addOption(new BooleanOption(Text.of("Rainbow"),
                () -> this.rainbow, value -> this.rainbow = value,
                BooleanOption.BooleanType.ON_OFF)
                .description(Text.of("Adds rainbow effect to your text"))
        );
        menu.addOption(new ColorOption(Text.of("Text Color"),
                () -> this.textColor, value -> this.textColor = value, menu)
                .description(Text.of("Specify the color you want to add to your text"))
                .renderWhen(() -> !this.rainbow)
        );
        menu.addOption(new DoubleOption(Text.of("Rainbow Speed"),
                1, 5.0f, 1,
                () -> (double) this.rainbowSpeed, value -> this.rainbowSpeed = value.intValue(), menu)
                .renderWhen(() -> this.rainbow)
        );
        menu.addOption(new DoubleOption(Text.of("Rainbow Spread"),
                0.001f, 0.15f, 0.001f,
                () -> (double) this.rainbowSpread, value -> this.rainbowSpread = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Enhanced)
        );
        menu.addOption(new DoubleOption(Text.of("Rainbow Saturation"),
                0, 1.0f, 0.1f,
                () -> (double) this.rainbowSaturation, value -> this.rainbowSaturation = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Pro)
        );
        menu.addOption(new DoubleOption(Text.of("Rainbow Brightness"),
                0, 1.0f, 0.01f,
                () -> (double) this.rainbowBrightness, value -> this.rainbowBrightness = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Pro)
        );
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY) {
        if (menu == null) return;
        //int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % (5000 * rainbowSpeed) / (5000f * rainbowSpeed))) : textColor.getRGB();
        int color = textColor.getRGB();
        if (valueSupplier != null) {
            String text = getValue();
            if (rainbow) {
                DrawHelper.drawChromaText(drawContext, text, getX() + 2, getY() + 2, rainbowSpeed / 2f, rainbowSaturation, rainbowBrightness, rainbowSpread, shadow);
            } else {
                drawContext.drawText(mc.textRenderer, text, getX() + 2, getY() + 2, color, shadow);
            }
            drawContext.draw();
            widgetBox.setDimensions(getX(), getY(), mc.textRenderer.getWidth(text) + 3, mc.textRenderer.fontHeight + 2, this.shouldScale, GlobalConfig.get().getScale());
        }
        menu.set(getX(), getY(), (int) Math.ceil(getHeight()));

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        menu.toggleDisplay(widgetBox, mouseX, mouseY, button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void onClose() {
        super.onClose();
        menu.close();
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putBoolean("Shadow", shadow);
        tag.putBoolean("Rainbow", rainbow);
        tag.putInt("TextColor", textColor.getRGB());
        tag.putInt("RainbowSpeed", rainbowSpeed);
        tag.putFloat("RainbowSpread", rainbowSpread);
        tag.putFloat("RainbowSaturation", rainbowSaturation);
        tag.putFloat("RainbowBrightness", rainbowBrightness);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        shadow = tag.getBoolean("Shadow").orElse(false);
        rainbow = tag.getBoolean("Rainbow").orElse(false);
        rainbowSpeed = tag.getInt("RainbowSpeed").orElse(1);
        rainbowSpread = tag.getFloat("RainbowSpread").orElse(1.0f);
        rainbowSaturation = tag.getFloat("RainbowSaturation").orElse(1.0f);
        rainbowBrightness = tag.getFloat("RainbowBrightness").orElse(1.0f);
        textColor = new Color(tag.getInt("TextColor").orElse(0xFFFFFFFF), true); // default white
        registryKey = tag.getString("RegistryKey").orElse("default:key");
        registryID = tag.getString("RegistryID").orElse("default:id");

        //createMenu();
    }

    @Override
    public String getValue() {
        return (String) valueSupplier.get();
    }

    @Override
    public ContextMenu<?> getContextMenu() {
        return menu;
    }


    public static class Builder extends DynamicValueWidgetBuilder<Builder, TextWidget> {
        private boolean shadow = false;
        private boolean rainbow = false;
        private Color textColor = Color.WHITE;

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return this;
        }

        public Builder rainbow(boolean rainbow) {
            this.rainbow = rainbow;
            return this;
        }

        public Builder textColor(Color textColor) {
            this.textColor = textColor;
            return this;
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public TextWidget build() {
            TextWidget widget = new TextWidget(registryID, registryKey, shadow, rainbow, textColor, modID);

            widget.setPosition(x, y);
            widget.setDraggable(isDraggable);
            widget.setShouldScale(shouldScale);
            return widget;
        }
    }
}
