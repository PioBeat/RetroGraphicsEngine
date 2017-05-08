package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.auxiliary.matheusdev.GridCollection;
import net.offbeatpioneer.retroengine.auxiliary.matheusdev.Rect;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.List;

/**
 * @author Dominik Grzelak
 * @since 07.05.2017
 */

public class SpriteGridGroup extends AbstractSprite implements ISpriteGroup<AbstractSprite> {
    private GridCollection<AbstractSprite> children = new GridCollection<>(10, 10);
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
        List<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            this.draw(childs,
                    canvas,
                    currentTime);
        }
    }

    public synchronized void draw(List<AbstractSprite> childs, Canvas canvas, long currentTime) {
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                List<AbstractSprite> tmp = ((SpriteGridGroup) child).getChildren();
                draw(tmp, canvas, currentTime);
            } else {
                child.draw(canvas, currentTime);
            }
        }
    }

    public void removeInActive() {
        this.removeInActive(getChildren());
    }

    public void removeInActive(List<AbstractSprite> children) {
        for (AbstractSprite eachSprite : children) {
            if (eachSprite.hasChildren()) {
                if (!eachSprite.isActive()) {
                    children.remove(eachSprite);
                } else {
                    removeInActive(((SpriteGridGroup) eachSprite).getChildren());
                }
            } else {
                if (!eachSprite.isActive()) {
                    children.remove(eachSprite);
                    childCnt--;
                }
            }
        }
    }

    @Override
    public void updateLogic() {
        this.update(getChildren());
    }

    protected synchronized void update(final List<AbstractSprite> children) {
        for (AbstractSprite each : children) {


            if (each.hasChildren() && each.isActive()) {
                each.updateLogicTemplate();
                update(((SpriteGridGroup) each).getChildren()); //safe case because only groups have children
            } else {
                if (each.isActive()) {
                    each.updateLogic();
                    ((ISpriteGroup) each.getParent()).add(each);
                } else {
                    children.remove(each);
                }
            }
        }
    }

    public void updateGrid() {
        children.update();
    }

    public synchronized void add(AbstractSprite child) {
        // Add the child to the list of children.
        if (child == null) return;
        float x = position.x;
        float y = position.y;

        if (x > child.getPosition().x) {
            x = child.getPosition().x;
        } else {
            frameW = (int) (Math.abs(x) - Math.abs(child.getPosition().x));
        }
        if (y > child.getPosition().y) {
            y = child.getPosition().y;
        } else {
            frameH = (int) (Math.abs(y) - Math.abs(child.getPosition().y));
        }
        setPosition(new PointF(x, y));
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
    public List<AbstractSprite> getChildren() {
        return children.query(makeRect(queryRange));
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
