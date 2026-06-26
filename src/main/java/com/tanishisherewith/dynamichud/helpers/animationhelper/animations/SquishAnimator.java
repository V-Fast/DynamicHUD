package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;

import java.util.function.Consumer;

public class SquishAnimator extends ValueAnimation {
    private boolean isPressed = false;
    private final float normalScale;
    private final float pressedScale;
    private final ScaleHolder holder;

    private SquishAnimator(ScaleHolder holder, float normalScale, float pressedScale) {
        super(holder, normalScale, pressedScale);
        this.holder = holder;
        this.holder.value = normalScale;
        this.normalScale = normalScale;
        this.pressedScale = pressedScale;

        // default polished easing configurations
        this.easing(EasingType.EASE_OUT_BACK);
        this.duration(125);
    }

    public SquishAnimator(float normalScale, float pressedScale) {
        this(new ScaleHolder(), normalScale, pressedScale);
    }

    public SquishAnimator(){
        this(1.0f,0.95f);
    }

    public void update(boolean pressed) {
        if (this.isPressed != pressed) {
            this.isPressed = pressed;
            this.set(holder.value,pressed ? pressedScale : normalScale).start();
        }
        this.update();
    }

    public float getScale() { return holder.value; }

    private static class ScaleHolder implements Consumer<Float> {
        float value;

        @Override
        public void accept(Float v) {
            this.value = v;
        }
    }
}