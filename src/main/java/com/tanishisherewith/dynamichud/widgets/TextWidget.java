package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.ClassicSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.ModernSkin;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import javax.swing.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TextWidget extends DynamicValueWidget implements ContextMenuProvider {
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget", "Display Text on screen", TextWidget::new);

    private ContextMenu<?> menu;
    public Color textColor;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text
    protected int rainbowSpeed = 2; //Speed of the rainbow effect
    protected float rainbowSpread = 0.01f, rainbowSaturation = 1.0f, rainbowBrightness = 1.0f;
    private String registryKey;
    //private DynamicValueRegistry valueRegistry;
    private String registryID;

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
        ContextMenuProperties properties = ContextMenuProperties.builder().skin(new ModernSkin(Color.YELLOW)).build();
        menu = new ContextMenu<>(getX(), getY(), properties);

        // Boolean Option
        menu.addOption(new BooleanOption(Text.of("Toggle Shadow"),
                () -> this.shadow, value -> this.shadow = value,
                BooleanOption.BooleanType.ON_OFF)
                .description(Text.of("Enable or disable text shadow")));

        // Color Option
        menu.addOption(new ColorOption(Text.of("Text Color"),
                () -> this.textColor, value -> this.textColor = value, menu)
                .description(Text.of("Change the text color")));

        // Double Option
        menu.addOption(new DoubleOption(Text.of("Opacity"),
                0.0, 1.0, 0.1f,
                () -> (double) this.rainbowBrightness, value -> this.rainbowBrightness = value.floatValue(), menu)
                .description(Text.of("Adjust text opacity")));

        // Runnable Option
        AtomicBoolean ran = new AtomicBoolean(false);
        menu.addOption(new RunnableOption(Text.of("Reset Position"),
                ran::get, ran::set,
                () -> this.setPosition(0, 0))
                .description(Text.of("Reset widget to default position")));

        // List Option
        AtomicReference<String> style = new AtomicReference<>("Style1");
        List<String> styles = Arrays.asList("Style1", "Style2", "Style3");
        menu.addOption(new ListOption<>(Text.of("Text Style"),
                style::get, style::set, styles)
                .description(Text.of("Choose a text style")));

        // Enum Option
        menu.addOption(new EnumOption<>(Text.of("Alignment"),
                () -> GroupLayout.Alignment.CENTER, value -> {}, GroupLayout.Alignment.values())
                .description(Text.of("Set text alignment")));

        // Option Group
        OptionGroup group = new OptionGroup(Text.of("Display Options"));
        group.addOption(new BooleanOption(Text.of("Bold Text"),
                () -> false, value -> {}, BooleanOption.BooleanType.YES_NO)
                .description(Text.of("Enable bold text")));
        group.addOption(new DoubleOption(Text.of("Font Size"),
                8.0, 24.0, 1.0f,
                () -> 12.0, value -> {}, menu)
                .description(Text.of("Adjust font size")));
        menu.addOption(group);

        // SubMenu Option
        SubMenuOption subMenu = (SubMenuOption) new SubMenuOption(Text.of("Advanced Settings"), menu)
                .description(Text.of("Open advanced settings"));
        subMenu.getSubMenu().addOption(new BooleanOption(Text.of("Some Boolean"),
                () -> false, value -> {}, BooleanOption.BooleanType.TRUE_FALSE)
                .description(Text.of("True/False")));
        menu.addOption(subMenu);
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
        shadow = tag.getBoolean("Shadow");
        rainbow = tag.getBoolean("Rainbow");
        rainbowSpeed = tag.getInt("RainbowSpeed");
        rainbowSpread = tag.getFloat("RainbowSpread");
        rainbowSaturation = tag.getFloat("RainbowSaturation");
        rainbowBrightness = tag.getFloat("RainbowBrightness");
        textColor = new Color(tag.getInt("TextColor"));
        registryKey = tag.getString("RegistryKey");
        registryID = tag.getString("RegistryID");

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
