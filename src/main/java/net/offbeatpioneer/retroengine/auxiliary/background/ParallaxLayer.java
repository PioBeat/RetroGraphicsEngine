package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;

/**
 * Parallax background layer to use within a state.
 * <p>
 * A factor can be defined to change the velocity of the translation in relation
 * to a reference sprite.
 *
 * @author Dominik Grzelak
 */
public class ParallaxLayer implements BackgroundLayer {
    private Bitmap background;
    private Paint paint = new Paint();

    // Abmessungen einer Kachel für das Layer
    private int layerW;
    private int layerH;

    // Referenzpunkt der Parallaxebene, relativ zur Spielerposition
    private float refX;
    private float refY;

    // Vorherige Spielerposition
    private float oldPx;
    private float oldPy;

    private PointF referencePoint;
    private float factor;

    private PointF viewportOrigin;

    public ParallaxLayer(Bitmap br, float factor) {
        background = br;

        layerW = background.getWidth();
        layerH = background.getHeight();

        this.factor = factor;
    }

    public PointF getReferencePoint() {
        return referencePoint;
    }

    @Override
    public void recycle() {
        if (!background.isRecycled())
            background.recycle();
    }

    public void setReferencePoint(PointF referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        int displayW = canvas.getWidth();
        int displayH = canvas.getHeight();
        float px = referencePoint.x;
        float py = referencePoint.y;
        float diffX = px - oldPx;
        float diffY = py - oldPy;
        oldPx = px;
        oldPy = py;

        refX -= diffX / factor;
        refY -= diffY / factor;

        // Innerhalb dieser Grenzen wird der Referenzpunkt verschoben
        if (refX > layerW)
            refX = 0;
        if (refX < 0)
            refX = layerW;
        if (refY > layerH)
            refY = 0;
        if (refY < 0)
            refY = layerH;
//        PointF o = Gameplay.getViewportOrigin();
        PointF o = viewportOrigin;

        // Sichtwelt ausgehend vom Referenzpunkt vollstaendig bekacheln
        for (float x = o.x + refX - layerW; x < o.x + displayW; x += layerW) {
            for (float y = o.y + refY - layerH; y < o.y + displayH; y += layerH) {
                canvas.drawBitmap(background, x, y, paint);
            }
        }
    }

    public AbstractSprite getParent() {
        return null;
    }

    public PointF getViewportOrigin() {
        return viewportOrigin;
    }

    public void setViewportOrigin(PointF viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
    }


    @Override
    public void updateLogic() {

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
