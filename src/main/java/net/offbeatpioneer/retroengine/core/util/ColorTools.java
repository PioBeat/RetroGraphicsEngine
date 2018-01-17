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
     * Test if two colours are the same within a specified tolerance by calculating the euclidean distance
     * for all RGB values of the passed colors.
     * <p>
     * The alpha channel is not taken into account for measuring the distance between the two
     * colors. <br>
     * The maximum distance between two completely opposite colors (e.g. black and white) is ca. 441.67.
     * So the {@code tolerance} argument must be between 0 and 442.
     *
     * @param color1    first colour
     * @param color2    second colour
     * @param tolerance tolerance value between 0 - 442
     * @return true, if the colors matches within the tolerance, otherwise false
     */
    public static boolean closeMatch(int color1, int color2, int tolerance) {
        double dist = Math.pow(Color.red(color2) - Color.red(color1), 2)
                + Math.pow(Math.abs(Color.green(color2) - Color.green(color1)), 2)
                + Math.pow(Math.abs(Color.blue(color2) - Color.blue(color1)), 2);

        if (dist <= Math.pow(tolerance, 2)) {
            return true;
        }
        return false;
    }
}
