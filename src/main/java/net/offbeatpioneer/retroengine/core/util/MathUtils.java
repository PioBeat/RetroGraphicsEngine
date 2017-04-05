package net.offbeatpioneer.retroengine.core.util;

import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.core.RetroEngine;

import org.jetbrains.annotations.Contract;

/**
 * Helper class for mathematical and statistical calculations
 *
 * @author Dominik Grzelak
 */
public class MathUtils {

    /**
     * Random number between to integer values
     *
     * @param min minimum
     * @param max maximum
     * @return random value between minimum and maximum
     */
    public static int getRandomBetween(int min, int max) {
        return min + (int) (Math.random() * ((max - min) + 1));
    }

    @Contract(pure = true)
    public static int calcPixelsPerFrameUpdate(int fps, int way, int time) {
        float v = way / time;
        int amountOfUpdatesPerSecond = 1000 / fps;
        int result = (int) (v / amountOfUpdatesPerSecond + 0.5f);
        return result;

    }

    public static PointF getRandomStartPosition(PointF originPoint, RectF rect, float density) {
        int px = 0, py = 0, securityZone = (int) (rect.width()
                * density + 0.5f);
        switch (MathUtils.getRandomBetween(0, 1)) {
            case 0:
                // Oben
                px = MathUtils.getRandomBetween(0, RetroEngine.W);
                py = MathUtils.getRandomBetween(0, RetroEngine.H / 2
                        - securityZone);
                break;
            case 1:
                // Unten
                px = MathUtils.getRandomBetween(0, RetroEngine.W);
                py = MathUtils.getRandomBetween(RetroEngine.H / 2 + securityZone,
                        RetroEngine.H);
                break;
        }
        return new PointF(originPoint.x + px, originPoint.y + py);
    }

    /**
     * Conversion of degree in radian
     *
     * @param grad angle in degree
     * @return angle in radian
     */
    @Contract(pure = true)
    public static double getRad(float grad) {
        return (Math.PI / 180.0) * grad;
    }
}
