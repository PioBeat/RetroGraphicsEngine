package net.offbeatpioneer.retroengine.core.animation;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.util.InterpolationHelper;

/**
 * Alpha value animation for sprites
 *
 * @author Dominik Grzelak
 * @since 15.09.2014
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
                getAnimatedSprite().setAlphaValue(this.endAlpha);
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

    @Override
    public void reset() {
        super.reset();
        getAnimatedSprite().setAlphaValue(this.startAlpha);
    }
}
