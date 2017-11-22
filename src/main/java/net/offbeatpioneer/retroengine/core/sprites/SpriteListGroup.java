package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//extra bitmap for canvas https://stackoverflow.com/questions/11838022/how-to-paint-with-alpha#11838354

/**
 * Sprite node which acts as a collection for all sprite elements with type {@link AbstractSprite}.
 * <p>
 * It is also used as the root node of a {@link net.offbeatpioneer.retroengine.core.states.State}.
 * Its sufficient to set the viewport in a state only once for the root node. All child nodes should
 * access this value by the appropriate parent method.
 *
 * @author Dominik Grzelak
 * @since 26.01.2015
 */
public class SpriteListGroup extends SpatialPartitionGroup<AbstractSprite> {
    private final List<AbstractSprite> children = new ArrayList<>();

    public SpriteListGroup() {
        active = true;
        position = new PointF(0, 0);
        speed = new PointF(0, 0);
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        final List<AbstractSprite> childs = getChildren();
        synchronized (this.children) {
            draw(childs, canvas, currentTime);
        }
    }

    private void draw(final List<AbstractSprite> childs, Canvas canvas, long currentTime) {
        for (int i = 0, n = childs.size(); i < n; i++) {
            AbstractSprite child = childs.get(i);
            if (child.hasChildren()) {
                draw(((SpriteListGroup) child).getChildren(), canvas, currentTime); //safe case because only groups have children
            } else {
                child.draw(canvas, currentTime);
            }
        }
    }

    public void removeInActive() {
        final List<AbstractSprite> childs = getChildren();
        synchronized (this.children) {
            removeInActive(childs);
        }
    }


    private void removeInActive(final List<AbstractSprite> children) {
        for (int i = children.size() - 1; i >= 0; i--) {
            AbstractSprite eachSprite = children.get(i);
            if (eachSprite.hasChildren()) {
                if (!eachSprite.isActive()) {
                    children.remove(i);
                } else {
                    removeInActive(((SpriteListGroup) eachSprite).getChildren()); //safe case because only groups have children
                }
            } else {
                if (!eachSprite.isActive()) {
                    children.remove(i);
                }
            }
        }
    }

    @Override
    public void updateLogic() {
        final List<AbstractSprite> childs = getChildren();
        synchronized (this.children) {
            update(childs);
        }
    }

    /**
     * A group calls the base method {@code updateLogic} to update the animation logic if any.
     */
    @Override
    public void updateLogicTemplate() {
        for (int i = 0, n = animations.size(); i < n; i++) {
            AnimationSuite animation = animations.get(i);
            animation.animationLogic();
        }
    }

    /**
     * Calls the onAction method of every child in the group.
     *
     * @param parameter Parameter for the action event
     */
    @Override
    public void onAction(Object parameter) {
        final List<AbstractSprite> childs = getChildren();
        for (int i = 0, n = childs.size(); i < n; i++) {
            AbstractSprite each = childs.get(i);
            each.onAction(parameter);
        }
    }

    protected void update(final List<AbstractSprite> childs) {
        for (int i = childs.size() - 1; i >= 0; i--) {
            AbstractSprite each = childs.get(i);
            if (each.hasChildren() && each.isActive()) {
                each.updateLogicTemplate();
                update(((SpriteListGroup) each).getChildren()); //safe case because only groups have children
            } else {
                if (each.isActive()) {
                    each.updateLogic();
                } else {
//                    childs.remove(i); //each.remove(i);
                }
            }
        }
    }

    public void add(AbstractSprite child) {
        add(child, -1);
    }

    public void add(AbstractSprite child, int index) {
        int n = children.size();
        // Add the child to the list of children
        if (child == null) return;
        if (index < 0 || index == n) { // then append
            children.add(child);
        } else if (index > n) {
            throw new IllegalArgumentException("Cannot add child to index " + index + ".  There are only " + n + " children.");
        } else { // insert
            children.set(index, child);
        }
        child.setParentSprite(this);
    }

    public List<AbstractSprite> getChildren() {
        return children;
    }

    @Override
    public Collection<AbstractSprite> getChildren(float left, float top, float right, float bottom) {
        return getChildren();
    }

    @Override
    public Collection<AbstractSprite> getChildren(RectF queryRange) {
        return getChildren();
    }

    @Override
    public int getChildrenSize() {
        return getChildren().size();
    }

    public void setChildren(List<AbstractSprite> children) {
        synchronized (this.children) {
            this.children.clear();
            this.children.addAll(children);
        }
    }

    public void clearSprites() {
        synchronized (children) {
            children.clear();
        }
    }

    @Override
    public void setQueryRange(RectF queryRange) {
        // ToDo
    }

    /**
     * Determine if the current sprite is the root sprite
     *
     * @return true if this Sprite is the root node, otherwise false
     */
    public boolean isRoot() {
        return getParent() == null;
    }

    /**
     * Has this {@link SpriteListGroup} child sprites?
     *
     * @return true if group has childrens, otherwise false
     */
    public boolean hasChildren() {
        return getChildrenSize() != 0;
    }

    /**
     * Get the index of this sprite in its current hierarchy level.
     *
     * @return index of the child, -1 if this is the root node
     */
    public int index() {
        if (getParent() != null) {
            for (int i = 0; ; i++) {
                if (getParent() instanceof SpriteListGroup) {
                    Object node = ((SpriteListGroup) getParent()).getChildren();
                    if (this == node) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

}
