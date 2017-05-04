package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.*;

import java.util.List;

/**
 * Decorator for sprites
 *
 * @author Dominik Grzelak
 *         Created by Dome on 14.09.2014.
 */
public abstract class Decorator extends AnimatedSprite {

    private AbstractSprite sprite;

    public Decorator(AbstractSprite sprite) {
        this.sprite = sprite;
    }

    public AbstractSprite getSprite() {
        return sprite;
    }

    public void setSprite(AbstractSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public boolean hasChildren() {
        return sprite.hasChildren();
    }

//    @Override
//    public List<AbstractSprite> getChildren() {
//        return sprite.getChildren();
//    }

    @Override
    public void resetPosition() {
        sprite.resetPosition();
    }

    @Override
    public void translate(PointF p) {
        sprite.translate(p);
    }

    @Override
    public PointF getViewportOrigin() {
        return sprite.getViewportOrigin();
    }

    @Override
    public void setViewportOrigin(PointF viewportOrigin) {
        sprite.setViewportOrigin(viewportOrigin);
    }

    @Override
    public void onAction(Object parameter) {
        if(this.sprite instanceof AnimatedSprite) {
            ((AnimatedSprite) this.sprite).onAction(parameter);
        }
    }

    //    @Override
//    public AbstractSprite onAction() {
//        return this.sprite.onAction();
//    }

    public PointF getPosition() {
        return sprite.getPosition();
    }

    public float getAngle() {
        return sprite.getAngle();
    }

    public int getFrameH() {
        return sprite.getFrameH();
    }

    public int getFrameW() {
        return sprite.getFrameW();
    }

    @Override
    public Bitmap getTexture() {
        return sprite.getTexture();
    }

    @Override
    public void setTexture(Bitmap texture) {
        sprite.setTexture(texture);
    }

    @Override
    public RectF getRect() {
        return sprite.getRect();
    }

    @Override
    public boolean isActive() {
        return sprite.isActive();
    }

    @Override
    public void setActive(boolean active) {
        sprite.setActive(active);
    }

    @Override
    public PointF getSpeed() {
        return sprite.getSpeed();
    }

    @Override
    public int getType() {
        return sprite.getType();
    }

    @Override
    public boolean isLoop() {
        return sprite.isLoop();
    }

    @Override
    public int getCnt() {
        return sprite.getCnt();
    }

    @Override
    public int getFrameNr() {
        return sprite.getFrameNr();
    }

    @Override
    public int getFrameCnt() {
        return sprite.getFrameCnt();
    }

    @Override
    public int getFrameStep() {
        return sprite.getFrameStep();
    }

    public void setPosition(PointF position) {
        sprite.setPosition(position);
    }

    @Override
    public IFrameUpdate getFrameUpdate() {
        return sprite.getFrameUpdate();
    }

    @Override
    public void setFrameUpdate(IFrameUpdate frameUpdate) {
        sprite.setFrameUpdate(frameUpdate);
    }
}
