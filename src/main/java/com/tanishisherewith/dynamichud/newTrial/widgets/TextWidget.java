package com.tanishisherewith.dynamichud.newTrial.widgets;

import com.tanishisherewith.dynamichud.helpers.ColorHelper;
import com.tanishisherewith.dynamichud.newTrial.utils.DynamicValueRegistry;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.ContextMenu;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options.BooleanOption;
import com.tanishisherewith.dynamichud.newTrial.utils.contextmenu.options.DoubleOption;
import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import com.tanishisherewith.dynamichud.newTrial.widget.WidgetData;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.nbt.NbtCompound;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.function.Supplier;

public class TextWidget extends Widget {
    public static WidgetData<TextWidget> DATA = new WidgetData<>("TextWidget","Display Text on screen",TextWidget::new);
    Supplier<String> textSupplier;
    String dynamicRegistryKey = "";
    DynamicValueRegistry dynamicValueRegistry = null;
    protected boolean shadow; // Whether to draw a shadow behind the text
    protected boolean rainbow; // Whether to apply a rainbow effect to the text
    protected int rainbowSpeed = 2; //Speed of the rainbow effect
    private ContextMenu menu;

    public TextWidget() {
       this(null,false,false,"unknown");
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#globalRegistry} using the given registryKey
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(String dynamicRegistryKey, boolean shadow, boolean rainbow,String modID) {
        super(DATA,modID);
        this.dynamicRegistryKey = dynamicRegistryKey;
        textSupplier = (Supplier<String>) DynamicValueRegistry.getGlobal(dynamicRegistryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
        createMenu();
    }
    public void createMenu(){
        menu = new ContextMenu(getX(),getY());
        menu.addOption(new BooleanOption("Shadow",()->this.shadow,value-> this.shadow = value));
        menu.addOption(new BooleanOption("Rainbow",()->this.rainbow,value-> this.rainbow = value));
        menu.addOption(new DoubleOption("RainbowSpeed",1,4,1.0f, ()->(double)this.rainbowSpeed, value-> this.rainbowSpeed = value.intValue()));
    }

    /**
     * Searches for the supplier within the {@link DynamicValueRegistry#localRegistry} using the given registryKey and registryValue
     *
     * @param dynamicRegistryKey
     * @param shadow
     * @param rainbow
     */
    public TextWidget(DynamicValueRegistry dynamicValueRegistry,String dynamicRegistryKey, boolean shadow, boolean rainbow,String modID) {
        super(DATA,modID);
        this.dynamicRegistryKey = dynamicRegistryKey;
        this.dynamicValueRegistry = dynamicValueRegistry;
        textSupplier = (Supplier<String>) dynamicValueRegistry.get(dynamicRegistryKey);
        this.shadow = shadow;
        this.rainbow = rainbow;
        createMenu();
    }

    @Override
    public void renderWidget(DrawContext drawContext,int mouseX, int mouseY) {
        menu.render(drawContext, getX() - 2, getY(), (int) Math.ceil(getHeight()));
        int color = rainbow ? ColorHelper.getColorFromHue((System.currentTimeMillis() % (5000 * rainbowSpeed) / (float) (5000f * rainbowSpeed))) : Color.WHITE.getRGB();
        if (textSupplier != null) {
            String text = textSupplier.get();
            drawContext.drawText(mc.textRenderer, text, (int) getX(), (int) getY(), color, shadow);
            widgetBox.setSizeAndPosition(getX() - 2, getY() - 2, mc.textRenderer.getWidth(text) + 2, mc.textRenderer.fontHeight + 2);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && widgetBox.isMouseOver(mouseX,mouseY)){
            menu.toggleDisplay();
        }
        menu.mouseClicked(mouseX,mouseY,button);
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public void mouseReleased(double mouseX, double mouseY, int button) {
        menu.mouseReleased(mouseX,mouseY,button);
        super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, int snapSize) {
        menu.mouseDragged(mouseX,mouseY,button);
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
        tag.putString("DynamicRegistryKey",dynamicRegistryKey);
        tag.putBoolean("shadow",shadow);
        tag.putBoolean("rainbow",rainbow);

        // If true then it means that we should use local registry and if false (i.e. null) then use global registry
        tag.putBoolean("DynamicValueRegistry", dynamicValueRegistry != null);
    }

    @Override
    public void readFromTag(NbtCompound tag) {
        super.readFromTag(tag);
        shadow = tag.getBoolean("shadow");
        rainbow = tag.getBoolean("rainbow");
        dynamicRegistryKey = tag.getString("DynamicRegistryKey");

        // If true then it means that we should use local registry and if false (i.e. null) then use global registry
        boolean dvrObj = tag.getBoolean("DynamicValueRegistry");

        if(!dvrObj && dynamicRegistryKey != null){
            textSupplier = (Supplier<String>) DynamicValueRegistry.getGlobal(dynamicRegistryKey);
            return;
        }

        for(DynamicValueRegistry dvr: DynamicValueRegistry.getInstances(modId)){
            //Unfortunately, this method takes the value from the first local registry with the key.
            //It returns to prevent overriding with other registries
            textSupplier = (Supplier<String>) dvr.get(dynamicRegistryKey);
            System.out.println(dvr);
            System.out.println(DynamicValueRegistry.getInstances(modId));
            return;
        }
        createMenu();
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
        public Builder setDRKey(String dynamicRegistryKey) {
            this.dynamicRegistryKey = dynamicRegistryKey;
            return self();
        }
        public Builder setDVR(DynamicValueRegistry dynamicValueRegistry) {
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
                widget = new TextWidget(dynamicRegistryKey, shadow, rainbow,modID);
            }else{
                widget = new TextWidget(dynamicValueRegistry,dynamicRegistryKey, shadow, rainbow,modID);
            }
            widget.setPosition(x,y);
            widget.setDraggable(isDraggable);
            widget.setShouldScale(shouldScale);
            return widget;
        }
    }
}
