package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.auxiliary.matheusdev.CollectionTraverser;
import net.offbeatpioneer.retroengine.auxiliary.matheusdev.GridCollection;
import net.offbeatpioneer.retroengine.auxiliary.matheusdev.Rect;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Grzelak
 * @since 07.05.2017
 */

public class SpriteGridGroup extends AbstractSprite implements ISpriteGroup {
    GridCollection<AbstractSprite> children = new GridCollection<>(100, 100);
    private RectF queryRange = new RectF();
    private int childCnt = 0;

    public SpriteGridGroup() {
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
        GridCollection<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            List<AbstractSprite> child1 = childs.query(makeRect(queryRange));
            this.draw(child1,
                    canvas,
                    currentTime);
        }

    }

    public synchronized void draw(List<AbstractSprite> childs, Canvas canvas, long currentTime) {
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                List<AbstractSprite> tmp = ((SpriteGridGroup) child).getChildren().query(makeRect(queryRange));
                draw(tmp, canvas, currentTime);
            } else {
                child.draw(canvas, currentTime);
            }
        }
    }

    public void removeInActive() {
        this.removeInActive(children);
    }

    private void removeInActive(GridCollection<AbstractSprite> children) {
        children.query(new CollectionTraverser<AbstractSprite>() {
            @Override
            public boolean handleElement(int xPosition, int yPosition, AbstractSprite eachSprite, List<AbstractSprite> elements) {
                if (eachSprite.hasChildren()) {
                    if (!eachSprite.isActive()) {
                        elements.remove(eachSprite);
                    } else {
//                        List<AbstractSprite> tmp = ((SpriteGridGroup) eachSprite).getChildren().query(makeRect(queryRange));
                        removeInActive(((SpriteGridGroup) eachSprite).getChildren()); //safe case because only groups have children
                    }
                } else {
                    if (!eachSprite.isActive()) {
                        elements.remove(eachSprite);
                        childCnt--;
                    }
                }
                return true;
            }
        }, makeRect(queryRange));
    }

//    private void removeInActive(List<AbstractSprite> children) {
//        for (int i = children.size() - 1; i >= 0; i--) {
//            AbstractSprite eachSprite = children.get(i);
//            if (eachSprite.hasChildren()) {
//                if (!eachSprite.isActive()) {
//                    children.remove(i);
//                } else {
//                    List<AbstractSprite> tmp = ((SpriteGridGroup) eachSprite).getChildren().query(makeRect(queryRange));
//                    removeInActive(tmp); //safe case because only groups have children
//                }
//            } else {
//                if (!eachSprite.isActive()) {
//                    children.remove(i);
//                    childCnt--;
//                }
//            }
//        }
//    }

    @Override
    public void updateLogic() {
//        List<AbstractSprite> child1 = children.query(makeRect(queryRange));
//        synchronized (children) {
//            update(child1);
//        }
        this.update(children);
    }

    protected synchronized void update(final GridCollection<AbstractSprite> children) {
        children.query(new CollectionTraverser<AbstractSprite>() {
            @Override
            public boolean handleElement(int xPosition, int yPosition, AbstractSprite each, List<AbstractSprite> elements) {
                if (each.hasChildren() && each.isActive()) {
                    each.updateLogicTemplate();
                    update(((SpriteGridGroup) each).getChildren()); //safe case because only groups have children
                } else {
                    if (each.isActive()) {
                        each.updateLogic();
                    } else {
                        elements.remove(each); //each.remove(i);
                    }
                    children.add(each);
                }
                return true;
            }
        }, makeRect(queryRange));
    }

//    protected synchronized void update(final List<AbstractSprite> childs) {
//        for (int i = childs.size() - 1; i >= 0; i--) {
//            AbstractSprite each = childs.get(i);
//            if (each.hasChildren() && each.isActive()) {
//                each.updateLogicTemplate();
//                List<AbstractSprite> tmp = ((SpriteGridGroup) each).getChildren().query(makeRect(queryRange));
//                update(tmp); //safe case because only groups have children
//            } else {
//                if (each.isActive()) {
//                    each.updateLogic();
//                } else {
//                    childs.remove(i); //each.remove(i);
//                }
//                children.add(each);
//            }
//        }
////        children.updateRegion(makeRect(queryRange), (ArrayList<AbstractSprite>) childs);
//    }

    public void updateGrid() {
        children.update();
    }

    public synchronized void add(AbstractSprite child) {
        // Add the child to the list of children.
        if (child == null) return;
        children.add(child);
        child.setParentSprite(this);
        childCnt++;
    }

    /**
     * A group calls the base method {@code updateLogic} to update the animation logic if any.
     */
    @Override
    public void updateLogicTemplate() {
        for (AnimationSuite animation : getAnimations()) {
            animation.animationLogic();
        }
    }

    public Rect makeRect(RectF queryRange) {
        try {
            return new Rect(queryRange.left, queryRange.top, queryRange.width(), queryRange.height());
        } catch (Exception e) {
            return new Rect(0, 0, 1, 1);
        }
    }

    @Override
    public boolean isRoot() {
        return getParent() == null;
    }

    public synchronized boolean hasChildren() {
        return getChildrenSize() != 0;
    }

    @Override
    public GridCollection<AbstractSprite> getChildren() {
        return children;
    }

    @Override
    public int getChildrenSize() {
        return childCnt;
    }

    @Override
    public void clearSprites() {

    }

    public RectF getQueryRange() {
        return queryRange;
    }

    public void setQueryRange(RectF queryRange) {
        this.queryRange = queryRange;
    }
}
