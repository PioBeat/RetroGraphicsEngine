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
     * Default constructor.
     * Bitmap is scaled to fit into the screen
     *
     * @param br Bitmap for the bitmap
     */
    public StaticBackgroundLayer(Bitmap br) {
        this(br, true);
    }


    public StaticBackgroundLayer(Bitmap br, boolean scaleToFit) {
        background = br;

        layerW = background.getWidth();
        layerH = background.getHeight();

        if (scaleToFit)
            scaleToFit();
        else
            tiledBitmap();
    }

    public PointF getReferencePoint() {
        return referencePoint;
    }

    public void setReferencePoint(PointF referencePoint) {
        this.referencePoint = referencePoint;
    }


    private void tiledBitmap() {
        if (background == null) {
            return;
        }

        int m = (int) Math.ceil(RetroEngine.W / layerW);
        int n = (int) Math.ceil(RetroEngine.H / layerH);

        Bitmap.Config conf = Bitmap.Config.RGB_565; // see other conf types
        backgroundResized = Bitmap.createBitmap(RetroEngine.W, RetroEngine.H, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(backgroundResized);
        Paint paint = new Paint();
        for (int i = 0; i <= n; i++) {
            for (int j = 0; j <= m; j++) {
                PointF p = calcPosition(j, i);
                canvas.drawBitmap(background, p.x, p.y, paint);
            }
        }
    }

    private PointF calcPosition(int x, int y) {
        return new PointF(x * layerW, y * layerH);
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

    public void recycle() {
        if (!backgroundResized.isRecycled())
            backgroundResized.recycle();
        if (!background.isRecycled())
            background.recycle();
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

//    @Override
//    public void addAnimation(AnimationSuite animation) {
//
//    }
//
//    @Override
//    public void beginAnimation() {
//
//    }
//
//    @Override
//    public void stopAnimations() {
//
//    }
//
//    @Override
//    public void beginAnimation(int idx) {
//
//    }
//
//    @Override
//    public void beginAnimation(Class<? extends AnimationSuite> suiteClass) {
//
//    }
//
//    @Override
//    public AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass) {
//        return null;
//    }
}
