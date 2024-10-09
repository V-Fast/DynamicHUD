package com.tanishisherewith.dynamichud.widgets;

import com.tanishisherewith.dynamichud.config.GlobalConfig;
import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuManager;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProvider;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.BooleanOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.ColorOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.DoubleOption;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.MinecraftSkin;
import com.tanishisherewith.dynamichud.widget.Widget;
import com.tanishisherewith.dynamichud.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Supplier;

public class TextWidget extends Widget implements ContextMenuProvider {
    public Color textColor;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget", "Display Text on screen", TextWidget::new);
    protected int rainbowSpeed = 2; //Speed of the rainbow effect
    Supplier<String> textSupplier;
    String dynamicRegistryKey;
    DynamicValueRegistry dynamicValueRegistry = null;
    private ContextMenu menu;
    public TextWidget() {
        this(null, null, false, false, Color.WHITE, "unknown");
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#globalRegistry} using the given registryKey
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(String dynamicRegistryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        super(DATA, modID);
        this.dynamicRegistryKey = dynamicRegistryKey;
        textSupplier = (Supplier<String>) DynamicValueRegistry.getGlobal(dynamicRegistryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
        this.textColor = color;
        createMenu();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#localRegistry} using the given registryKey and registryValue
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(DynamicValueRegistry dynamicValueRegistry, String dynamicRegistryKey, boolean shadow, boolean rainbow, Color color, String modID) {
        super(DATA, modID);
        this.dynamicRegistryKey = dynamicRegistryKey;
        this.dynamicValueRegistry = dynamicValueRegistry;
        if (dynamicValueRegistry != null) {
            textSupplier = (Supplier<String>) dynamicValueRegistry.get(dynamicRegistryKey);
        }
        this.textColor = color;
        this.shadow = shadow;
        this.rainbow = rainbow;
        createMenu();
        ContextMenuManager.getInstance().registerProvider(this);
    }

    public void createMenu() {
        ContextMenuProperties properties = ContextMenuProperties.builder()
                .skin(new MinecraftSkin(MinecraftSkin.PanelColor.DARK_PANEL))
                .build();

        menu = new ContextMenu(getX(), getY(), properties);
        menu.getProperties().getSkin().setContextMenu(menu);
        menu.addOption(new BooleanOption("Shadow", () -> this.shadow, value -> this.shadow = value, BooleanOption.BooleanType.ON_OFF));
        menu.addOption(new BooleanOption("Rainbow", () -> this.rainbow, value -> this.rainbow = value, BooleanOption.BooleanType.ON_OFF));
        menu.addOption(new ColorOption("TextColor", menu, () -> this.textColor, value -> this.textColor = value));
        menu.addOption(new DoubleOption("RainbowSpeed", 1, 5.0f, 1, () -> (double) this.rainbowSpeed, value -> this.rainbowSpeed = value.intValue(), menu));

        /* TEST
        AtomicReference<String> option = new AtomicReference<>("Enum1");
        List<String> options = Arrays.asList("List1", "LONGER LIST 2", "List3");
        AtomicBoolean running = new AtomicBoolean(false);
        AtomicBoolean subMenu = new AtomicBoolean(false);
        menu.addOption(new ListOption<>("List", option::get, option::set, options));
        menu.addOption(new RunnableOption("Runnable Test",running::get,running::set, ()-> System.out.println("Runnable ran")));
        SubMenuOption subMenuOption = new SubMenuOption("SubMenu",menu,subMenu::get,subMenu::set);
        subMenuOption.getSubMenu().addOption(new BooleanOption("Shadows2", () -> this.shadow, value -> this.shadow = value));
        subMenuOption.getSubMenu().addOption(new BooleanOption("Shadows3", () -> this.shadow, value -> this.shadow = value));
        subMenuOption.getSubMenu().addOption(new BooleanOption("Shadows4", () -> this.shadow, value -> this.shadow = value));
        menu.addOption(subMenuOption);

         */
    }

    @Override
    public void renderWidget(DrawContext drawContext, int mouseX, int mouseY) {
        int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % (5000 * rainbowSpeed) / (5000f * rainbowSpeed))) : textColor.getRGB();
        if (textSupplier != null) {
            String text = textSupplier.get();
            drawContext.drawText(mc.textRenderer, text, getX() + 2, getY() + 2, color, shadow);
            widgetBox.setSizeAndPosition(getX(), getY(), mc.textRenderer.getWidth(text) + 3, mc.textRenderer.fontHeight + 2, this.shouldScale, GlobalConfig.get().getScale());
        }
        menu.set(getX(), getY(), (int) Math.ceil(getHeight()));
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && widgetBox.isMouseOver(mouseX, mouseY)) {
            menu.toggleDisplay();
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, int snapSize) {
        return super.mouseDragged(mouseX, mouseY, button, snapSize);
    }

    @Override
    public void onClose() {
        super.onClose();
        menu.close();
    }

    @Override
    public void writeToTag(NbtCompound tag) {
        super.writeToTag(tag);
        tag.putString("DynamicRegistryKey", dynamicRegistryKey);
        tag.putBoolean("Shadow", shadow);
        tag.putBoolean("Rainbow", rainbow);
        tag.putInt("TextColor", textColor.getRGB());
        tag.putInt("RainbowSpeed", rainbowSpeed);
        // If true then it means that we should use local registry and if false (i.e. null) then use global registry
        tag.putBoolean("DynamicValueRegistry", dynamicValueRegistry != null);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        this.shadow = tag.getBoolean("Shadow");
        this.rainbow = tag.getBoolean("Rainbow");
        this.rainbowSpeed = tag.getInt("RainbowSpeed");
        this.textColor = new Color(tag.getInt("TextColor"));
        this.dynamicRegistryKey = tag.getString("DynamicRegistryKey");

        // If true then it means that we should use local registry and if false (i.e. null) then use global registry
        boolean dvrObj = tag.getBoolean("DynamicValueRegistry");
        if (!dvrObj) {
            this.textSupplier = (Supplier<String>) DynamicValueRegistry.getGlobal(dynamicRegistryKey);
            return;
        }

        for (DynamicValueRegistry dvr : DynamicValueRegistry.getInstances(modId)) {
            //Unfortunately, this method takes the value from the first local registry with the key.
            //It returns to prevent overriding with other registries
            this.textSupplier = (Supplier<String>) dvr.get(dynamicRegistryKey);
            dynamicValueRegistry = dvr;
            return;
        }
        createMenu();
    }

    @Override
    public ContextMenu getContextMenu() {
        return menu;
    }

    public static class Builder extends WidgetBuilder<Builder, TextWidget> {
        protected boolean shadow = false;
        protected boolean rainbow = false;
        protected String dynamicRegistryKey = "";
        DynamicValueRegistry dynamicValueRegistry = null;
        Color textColor = Color.WHITE;

        public Builder shadow(boolean shadow) {
            this.shadow = shadow;
            return self();
        }

        public Builder rainbow(boolean rainbow) {
            this.rainbow = rainbow;
            return self();
        }

        public Builder setDRKey(String dynamicRegistryKey) {
            this.dynamicRegistryKey = dynamicRegistryKey;
            return self();
        }

        public Builder setDVR(DynamicValueRegistry dynamicValueRegistry) {
            this.dynamicValueRegistry = dynamicValueRegistry;
            return self();
        }

        public Builder setTextColor(Color textColor) {
            this.textColor = textColor;
            return self();
        }

        @Override
        protected Builder self() {
            return this;
        }

        @Override
        public TextWidget build() {
            TextWidget widget;
            if (dynamicValueRegistry == null) {
                widget = new TextWidget(dynamicRegistryKey, shadow, rainbow, textColor, modID);
            } else {
                widget = new TextWidget(dynamicValueRegistry, dynamicRegistryKey, shadow, rainbow, textColor, modID);
            }
            widget.setPosition(x, y);
            widget.setDraggable(isDraggable);
            widget.setShouldScale(shouldScale);
            return widget;
        }
    }


}
