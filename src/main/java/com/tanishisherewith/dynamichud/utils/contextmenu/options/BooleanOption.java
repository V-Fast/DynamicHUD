package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import com.tanishisherewith.dynamichud.utils.BooleanPool;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class BooleanOption extends Option<Boolean> {
    private final BooleanType booleanType;

    public BooleanOption(Component name, Supplier<Boolean> getter, Consumer<Boolean> setter, BooleanType booleanType) {
        super(name, getter, setter);
        this.booleanType = booleanType;
    }

    public BooleanOption(Component name, Supplier<Boolean> getter, Consumer<Boolean> setter) {
        this(name, getter, setter, BooleanType.TRUE_FALSE);
    }

    public BooleanOption(Component name, boolean defaultValue) {
        this(name, defaultValue, BooleanType.TRUE_FALSE);
    }

    public BooleanOption(Component name, boolean defaultValue, BooleanType type) {
        this(name, () -> BooleanPool.get(name.getString()), value -> BooleanPool.put(name.getString(), value), type);
        BooleanPool.put(name.getString(), defaultValue);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver(mouseX, mouseY) && button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            this.value = !this.value;
            set(value);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    public BooleanType getBooleanType() {
        return booleanType;
    }

    public enum BooleanType {
        ON_OFF(aBoolean -> aBoolean ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF),
        TRUE_FALSE(aBoolean -> aBoolean ? Component.literal("True") : Component.literal("False")),
        YES_NO(aBoolean -> aBoolean ? CommonComponents.GUI_YES : CommonComponents.GUI_NO);

        private final Function<Boolean, Component> function;

        BooleanType(Function<Boolean, Component> function) {
            this.function = function;
        }

        public Component getText(boolean val) {
            return function.apply(val);
        }
    }
}
