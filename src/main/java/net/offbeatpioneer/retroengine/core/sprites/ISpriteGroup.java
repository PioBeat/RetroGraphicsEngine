package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.PointF;
import android.graphics.RectF;

import java.util.Collection;
import java.util.List;

/**
 * Interface for group structures that can hold sprites.
 * <p>
 * Subclasses implement different collections on how to hold and manage the sprites.
 *
 * @author Dominik Grzelak
 * @since 04.05.2017
 */
public interface ISpriteGroup extends ISprite {
    boolean isRoot();

    void removeInActive();
//    void removeInActive(List<T> childs);

    void add(AbstractSprite child);

    void setViewportOrigin(PointF viewportOrigin);

    PointF getViewportOrigin();

    int getChildrenSize();

    void clearSprites();

    void setQueryRange(RectF queryRange);
}
