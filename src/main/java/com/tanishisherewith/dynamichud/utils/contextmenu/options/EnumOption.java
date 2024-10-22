package com.tanishisherewith.dynamichud.utils.contextmenu.options;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnumOption<E extends Enum<E>> extends Option<E> {
    private final E[] values;
    public String name = "Empty";
    private int currentIndex = 0;

    public EnumOption(String name, Supplier<E> getter, Consumer<E> setter, E[] values) {
        super(getter, setter);
        this.name = name;
        this.values = values;
        this.value = get();
        for (int i = 0; i < values.length; i++) {
            if (values[i] == value) {
                currentIndex = i;
                break;
            }
        }
        this.renderer.init(this);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            if (button == 0) {
                currentIndex = (currentIndex + 1) % values.length;
                if (currentIndex > values.length - 1) {
                    currentIndex = 0;
                }
                value = values[currentIndex];
            } else if (button == 1) {
                currentIndex = (currentIndex - 1) % values.length;
                if (currentIndex < 0) {
                    currentIndex = values.length - 1;
                }
                value = values[currentIndex];
            }
            set(value);
        }
        return true;
    }

    public E[] getValues() {
        return values;
    }
}
