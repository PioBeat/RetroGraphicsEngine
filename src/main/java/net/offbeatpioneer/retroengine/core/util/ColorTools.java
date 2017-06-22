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
     * Test if two colours are the same
     *
     * @param color1
     * @param color2
     * @param tolerance
     * @return
     */
    public static boolean closeMatch(int color1, int color2, int tolerance) {
        if ((int) Math.abs(Color.red(color1) - Color.red(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.green(color1) - Color.green(color2)) > tolerance)
            return false;
        if ((int) Math.abs(Color.blue(color1) - Color.blue(color2)) > tolerance)
            return false;
        return true;
    }
}
