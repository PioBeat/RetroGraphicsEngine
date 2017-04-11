package net.offbeatpioneer.retroengine.core.animation;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * {@link RotationAnimation} for sprites
 *
 * @author Dominik Grzelak
 * @since 2014-09-14
 */
public class RotationAnimation extends AnimationSuite {
    private float currentAngle;
    private float startAngle = 0;
    private float[] anglePoints;
    private int cnt = 0;

    /**
     * Default constructor for a rotation animation of a sprite
     *
     * @param angleStart  start angle in degrees
     * @param angleDegree amount of degrees to rotate
     * @param time        amount of time for the cycle rotation
     */
    public RotationAnimation(float angleStart, float angleDegree, int time) {
        super();
        setTime(time);
        this.startAngle = angleStart;
        int N = (time / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        anglePoints = InterpolationHelper.linear(this.startAngle, this.startAngle + angleDegree, N);
        cnt = 0;
        currentAngle = anglePoints[cnt];
    }

    /**
     * Constructor which initialises the rotation animation with default values <br>
     * Three cycles in three seconds are realised. <br>
     * Loop is set to {@code false}. The animation will not be repeated.
     */
    public RotationAnimation() {
        this(0, 1080, 3000);
    }

    @Override
    protected void animationLogicTemplate() {

        if (cnt >= anglePoints.length) {
//            currentAngle = anglePoints[anglePoints.length - 1];
            getAnimatedSprite().setAngle(currentAngle);
            cnt = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if (isDoReset()) {
                    reset();
                } else {
                    getAnimatedSprite().setAngle(anglePoints[anglePoints.length - 1]);
                }
                if (getTimer() != null)
                    getTimer().cancel();
                return;
            }
            getListener().onAnimationRepeat(this);
        }
        System.out.println("currentAngle=" + currentAngle);
        currentAngle = anglePoints[cnt++];
        getAnimatedSprite().setAngle(currentAngle);
    }

    public float getCurrentAngle() {
        return currentAngle;
    }

    public void setCurrentAngle(float currentAngle) {
        this.currentAngle = currentAngle;
    }

    public float getStartAngle() {
        return startAngle;
    }

    @Override
    public void reset() {
        super.reset();
        getAnimatedSprite().setAngle(this.startAngle);
    }
}
