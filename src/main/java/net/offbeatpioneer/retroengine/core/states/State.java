package net.offbeatpioneer.retroengine.core.states;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;

import net.offbeatpioneer.retroengine.auxiliary.background.BackgroundLayer;
import net.offbeatpioneer.retroengine.auxiliary.background.BackgroundNode;
import net.offbeatpioneer.retroengine.core.GamestateManager;
import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.ISpriteGroup;
import net.offbeatpioneer.retroengine.core.sprites.SpriteListGroup;
import net.offbeatpioneer.retroengine.core.sprites.SpriteQuadtreeGroup;
import net.offbeatpioneer.retroengine.view.DrawView;

import java.util.List;

/**
 * Abstract class which represents a scene or state of an graphics application.
 * All states are managed by the {@link GamestateManager} class.
 * <p>
 * A state has always a root node where all created sprites should be appended to. Background layers
 * should be added with the {@code addBackgroundLayer} method.
 * <p>
 * Subclasses have to implement the abstract methods which define the behaviour of a state
 * when it is initialised, rendered or when a state finishes.
 * <p>
 * Furthermore key and touch events are handled by every state and can be intercepted with the
 * class methods {@code onKeyEvent} and {@code onTouchEvent}. During the initialisation of the
 * {@link DrawView} component those methods are bound to the Key- and TouchListener of the
 * {@link DrawView}.
 * <p>
 * State can be initialized asynchronously or synchronously. This property will be read by the
 * {@link DrawView} component and loaded with the {@link android.os.AsyncTask} class.
 * <p>
 *
 * @author Dominik Grzelak
 */
public abstract class State {

    //TODO add display informaion

    public String StateName = "State";
    /**
     * Gibt an, ob der Spielszene aktiv ist.
     */
    protected boolean isActive = false;

    protected boolean isFinished = false;

    public boolean initAsync = false;

    private ISpriteGroup rootGroup;
    private BackgroundNode backgroundNode;
    private boolean scrollWorld = true;

    /**
     * Gibt für jeden State die aktuelle Zeit in ms an, nachdem ein Frame-Update passiert ist.
     */
    //@Deprecated
    protected long timeNow = 0;

    // Save reference to the GamestateManager
    protected GamestateManager manager = GamestateManager.getInstance();

    public State() {
        this(new SpriteListGroup());
    }

    protected State(ISpriteGroup rootGroup) {
        this.rootGroup = rootGroup;
        backgroundNode = BackgroundNode.Builder.create();
        setFinished(false);
//        setReferenceSpriteOffsets(-RetroEngine.W / 2, -RetroEngine.H / 2);
    }

    /**
     * Delegate method from {@link GamestateManager}
     * Returns the {@link Handler} instance from the {@link DrawView} component.
     *
     * @return android {@link Handler} instance from the {@link DrawView} component
     */
    public Handler getHandler() {
        return manager.getHandler();
    }

    public void drawSprites(Canvas canvas, long currentTime) {
        rootGroup.draw(canvas, currentTime);
    }

    public void updateSprites() {
        rootGroup.removeInActive();
        rootGroup.updateLogic();
    }

    /**
     * Add a background layer to the state
     * Order of adding matters.
     *
     * @param backgroundLayer background layer
     */
    public void addBackgroundLayer(BackgroundLayer backgroundLayer) {
        backgroundNode.addLayer(backgroundLayer);
    }

    /**
     * Set the rectangle that defines the search area of the drawing surface. Only necessary when
     * the {@link SpriteQuadtreeGroup} is used as root node of this {@link State}.
     * The update and draw methods of {@link SpriteQuadtreeGroup} will find all points that appear
     * within this range and apply those methods only for the found sprites.
     *
     * @param rect the rectangle where to search for sprites on the drawing surface
     */
    public void setQueryRange(RectF rect) {
        rootGroup.setQueryRange(rect);
    }

    /**
     * Has to be set only if scrolling background are used in a state like {@link net.offbeatpioneer.retroengine.auxiliary.background.ParallaxLayer}
     * Call the method once in the {@code init()} method and in the {@code updateLogic} method.
     *
     * @param referenceSprite reference sprite
     */
    public void setReferenceSprite(AbstractSprite referenceSprite) {
        backgroundNode.setReferenceRect(referenceSprite.getRect()); //einmalig, spielergröße
        backgroundNode.setReferencePoint(referenceSprite.getPosition());
        rootGroup.setViewportOrigin(backgroundNode.getViewportOrigin());
        backgroundNode.setHeight(RetroEngine.H);
        backgroundNode.setWidth(RetroEngine.W);
    }

