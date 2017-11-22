package net.offbeatpioneer.retroengine.core.util;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    /**
     * Load a Scaled Down Version into Memory
     *
     * Source: <a href="https://developer.android.com/topic/performance/graphics/load-bitmap.html#read-bitmap">
     * https://developer.android.com/topic/performance/graphics/load-bitmap.html#read-bitmap</a>
     *
     * @param res resource object
     * @param resId resource id of the drawable
     * @param reqWidth new width
     * @param reqHeight new height
     * @return the loaded bitmap with the specified size
     */
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    /**
     * Load a Scaled Down Version into Memory
     *
     * Source: <a href="https://developer.android.com/topic/performance/graphics/load-bitmap.html#read-bitmap">
     * https://developer.android.com/topic/performance/graphics/load-bitmap.html#read-bitmap</a>
     *
     * @param options options for the bitmap
     * @param reqWidth new width
     * @param reqHeight new height
     * @return the new sampled size
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }
}
