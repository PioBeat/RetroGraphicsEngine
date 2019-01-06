package net.offbeatpioneer.retroengine.core.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import net.offbeatpioneer.retroengine.core.RetroEngine;


/**
 * Helper class for mathematical and statistical calculations as well as conversion
 * functions.
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
    public static double getRad(float grad) {
        return (Math.PI / 180.0) * grad;
    }

    /**
     * Conversion of radian to degrees
     */
    public static double getDegree(double rad) {
        return Math.toDegrees(rad);
    }

    /**
     * Conversion of pixels to density independent pixels.
     * <p>
     * Uses the density value in {@link RetroEngine} for the conversion. If not initialised (density equals zero)
     * then the system resource is acquired with {@code Resources.getSystem().getDisplayMetrics().densityDpi}
     * to get the density dpi value.
     *
     * @param pixel a pixel value
     * @return value in dp
     */
    public static float convertPixelToDp(float pixel) {
        float density = RetroEngine.DENSITY == 0f ?
                Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT :
                RetroEngine.DENSITY;
        float dp = pixel / density;
        return Math.round(dp);
    }

    /**
     * Conversion of pixels to density independent pixels.
     * <p>
     * Uses the density value in {@link RetroEngine} for the conversion. If not initialised (density equals zero)
     * then the system resource is acquired with {@code Resources.getSystem().getDisplayMetrics().densityDpi}
     * to get the density dpi value.
     *
     * @param dp a dp value
     * @return value in pixels
     */
    public static float convertDpToPixel(float dp) {
        float density = RetroEngine.DENSITY == 0f ?
                Resources.getSystem().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT :
                RetroEngine.DENSITY;
        return Math.round(dp * density);
    }

}
