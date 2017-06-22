package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.IFrameUpdate;

import static net.offbeatpioneer.retroengine.core.util.BitmapHelper.scaleToFit;

/**
 * Scrollable background layer with the possibility to load a second background for
 * color coded collision detection
 *
 * @author Dominik Grzelak
 * @since 13.01.2017
 */
public class FixedScrollableLayer implements BackgroundLayer {
    private Bitmap background;
    private Bitmap backgroundBackUp;
    private Paint paint = new Paint();

    // Abmessungen einer Kachel für das Layer
    private int layerW;
    private int layerH;

    // Referenzpunkt der Parallaxebene, relativ zur Spielerposition
    private float refX;
    private float refY;

    private Rect sRectangle = new Rect(0, 0, 0, 0);

    // Vorherige Spielerposition
    float oldPx = 0;
    float oldPy;

    private PointF referencePoint;
    private float factor;

    private PointF viewportOrigin;
    int sectionWidth;
    private int frameCnt;
    private int frameNr;
    private int nx2;
    float diffX = 0;
    IFrameUpdate frameUpdate;

    public FixedScrollableLayer(Bitmap br, int sectionWidth, float factor) {
        background = br;
        backgroundBackUp = br;
        frameCnt = 2;
        frameNr = 0;

        layerW = sectionWidth;
        layerH = background.getHeight();
        nx2 = layerW;

        this.sRectangle.top = 0;
        this.sRectangle.bottom = layerH;
        this.sRectangle.left = 0;
        this.sRectangle.right = layerW;

        this.factor = factor;
        viewportOrigin = new PointF(0, 0);
        float scaleWidth = ((float) RetroEngine.W) / layerW;
        float scaleHeight = (float) Math.ceil((float) RetroEngine.H / (float) layerH);
//        float scaleHeight2 = (float)layerH / RetroEngine.H;
        background = scaleToFit(backgroundBackUp, scaleWidth, scaleHeight);
    }

    public int updateFrame() {
//        sprite.setFrameNr(sprite.getFrameNr() + 1);
        if (frameNr >= frameCnt) {
            frameNr = 0;
//            sprite.setFrameNr(0);
//            if (!sprite.isLoop())
//                sprite.setActive(false);
        }

        int xx = (int) (refX + layerW); //viewportOrigin.x;
        if (xx < 0) xx = 0;
        if (xx >= backgroundBackUp.getWidth()) xx = backgroundBackUp.getWidth() - layerW;

        Rect sRectangle = this.sRectangle;
        sRectangle.left = xx; // * layerW;
        sRectangle.right = (sRectangle.left + layerW);
        Bitmap texture;
        try {
            texture = Bitmap.createBitmap(backgroundBackUp,
                    sRectangle.left,
                    sRectangle.top,
                    sRectangle.width(),
                    sRectangle.height()
            );
            this.sRectangle = sRectangle;
//            background = texture;
            float scaleWidth = ((float) RetroEngine.W) / layerW;
            float scaleHeight = ((float) RetroEngine.H) / layerH;
            background = scaleToFit(texture, scaleWidth, scaleHeight);
//            sprite.setsRectangle(sRectangle);
//            sprite.setTexture2(texture);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 1;
        //return sprite.getFrameNr();
    }

    public PointF getReferencePoint() {
        return referencePoint;
    }

    @Override
    public void recycle() {
        if (!background.isRecycled()) background.recycle();
        if (!backgroundBackUp.isRecycled()) backgroundBackUp.recycle();
    }

    public Bitmap getBackground() {
        return background;
    }

    public void setBackground(Bitmap background) {
        this.background = background;
    }

    public Bitmap getBackgroundBackUp() {
        return backgroundBackUp;
    }

    public void setBackgroundBackUp(Bitmap backgroundBackUp) {
        this.backgroundBackUp = backgroundBackUp;
    }

    public void setReferencePoint(PointF referencePoint) {
        this.referencePoint = referencePoint;
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        canvas.drawBitmap(background, 0, 0, paint);
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
