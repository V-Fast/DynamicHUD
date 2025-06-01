package com.tanishisherewith.dynamichud.helpers.animationhelper;

public class Easing {
    public static float apply(EasingType easingType, float progress) {
        return switch (easingType) {
            case LINEAR -> progress;
            case EASE_IN_SINE -> (float) (1 - Math.cos((progress * Math.PI) / 2));
            case EASE_OUT_SINE -> (float) Math.sin((progress * Math.PI) / 2);
            case EASE_IN_OUT_SINE -> (float) (-(Math.cos(Math.PI * progress) - 1) / 2);
            case EASE_IN_QUAD -> progress * progress;
            case EASE_OUT_QUAD -> 1 - (1 - progress) * (1 - progress);
            case EASE_IN_OUT_QUAD ->
                    progress < 0.5 ? 2 * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 2) / 2);
            case EASE_IN_CUBIC -> progress * progress * progress;
            case EASE_OUT_CUBIC -> (float) (1 - Math.pow(1 - progress, 3));
            case EASE_IN_OUT_CUBIC ->
                    progress < 0.5 ? 4 * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 3) / 2);
            case EASE_IN_QUART -> progress * progress * progress * progress;
            case EASE_OUT_QUART -> (float) (1 - Math.pow(1 - progress, 4));
            case EASE_IN_OUT_QUART ->
                    progress < 0.5 ? 8 * progress * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 4) / 2);
            case EASE_IN_QUINT -> progress * progress * progress * progress * progress;
            case EASE_OUT_QUINT -> (float) (1 - Math.pow(1 - progress, 5));
            case EASE_IN_OUT_QUINT ->
                    progress < 0.5 ? 16 * progress * progress * progress * progress * progress : (float) (1 - Math.pow(-2 * progress + 2, 5) / 2);
            case EASE_IN_EXPO -> (float) (progress == 0 ? 0 : Math.pow(2, 10 * progress - 10));
            case EASE_OUT_EXPO -> (float) (progress == 1 ? 1 : 1 - Math.pow(2, -10 * progress));
            case EASE_IN_OUT_EXPO -> {
                if (progress == 0 || progress == 1) yield progress;
                yield (float) (progress < 0.5
                        ? Math.pow(2, 20 * progress - 10) / 2
                        : (2 - Math.pow(2, -20 * progress + 10)) / 2);
            }
            case EASE_IN_CIRC -> (float) (1 - Math.sqrt(1 - Math.pow(progress, 2)));
            case EASE_OUT_CIRC -> (float) Math.sqrt(1 - Math.pow(progress - 1, 2));
            case EASE_IN_OUT_CIRC -> progress < 0.5
                    ? (float) ((1 - Math.sqrt(1 - Math.pow(2 * progress, 2))) / 2)
                    : (float) ((Math.sqrt(1 - Math.pow(-2 * progress + 2, 2)) + 1) / 2);
            case EASE_IN_BACK -> (float) (2.70158 * progress * progress * progress - 1.70158 * progress * progress);
            case EASE_OUT_BACK -> {
                float c1 = 1.70158f;
                float c3 = c1 + 1;
                yield (float) (1 + c3 * Math.pow(progress - 1, 3) + c1 * Math.pow(progress - 1, 2));
            }
            case EASE_IN_OUT_BACK -> {
                float c1 = 1.70158f;
                float c2 = c1 * 1.525f;
                yield (float) (progress < 0.5
                        ? (Math.pow(2 * progress, 2) * ((c2 + 1) * 2 * progress - c2)) / 2
                        : (Math.pow(2 * progress - 2, 2) * ((c2 + 1) * (progress * 2 - 2) + c2) + 2) / 2);
            }
            case EASE_IN_ELASTIC -> {
                float c4 = (float) (2 * Math.PI / 3);
                yield progress == 0 ? 0 : progress == 1 ? 1 : (float) (-Math.pow(2, 10 * progress - 10) * Math.sin((progress * 10 - 10.75) * c4));
            }
            case EASE_OUT_ELASTIC -> {
                float c4 = (float) (2 * Math.PI / 3);
                yield progress == 0 ? 0 : progress == 1 ? 1 : (float) (Math.pow(2, -10 * progress) * Math.sin((progress * 10 - 0.75) * c4) + 1);
            }
            case EASE_IN_OUT_ELASTIC -> {
                float c5 = (float) (2 * Math.PI / 4.5);
                yield progress == 0 ? 0 : progress == 1 ? 1 : progress < 0.5
                        ? (float) (-(Math.pow(2, 20 * progress - 10) * Math.sin((20 * progress - 11.125) * c5)) / 2)
                        : (float) (Math.pow(2, -20 * progress + 10) * Math.sin((20 * progress - 11.125) * c5) / 2 + 1);
            }
            case EASE_IN_BOUNCE -> 1 - bounceOut(1 - progress);
            case EASE_OUT_BOUNCE -> bounceOut(progress);
            case EASE_IN_OUT_BOUNCE -> progress < 0.5
                    ? (1 - bounceOut(1 - 2 * progress)) / 2
                    : (1 + bounceOut(2 * progress - 1)) / 2;
        };
    }

    private static float bounceOut(float progress) {
        float n1 = 7.5625f;
        float d1 = 2.75f;
        if (progress < 1 / d1) {
            return n1 * progress * progress;
        } else if (progress < 2 / d1) {
            return n1 * (progress -= 1.5f / d1) * progress + 0.75f;
        } else if (progress < 2.5 / d1) {
            return n1 * (progress -= 2.25f / d1) * progress + 0.9375f;
        } else {
            return n1 * (progress -= 2.625f / d1) * progress + 0.984375f;
        }
    }
}
