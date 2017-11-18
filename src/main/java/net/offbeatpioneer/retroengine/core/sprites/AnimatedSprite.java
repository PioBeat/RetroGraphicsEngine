package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.*;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;
import net.offbeatpioneer.retroengine.core.eventhandling.EmptyAction;
import net.offbeatpioneer.retroengine.core.eventhandling.IActionEventCallback;

import java.util.ArrayList;

/**
 * Sprite class which represents a animated sprite and implements some basic functionalities which
 * influences the visuals and properties of it.
 * Subclass from {@link AbstractSprite}.
 * A sprite can be animated or a static one.
 * The position, angle (Ausrichtung), size, alpha value can be changed.
 * This includes the bounding box for collision as well.
 *
 * @author Dominik Grzelak
 */
public class AnimatedSprite extends AbstractSprite implements ISpriteAnimateable {

    private IActionEventCallback actionEventCallback = new EmptyAction();
    protected RectF checkBoundsRect;

    public AnimatedSprite() {
        disable = false;
        parentSprite = null;
        sRectangle = new Rect(0, 0, 0, 0);
        loop = false;
        animations = new ArrayList<>();
        actionEventCallback = new EmptyAction();
        viewportOrigin = new PointF(0, 0);
        frameUpdate = new AnimatedFrameUpdate(this);
        scale = 1f;
        position = new PointF(0, 0);
        checkBoundsRect = new RectF();
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
        this.disable = false;

        this.framePeriod = 1000 / fps;
        this.sRectangle.left = 0;
        this.sRectangle.right = frameW;
        this.frameCnt = frameCount;
//        this.position = pos;
        oldPosition = new PointF(pos.x, pos.y);
        position.set(pos.x, pos.y);
        int speedScalar = 5;
        speed = new PointF(0, 0);
        speed.x = speedScalar;
        speed.y = -speedScalar;

        this.loop = loop;

        cnt = 0;
        frameNr = 0;
        angle = 0;
        active = true;
        autoDestroy = true;

        rect = new RectF(position.x, position.y, position.x + frameW, position.y + frameH);
//        oldPosition = new PointF(p.x, p.y);

        if (parentSprite != null) {
            viewportOrigin = parentSprite.getViewportOrigin() != null ? parentSprite.getViewportOrigin() : new PointF(0, 0);
        }
        if (frameUpdate == null)
            frameUpdate = new AnimatedFrameUpdate(this);
        frameNr = frameUpdate.updateFrame();
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
     */
    public AnimatedSprite init(Bitmap texture, PointF position) {
        return this.init(texture, position, new PointF(0, 0));
    }

    public void updateLogicTemplate() {
        if (autoDestroy) {
            PointF o = getViewportOrigin();
            checkBoundsRect.set(o.x - (int) (RetroEngine.W * bufferZoneFactor),
                    o.y - (int) (RetroEngine.H * bufferZoneFactor),
                    o.x + (int) (RetroEngine.W * (1.0 + bufferZoneFactor)),
                    o.y + (int) (RetroEngine.H * (1.0 + bufferZoneFactor))
            );
            if (!ContainsRect(checkBoundsRect)) {
                active = false;
            }
        }
    }

    /**
     * Overrides the action method by calling the action method of the {@code actionEventCallback} class member
     * instance.
     * Switching actions at runtime is therefore possible
     *
     * @param parameter
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
