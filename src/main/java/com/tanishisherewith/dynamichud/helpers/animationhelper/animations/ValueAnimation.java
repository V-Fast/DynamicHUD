package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.Animation;
import com.tanishisherewith.dynamichud.helpers.animationhelper.Easing;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;

import java.util.function.Consumer;

public class ValueAnimation extends Animation {
    private final Consumer<Float> setter;
    private float startValue;
    private float endValue;
    private EasingType easing;
    private float value;

    /**
     * Creates a new ValueAnimation with an easing type.
     *
     * @param setter     A functional callback (such as a lambda expression) to receive the updated float.
     * @param start      The starting float value.
     * @param end        The target ending float value.
     * @param easingType The mathematical easing equation to apply.
     */

    public ValueAnimation(Consumer<Float> setter, float start, float end, EasingType easingType) {
        this.setter = setter;
        this.startValue = start;
        this.endValue = end;
        this.easing = easingType;
        this.value = startValue;
    }

    /**
     * Creates a new ValueAnimation with linear easing.
     *
     * @param setter A functional callback (such as a lambda expression) to receive the updated float.
     * @param start  The starting float value.
     * @param end    The target ending float value.
     */
    public ValueAnimation(Consumer<Float> setter, float start, float end) {
        this(setter, start, end, EasingType.LINEAR);
    }

    @Override
    protected void applyAnimation(float progress) {
        this.value = startValue + (endValue - startValue) * Easing.apply(easing, progress);
        setter.accept(value);
    }

    public ValueAnimation set(float startValue, float endValue){
        this.startValue = startValue;
        this.endValue = endValue;
        return this;
    }

    public ValueAnimation easing(EasingType easing) {
        this.easing = easing;
        return this;
    }

    public ValueAnimation startValue(float startValue) {
        this.startValue = startValue;
        return this;
    }

    public ValueAnimation endValue(float endValue) {
        this.endValue = endValue;
        return this;
    }

    public void setValue(float value) {
        this.value = value;
        setter.accept(value);
    }

    public float getValue() {
        return value;
    }
}