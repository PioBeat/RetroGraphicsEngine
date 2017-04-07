package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.eventhandling.EmptyAction;
import net.offbeatpioneer.retroengine.core.eventhandling.IActionEventCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * AbstractSprite base class.
 *
 * @author Dominik Grzelak
 * @since 14.01.2017.
 */

public abstract class AbstractSprite implements ISprite {
    AbstractSprite parentSprite;
    final double bufferZoneFactor = 0.2;
    List<AnimationSuite> animations = new ArrayList<AnimationSuite>();

    protected Bitmap texture; // Textur-Filmstreifen
    //    protected Bitmap texture2;
    Bitmap backupTexture;
    protected PointF speed; // Pixelgeschwindigkeit pro Frame in x-, y-Richtung
    protected PointF position; // aktuelle Position
    PointF oldPosition; //Backup, wenn transliert wurde, die ursprüngliche Version beibehalten zum zurücksetzen
    PointF viewportOrigin;
    Rect sRectangle;
    private int bufferZone = 0;
    private int fps;
    int alphaValue = 255;
    boolean loop;
    int cnt; // interner Zaehler
    int frameNr = 0; // aktuelles Frame
    int frameCnt; // Anzahl Frames im Filmstreifen
    int framePeriod;    // milliseconds between each frame (1000/fps)
    @Deprecated
    int frameStep; // Anzahl Frames pro Durchlauf
    protected int frameW; // Breite eines Frames
    protected int frameH; // Höhes eines Frames
    protected float angle; // Winkel in Grad, um den das Sprite gedreht wird

    private int type; // Sprite-Typ
    //int tolBB; // Bounding Box - Toleranz
    private int cycleCnt; // Anzahl der Wiederholungen des Filmstreifens
    //boolean forceIdleness; // keine Animation, wenn Sprite stillsteht
    protected boolean active; // inaktive Sprites werden vom GameManager gel�scht
    protected boolean autoDestroy; // Außerhalb eines Toleranzbereiches wird active = false gesetzt
    protected long starttime = 0;
    protected Paint paint = new Paint();
    float scale = 1.0f;

    //Nicht gleich löschen, sondern nur nicht zeichnen
    //Wird für Gruppen-Nodes verwendet
    boolean disable;

    protected IFrameUpdate frameUpdate = new NoFrameUpdate();

    @Override
    public void updateLogic() {
        frameNr = frameUpdate.updateFrame();
        updateLogicTemplate();
        for (AnimationSuite animation : getAnimations()) {
            animation.animationLogic();
        }
    }

    public int getFps() {
        return fps;
    }

    public void setFps(int fps) {
        this.fps = fps;
    }

    public abstract void updateLogicTemplate();

    /**
     * Draw/Paint method of a sprite. This defines the logic in which way the sprite is drawn on the surface.
     * The surface where the sprite is drawn should be either a {@link net.offbeatpioneer.retroengine.view.DrawView} or a {@link android.view.View} with canvas.
     * <p>
     * If the the sprites property disable is true then the it will not be drawn. It still exists in the
     * root node of a state.
     * <p>
     * The texture of the sprite is drawn via transformation of matrices (Scale, Translate, Rotation) on the surface.
     * This is a generic drawing function which is working with bitmap textures.
     * <p>
     * The method is empty but will stop the execution if the attribute {@code disable} is true.
     *
     * @param canvas      a canvas, the drawing surface
     * @param currentTime current time in milliseconds
     */
    public void draw(Canvas canvas, long currentTime) {
        //Don't draw the sprite if it's disabled
        if (disable) {
            return;
        }

        PointF pivotPoint = new PointF(
                getPosition().x + getFrameW() / 2,
                getPosition().y + getFrameH() / 2);

        paint.setAlpha(getAlphaValue());

        Matrix transformationMatrix = new Matrix();
        transformationMatrix.postScale(getScale(), getScale(), pivotPoint.x, pivotPoint.y);
        transformationMatrix.postRotate(getAngle(), pivotPoint.x, pivotPoint.y);
        transformationMatrix.preTranslate(getPosition().x, getPosition().y);

        canvas.drawBitmap(getTexture(), transformationMatrix, paint);
    }

    // Composite ops
    public boolean hasChildren() {
        return false;
    }

    public List<AbstractSprite> getChildren() {
        return new ArrayList<>();
    }

    public void translate(PointF p) {
        if (oldPosition == null) {
            oldPosition = getPosition();
        }
        this.position = new PointF(position.x + p.x, position.y + p.y);
    }

    /**
     * Functionality of a sprite for events or such alike. For example if a collision with this
     * sprite is detected.
     * <p>
     * Sub classes should override this method to implement their own behaviour or
     * handling of action events.
     *
     * @param parameter Parameter for the action event
     */
    public void onAction(Object parameter) {
    }

    public void resetPosition() {
        this.position = oldPosition;
        oldPosition = null;
    }

