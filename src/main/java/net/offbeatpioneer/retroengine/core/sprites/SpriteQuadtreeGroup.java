package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.auxiliary.struct.quadtree.QuadTree;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * Sprite group which holds its child sprites in a quadtree data structure. The
 * sprites will be arranged in a 2-dimensional space which is the drawing surface in this case.
 * <p>
 * The update and draw methods of {@link SpriteQuadtreeGroup} will find all points that appear
 * within the specified query range and apply those methods only to the found sprites
 * in this area.
 * <p>
 * It may be necessary to regularly update the query range (search rectangle) where the search
 * in the quadtree is performed.
 *
 * @author Dominik Grzelak
 * @since 2017-05-04
 */
public class SpriteQuadtreeGroup extends AbstractSprite implements ISpriteGroup<QuadTree<AbstractSprite>.CoordHolder> {
    private QuadTree<AbstractSprite> children = new QuadTree<>();
    private RectF queryRange = new RectF();

    public SpriteQuadtreeGroup() {
        active = true;
        position = new PointF(0, 0);
        speed = new PointF(0, 0);
    }

    /**
     * you have to set queryRange before or use the other draw methods.
     *
     * @param canvas      a canvas, the drawing surface
     * @param currentTime current time in milliseconds
     */
    @Override
    public void draw(Canvas canvas, long currentTime) {
        List<QuadTree<AbstractSprite>.CoordHolder> childs = getChildren();
        synchronized (childs) {
            this.draw(childs, canvas, currentTime);
        }
    }

    public List<QuadTree<AbstractSprite>.CoordHolder> findAll() {
        return this.findAll(getQueryRange());
    }

    public List<QuadTree<AbstractSprite>.CoordHolder> findAll(RectF queryRange) {
        if (getChildren() == null) {
            return new ArrayList<>();
        }
        return getChildren();
    }

//    public synchronized void draw(QuadTree<AbstractSprite> childs, Canvas canvas, RectF rect, long currentTime) {
//        this.draw(childs, canvas, rect.left, rect.top, rect.right, rect.bottom, currentTime);
//    }

    public synchronized void draw(List<QuadTree<AbstractSprite>.CoordHolder> childs, Canvas canvas,
                                  long currentTime) {
        for (QuadTree.CoordHolder each : childs) {
            AbstractSprite eachSprite = (AbstractSprite) each.o;
            if (eachSprite.hasChildren()) {
                List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren();
                draw(list2, canvas, currentTime);
            } else {
                eachSprite.draw(canvas, currentTime);
            }
        }
    }

    @Override
    public void updateLogic() {
        List<QuadTree<AbstractSprite>.CoordHolder> childs = getChildren();
        synchronized (childs) {
            update(childs);
        }
    }

    private List<QuadTree<AbstractSprite>.CoordHolder> collectAllItems(QuadTree<AbstractSprite> childs) {
        List<QuadTree<AbstractSprite>.CoordHolder> list = new ArrayList<>();
        list.addAll(childs.root.LL.items);
        list.addAll(childs.root.LR.items);
        list.addAll(childs.root.UL.items);
        list.addAll(childs.root.UR.items);
        return list;
    }

    public synchronized void update(List<QuadTree<AbstractSprite>.CoordHolder> list) {
        for (QuadTree<AbstractSprite>.CoordHolder each : list) {
            AbstractSprite eachSprite = each.o;
            if (eachSprite.hasChildren() && eachSprite.isActive()) {
                eachSprite.updateLogicTemplate();
                List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren();
                update(list2);
            } else {
                if (eachSprite.isActive()) {
                    eachSprite.updateLogic();
                } else {
                    each.remove();
                }
            }
        }
    }

    @Override
    public void updateLogicTemplate() {
        for (AnimationSuite animation : getAnimations()) {
            animation.animationLogic();
        }
    }

    public synchronized void add(AbstractSprite child) {
        children.place(child.getPosition().x, child.getPosition().y, child);
    }

    public void removeInActive() {
        List<QuadTree<AbstractSprite>.CoordHolder> childs = getChildren();
        synchronized (childs) {
            removeInActive(childs);
        }
    }

    public void removeInActive(List<QuadTree<AbstractSprite>.CoordHolder> list) {
        for (int i = list.size() - 1; i >= 0; i--) {
            QuadTree.CoordHolder each = list.get(i);
            AbstractSprite eachSprite = (AbstractSprite) each.o;
            if (eachSprite.hasChildren()) {
                if (!eachSprite.isActive()) {
                    each.remove();
                } else {
                    List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren();
                    removeInActive(list2);
                }
            } else {
                if (!eachSprite.isActive()) {
                    each.remove();
                }
            }
        }
    }

    /**
     * Calls the onAction method of every child in the group.
     *
     * @param parameter Parameter for the action event
     */
    @Override
    public void onAction(Object parameter) {
        List<QuadTree<AbstractSprite>.CoordHolder> childs = getChildren();

        for (QuadTree.CoordHolder each : childs) {
            ((AbstractSprite) each.o).onAction(parameter);
        }
    }

    public synchronized void clearSprites() {
        children.root.items.clear();
    }

    /**
     * Determine if the current sprite is the root sprite
     *
     * @return true if this Sprite is the root node, otherwise false
     */
    @Override
    public boolean isRoot() {
        return getParent() == null;
    }


    public synchronized boolean hasChildren() {
        return getChildrenSize() != 0;
    }

    public RectF getQueryRange() {
        return queryRange;
    }

    public void setQueryRange(RectF queryRange) {
        this.queryRange = new RectF(queryRange);
    }


    public List<QuadTree<AbstractSprite>.CoordHolder> getChildren() {
        return children.findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
    }

    @Override
    public int getChildrenSize() {
        return children.size();
    }
}
