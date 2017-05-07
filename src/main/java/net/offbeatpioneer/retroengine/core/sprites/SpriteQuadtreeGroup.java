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
public class SpriteQuadtreeGroup extends AbstractSprite implements ISpriteGroup {
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
        QuadTree<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            List<QuadTree<AbstractSprite>.CoordHolder> childCoords =
                    childs.findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
            this.draw(childCoords,
                    canvas,
                    currentTime);
        }

    }

    public List<QuadTree<AbstractSprite>.CoordHolder> findAll() {
        return this.findAll(getQueryRange());
    }

    public List<QuadTree<AbstractSprite>.CoordHolder> findAll(RectF queryRange) {
        if (getChildren().root == null) {
            return new ArrayList<>();
        }
        return getChildren().findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
    }

//    public synchronized void draw(QuadTree<AbstractSprite> childs, Canvas canvas, RectF rect, long currentTime) {
//        this.draw(childs, canvas, rect.left, rect.top, rect.right, rect.bottom, currentTime);
//    }

    public synchronized void draw(List<QuadTree<AbstractSprite>.CoordHolder> childs, Canvas canvas,
                                  long currentTime) {
//        List<QuadTree<AbstractSprite>.CoordHolder> list = childs.findAll(x1, y1, x2, y2);
        for (QuadTree.CoordHolder each : childs) {
            AbstractSprite eachSprite = (AbstractSprite) each.o;
            if (eachSprite.hasChildren()) {
                List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren().findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
                draw(list2,
                        canvas,
                        currentTime); //safe case because only groups have children
            } else {
                eachSprite.draw(canvas, currentTime);
            }
        }
    }

    @Override
    public void updateLogic() {
        QuadTree<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            List<QuadTree<AbstractSprite>.CoordHolder> list2 = childs.findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
            update(list2);
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
                List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren().findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
                update(list2); //safe case because only groups have children
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
        QuadTree<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            List<QuadTree<AbstractSprite>.CoordHolder> list = childs.findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
            removeInActive(list);
        }
    }

    private void removeInActive(List<QuadTree<AbstractSprite>.CoordHolder> list) {
//        List<QuadTree.CoordHolder> list = children.findAll(queryRange.left,
//                queryRange.top, queryRange.right, queryRange.bottom);
//        List<QuadTree<AbstractSprite>.CoordHolder> list = children.root.items;
//        List<QuadTree<AbstractSprite>.CoordHolder> list = collectAllItems(childs);
        for (int i = list.size() - 1; i >= 0; i--) {
            QuadTree.CoordHolder each = list.get(i);
            AbstractSprite eachSprite = (AbstractSprite) each.o;
            if (eachSprite.hasChildren()) {
                if (!eachSprite.isActive()) {
                    each.remove();
                } else {
                    List<QuadTree<AbstractSprite>.CoordHolder> list2 = ((SpriteQuadtreeGroup) eachSprite).getChildren().findAll(queryRange.left, queryRange.top, queryRange.right, queryRange.bottom);
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
        QuadTree<AbstractSprite> childs = getChildren();

        for (QuadTree.CoordHolder each : childs.root.items) {
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


    public QuadTree<AbstractSprite> getChildren() {
        return children;
    }

    @Override
    public int getChildrenSize() {
        return children.size();
    }
}
