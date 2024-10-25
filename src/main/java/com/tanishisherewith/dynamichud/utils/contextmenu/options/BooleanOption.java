package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.BooleanPool;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BooleanOption extends Option<Boolean> {
    private final BooleanType booleanType;

    public BooleanOption(String name, Supplier<Boolean> getter, Consumer<Boolean> setter, BooleanType booleanType) {
        super(name,getter, setter);
        this.booleanType = booleanType;
        this.renderer.init(this);
    }

    public BooleanOption(String name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(name, getter, setter, BooleanType.TRUE_FALSE);
    }

    public BooleanOption(String name, boolean defaultValue) {
        this(name, defaultValue, BooleanType.TRUE_FALSE);
    }

    public BooleanOption(String name, boolean defaultValue, BooleanType type) {
        this(name, () -> BooleanPool.get(name), value -> BooleanPool.put(name, value), type);
        BooleanPool.put(name, defaultValue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            value = !value;
            set(value);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public BooleanType getBooleanType() {
        return booleanType;
    }

    public enum BooleanType {
        ON_OFF(ScreenTexts::onOrOff),
        TRUE_FALSE(aBoolean -> aBoolean ? Text.of("True") : Text.of("False")),
        YES_NO(aBoolean -> aBoolean ? ScreenTexts.YES : ScreenTexts.NO);

        private final Function<Boolean, Text> function;

        BooleanType(Function<Boolean, Text> function) {
            this.function = function;
        }

        public Text getText(boolean val) {
            return function.apply(val);
        }
    }
}
