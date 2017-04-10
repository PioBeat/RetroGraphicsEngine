package net.offbeatpioneer.retroengine.core.animation;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * Scale animation for sprites
 *
 * @author Dominik Grzelak
 * @since 24.01.2017
 */

public class ScaleAnimation extends AnimationSuite {

    private float currentScale;
    private float startScale;
    private float endScale;
    private int milliseconds;
    private float[] scaleValues;
    private int N;
    private int counter;

    private ScaleAnimation() {
        super();
    }

    public ScaleAnimation(float startScale, float endScale, int milliseconds) {
        this.startScale = startScale;
        this.endScale = endScale;
        this.milliseconds = milliseconds;
        this.counter = 0;
        this.currentScale = this.startScale;
        this.milliseconds = milliseconds;
        N = (this.milliseconds / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        this.scaleValues = InterpolationHelper.linear(this.startScale, this.endScale, N);
    }

    /**
     * Concrete logic for scale animation
     */
    @Override
    protected void animationLogicTemplate() {
        if (counter >= this.scaleValues.length) {
            getAnimatedSprite().setScale(currentScale);
            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if(isDoReset()) {
                    reset();
                } else {
                    getAnimatedSprite().setScale(this.endScale);
                }
                if (getTimer() != null)
                    getTimer().cancel();
                return;
            }
            getListener().onAnimationRepeat(this);
        }

        currentScale = scaleValues[counter++];
        getAnimatedSprite().setScale(currentScale);
    }

    @Override
    public void reset() {
        super.reset();
        getAnimatedSprite().setScale(this.startScale);
    }
}
