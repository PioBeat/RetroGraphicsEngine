package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.sprites.eventhandling.EmptyAction;
import net.offbeatpioneer.retroengine.core.sprites.eventhandling.IActionEventCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Sprite class which represents an animated sprite and implements some basic functionality which
 * influences the visuals and properties of it. Subclass of {@link AbstractSprite}.
 * <p>
 * A sprite can be animated or a static one.
 * The position, angle (i.e., direction), size, alpha value can be modified.
 * This includes the bounding box for collision as well.
 *
 * @author Dominik Grzelak
 */
public class AnimatedSprite extends AbstractSprite {

    private IActionEventCallback actionEventCallback;
    protected RectF checkBoundsRect;
    final List<AnimationSuite> animations = new ArrayList<>();

    public AnimatedSprite() {
        super();
        this.actionEventCallback = new EmptyAction();
        this.checkBoundsRect = new RectF();
    }


    public AnimatedSprite initAsAnimation(Bitmap bitmap, int height, int width, int fps, int frameCount, PointF pos, boolean loop) {
        if (bitmap != null) {
            this.texture = bitmap;
            this.backupTexture = bitmap;
        }
        this.frameH = height;
        this.frameW = width;
        this.sRectangle.top = 0;
        this.sRectangle.bottom = frameH;
        this.disabled = false;
        this.frameUpdate = new AnimatedFrameUpdate(this);
        this.framePeriod = 1000 / fps;
        this.sRectangle.left = 0;
        this.sRectangle.right = frameW;
        this.frameCnt = frameCount;

        this.oldPosition = new PointF(pos.x, pos.y);
        this.position.set(pos.x, pos.y);
        this.speed = new PointF(0, 0); // initial speed
        int speedScalar = 5;
        this.speed.x = speedScalar;
        this.speed.y = -speedScalar;

        this.loop = loop;
        this.viewportOrigin = new PointF(0, 0);
        this.cnt = 0;
        this.frameNr = 0;
        this.angle = 0;
        this.active = true;
        this.autoDestroy = true;

        this.rect = new RectF(position.x, position.y, position.x + frameW, position.y + frameH);
//        oldPosition = new PointF(p.x, p.y);

        if (this.parentSprite != null) {
            this.viewportOrigin = this.parentSprite.getViewportOrigin() != null ? this.parentSprite.getViewportOrigin() : new PointF(0, 0);
        }
        if (this.frameUpdate == null)
            this.frameUpdate = new AnimatedFrameUpdate(this);
        this.frameNr = this.frameUpdate.updateFrame();
        return this;
    }

    /**
     * Basic initialisation of a sprite object. Sprite is not animated and represents a static graphic.
     * <p>
     * If sprite shouldn't move you can set the speed vector to (0,0) or call the
     * alternative init-method {@code init(Bitmap texture, PointF position)}.
     *
     * @param tex Texture of the sprite
     * @param pos Position of the sprite
     * @param spd Speed of the sprites. For still ("stationary") sprites use {@code PointF(0,0)}
     * @return the initialized sprite
     */
    public AnimatedSprite init(Bitmap tex, PointF pos, PointF spd) {
        if (tex != null) {
            this.texture = tex;
            this.backupTexture = tex;
        }

        if (this.texture != null) {
            this.frameW = this.texture.getWidth();
            this.frameH = this.texture.getHeight();
        } else {
            this.frameW = 0;
            this.frameH = 0;
        }

        this.viewportOrigin = new PointF(0, 0);
        this.frameCnt = 1;
        this.frameStep = 1;
        this.speed = spd;
        this.oldPosition = new PointF(pos.x, pos.y);
        this.position = new PointF(pos.x, pos.y);
        this.alphaValue = 255;
        this.cnt = 0;
        this.frameNr = 0;
        this.framePeriod = 1000 / 25;
        this.rect = new RectF(position.x, position.y, position.x + frameW, position.y + frameH);
        this.angle = 0;
        //forceIdleness = false;
        this.active = true;
        this.autoDestroy = true;
        this.frameUpdate = new NoFrameUpdate();
        return this;
    }

