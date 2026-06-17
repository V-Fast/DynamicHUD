package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.integration.IntegrationManager;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.ModernSkin;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;

import javax.swing.*;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class TextWidget extends DynamicValueWidget implements ContextMenuProvider {
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget", "Display Component on screen", TextWidget::new);

    private ContextMenu<?> menu;
    public Color textColor;
    protected boolean shadow; // Whether to draw a shadow behind the Component
    protected boolean rainbow; // Whether to apply a rainbow effect to the Component
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
        menu = new ContextMenu<>(getX(), getY(),ContextMenuProperties.builder().skin(new ModernSkin()).build());

    //    if(IntegrationManager.IS_TEST_MODE) menu.setLayoutEngine(new LayoutEngine(20,20,20,80));

        menu.addOption(new BooleanOption(Component.literal("Shadow"),
                () -> this.shadow, value -> this.shadow = value,
                BooleanOption.BooleanType.ON_OFF)
                .description(Component.literal("Adds shadow to your Component"))
        );
        menu.addOption(new BooleanOption(Component.literal("Rainbow"),
                () -> this.rainbow, value -> this.rainbow = value,
                BooleanOption.BooleanType.ON_OFF)
                .description(Component.literal("Adds rainbow effect to your Component"))
        );
        menu.addOption(new ColorOption(Component.literal("Component Color"),
                () -> this.textColor, value -> this.textColor = value, menu)
                .description(Component.literal("Specify the color you want to add to your Component"))
                .renderWhen(() -> !this.rainbow)
        );
        menu.addOption(new DoubleOption(Component.literal("Rainbow Speed"),
                1, 5, 1,
                () -> (double) this.rainbowSpeed, value -> this.rainbowSpeed = value.intValue(), menu)
                .renderWhen(() -> this.rainbow)
        );
        menu.addOption(new DoubleOption(Component.literal("Rainbow Spread"),
                0.001f, 0.15f, 0.001f,
                () -> (double) this.rainbowSpread, value -> this.rainbowSpread = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Enhanced)
        );
        menu.addOption(new DoubleOption(Component.literal("Rainbow Saturation"),
                0, 1.0f, 0.1f,
                () -> (double) this.rainbowSaturation, value -> this.rainbowSaturation = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Pro)
        );
        menu.addOption(new DoubleOption(Component.literal("Rainbow Brightness"),
                0, 1.0f, 0.01f,
                () -> (double) this.rainbowBrightness, value -> this.rainbowBrightness = value.floatValue(), menu)
                .renderWhen(() -> this.rainbow)
                .withComplexity(Option.Complexity.Pro)
        );
        if(IntegrationManager.IS_TEST_MODE) {
            // Runnable Option
            AtomicBoolean ran = new AtomicBoolean(false);
            menu.addOption(new RunnableOption(Component.literal("Reset Position"),
                    ran::get, ran::set,
                    () -> this.setPosition(0, 0))
                    .description(Component.literal("Reset widget to default position")));

            AtomicReference<String> style = new AtomicReference<>("Style1");
            AtomicReference<GroupLayout.Alignment> align = new AtomicReference<>(GroupLayout.Alignment.CENTER);

            // List Option
            List<String> styles = Arrays.asList("Style1", "Style2", "Style3");
            menu.addOption(new CycleOption<>(Component.literal("Text Style"), style::get, style::set, styles));

            // Enum Option
            menu.addOption(new CycleOption<>(Component.literal("Alignment"), align::get, align::set, GroupLayout.Alignment.values()));


            // Option Group
            OptionGroup group = new OptionGroup(Component.literal("Display Options"));
            group.addOption(new BooleanOption(Component.literal("Bold Text"),
                    () -> false, value -> {
            }, BooleanOption.BooleanType.YES_NO)
                    .description(Component.literal("Enable bold text")));
            group.addOption(new DoubleOption(Component.literal("Font Size"),
                    8.0, 24.0, 1.0f,
                    () -> 12.0, value -> {
            }, menu).description(Component.literal("Adjust font size")));

            menu.addOption(group);

            // SubMenu Option
            SubMenuOption subMenu = (SubMenuOption) new SubMenuOption(Component.literal("Advanced Settings"), menu)
                    .description(Component.literal("Open advanced settings"));
            subMenu.getSubMenu().addOption(new BooleanOption(Component.literal("Some Boolean"),
                    () -> false, value -> {
            }, BooleanOption.BooleanType.TRUE_FALSE)
                    .description(Component.literal("True/False")));
            menu.addOption(subMenu);
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY) {
        if (menu == null) return;
        //int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % (5000 * rainbowSpeed) / (5000f * rainbowSpeed))) : textColor.getRGB();
        int color = textColor.getRGB();
        if (valueSupplier != null) {
            String Component = getValue();
            if (rainbow) {
                DrawHelper.drawChromaText(graphics, Component, getX() + 2, getY() + 2, rainbowSpeed / 2f, rainbowSaturation, rainbowBrightness, rainbowSpread, shadow);
            } else {
                graphics.drawString(mc.font, Component, getX() + 2, getY() + 2, color, shadow);
            }
            widgetBox.setDimensions(getX(), getY(), mc.font.width(Component) + 3, mc.font.lineHeight + 2, this.shouldScale, DynamicHUD.getGlobalScale());
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
    public void writeToTag(CompoundTag tag) {
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
    public void readFromTag(CompoundTag tag) {
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
