package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.Animation;

import java.util.ArrayList;
import java.util.List;

public class CompositeAnimation extends Animation {
    private final List<Animation> children = new ArrayList<>();
    private final boolean parallel;
    private int currentChildIndex = 0;
    private long[] childStartTimes;

    public CompositeAnimation(boolean parallel) {
        this.parallel = parallel;
    }

    public CompositeAnimation add(Animation animation) {
        children.add(animation);
        return this;
    }

    @Override
    public void start() {
        super.start();
        if (parallel) {
            children.forEach(Animation::start);
        } else {
            // Calculate total duration as sum of children's durations
            this.duration = children.stream().mapToLong(a -> a.duration).sum();
            this.childStartTimes = new long[children.size()];
            long accumulated = 0;
            for (int i = 0; i < children.size(); i++) {
                childStartTimes[i] = accumulated;
                accumulated += children.get(i).duration;
            }
            startChild(0);
        }
    }

    private void startChild(int index) {
        if (index < children.size()) {
            Animation child = children.get(index);
            child.start();
            // Adjust child's start time to match group timeline
            child.startTime = this.startTime + childStartTimes[index];
        }
    }

    @Override
    protected void applyAnimation(float progress) {
        if (parallel) {
            children.forEach(Animation::update);
        } else {
            long elapsed = System.currentTimeMillis() - startTime;

            // Find active child
            for (int i = 0; i < children.size(); i++) {
                long childDuration = children.get(i).duration;
                if (elapsed < childStartTimes[i] + childDuration) {
                    if (currentChildIndex != i) {
                        currentChildIndex = i;
                        startChild(i);
                    }
                    children.get(i).update();
                    break;
                }
            }
        }
    }

    @Override
    public void stop() {
        super.stop();
        children.forEach(Animation::stop);
    }

    @Override
    public boolean isFinished() {
        if (parallel) {
            return children.stream().allMatch(Animation::isFinished);
        } else {
            long elapsed = System.currentTimeMillis() - startTime;
            return elapsed >= duration;
        }
    }

    @Override
    public void finish() {
        // Ensure all children finish properly
        if (!parallel) {
            children.forEach(child -> {
                if (!child.isFinished()) {
                    child.finish();
                }
            });
        }
        super.finish();
    }
}