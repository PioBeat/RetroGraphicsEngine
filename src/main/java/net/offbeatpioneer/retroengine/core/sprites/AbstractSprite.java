package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import net.offbeatpioneer.retroengine.auxiliary.matheusdev.CollectionEntity;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AbstractSprite} is the base class for sprites
 *
 * @author Dominik Grzelak
 * @since 2017-01-14
 */
public abstract class AbstractSprite implements CollectionEntity, ISprite {
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
    private RectF rect;
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

    @Override
    public net.offbeatpioneer.retroengine.auxiliary.matheusdev.Rect getAABB() {
        return new net.offbeatpioneer.retroengine.auxiliary.matheusdev.Rect(getPosition().x, getPosition().y, frameW, frameH);
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
    public void draw(final Canvas canvas, final long currentTime) {
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

        paint.setAntiAlias(true);
        paint.setFilterBitmap(false);
        paint.setDither(false);

        canvas.drawBitmap(getTexture(), transformationMatrix, paint);
    }

    // Composite ops
    public boolean hasChildren() {
        return false;
    }

//    public Object getChildren() {
//        return new ArrayList<>();
//    }

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
     * Return the extent of this sprite as rectangle
     *
     * @return Extent of the sprite
     */
    public RectF getRect() {
        if(rect == null)
            rect = new RectF(position.x, position.y, position.x + frameW, position.y + frameH);
        else {
            rect.set(position.x, position.y, position.x + frameW, position.y + frameH);
        }
        return rect;
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
     * Check for collision with a point.
     *
     * @param p Point of type {@link PointF} to check if it's inside the sprites extent
     * @return true if point is inside the sprite, otherwise false
     */
    public boolean checkColWithPoint(PointF p) {
        RectF rect = this.getRect();
        return rect.contains(p.x, p.y);
    }

    /**
     * Check for collision.
     * <p>
     * Check if this sprites intersects with another sprite.
     *
     * @param r Extent of another sprite a {@link RectF}
     * @return true if this sprites intersects with a rectangle, otherwise false
     */
    public boolean intersectWithRect(RectF r) {
        RectF rect = this.getRect();
        return rect.intersect(r);
    }

    /**
     * Check for collision.
     * <p>
     * Determine if a rectangle is fully contained in the sprites extent.
     *
     * @param r Extent of another sprite as {@link RectF}
     * @return true if rectangle is contained in the sprites extent, otherwise false
     */
    public boolean ContainsRect(RectF r) {
        RectF rect = this.getRect();
        return r.contains(rect);
    }

    /**
     * Set position of the sprite. You have to add the viewport origin by yourself
     * if the canvas is translated to set the correct position.
     *
     * @param p position vector
     */
    public void setPosition(PointF p) {
        if (oldPosition == null) {
            oldPosition = p;
        }
        this.position = p;
    }

    /**
     * Position of the sprite
     *
     * @return position of type {@link PointF}
     */
    public PointF getPosition() {
//        if(getViewportOrigin() != null) {
//            this.position = new PointF(getViewportOrigin().x + position.x, getViewportOrigin().y + position.y);
//        }
        return this.position;
    }

    /**
     * Check if sprite is <i>active</i>, that means that the sprite will be drawn by the
     * {@link net.offbeatpioneer.retroengine.core.states.State} class.
     * <p>
     * This property is evaluated in the {@link net.offbeatpioneer.retroengine.core.states.State} class
     * when sprites are checked to be removed from the render cycle.
     *
     * @return true, if sprite is active, otherwise false.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set the status of a sprite whether it should be removed from the {@link net.offbeatpioneer.retroengine.core.states.State}
     * class or not.
     *
     * @param active true, if sprite should be drawn, otherwise false if it should be removed
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Set the velocity of a sprite in the (x,y)-direction.
     *
     * @param sxy velocity as vector
     */
    public void setSpeed(PointF sxy) {
        speed = sxy;
    }

    /**
     * Get the current velocity vecotor of the sprite in (x,y)-direction
     *
     * @return velocity vector in the (x,y)-direction
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
     * Get the current rotation angle of the sprite in degrees
     *
     * @return rotation angle of sprite in degrees
     */
    public float getAngle() {
        return angle;
    }

    /**
     * Set the rotation angle of the sprite in degrees
     *
     * @param angle rotation angle from 0° to n
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
