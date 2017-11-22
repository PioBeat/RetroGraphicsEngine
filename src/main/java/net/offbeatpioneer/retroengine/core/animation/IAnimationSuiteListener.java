package net.offbeatpioneer.retroengine.core.animation;

/**
 * Listener interface for listening of events from an animation.
 * Possible events:
 * <ul>
 * <li>animation starts</li>
 * <li>animation repeats</li>
 * <li>animation ends</li>
 * </ul>
 * <p>
 * Implement this interface to listen for those event types to take user defined actions.
 *
 * @author Dominik Grzelak
 * @since 2017-03-03
 */

public interface IAnimationSuiteListener {
    /**
     * Listens for the start of an animation
     *
     * @param animationSuite animation class which was responsible for the animation
     */
    void onAnimationStart(AnimationSuite animationSuite);

    /**
     * Listens for a repeat of an animation, if its in looping mode
     *
     * @param animationSuite animation class which was responsible for the animation
     */
    void onAnimationRepeat(AnimationSuite animationSuite);

    /**
     * Listens to the end of an animations, if its finished
     *
     * @param animationSuite animation class which was responsible for the animation
     */
    void onAnimationEnd(AnimationSuite animationSuite);
}
