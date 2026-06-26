package com.tanishisherewith.dynamichud.helpers.animationhelper.animations;

import com.tanishisherewith.dynamichud.helpers.animationhelper.Easing;
import com.tanishisherewith.dynamichud.helpers.animationhelper.EasingType;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec2;

import java.util.Random;

public class MathAnimations {
    private static final RandomSource RANDOM = RandomSource.create();

    /**
     * SHAKE: Random offset animation with smooth decay.
     * @param intensity   max displacement
     * @param frequency   in radians per second (e.g. 20 rad/s = ~3 oscillations/sec)
     * @param decay       decay rate per second (e.g. 2.0 means amplitude halves every 0.35s)
     */
    public static float shake(float intensity, float frequency, float decay) {
        double time = System.currentTimeMillis() / 1000.0;
        return (float) (Math.sin(time * frequency) * Math.exp(-decay * time) * intensity);
    }

    /**
     * 2D Shake with different X/Y frequencies.
     */
    public static Vec2 shake2D(float intensity, float freqX, float freqY) {
        double time = System.currentTimeMillis() / 1000.0;
        return new Vec2(
                (float) Math.sin(time * freqX) * intensity,
                (float) Math.cos(time * freqY) * intensity
        );
    }

    /**
     * FLICKER: Random flashing effect.
     * @param chance  probability (0..1) to return a random value in [min, max]; otherwise returns max.
     */
    public static float flicker(float min, float max, float chance) {
        return RANDOM.nextFloat() < chance ?
                min + (max - min) * RANDOM.nextFloat() :
                max;
    }

    /**
     * CIRCULAR MOTION: Orbital animation.
     * @param radius   orbit radius
     * @param speed    radians per second
     * @param phase    initial angle offset in radians
     */
    public static Vec2 circularMotion(float radius, float speed, float phase) {
        double time = System.currentTimeMillis() / 1000.0;
        double angle = time * speed + phase;
        return new Vec2(
                (float) (Math.cos(angle) * radius),
                (float) (Math.sin(angle) * radius)
        );
    }

    /**
     * SAWTOOTH WAVE: Linear rise with sudden drop.
     * @param period  in seconds
     */
    public static float sawtooth(float period, float min, float max) {
        double time = System.currentTimeMillis() / 1000.0;
        double phase = (time % period) / period;
        return (float) (min + (max - min) * phase);
    }

    /**
     * TRIANGULAR WAVE: Linear rise and fall.
     */
    public static float triangleWave(float period, float min, float max) {
        double time = System.currentTimeMillis() / 1000.0;
        double halfPeriod = period / 2;
        double phase = time % period;
        double value = phase < halfPeriod ?
                (phase / halfPeriod) :
                2 - (phase / halfPeriod);
        return (float) (min + (max - min) * value);
    }

    /**
     * BOUNCE: Simulates physical bouncing.
     * @param dropHeight  initial height
     * @param gravity     acceleration (e.g. 9.8)
     * @param dampening   damping factor (e.g. 0.5)
     */
    public static float bounce(float dropHeight, float gravity, float dampening) {
        double t = System.currentTimeMillis() / 1000.0;
        return (float) (dropHeight * Math.abs(Math.sin(t * Math.sqrt(gravity))) *
                Math.exp(-dampening * t));
    }

    /**
     * PULSE (sine): Smooth heartbeat-like.
     * @param frequency  in radians per second
     */
    public static float pulse1(float base, float amplitude, float frequency) {
        double time = System.currentTimeMillis() / 1000.0;
        return (float) (base + amplitude * (0.5 + 0.5 * Math.sin(time * frequency)));
    }

    /**
     * SPIRAL: Circular motion with expanding radius.
     */
    public static Vec2 spiral(float baseRadius, float expansionRate, float speed) {
        double t = System.currentTimeMillis() / 1000.0;
        return new Vec2(
                (float) ((baseRadius + expansionRate * t) * Math.cos(t * speed)),
                (float) ((baseRadius + expansionRate * t) * Math.sin(t * speed))
        );
    }

    /**
     * PULSE (sine) with explicit min/max.
     * @param speed  radians per second
     */
    public static float pulse2(float speed, float min, float max) {
        double time = System.currentTimeMillis() / 1000.0; // seconds
        double val = (float) Math.sin(time * speed);     // -1 to 1
        return  (float) (val + 1) / 2 * (max - min) + min;  // remap to [min, max]
    }

    /**
     * Linear interpolation between values over time.
     * @param startTime  time when animation started (System.currentTimeMillis())
     * @param duration   in milliseconds
     */
    public static float lerp(float start, float end, long startTime, float duration) {
        return lerp(start, end, startTime, duration, EasingType.LINEAR);
    }

    public static float lerp(float start, float end, long startTime, float duration, EasingType easing) {
        double progress = (System.currentTimeMillis() - startTime) / duration;
        progress = Mth.clamp(progress, 0f, 1f);
        return start + (end - start) * Easing.apply(easing, (float) progress);
    }

    /**
     * Bounce animation (quadratic ease-out) over time.
     * @param startTime  when animation started
     * @param duration   in milliseconds
     */
    public static float bounceAnim(float start, float end, long startTime, float duration) {
        double time = (System.currentTimeMillis() - startTime) / duration;
        time = Mth.clamp(time, 0f, 1f);
        return (float) (end * (1 - (time - 1) * (time - 1)) + start);
    }

    /**
     * Continuous rotation (returns degrees 0..360) based on speed in degrees per second.
     */
    public static float continuousRotation(float speedDegPerSec) {
        double time = System.currentTimeMillis() / 1000.0;
        return (float) ((time * speedDegPerSec) % 360);
    }

    /**
     * Elastic wobble (damped sine).
     * @param speed     in radians per second
     * @param magnitude initial amplitude
     */
    public static float elasticWobble(float speed, float magnitude) {
        double t = System.currentTimeMillis() / 1000.0;
        return (float) (Math.sin(t * speed) * Math.exp(-t * 0.5) * magnitude);
    }
}