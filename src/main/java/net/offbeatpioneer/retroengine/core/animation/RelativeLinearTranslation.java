package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.SpriteGroup;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

import java.util.List;

/**
 * @author Dominik Grzelak
 * @since 12.09.2014
 */
public class RelativeLinearTranslation extends AnimationSuite {

    private PointF currentPosition;
    private PointF[] values;
    private int N;
    private int counter;
    private PointF start;
    private PointF end;
    private float amountX = 0;
    private float amountY = 0;

    //Typ mitgeben welche art von end-Vektor: zB absolute angabe oder als "speed"-Vektor
    //Speed-vektor kann man auch ausrechnen: v=s/t
    public RelativeLinearTranslation(PointF end, int time) {
        super();
        this.counter = 0;
        //milliseconds = time;
        setTime(time);
        setStart(start);
        setEnd(end);
        currentPosition = start;
        float timeReal = ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS) * (1.0f));
//        (this.milliseconds / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        float vx = (end.x / timeReal);
        float vy = (end.y / (timeReal));
        N = (int) ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
//        float N2 = (time / ((RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS)*(1.0f)));
        amountX = vx; //(float)(N*vx);
        amountY = vy; //(float)(N*vy);
//        Log.v("N", ""+N);
//        Log.v("Speed aufgeteilt nach N", ""+(N/v));
//        int[] xvalues = new int[N]; //InterpolationHelper.linear(start.x, end.x, N);
//        int[] yvalues = InterpolationHelper.linear(start.y, end.y, N);
//        int[] yvalues = new int[N];
//        Arrays.fill(yvalues, end.y);
//        Arrays.fill(xvalues, end.x);
        values = new PointF[N];
        for (int i = 0; i < N; i++) {
//            values[i] = new Point(end.x, end.y);
            values[i] = new PointF(amountX, amountY);
        }
    }

    @Override
    protected void animationLogicTemplate() {
        if (counter >= N) {

            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if (isDoReset()) {
                    animateResetPosition(getAnimatedSprite().getChildren());
                    getAnimatedSprite().resetPosition();
                } else {
                    //Do nothing because sprite will end in the last position set
                }
                if (getTimer() != null)
                    getTimer().cancel();
                return;
            } else {
                if (getAnimatedSprite() instanceof SpriteGroup) {
                    animateResetPosition(getAnimatedSprite().getChildren());
                } else {
                    getAnimatedSprite().resetPosition();
                }
                getListener().onAnimationRepeat(this);
            }
        }

        PointF start = new PointF(getAnimatedSprite().getPosition().x, getAnimatedSprite().getPosition().y);
        PointF pp = new PointF(start.x + amountX, start.y + amountY);
//        float xValue = InterpolationHelper.linearPointBetween(start.x, pp.x, counter, N);
//        float yValue = InterpolationHelper.linearPointBetween(start.y, pp.y, counter, N);
        float xValue = values[counter].x;
        float yValue = values[counter].y;
        currentPosition = new PointF(xValue, yValue);
        counter++;
        if (getAnimatedSprite() instanceof SpriteGroup) {
            animateSetPosition(getAnimatedSprite().getChildren(), currentPosition);
        } else {
            getAnimatedSprite().translate(currentPosition);
        }
    }

    private void animateSetPosition(List<AbstractSprite> childs, PointF position) {
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                animateSetPosition(child.getChildren(), position);

            }
            child.translate(position);
//            child.setPosition(new PointF(position.x, position.y));
        }
    }

    private void animateResetPosition(List<AbstractSprite> childs) {
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                animateResetPosition(child.getChildren());

            }
            child.resetPosition();
        }
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
