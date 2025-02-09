package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.Animation;
import com.tanishisherewith.dynamichud.helpers.animationhelper.AnimationProperty;
import com.tanishisherewith.dynamichud.helpers.animationhelper.Easing;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;

public class ValueAnimation extends Animation {
    private final AnimationProperty<Float> property;
    private float startValue;
    private float endValue;
    private EasingType easing;
    private float value;

    public ValueAnimation(AnimationProperty<Float> property, float start, float end, EasingType easingType) {
        this.property = property;
        this.startValue = start;
        this.endValue = end;
        this.easing = easingType;
    }
    public ValueAnimation(AnimationProperty<Float> property, float start, float end) {
        this(property,start,end,EasingType.LINEAR);
    }

    @Override
    protected void applyAnimation(float progress) {
        value = startValue + (endValue - startValue) * Easing.apply(easing,progress);
        property.set(value);
    }

    public ValueAnimation setEasing(EasingType easing) {
        this.easing = easing;
        return this;
    }

    public ValueAnimation setStartValue(float startValue) {
        this.startValue = startValue;
        return this;
    }

    public ValueAnimation setEndValue(float endValue) {
        this.endValue = endValue;
        return this;
    }

    public float getValue() {
        return value;
    }
}