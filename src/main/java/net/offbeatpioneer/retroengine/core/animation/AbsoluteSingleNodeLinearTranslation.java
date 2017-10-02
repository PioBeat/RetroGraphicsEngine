package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.ISprite;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * Absolute translation for one sprite for which this animation is introduced.
 * It operates on the screen coordinates. So even if the canvas / background is scrolled (translated)
 * translation will be the same. <br>
 * It supports the 4 directions defined in the enum  {@link Direction}, where the start point is defined
 * in the constructor by the first argument {@code start}
 *
 * @author Dominik Grzelak
 * @since 12.09.2014
 */
public class AbsoluteSingleNodeLinearTranslation extends AnimationSuite {

    public enum Direction {
        TOPRIGHT, TOPLEFT,
        BOTTOMRIGHT, BOTTOMLEFT,
        TOPCENTER, BOTTOMCENTER,
        CENTERRIGHT, CENTERLEFT
    }

    private Direction direction;
    private PointF currentPosition;

    //    private PointF[] values;
    private int N;
    private int counter;
    private PointF start;
    private PointF end;
    private long now;

    private int offsetX = 0;
    private int offsetY = 0;

//    long timePassed = 0;

    public AbsoluteSingleNodeLinearTranslation(Direction direction, int time) {
        super();
        this.counter = 0;
        setTime(time);
        now = 0;
        this.direction = direction;
        N = (time / 1000 * RetroEngine.TICKS_PER_SECOND);
        currentPosition = new PointF(0, 0);
    }

    public AbsoluteSingleNodeLinearTranslation(AbstractSprite sprite, Direction direction, int time) {
        this(direction, time);
        setStart(sprite.getPosition());

        //Abstand
        offsetX = (int) Math.abs(start.x - (RetroEngine.W / 2));
        offsetY = (int) Math.abs(start.y - (RetroEngine.H / 2));
    }


    public PointF getTopRightCorner() {
        float x = start.x + offsetX + (RetroEngine.W / 2);
        float y = start.y - offsetY - (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getTopLeftCorner() {
        float x = start.x - offsetX - (RetroEngine.W / 2);
        float y = start.y - offsetY - (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getBottomLeftCorner() {
        float x = start.x - offsetX - (RetroEngine.W / 2);
        float y = start.y + offsetY + (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getBottomRightCorner() {
        float x = start.x + offsetX + (RetroEngine.W / 2);
        float y = start.y + offsetY + (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getBottomCenter() {
        float x = start.x;
        float y = start.y + offsetY + (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getTopCenter() {
        float x = start.x;
        float y = start.y - offsetY - (RetroEngine.H / 2);
        return new PointF(x, y);
    }

    public PointF getCenterLeft() {
        float x = start.x - offsetX - (RetroEngine.W / 2);
        float y = start.y;
        return new PointF(x, y);
    }

    public PointF getCenterRight() {
        float x = start.x + offsetX + (RetroEngine.W / 2);
        float y = start.y;
        return new PointF(x, y);
    }

    @Override
    protected void animationLogicTemplate() {
        PointF ende;
        switch (this.direction) {
            case TOPLEFT:
                ende = getTopLeftCorner();
                break;
            case TOPRIGHT:
                ende = getTopRightCorner();
                break;
            case BOTTOMLEFT:
                ende = getBottomLeftCorner();
                break;
            case BOTTOMRIGHT:
                ende = getBottomRightCorner();
                break;
            case BOTTOMCENTER:
                ende = getBottomCenter();
                break;
            case CENTERLEFT:
                ende = getCenterLeft();
                break;
            case CENTERRIGHT:
                ende = getCenterRight();
                break;
            case TOPCENTER:
                ende = getTopCenter();
                break;
            default:
                ende = getTopRightCorner();
                break;
        }

        float xValue = InterpolationHelper.linearPointBetween(start.x, ende.x, counter, N);
        float yValue = InterpolationHelper.linearPointBetween(start.y, ende.y, counter, N);
        currentPosition.set(xValue, yValue);

        if (counter >= N) {
            getAnimatedSprite().setPosition(currentPosition);
            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                return;
            }
            getListener().onAnimationRepeat(this);
        }

        counter++;
        getAnimatedSprite().setPosition(currentPosition);
    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }
}