    /**
     * Convenient method for sprites with no texture film stripe.
     * Speed is set to zero.
     *
     * @param texture  Texture of the sprite
     * @param position Position of the sprite
     * @return the initialized sprite
     */
    public AnimatedSprite init(Bitmap texture, PointF position) {
        return this.init(texture, position, new PointF(0, 0));
    }

    @Override
    public void updateLogic() {
        super.updateLogic();
        for (int i = 0, n = animations.size(); i < n; i++) {
            AnimationSuite animation = animations.get(i);
            animation.animationLogic();
        }
    }

    public void updateLogicHook() {
        // do nothing here
    }

    /**
     * Add an animation to the sprite
     *
     * @param animation the animation to add
     */
    public void addAnimation(AnimationSuite animation) {
        if (animation.getAnimatedSprite() == null)
            animation.setAnimatedSprite(this);
        animations.add(animation);
    }


    /**
     * Add multiple animation at once. The order matters.
     * {@link AbstractSprite#addAnimations(AnimationSuite...)}
     *
     * @param animation the animations to add
     */
    public void addAnimations(AnimationSuite... animation) {
        for (AnimationSuite each : animation) {
            addAnimation(each);
        }
    }

    /**
     * Start all animations for the current sprite.
     */
    public void beginAnimation() {
        for (int i = 0, n = animations.size(); i < n; i++) {
            AnimationSuite animationSuite = animations.get(i);
            animationSuite.startAnimation();
        }
    }

    /**
     * Stop all animations.
     */
    public void stopAnimations() {
        for (int i = 0, n = animations.size(); i < n; i++) {
            AnimationSuite animationSuite = animations.get(i);
            animationSuite.stop();
        }
    }


    /**
     * Starts a specific animation
     *
     * @param idx Index of the added animation to start
     */
    public void beginAnimation(int idx) {
        if (idx >= 0 && idx < animations.size()) {
            try {
                animations.get(idx).startAnimation();
            } catch (Exception e) {
                Log.e("Animation Error", "Animation could not be started, index does not exist.", e);
            }
        }
    }

    /**
     * Starts a specific animation by providing the class type.
     * The first occurrence is used, see {@link AbstractSprite#findAnimation(Class)}.
     *
     * @param suiteClass Class type of the animation to start
     */
    public void beginAnimation(Class<? extends AnimationSuite> suiteClass) {
        try {
            AnimationSuite animation;
            if ((animation = findAnimation(suiteClass)) != null) {
                animation.startAnimation();
            }
        } catch (Exception e) {
            Log.e("Animation Error", "Animation could not be started, it does not exist.", e);
        }
    }

    /**
     * Search for an animation by the class of a sprite.
     * The first occurrence is returned of the given class.
     *
     * @param suiteClass Class type of the animation to look for
     * @return Animation of type {@link AnimationSuite} or {@code null} if animation is not present.
     */
    public AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass) {
        for (int i = 0, n = animations.size(); i < n; i++) {
            AnimationSuite animationSuite = animations.get(i);
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

    /**
     * Replaces all previous animations by the new one.
     *
     * @param animations Animations to replace the previous set ones.
     */
    public void setAnimations(List<AnimationSuite> animations) {
        this.animations.clear();
        this.animations.addAll(animations);
    }

    /**
     * Overrides the action method by calling the action method of the {@code actionEventCallback} class member
     * instance.
     * Switching actions at runtime is therefore possible
     *
     * @param parameter additional parameter for the action callback method
     */
    @Override
    public void onAction(Object parameter) {
        actionEventCallback.onAction(parameter);
    }

    public IActionEventCallback getActionEventCallback() {
        return actionEventCallback;
    }

    /**
     * Sets the action class for event handling. If null is supplied an {@link EmptyAction}
     * will be assigned instead. So null actions are not allowed.
     *
     * @param actionEventCallback specific action
     */
    public void setActionEventCallback(IActionEventCallback actionEventCallback) {
        if (actionEventCallback == null) {
            this.actionEventCallback = new EmptyAction();
        } else {
            this.actionEventCallback = actionEventCallback;
        }
    }

}
