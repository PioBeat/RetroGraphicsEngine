package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.sprites.ISprite;
import net.offbeatpioneer.retroengine.core.sprites.ISpriteAnimateable;

/**
 * Created by Dome on 13.01.2017.
 */

public interface BackgroundLayer extends ISprite, ISpriteAnimateable {
    PointF getViewportOrigin();

    void setViewportOrigin(PointF viewportOrigin);

    PointF getReferencePoint();

    void setReferencePoint(PointF referencePoint);

}
