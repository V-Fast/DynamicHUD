package com.tanishisherewith.dynamichud.utils.contextmenu;

import com.tanishisherewith.dynamichud.DynamicHUD;
import com.tanishisherewith.dynamichud.helpers.DrawHelper;
import com.tanishisherewith.dynamichud.helpers.animationhelper.AnimationProperty;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import com.tanishisherewith.dynamichud.helpers.animationhelper.animations.ValueAnimation;
import com.tanishisherewith.dynamichud.internal.System;
import com.tanishisherewith.dynamichud.utils.Input;
import com.tanishisherewith.dynamichud.utils.contextmenu.layout.LayoutEngine;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.factory.ContextMenuScreenFactory;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.ContextMenuScreenRegistry;
import com.tanishisherewith.dynamichud.utils.contextmenu.screen.factory.DefaultContextMenuScreenFactory;
import com.tanishisherewith.dynamichud.utils.contextmenu.options.Option;
import com.tanishisherewith.dynamichud.widget.WidgetBox;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ContextMenu<T extends ContextMenuProperties> implements Input {
    public final Color darkerBackgroundColor;
    //The properties of a context menu
    @NotNull
    protected final T properties;
    protected final List<Option<?>> options = new ArrayList<>(); // The list of options in the context menu
    protected final ContextMenuScreenFactory screenFactory;
    public int x, y;

    protected LayoutEngine layoutEngine;

    protected int width = 0;
    protected int height = 0, widgetHeight = 0;
    protected boolean shouldDisplay = false;
    protected float animScale = 0.0f;
    protected Screen parentScreen = null;
    protected boolean newScreenFlag = false;
    protected float menuScale = 1.0f;

    private final ValueAnimation scaleAnimation;

    @Nullable
    private final ContextMenu<?> parentMenu;

    public ContextMenu(int x, int y, T properties) {
        this(x, y, properties, new DefaultContextMenuScreenFactory());
    }

    public ContextMenu(int x, int y, T properties, ContextMenuScreenFactory screenFactory) {
        this(x, y, properties, screenFactory, null);
    }

    public ContextMenu(int x, int y, @NotNull T properties, ContextMenuScreenFactory screenFactory, @Nullable ContextMenu<?> parentMenu) {
        Objects.requireNonNull(screenFactory, "ContextMenuScreenFactory cannot be null!");
        Objects.requireNonNull(properties, "ContextMenu Properties cannot be null!");

        this.x = x;
        this.y = y + properties.getHeightOffset();
        this.properties = properties;
        this.screenFactory = screenFactory;
        this.darkerBackgroundColor = properties.getBackgroundColor().darker().darker().darker().darker().darker().darker();
        this.parentMenu = parentMenu;
        this.properties.getSkin().setContextMenu(this);
        this.layoutEngine = new LayoutEngine();

        this.scaleAnimation = new ValueAnimation(new AnimationProperty<>() {
            @Override
            public Float get() {
                return animScale;
            }

            @Override
            public void set(Float value) {
                animScale = value;
            }
        }, 0.0f, 1.0f);
        this.scaleAnimation.easing(EasingType.EASE_IN_CUBIC);
        this.scaleAnimation.duration(280);
        this.scaleAnimation.onComplete(() -> {
            if (animScale <= 0.0f && parentScreen != null && properties.getSkin().shouldCreateNewScreen()) {
                DynamicHUD.MC.setScreen(parentScreen);
            }
        });


        Screen dummy = screenFactory.create(this, properties);
        System.registerInstance(new ContextMenuScreenRegistry(dummy.getClass()), DynamicHUD.MOD_ID);
    }

    public void addOption(Option<?> option) {
        option.updateProperties(this.getProperties());
        options.add(option);
    }

    public float getMenuScale() {
        return animScale * menuScale;
    }

    public void setMenuScale(float menuScale) {
        this.menuScale = Math.clamp(menuScale, 0.3f, 2.0f);
    }

    public void render(GuiGraphics graphics, int xPos, int yPos, int mouseX, int mouseY) {
        if (newScreenFlag && screenFactory != null) {
            DynamicHUD.MC.setScreen(screenFactory.create(this, properties));
            return;
        }

        this.x = xPos;
        this.y = yPos + properties.getHeightOffset() + widgetHeight;

        update();

        if (animScale <= 0.0f || newScreenFlag) return;

        DrawHelper.scaleAndPosition(graphics.pose(), this.x, this.y,this.width,this.height, getMenuScale());

        properties.getSkin().setContextMenu(this);
        properties.getSkin().renderContextMenu(graphics, this, getTMouseX(mouseX), getTMouseY(mouseY));

        DrawHelper.stopScaling(graphics.pose());
    }

    public void update() {
        if (layoutEngine != null) {
            layoutEngine.applyLayout(this);
        }

        if (!properties.enableAnimations()) {
            animScale = shouldDisplay ? 1.0f : 0.0f;
            return;
        }

        scaleAnimation.update();
    }

    public void close() {
        shouldDisplay = false;
        newScreenFlag = false;
        for (Option<?> option : options) {
            option.onClose();
        }
        if (properties.enableAnimations()) {
            scaleAnimation.startValue(animScale);
            scaleAnimation.endValue(0.0f);
            scaleAnimation.start();
        } else {
            animScale = 0.0f;
            if (properties.getSkin().shouldCreateNewScreen() && parentScreen != null) {
                DynamicHUD.MC.setScreen(parentScreen);
            }
        }
    }

    public void open() {
        shouldDisplay = true;
        update();
        parentScreen = DynamicHUD.MC.screen;
        if (properties.getSkin().shouldCreateNewScreen()) {
            newScreenFlag = true;
        }
        if (properties.enableAnimations()) {
            scaleAnimation.startValue(animScale);
            scaleAnimation.endValue(1.0f);
            scaleAnimation.start();
        } else {
            animScale = 0.0f;
            if (properties.getSkin().shouldCreateNewScreen() && parentScreen != null) {
                DynamicHUD.MC.setScreen(parentScreen);
            }
        }
    }

    public void toggleDisplay() {
        if (shouldDisplay) {
            close();
        } else {
            open();
        }
    }

    protected int getTMouseX(double mouseX) {
        return (int) ((mouseX - getX()) / getMenuScale() + getX());
    }

    protected int getTMouseY(double mouseY) {
        return (int) ((mouseY - getY()) / getMenuScale() + getY());
    }

    public void toggleDisplay(WidgetBox widgetBox, double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && widgetBox.isMouseOver(mouseX, mouseY)) {
            toggleDisplay();
        }
    }

    public void resetAllOptions() {
        for (Option<?> option : options) {
            option.reset();
        }
    }

    public LayoutEngine getLayoutEngine() {
        return layoutEngine;
    }

    public void setLayoutEngine(LayoutEngine layoutEngine) {
        this.layoutEngine = layoutEngine;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!shouldDisplay) return false;

        for (Option option : options) {
            if (option.shouldRender() && option.getRenderer().mouseClicked(option ,getTMouseX(mouseX), getTMouseY(mouseY), button)) {
                return true;
            }
        }
        return properties.getSkin().mouseClicked(this, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!shouldDisplay) return false;
        for (Option option : options) {
            if(option.shouldRender()){
                option.getRenderer().mouseReleased(option, getTMouseX(mouseX), getTMouseY(mouseY), button);
            }
        }
        return properties.getSkin().mouseReleased(this, mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!shouldDisplay) return false;

        double tDeltaX = deltaX / getMenuScale();
        double tDeltaY = deltaY / getMenuScale();

        for (Option option : options) {
           if(option.shouldRender() && option.getRenderer().mouseDragged(option, getTMouseX(mouseX), getTMouseY(mouseY), button, tDeltaX, tDeltaY)){
               return true;
           }
        }
        return properties.getSkin().mouseDragged(this, mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (!shouldDisplay) return;
        for (Option option : options) {
            if(option.shouldRender()){
                option.getRenderer().keyPressed(option, key, scanCode, modifiers);
            }
        }

        properties.getSkin().keyPressed(this, key, scanCode, modifiers);
    }

    @Override
    public void keyReleased(int key, int scanCode, int modifiers) {
        if (!shouldDisplay) return;
        for (Option option : options) {
            if(option.shouldRender()) {
                option.getRenderer().keyReleased(option, key, scanCode, modifiers);
            }
        }
        properties.getSkin().keyReleased(this, key, scanCode, modifiers);

    }

    @Override
    public void mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (!shouldDisplay) return;
        for (Option option : options) {
            if(option.shouldRender()) {
                option.getRenderer().mouseScrolled(option, mouseX, mouseY, horizontalAmount, verticalAmount);
            }
        }
        properties.getSkin().mouseScrolled(this, mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void charTyped(char c, int modifiers) {
        if (!shouldDisplay) return;
        for (Option<?> option : options) {
            if(option.shouldRender()) {
                option.charTyped(c, modifiers);
            }
        }
    }

    public void set(int x, int y, int widgetHeight) {
        this.x = x;
        this.y = y;
        this.widgetHeight = widgetHeight;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public List<Option<?>> getOptions() {
        return Collections.unmodifiableList(options);
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

    public @NotNull T getProperties() {
        return properties;
    }

    public void setWidgetHeight(int widgetHeight) {
        this.widgetHeight = widgetHeight;
    }

    @Nullable
    public ContextMenu<?> getParentMenu() {
        return parentMenu;
    }

    public <K extends ContextMenuProperties> ContextMenu<K> createSubMenu(int x, int y, K properties) {
        return new ContextMenu<>(x, y, properties, screenFactory, this);
    }

    public boolean isVisible() {
        return shouldDisplay;
    }

    public void setVisible(boolean shouldDisplay) {
        this.shouldDisplay = shouldDisplay;
    }
}
