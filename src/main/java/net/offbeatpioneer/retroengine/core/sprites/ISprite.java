package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.Paint;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

/**
 * Sprite interface for all sprite instances.
 *
 * Sprites can be drawn and have logic for interaction or processing.
 *
 * @author Dominik Grzelak
 * @since 14.09.2014.
 */
public interface ISprite {

    void draw(final Canvas canvas, final long currentTime);

    void updateLogic();

}
