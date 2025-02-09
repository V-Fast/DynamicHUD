package com.tanishisherewith.dynamichud.helpers.animationhelper;

import java.util.ArrayList;
import java.util.List;

public abstract class Animation {
    public long startTime;
    public long duration;
    protected boolean running = false;
    protected boolean finished = false;
    protected EasingType easing = EasingType.LINEAR;
    protected final List<Runnable> completionCallbacks = new ArrayList<>();

    public void start() {
        startTime = System.currentTimeMillis();
        running = true;
        finished = false;
    }

    public void stop() {
        running = false;
        finished = true;
    }

    public void update() {
        if (!running || finished) return;

        long elapsed = System.currentTimeMillis() - startTime;
        float progress = Math.min(elapsed / (float) duration, 1.0f);
        float easedProgress = Easing.apply(easing, progress);

        applyAnimation(easedProgress);

        if (progress >= 1.0f) {
            finish();
        }
    }

    protected abstract void applyAnimation(float progress);

    public Animation duration(long durationMs) {
        this.duration = durationMs;
        return this;
    }

    public Animation easing(EasingType easing) {
        this.easing = easing;
        return this;
    }

    public Animation onComplete(Runnable callback) {
        completionCallbacks.add(callback);
        return this;
    }

    public void finish() {
        finished = true;
        running = false;
        completionCallbacks.forEach(Runnable::run);
    }

    public boolean isFinished() { return finished; }
}