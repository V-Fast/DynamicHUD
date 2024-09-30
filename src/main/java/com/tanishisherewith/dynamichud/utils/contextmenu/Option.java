package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.SkinRenderer;
import com.tanishisherewith.dynamichud.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Option<T> {
    public int x, y;
    public int width = 0;
    public int height = 0;
    public T value = null;
    public Supplier<Boolean> shouldRender = () -> true;
    protected float scale = 0.0f;
    protected Supplier<T> getter;
    protected Consumer<T> setter;
    protected T defaultValue = null;
    protected MinecraftClient mc = MinecraftClient.getInstance();
    protected ContextMenuProperties properties;
    protected SkinRenderer<Option<T>> renderer;

    public Option(Supplier<T> getter, Consumer<T> setter) {
        this(getter,setter,()->true);
    }

    public Option(Supplier<T> getter, Consumer<T> setter, Supplier<Boolean> shouldRender,ContextMenuProperties properties) {
        this.getter = getter;
        this.setter = setter;
        this.shouldRender = shouldRender;
        value = get();
        defaultValue = get();
        updateProperties(properties);
    }

    public Option(Supplier<T> getter, Consumer<T> setter, Supplier<Boolean> shouldRender) {
       this(getter,setter,shouldRender,ContextMenuProperties.createGenericSimplified());
    }

    public T get() {
        return getter.get();
    }

    public void set(T value) {
        this.value = value;
        setter.accept(value);
    }

    public void updateProperties(ContextMenuProperties properties){
        this.properties = properties;
        this.renderer = properties.getSkin().getRenderer((Class<Option<T>>) this.getClass());
        if (renderer == null) {
            System.err.println("Renderer not found for class: " + this.getClass().getName());
            throw new RuntimeException();
        }
    }

    public void render(DrawContext drawContext, int x, int y,int mouseX, int mouseY) {
        this.x = x;
        this.y = y;

        // Retrieve the renderer and ensure it is not null
        renderer.render(drawContext, this, x, y, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY) || renderer.mouseClicked(this,mouseX,mouseY,button);

    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY) || renderer.mouseReleased(this,mouseX,mouseY,button);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button,double deltaX, double deltaY) {
        return isMouseOver(mouseX, mouseY) || renderer.mouseDragged(this,mouseX,mouseY,button,deltaX,deltaY);
    }

    public void keyPressed(int key) {
         renderer.keyPressed(this,key);
    }
    public void keyReleased(int key) {
         renderer.keyReleased(this,key);
    }
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        renderer.mouseScrolled(this,mouseX,mouseY,horizontalAmount,verticalAmount);
    }

    //Called when the context menu closes
    public void onClose(){}

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Option<T> setShouldRender(Supplier<Boolean> shouldRender) {
        this.shouldRender = shouldRender;
        return this;
    }

    public boolean shouldRender() {
        return shouldRender.get();
    }

    public ContextMenuProperties getProperties() {
        return properties;
    }
}
