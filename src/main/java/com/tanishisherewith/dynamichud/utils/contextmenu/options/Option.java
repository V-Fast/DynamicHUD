package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.utils.contextmenu.ContextMenuProperties;
import com.tanishisherewith.dynamichud.utils.contextmenu.skinsystem.interfaces.SkinRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class Option<T> implements Input {
    public String name = "Empty", description = "";
    public T value = null;
    protected int x, y;
    protected int width = 0;
    protected int height = 0;
    protected Supplier<Boolean> shouldRender;
    protected Supplier<T> getter;
    protected Consumer<T> setter;
    protected T defaultValue = null;
    protected MinecraftClient mc = MinecraftClient.getInstance();
    protected ContextMenuProperties properties;
    protected SkinRenderer<Option<T>> renderer;

    public Option(String name,Supplier<T> getter, Consumer<T> setter) {
        this(name,getter, setter, () -> true);
    }

    public Option(String name,Supplier<T> getter, Consumer<T> setter, Supplier<Boolean> shouldRender, ContextMenuProperties properties) {
        this.name = name;
        this.getter = getter;
        this.setter = setter;
        this.shouldRender = shouldRender;
        value = get();
        defaultValue = get();
        updateProperties(properties);
    }

    public Option(String name,Supplier<T> getter, Consumer<T> setter, Supplier<Boolean> shouldRender) {
        this(name,getter, setter, shouldRender, ContextMenuProperties.createGenericSimplified());
    }

    public T get() {
        return getter.get();
    }

    public void set(T value) {
        this.value = value;
        setter.accept(value);
    }

    public void updateProperties(ContextMenuProperties properties) {
        this.properties = properties;
        this.renderer = properties.getSkin().getRenderer((Class<Option<T>>) this.getClass());
        if (renderer == null) {
            DynamicHUD.logger.error("Renderer not found for class: {} in the following skin: {}", this.getClass().getName(), properties.getSkin());
            throw new RuntimeException();
        }
    }

    public void render(DrawContext drawContext, int x, int y, int mouseX, int mouseY) {
        this.x = x;
        this.y = y;
        this.value = get();

        // Retrieve the renderer and ensure it is not null
        renderer.render(drawContext, this, x, y, mouseX, mouseY);
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        return isMouseOver(mouseX, mouseY);
    }

    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return isMouseOver(mouseX, mouseY);
    }

    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {}

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {}

    @Override
    public void charTyped(char c) {}

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {}

    /**
     * Called when the context menu closes
     */
    public void onClose() {}

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

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Option<T> description(String description){
        this.description = description;
        return this;
    }

    public SkinRenderer<Option<T>> getRenderer() {
        return renderer;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
