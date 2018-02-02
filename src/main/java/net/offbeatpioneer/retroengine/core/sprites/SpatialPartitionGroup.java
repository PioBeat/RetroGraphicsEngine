package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.RectF;

import java.util.Collection;

/**
 * Base class for sprite groups that can be spatially distributed.
 * <p>
 * Sub classes implement the concrete data structure. <br>
 * Currently the following kind of group exist:
 * <ul>
 * <li>Quadtree (development)</li>
 * <li>Grid (development)</li>
 * </ul>
 *
 * @author Dominik Grzelak
 * @since 22.11.2017.
 */
public abstract class SpatialPartitionGroup<T> extends AbstractSprite implements ISpriteGroup {
    private RectF queryRange = new RectF();

    /**
     * Return all children of the group no matter of the position.
     *
     * @return collection of all children
     */
    public abstract Collection<T> getChildren();

    /**
     * Return only children within a specific query range.
     * A subset of the children is returned that match the query and only lying in this
     * rect.
     *
     * @param left   left position
     * @param top    top position
     * @param right  right position
     * @param bottom bottom position
     * @return collection of children lying wihtin the query range
     */
    public abstract Collection<T> getChildren(float left, float top, float right, float bottom);

    /**
     * Convenient method for {@link SpatialPartitionGroup#getChildren(float, float, float, float)}
     *
     * @param queryRange the query range
     * @return collection of children lying wihtin the query range
     */
    public abstract Collection<T> getChildren(RectF queryRange);

    @Override
    public boolean isRoot() {
        return getParent() == null;
    }

    public RectF getQueryRange() {
        return queryRange;
    }

    public void setQueryRange(RectF queryRange) {
        this.queryRange = queryRange;
    }
}
