package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.Easing;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.random.Random;

public class MathAnimations {
    // SHAKE: Random offset animation with smooth decay
    public static float shake(float intensity, float frequency, float decay) {
        long time = System.currentTimeMillis();
        return (float) (Math.sin(time * frequency) *
                Math.exp(-decay * time) * intensity);
    }

    // 2D Shake with different X/Y frequencies
    public static Vec2f shake2D(float intensity, float freqX, float freqY) {
        return new Vec2f(
                (float) Math.sin(System.currentTimeMillis() * freqX) * intensity,
                (float) Math.cos(System.currentTimeMillis() * freqY) * intensity
        );
    }

    // FLICKER: Random flashing effect
    public static float flicker(float min, float max, float chance) {
        Random rand = Random.create();
        return rand.nextFloat() < chance ?
                min + (max - min) * rand.nextFloat() :
                max;
    }

    // CIRCULAR MOTION: Perfect for rotation/orbital animations
    public static Vec2f circularMotion(float radius, float speed, float phase) {
        double angle = Math.toRadians((System.currentTimeMillis() * speed) % 360 + phase);
        return new Vec2f(
                (float) (Math.cos(angle) * radius),
                (float) (Math.sin(angle) * radius)
        );
    }

    // SAWTOOTH WAVE: Linear rise with sudden drop
    public static float sawtooth(float period, float min, float max) {
        float phase = (System.currentTimeMillis() % period) / period;
        return min + (max - min) * phase;
    }

    // TRIANGULAR WAVE: Linear rise and fall
    public static float triangleWave(float period, float min, float max) {
        float halfPeriod = period / 2;
        float phase = (System.currentTimeMillis() % period);
        float value = phase < halfPeriod ?
                (phase / halfPeriod) :
                2 - (phase / halfPeriod);
        return min + (max - min) * value;
    }

    // BOUNCE: Simulates physical bouncing
    public static float bounce(float dropHeight, float gravity, float dampening) {
        float t = System.currentTimeMillis() / 1000f;
        return (float) (dropHeight * Math.abs(Math.sin(t * Math.sqrt(gravity))) *
                Math.exp(-dampening * t));
    }

    // PULSE: Smooth heartbeat-like effect
    public static float pulse1(float base, float amplitude, float frequency) {
        return (float) (base + amplitude *
                (0.5 + 0.5 * Math.sin(System.currentTimeMillis() * frequency)));
    }

    // SPIRAL: Circular motion with expanding radius
    public static Vec2f spiral(float baseRadius, float expansionRate, float speed) {
        float t = System.currentTimeMillis() / 1000f;
        return new Vec2f(
                (float) ((baseRadius + expansionRate * t) * Math.cos(t * speed)),
                (float) ((baseRadius + expansionRate * t) * Math.sin(t * speed))
        );
    }

    // Continuous pulsating effect using sine wave
    public static float pulse2(float speed, float min, float max) {
        return (float) ((Math.sin(System.currentTimeMillis() * speed) + 1) / 2 * (max - min) + min);
    }

    // Linear interpolation between values over time
    public static float lerp(float start, float end, long startTime, float duration) {
        return lerp(start, end, startTime, duration, EasingType.LINEAR);
    }

    public static float lerp(float start, float end, long startTime, float duration, EasingType easing) {
        float progress = (System.currentTimeMillis() - startTime) / duration;
        progress = Math.min(1, Math.max(0, progress)); // Clamp 0-1
        return start + (end - start) * Easing.apply(easing, progress);
    }

    // Bouncing animation using quadratic ease-out
    public static float bounce(float start, float end, long startTime, float duration) {
        float time = System.currentTimeMillis() - startTime;
        time /= duration;
        return end * (1 - (time - 1) * (time - 1)) + start;
    }

    // Continuous rotation using modulo
    public static float continuousRotation(float speed) {
        return (System.currentTimeMillis() % (360_000 / speed)) * (speed / 1000);
    }

    // Elastic wobble effect
    public static float elasticWobble(float speed, float magnitude) {
        return (float) (Math.sin(System.currentTimeMillis() * speed) *
                Math.exp(-0.001 * System.currentTimeMillis()) * magnitude);
    }
}