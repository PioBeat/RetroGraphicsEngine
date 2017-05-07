package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Interface for grouping sprites
 * <p>
 * Subclasses implement different collections on how to hold and manage the sprites.
 *
 * @author Dominik Grzelak
 * @since 04.05.2017
 */
public interface ISpriteGroup<T> extends ISprite {
    boolean isRoot();

    void removeInActive();

    void add(AbstractSprite child);

    void setViewportOrigin(PointF viewportOrigin);

    PointF getViewportOrigin();

    T getChildren();

    int getChildrenSize();

    void clearSprites();

    void setQueryRange(RectF queryRange);
}
