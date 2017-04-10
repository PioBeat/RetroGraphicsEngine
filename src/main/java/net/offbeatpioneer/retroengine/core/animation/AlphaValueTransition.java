package net.offbeatpioneer.retroengine.core.animation;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * Alpha value animation for sprites.
 * <p>
 * The alpha value ranges from 0 to 255, where 255 means the sprite is fully opaque.
 *
 * @author Dominik Grzelak
 * @since 2014-09-15
 */
public class AlphaValueTransition extends AnimationSuite {

    private int currentAlpha;
    private int startAlpha;
    private int endAlpha;
    private int milliseconds;
    private int[] alphaValues;
    private int N;
    private int counter;


    public AlphaValueTransition() {
        super();
    }

    /**
     * Constructor to initialise the animation. Set the start- and end value of the animation.
     * Alpha value range is between 0 and 255, where 255 means fully opaque for the sprite.
     *
     * @param startAlpha   Starting value for the alpha animation. Value between 0 and 255
     * @param endAlpha     Ending value for the alpha animation. Value between 0 and 255
     * @param milliseconds Duration of the animation
     */
    public AlphaValueTransition(int startAlpha, int endAlpha, int milliseconds) {
        this.counter = 0;
        this.startAlpha = startAlpha; //zum Zurücksetzen
        this.endAlpha = endAlpha;
        this.currentAlpha = startAlpha;
        this.milliseconds = milliseconds;
        this.N = (this.milliseconds / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        this.alphaValues = InterpolationHelper.linear(this.startAlpha, this.endAlpha, N);
    }

    @Override
    protected void animationLogicTemplate() {
        if (counter >= alphaValues.length) {
            getAnimatedSprite().setAlphaValue(currentAlpha);
            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if (isDoReset()) {
                    reset();
                } else {
                    getAnimatedSprite().setAlphaValue(this.endAlpha);
                }
                if (getTimer() != null)
                    getTimer().cancel();
                return;
            }
            getListener().onAnimationRepeat(this);
        }

        currentAlpha = alphaValues[counter++];
        getAnimatedSprite().setAlphaValue(currentAlpha);
    }

    public int getEndAlpha() {
        return endAlpha;
    }

    public void setEndAlpha(int endAlpha) {
        this.endAlpha = endAlpha;
    }

    /**
     * Reset the alpha value for the sprite while using the start value of this animation.
     */
    @Override
    public void reset() {
        super.reset();
        getAnimatedSprite().setAlphaValue(this.startAlpha);
    }
}
