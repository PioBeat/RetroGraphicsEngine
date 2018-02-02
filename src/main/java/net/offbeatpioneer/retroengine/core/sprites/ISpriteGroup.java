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

    /**
     * Determine if the current sprite is the root sprite
     *
     * @return true if this Sprite is the root node, otherwise false
     */
    boolean isRoot();

    /**
     * Remove all inactive sprites.
     * Sprites that have the attribute active set to {@code false} should be removed
     * from the group.
     */
    void removeInActive();

    /**
     * Add a sprite to the group
     *
     * @param child the sprite to add
     */
    void add(AbstractSprite child);

    /**
     * Set the viewpoint origin for the sprite group. This information is necessary for
     * the position calculation to get the real world coordinates.
     *
     * @param viewportOrigin the viewport origin
     */
    void setViewportOrigin(PointF viewportOrigin);

    /**
     * Return the viewport origin
     *
     * @return the viewport origin
     */
    PointF getViewportOrigin();

    /**
     * Get the count of the childrens added to this group.
     * <p>
     * If a child in this group is also a group, then its children aren't included for the overall
     * size.
     *
     * @return number of direct children in the group
     */
    int getChildrenSize();

    /**
     * Remove all sprites in the group
     */
    void clearSprites();
}
