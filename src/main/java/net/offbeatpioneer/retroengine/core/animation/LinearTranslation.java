package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * Created by Dome on 12.09.2014.
 */
@Deprecated
public class LinearTranslation extends AnimationSuite {

    private float currentPosition;

    private float[] xvalues;
    private int N;
    private int counter;

    public LinearTranslation(float x, float xend, int time) {
        super();
        currentPosition = x;
        this.counter = 0;
        N = (int) (time / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        xvalues = InterpolationHelper.linear(x, xend, N);
    }

    @Override
    protected void animationLogicTemplate() {

        if (counter >= xvalues.length) {

            PointF newPos = getAnimatedSprite().getPosition();
            newPos.set(currentPosition, newPos.y);
            getAnimatedSprite().setPosition(newPos);
            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                return;
            }
            getListener().onAnimationRepeat(this);
        }

        currentPosition = xvalues[counter++];
        PointF newPos = getAnimatedSprite().getPosition();
        newPos.set(currentPosition, newPos.y);
        getAnimatedSprite().setPosition(newPos);
    }

}
