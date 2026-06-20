package com.tanishisherewith.dynamichud.helpers.animationhelper;

// Use this to set/get the variable on which the animation should apply to
public interface AnimationProperty<T> {
    T get();
    void set(T value);
}