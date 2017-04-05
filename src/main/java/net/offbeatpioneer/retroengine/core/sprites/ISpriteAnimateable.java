package net.offbeatpioneer.retroengine.core.sprites;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

/**
 * Sprite interface for animated sprites
 *
 * @author Dominik Grzelak
 * @since 03.03.2017.
 */

public interface ISpriteAnimateable {
    void addAnimation(AnimationSuite animation);

    void beginAnimation();

    void stopAnimations();

    void beginAnimation(int idx);

    void beginAnimation(Class<? extends AnimationSuite> suiteClass);

    AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass);
}
