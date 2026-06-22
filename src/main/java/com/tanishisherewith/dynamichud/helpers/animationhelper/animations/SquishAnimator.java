package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.AnimationProperty;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;

public class SquishAnimator {
    private final ValueAnimation animation;
    private final AnimationProperty<Float> property;
    private boolean isPressed = false;
    private float normalScale;
    private float pressedScale;

    public SquishAnimator(float normalScale, float pressedScale) {
        this.normalScale = normalScale;
        this.pressedScale = pressedScale;
        this.property = new AnimationProperty<>() {
            private float val = normalScale;
            public Float get() { return val; }
            public void set(Float v) { val = v; }
        };

        this.animation = new ValueAnimation(property, normalScale, pressedScale);
        this.animation.easing(EasingType.EASE_OUT_BACK);
        this.animation.duration(125);
    }
    public SquishAnimator(){
        this(1.0f,0.95f);
    }

    public void update(boolean pressed) {
        if (this.isPressed != pressed) {
            this.isPressed = pressed;
            animation.set(property.get(),pressed ? pressedScale : normalScale).start();
        }
        animation.update();
    }

    public float getScale() { return property.get(); }
}