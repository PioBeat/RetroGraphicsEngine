package net.offbeatpioneer.retroengine.core.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Various helper methods for bitmap operations
 *
 * @author Dominik Grzelak
 * @since 2017-04-01
 */
public class BitmapHelper {

    /**
     * Scales a bitmap to fit a specified width and size
     *
     * @param bitmap      source bitmap
     * @param scaleWidth  new width of the bitmap
     * @param scaleHeight new height of the bitmap
     * @return new scaled bitmap or null if bitmap was null
     */
    public static Bitmap scaleToFit(Bitmap bitmap, float scaleWidth, float scaleHeight) {
        if (bitmap == null) {
            return null;
        }

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
    }
}
