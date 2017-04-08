package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;

/**
 * A static background layer to use within a state.
 * It will fill the whole drawing surface with a bitmap.
 *
 * @author Domini Grzelak
 */
public class StaticBackgroundLayer implements BackgroundLayer {
    private Bitmap background;
    private Bitmap backgroundResized;
    private Paint paint = new Paint();

    // Abmessungen einer Kachel für das Layer
    private int layerW;
    private int layerH;

    private PointF referencePoint;

    private PointF viewportOrigin;

    /**
     * Default constructor
     *
     * @param br Bitmap for the bitmap
     */
    public StaticBackgroundLayer(Bitmap br) {
        background = br;

        layerW = background.getWidth();
        layerH = background.getHeight();

        scaleToFit();
    }

    public PointF getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(PointF referencePoint) {
        this.referencePoint = referencePoint;
    }


    private void scaleToFit() {
        if (background == null) {
            return;
        }
        float scaleWidth = ((float) RetroEngine.W) / layerW;
        float scaleHeight = ((float) RetroEngine.H) / layerH;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        backgroundResized = Bitmap.createBitmap(background, 0, 0, layerW, layerH, matrix, false);
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        int displayW = canvas.getWidth();
        int displayH = canvas.getHeight();

        PointF o = viewportOrigin;

        // Sichtwelt ausgehend vom Referenzpunkt vollstaendig bekacheln
        canvas.drawBitmap(backgroundResized, o.x, o.y, paint);
    }

    public AbstractSprite getParent() {
        return null;
    }

    @Override
    public void updateLogic() {

    }

    public PointF getViewportOrigin() {
        return viewportOrigin;
    }

    public void setViewportOrigin(PointF viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
    }

    @Override
    public void addAnimation(AnimationSuite animation) {

    }

    @Override
    public void beginAnimation() {

    }

    @Override
    public void stopAnimations() {

    }

    @Override
    public void beginAnimation(int idx) {

    }

    @Override
    public void beginAnimation(Class<? extends AnimationSuite> suiteClass) {

    }

    @Override
    public AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass) {
        return null;
    }
}
