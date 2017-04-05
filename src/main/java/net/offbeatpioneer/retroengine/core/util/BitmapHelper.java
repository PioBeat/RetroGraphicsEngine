package net.offbeatpioneer.retroengine.core.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Various helper methods for bitmap operations
 *
 * @author Dominik Grzelak
 * @since 01.04.2017
 */
public class BitmapHelper {

    public static Bitmap scaleToFit(Bitmap background, float scaleWidth, float scaleHeight) {
        if (background == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        background = Bitmap.createBitmap(background, 0, 0, background.getWidth(), background.getHeight(), matrix, false);
        return background;
    }
}
