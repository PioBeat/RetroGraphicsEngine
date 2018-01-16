package net.offbeatpioneer.retroengine.core.util;

import android.graphics.Color;

/**
 * Helper class for color operations
 *
 * @author Dominik Grzelak
 * @since 2017-01-15
 */
public class ColorTools {

    /**
     * Test if two colours are the same within a specified tolerance
     *
     * @param color1    first colour
     * @param color2    second colour
     * @param tolerance tolerance, between 0 - 255 (RGB range)
     * @return true, if the colours matches within the tolerance, otherwise false
     */
    public static boolean closeMatch(int color1, int color2, int tolerance) {
        if (Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if (Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if (Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }
}
