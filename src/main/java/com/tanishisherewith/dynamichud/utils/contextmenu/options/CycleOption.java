package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class CycleOption<T> extends Option<T> {
    protected final List<T> values;
    protected int currentIndex;

    @SafeVarargs
    public CycleOption(Component name, Supplier<T> getter, Consumer<T> setter, T... values) {
        this(name, getter, setter, Arrays.asList(values));
    }

    public CycleOption(Component name, Supplier<T> getter, Consumer<T> setter, List<T> values) {
        super(name, getter, setter);
        this.values = values;
        this.currentIndex = values.indexOf(value);
        if (currentIndex == -1) {
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i).toString().equals(value)) {
                    currentIndex = i;
                    break;
                }
            }
        }
    }

    public void cycle(int direction) {
        currentIndex = (currentIndex + direction + values.size()) % values.size();
        if (currentIndex < 0) currentIndex += values.size();

        set(values.get(currentIndex));
    }

    public T getCurrentValue() { return value; }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                cycle(1);
            } else if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
                cycle(-1);
            }
            set(value);
            return true;
        }
        return false;
    }

    public List<T> getValues() {
        return values;
    }
}