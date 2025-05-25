package com.tanishisherewith.dynamichud.utils;

import com.tanishisherewith.dynamichud.DynamicHUD;

public class Util {
    public static Quadrant getQuadrant(int x, int y) {
        int screenWidth = DynamicHUD.MC.getWindow().getScaledWidth();
        int screenHeight = DynamicHUD.MC.getWindow().getScaledHeight();

        if (x < screenWidth / 2) {
            if (y < screenHeight / 2) {
                return Quadrant.UPPER_LEFT;
            } else {
                return Quadrant.BOTTOM_LEFT;
            }
        } else {
            if (y < screenHeight / 2) {
                return Quadrant.UPPER_RIGHT;
            } else {
                return Quadrant.BOTTOM_RIGHT;
            }
        }
    }

    public enum Quadrant {
        UPPER_LEFT, UPPER_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT
    }

    public static boolean errorIfTrue(boolean expression, String message, Object... objects){
        if(!expression) DynamicHUD.logger.error(message, objects);
        return expression;
    }

    public static boolean warnIfTrue(boolean expression, String message, Object... objects){
        if(expression) DynamicHUD.logger.warn(message, objects);
        return expression;
    }
}