    /**
     * Set the offset of the position of the reference sprite so that the reference sprite will
     * not be drawn in the center of the screen. Normally it will be positioned in the center
     * if a sprite is set as reference sprite. This is only necessary if: <br>
     * you have a moving / parallax background <b>AND</b> if you don't want the "main" actor
     * (reference sprite) in the center of the screen.
     * <p>
     * Example: To position a reference sprite at the top-left corner you have to call the method
     * like this: {@code setReferenceSpriteOffsets(-RetroEngine.W / 2, -RetroEngine.H / 2);}
     * <p>
     * You need to call this method only once, e.g. in the {@code init()} method of a {@link State}
     *
     * @param offsetX relative offset for the "x-axis"
     * @param offsetY relative offset for the "y-axis"
     */
    public void setReferenceSpriteOffsets(int offsetX, int offsetY) {
        backgroundNode.offsetX = offsetX;
        backgroundNode.offsetY = offsetY;
    }

    public void drawBackground(Canvas canvas) {
        backgroundNode.scrollWorld(canvas, scrollWorld);
    }

    /**
     * Get the number of sprites inserted as direct child in the {@code rootGroup}.
     *
     * @return Number of sprites in {@code rootGroup}
     */
    public int getSpriteCount() {
        return rootGroup.getChildrenSize();
    }

    private ISpriteGroup getRootGroup() {
        return this.rootGroup;
    }

    /**
     * Get the background layer at index position {@code i}
     *
     * @param i Index of the background layer
     * @return background layer at postion {@code i}, otherwise null
     */
    public BackgroundLayer getBackgroundLayer(int i) {
        if (backgroundNode == null || backgroundNode.getBackgrounds().size() == 0) {
            return null;
        }
        return backgroundNode.getBackgrounds().get(i);
    }

    /**
     * Returns the current origin of the viewport that means the origin coordinate of the drawing
     * surface. It will be (0,0) if the canvas is not translated or no background layer was used
     * in the state.
     *
     * @return viewport origin coordinate
     */
    public PointF getViewportOrigin() {
        if (backgroundNode == null) {
            return new PointF(0, 0);
        }
        return backgroundNode.getViewportOrigin();
    }

    synchronized public void addSprite(AbstractSprite sprite) {
        rootGroup.add(sprite);
    }

    synchronized public void addSprites(List<AbstractSprite> sprite) {
        for (AbstractSprite each : sprite) {
            rootGroup.add(each);
        }
    }

    synchronized public void clearSprites() {
        rootGroup.clearSprites();
    }

    public boolean isScrollWorld() {
        return scrollWorld;
    }

    public void setScrollWorld(boolean scrollWorld) {
        this.scrollWorld = scrollWorld;
    }

    /**
     * Initialise method of a state. It will be called from the {@link GamestateManager}.
     * It is recommended to only do initialisation of things that should and can be repeated every time
     * a state is restarted or switched.
     * It's not recommended to load resources like images or such alike.
     */
    public abstract void init();

    public abstract void updateLogic();

    /**
     * Render logic for a state
     *
     * @param canvas      Drawing surface
     * @param paint       Paint
     * @param currentTime current time
     */
    public abstract void render(Canvas canvas, Paint paint, long currentTime);

    /**
     * Process key events
     *
     * @param v        view which sends an event
     * @param keyCode
     * @param keyEvent
     * @return
     */
    public abstract boolean onKeyEvent(View v, int keyCode, KeyEvent keyEvent);


    /**
     * Method to clean all used resources after state is switched or finished
     */
    public abstract void cleanUp();

    /**
     * Methode für das Abfangen von Touchscreeneingaben.  Muss an einen TouchListener gebunden werden.
     *
     * @param v     Event-Auslösende {@link View}
     * @param event
     * @return
     */
    public abstract boolean onTouchEvent(View v, MotionEvent event);

    public String getStateName() {
        return StateName;
    }

    public void setStateName(String stateName) {
        StateName = stateName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean _isActive) {
        this.isActive = _isActive;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }

    public boolean isInitAsync() {
        return initAsync;
    }

    public void setInitAsync(boolean initAsync) {
        this.initAsync = initAsync;
    }
}
