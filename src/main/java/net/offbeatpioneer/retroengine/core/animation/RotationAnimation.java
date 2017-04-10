package net.offbeatpioneer.retroengine.core.animation;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * RotationAnimation for sprites
 *
 * @author Dominik Grzelak
 * @since 14.09.2014
 */
public class RotationAnimation extends AnimationSuite {
    private float currentAngle;

    private float[] anglePoints;
    private int cnt = 0;

    /**
     * Default constructor for a rotation animation of a sprite
     *
     * @param angleDegree degrees
     * @param time        amount of time for the cycle rotation
     */
    public RotationAnimation(float angleDegree, int time) {
        setTime(time);
        int N = (time / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        anglePoints = InterpolationHelper.linear(0.0f, angleDegree, N);
        cnt = 0;
        currentAngle = anglePoints[cnt];
    }

    /**
     * Constructor which initialises the rotation animation with default values <br>
     * Three cycles in three seconds are realised. <br>
     * Loop is set to {@code false}. The animation will not be repeated.
     */
    public RotationAnimation() {
        this(1080, 3000);
    }

    @Override
    protected void animationLogicTemplate() {

        if (cnt >= anglePoints.length) {
            currentAngle = anglePoints[anglePoints.length - 1];
            cnt = 0;
            getAnimatedSprite().setAngle(0);
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if (isDoReset()) {
                    reset();
                } else {
                    getAnimatedSprite().setAngle(anglePoints[anglePoints.length - 1]);
                }
                return;
            }
            getListener().onAnimationRepeat(this);
        }

        currentAngle = anglePoints[cnt++];
        getAnimatedSprite().setAngle(currentAngle);
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }
}
