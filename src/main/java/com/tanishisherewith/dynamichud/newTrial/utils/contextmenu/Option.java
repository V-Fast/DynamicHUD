package com.tanishisherewith.dynamichud.newTrial.utils.contextmenu;

import com.tanishisherewith.dynamichud.newTrial.widget.Widget;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Option<T> {
    public int x, y;
    public int width = 0;
    public int height = 0;
    public T value = null;
    protected float scale = 0.0f;
    protected Supplier<T> getter;
    protected Consumer<T> setter;
    protected T defaultValue = null;
    protected MinecraftClient mc = MinecraftClient.getInstance();
    private Widget selectedWidget; // The widget that this context menu is associated with
    public Supplier<Boolean> shouldRender = ()->true;

    public Option(Supplier<T> getter, Consumer<T> setter) {
        this.getter = getter;
        this.setter = setter;
        value = get();
        defaultValue = get();
    }
    public Option(Supplier<T> getter, Consumer<T> setter,Supplier<Boolean> shouldRender) {
        this.getter = getter;
        this.setter = setter;
        this.shouldRender = shouldRender;
        value = get();
        defaultValue = get();
    }

    protected T get() {
        return getter.get();
    }

    protected void set(T value) {
        this.value = value;
        setter.accept(value);
    }

    public void render(DrawContext drawContext, int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    public void keyPressed(int key) {

    }

    public void keyReleased(int key) {

    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }

    public Option<T> setShouldRender(Supplier<Boolean> shouldRender) {
        this.shouldRender = shouldRender;
        return this;
    }

    public boolean shouldRender(){
        return shouldRender.get();
    }
}
