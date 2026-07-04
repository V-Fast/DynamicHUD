package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class KeybindOption extends Option<Integer> {
    private boolean listening = false;

    public KeybindOption(Component name, Supplier<Integer> getter, Consumer<Integer> setter) {
        super(name, getter, setter);
    }

    public boolean isListening() {
        return listening;
    }

    public void setListening(boolean listening) {
        this.listening = listening;
    }

    @Override
    public void keyPressed(int key, int scanCode, int modifiers) {
        if (listening) {
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                set(0);
            } else {
                set(key);
            }
            listening = false;
        }
    }

    public static String getKeyName(int key) {
        if (key <= 0) return "NONE";
        if (key == GLFW.GLFW_KEY_ESCAPE) return "ESC";
        if (key == GLFW.GLFW_KEY_SPACE) return "SPACE";
        if (key == GLFW.GLFW_KEY_LEFT_CONTROL) return "LCTRL";
        if (key == GLFW.GLFW_KEY_RIGHT_CONTROL) return "RCTRL";
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT) return "LSHIFT";
        if (key == GLFW.GLFW_KEY_RIGHT_SHIFT) return "RSHIFT";
        if (key == GLFW.GLFW_KEY_LEFT_ALT) return "LALT";
        if (key == GLFW.GLFW_KEY_RIGHT_ALT) return "RALT";
        if (key == GLFW.GLFW_KEY_ENTER) return "ENTER";
        if (key == GLFW.GLFW_KEY_TAB) return "TAB";
        if (key == GLFW.GLFW_KEY_BACKSPACE) return "BACKSPACE";
        String name = GLFW.glfwGetKeyName(key, 0);
        return name != null ? name.toUpperCase() : "KEY " + key;
    }
}
