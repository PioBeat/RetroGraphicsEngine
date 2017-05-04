package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.PointF;

/**
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
}
