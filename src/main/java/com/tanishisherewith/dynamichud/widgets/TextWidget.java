package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.*;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin;
import com.tanishisherewith.dynamichud.widget.DynamicValueWidget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.awt.*;

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
        this(DynamicValueRegistry.GLOBAL_ID,"unknown", false, false, Color.WHITE, "unknown");
    }

    public TextWidget(DynamicValueRegistry valueRegistry, String registryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        this(valueRegistry.getId(),registryKey,shadow,rainbow,color,modID);
    }

    public TextWidget(String registryID, String registryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        super(DATA, modID,registryID,registryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
        this.textColor = color;
        createMenu();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    public void createMenu() {
        boolean dark_mode = false;
        ContextMenuProperties properties = ContextMenuProperties.builder().skin(new MinecraftSkin(dark_mode ? MinecraftSkin.PanelColor.DARK_PANEL : MinecraftSkin.PanelColor.CREAMY)).build();
        menu = new ContextMenu<>(getX(), getY(), properties);

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
                .renderWhen(()-> !this.rainbow)
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

        /*
        OptionGroup group = new OptionGroup("Color");
        group.addOption(new ColorOption(Text.of("TextColor"), menu,
                () -> this.textColor, value -> this.textColor = value)
                .description(Text.of("Specify the color you want to add to your text"))
        );
        group.addOption(new DoubleOption(
                Text.of("RainbowSpeed"),
                1, 5.0f, 1,
                () -> (double) this.rainbowSpeed, value -> this.rainbowSpeed = value.intValue(), menu)
                .setShouldRender(() -> this.rainbow)
        );
        AtomicReference<String> option = new AtomicReference<>("Enum1");
        List<String> options = Arrays.asList("List1", "LONGER LIST 2", "List3");
        AtomicBoolean running = new AtomicBoolean(false);
        AtomicBoolean subMenu = new AtomicBoolean(false);
        menu.addOption(new ListOption<>(Text.of("List??? "), option::get, option::set, options));
        menu.addOption(new RunnableOption(Text.of("Runnable Test"), running::get, running::set, () -> System.out.println("Runnable ran")));
        SubMenuOption subMenuOption = new SubMenuOption(Text.of("SubMenu"), menu, subMenu::get, subMenu::set);
        subMenuOption.getSubMenu().addOption(new BooleanOption(Text.of("Shadows2"), () -> this.shadow, value -> this.shadow = value));
        subMenuOption.getSubMenu().addOption(new BooleanOption(Text.of("Shadows3"), () -> this.shadow, value -> this.shadow = value));
        subMenuOption.getSubMenu().addOption(new BooleanOption(Text.of("Shadows4"), () -> this.shadow, value -> this.shadow = value));
        menu.addOption(subMenuOption);
         */

    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY) {
        if(menu == null) return;
        //int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % (5000 * rainbowSpeed) / (5000f * rainbowSpeed))) : textColor.getRGB();
        int color = textColor.getRGB();
        if (valueSupplier != null) {
            String text = getValue();
            if(rainbow){
                DrawHelper.drawChromaText(drawContext,text,getX() + 2, getY() + 2, rainbowSpeed/2f,rainbowSaturation,rainbowBrightness,rainbowSpread,shadow);
            } else {
                drawContext.drawText(mc.textRenderer, text, getX() + 2, getY() + 2, color, shadow);
            }
            widgetBox.setSizeAndPosition(getX(), getY(), mc.textRenderer.getWidth(text) + 3, mc.textRenderer.fontHeight + 2, this.shouldScale, GlobalConfig.get().getScale());
        }
        menu.set(getX(), getY(), (int) Math.ceil(getHeight()));
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        menu.toggleDisplay(widgetBox,mouseX,mouseY,button);
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
        tag.putString("RegistryID", registryID);
        tag.putString("RegistryKey", registryKey);
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

        createMenu();
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