    public void addAnimation(AnimationSuite animation) {
        if (animations == null)
            animations = new ArrayList<AnimationSuite>();
        if (animation.getAnimatedSprite() == null)
            animation.setAnimatedSprite(this);
        animations.add(animation);
    }

    /**
     * Startet alle Animationen sofort
     */
    public void beginAnimation() {
        if (animations != null) {
            for (AnimationSuite animationSuite : animations)
                animationSuite.startAnimation();
        }
    }

    public void stopAnimations() {
        if (animations == null)
            return;
        for (AnimationSuite animationSuite : animations)
            animationSuite.stop();
    }


    /**
     * Starts a specific animation
     *
     * @param idx Index of the animation to start
     */
    public void beginAnimation(int idx) {
        if (animations != null) {
            try {
                animations.get(idx).startAnimation();
            } catch (Exception e) {
                Log.e("Animation Error", "Animation could not be started, Index does not exist.");
            }
        }
    }

    /**
     * Starts a specific animation
     *
     * @param suiteClass Class type of the animation to start
     */
    public void beginAnimation(Class<? extends AnimationSuite> suiteClass) {
        if (animations != null) {
            try {
                findAnimation(suiteClass).startAnimation();
            } catch (Exception e) {
                Log.e("Animation Error", "Animation could not be started, Index does not exist.");
            }
        }
    }

