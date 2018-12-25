package net.offbeatpioneer.retroengine.core.sprites;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.List;

/**
 * Sprite interface for animated sprites
 *
 * @author Dominik Grzelak
 * @since 03.03.2017.
 */

public interface ISpriteAnimateable {
    void addAnimation(AnimationSuite animation);
    void addAnimations(AnimationSuite... animation);

    void beginAnimation();

    void stopAnimations();

    List<AnimationSuite> getAnimations();

    void beginAnimation(int idx);

    void beginAnimation(Class<? extends AnimationSuite> suiteClass);

    AnimationSuite findAnimation(Class<? extends AnimationSuite> suiteClass);
}
