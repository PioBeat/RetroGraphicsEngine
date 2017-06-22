package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.sprites.ISprite;
import net.offbeatpioneer.retroengine.core.sprites.ISpriteAnimateable;

/**
 * Interface to implement a concrete background type
 *
 * @author Dominik Grzelak
 */
public interface BackgroundLayer extends ISprite, ISpriteAnimateable {
    PointF getViewportOrigin();

    void setViewportOrigin(PointF viewportOrigin);

    PointF getReferencePoint();

    void recycle();

    void setReferencePoint(PointF referencePoint);

}