    /**
     * Search an animation of a sprite
     *
     * @param suiteClass Class type of the animation to look for
     * @return Animation of type {@link AnimationSuite} or {@code null} if animation is not present
     */
    public AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass) {
        for (AnimationSuite animationSuite : animations) {
            if (animationSuite.getClass() == suiteClass)
                return animationSuite;
        }
        return null;
    }

    /**
     * Retrieve all animations
     *
     * @return List of animations of type {@link AnimationSuite}
     */
    public List<AnimationSuite> getAnimations() {
        return animations;
    }

    public void setAnimations(List<AnimationSuite> animations) {
        this.animations = animations;
    }

    /**
     * Ausmaße des Sprites zurückgeben als Rechteck.
     *
     * @return Ausmaße des Sprites von Typ {@link Rect}
     */
    public RectF getRect() {
        return new RectF(position.x, position.y, position.x + frameW, position.y + frameH);
    }

    /**
     * Get the origin of the viewport (absolute center point of the canvas). This is important if the
     * canvas itself is translated on which the sprites are drawn.
     * Technically the difference of the actor ("player") sprite position and the actual middle point of the canvas itself represents the viewport.
     * This point is only important if the canvas is translated to simulate player movement and simultaneously
     * have the player at the same position.
     *
     * @return origin point of the viewport, absolute (real) center point coordinates of the canvas
     */
    public PointF getViewportOrigin() {
        if (parentSprite != null) {
            viewportOrigin = parentSprite.getViewportOrigin() != null ? parentSprite.getViewportOrigin() : new PointF(0, 0);
        }
        return viewportOrigin;
    }

    /**
     * Set the viewport origin. This is the reference point to the whole system where all sprites
     * are drawn. There has to be an absolute "null" point.
     * This method is automatically called within a state by the rootNode sprite group which gets
     * this information from the background node.
     *
     * @param viewportOrigin origin viewport coordinates as {@link PointF}
     */
    public void setViewportOrigin(PointF viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
    }

    /**
     * Kollisionsabfrage des Sprites mit einem Punkt.
     *
     * @param p Punkt vom Typ {@link Point} der auf Kollision innerhalb des
     *          Sprites geprüft werden soll.
     * @return Wahrheitswert, <b>true</b> wenn sich der Punkt innerhalb des
     * Sprites befindet, andernfalls <b>false</b>
     */
    public boolean checkColWithPoint(PointF p) {
        RectF rect = this.getRect();
        if (rect.contains(p.x, p.y)) {
            return true;
        }
        return false;
    }

    /**
     * Kollisionsabfrage des Sprites mit einem anderem Sprite, ob diese sich
     * schneiden.
     *
     * @param r Ausmaße des Sprite muss als {@link Rect} übergeben werden.
     * @return Wahrheitswert: <b>true</b> wenn sich beide Sprites
     * schneiden/berühren, andernfalls <b>false</b>
     */
    public boolean intersectWithRect(RectF r) {
        RectF rect = this.getRect();
        if (rect.intersect(r)) {
            return true;
        }
        return false;
    }

    /**
     * Kollisionsabfrage des Sprites mit einem anderem Sprite, ob sich dieser
     * vollst�ndig in einem anderen befindet.
     *
     * @param r Ausma�e des Sprite muss als {@link Rect} �bergeben weden.
     * @return Wahrheitswert: <b>true</b> wenn sich der �bergebene Sprite
     * innerhalb des anderen befindet, andernfalls <b>false</b>
     */
    public boolean ContainsRect(RectF r) {
        RectF rect = this.getRect();
        if (r.contains(rect)) {
            return true;
        }
        return false;
    }

    /**
     * Position des Sprite setzen.
     *
     * @param p Positionsangabe vom Typ {@link Point}
     */
    public void setPosition(PointF p) {
        if (oldPosition == null) {
            oldPosition = p;
        }
        //TODO crashed bei sidescroller background (FixedBackground) evtl. unnötig
//        if (getViewportOrigin() != null) {
//            p = new PointF(getViewportOrigin().x + p.x, getViewportOrigin().y + p.y);
//        }
        this.position = p;
    }

    /**
     * Positionsabfrage
     *
     * @return Position vom Typ {@link Point}
     */
    public PointF getPosition() {
//        if(getViewportOrigin() != null) {
//            this.position = new PointF(getViewportOrigin().x + position.x, getViewportOrigin().y + position.y);
//        }
        return this.position;
    }

    /**
     * �berpr�ft, ob das Sprite noch "aktiv" ist, d.h. ob es entfernt werden
     * kann.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Setze den Aktiv-Status des Sprites.
     *
     * @param active Wahrheitswert, <b>true</b> f�r aktiv.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Setze die Geschwindigkeit des Sprites, mit der es sich in x,y-Richtung bewegen soll.
     *
     * @param sxy Geschwindigkeit als Vektor f�r x,y-Richtung vom Typ {@link Point}
     */
    public void setSpeed(PointF sxy) {
        speed = sxy;
    }

    /**
     * Gibt den aktuellen Geschwindigkeitsvektor des Sprites zur�ck.
     *
     * @return Geschwindigkeit als Vektor f�r x,y-Richtung vom Typ {@link Point}
     */
    public PointF getSpeed() {
        return speed;
    }

    public IFrameUpdate getFrameUpdate() {
        return frameUpdate;
    }

    public void setFrameUpdate(IFrameUpdate frameUpdate) {
        this.frameUpdate = frameUpdate;
    }

    /**
     * Gib aktuelle Rotation des Sprite zur�ck.
     *
     * @return Rotationswinkel des Sprites.
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Setze den Rotationswinkel des Sprites.
     *
     * @param angle Rotationswinkel von 0 bis 360.
     */
    public void setAngle(float angle) {
        this.angle = angle;
    }

    public AbstractSprite getParent() {
        return this.parentSprite;
    }

    public void setParentSprite(AbstractSprite parentSprite) {
        this.parentSprite = parentSprite;
    }


    /**
     * Like margin for a sprite
     *
     * @return
     */
    public int getBufferZone() {
        return bufferZone;
    }

    /**
     * set the "margin" of a sprite. Can be used for collision detection or other things
     *
     * @param bufferZone
     */
    public void setBufferZone(int bufferZone) {
        this.bufferZone = bufferZone;
    }

    public Rect getsRectangle() {
        return sRectangle;
    }

    public void setsRectangle(Rect sRectangle) {
        this.sRectangle = sRectangle;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Bitmap getTexture() {
        return texture;
    }

    public void setTexture(Bitmap texture) {
        this.texture = texture;
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public int getFrameNr() {
        return frameNr;
    }

    public void setFrameNr(int frameNr) {
        this.frameNr = frameNr;
    }

    public int getFrameCnt() {
        return frameCnt;
    }

    public void setFrameCnt(int frameCnt) {
        this.frameCnt = frameCnt;
    }

    public int getFrameStep() {
        return frameStep;
    }

    public void setFrameStep(int frameStep) {
        this.frameStep = frameStep;
    }

    public int getFrameW() {
        return frameW;
    }

    public int getFramePeriod() {
        return framePeriod;
    }

    public void setFramePeriod(int framePeriod) {
        this.framePeriod = framePeriod;
    }

    public void setFrameW(int frameW) {
        this.frameW = frameW;
    }

    public int getFrameH() {
        return frameH;
    }

    public void setFrameH(int frameH) {
        this.frameH = frameH;
    }

    public int getCycleCnt() {
        return cycleCnt;
    }

    public void setCycleCnt(int cycleCnt) {
        this.cycleCnt = cycleCnt;
    }

    public boolean isAutoDestroy() {
        return autoDestroy;
    }

    public void setAutoDestroy(boolean autoDestroy) {
        this.autoDestroy = autoDestroy;
    }

    public long getStarttime() {
        return starttime;
    }

    public void setStarttime(long starttime) {
        this.starttime = starttime;
    }

    public int getAlphaValue() {
        return alphaValue;
    }

    public void setAlphaValue(int alphaValue) {
        this.alphaValue = alphaValue;
    }

    public boolean isDisable() {
        return disable;
    }

    /**
     * Set the visibility of a sprite. If true then the draw method will do nothing. The sprite wont
     * be displayed.
     *
     * @param disable true, if sprite should not be displayed
     */
    public void setDisable(boolean disable) {
        this.disable = disable;
    }

    public Bitmap getBackupTexture() {
        return backupTexture;
    }

    public void setBackupTexture(Bitmap backupTexture) {
        this.backupTexture = backupTexture;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}
