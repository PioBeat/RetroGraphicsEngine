package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;

import java.util.ArrayList;
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
public class SpriteGroup extends AbstractSprite {
    private List<AbstractSprite> children = new ArrayList<>();
//    Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
//    Canvas c = new Canvas();

    public SpriteGroup() {
        active = true;
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        List<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            draw(childs, canvas, currentTime);
        }
    }

    public synchronized void draw(List<AbstractSprite> childs, Canvas canvas, long currentTime) {
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                draw(child.getChildren(), canvas, currentTime);
            } else {
                child.draw(canvas, currentTime);
            }
        }
    }

    public void removeInActive() {
        List<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            removeInActive(childs);
        }
    }

    private void removeInActive(List<AbstractSprite> children) {
        for (int i = children.size() - 1; i >= 0; i--) {
            if (children.get(i).hasChildren()) {
                if (!children.get(i).isActive()) {
                    children.remove(i);
                } else {
                    removeInActive(children.get(i).getChildren());
                }
            } else {
                AbstractSprite each = children.get(i);
                if (!each.isActive()) {
                    children.remove(i);
                }
            }
        }
    }

    @Override
    public void updateLogic() {
        List<AbstractSprite> childs = getChildren();
        synchronized (childs) {
            update(childs);
        }
    }

    @Override
    public void updateLogicTemplate() {

    }

    /**
     * Calls the onAction method of every child in the group.
     *
     * @param parameter Parameter for the action event
     */
    @Override
    public void onAction(Object parameter) {
        for(AbstractSprite each: getChildren()) {
            each.onAction(parameter);
        }
    }

    public synchronized void update(List<AbstractSprite> childs) {
        for (int i = childs.size() - 1; i >= 0; i--) {
            AbstractSprite each = childs.get(i);
            if (each.hasChildren()) {
                update(each.getChildren());
            } else {
                if (each.isActive()) {
                    each.updateLogic();
                } else {
                    childs.remove(i); //each.remove(i);
                }
            }
        }
    }

    public synchronized void add(AbstractSprite child) {
        add(child, -1);
    }

    public synchronized void add(AbstractSprite child, int index) {
        // Add the child to the list of children.
        if (child == null) return;
        if (index < 0 || index == children.size())  // then append
        {
            children.add(child);
        } else if (index > children.size()) {
            throw new IllegalArgumentException("Cannot add child to index " + index + ".  There are only " + children.size() + " children.");
        } else  // insert
        {
            children.set(index, child);
        }
        child.setParentSprite(this);
    }

    public synchronized List<AbstractSprite> getChildren() {
        return children;
    }

    public synchronized void setChildren(List<AbstractSprite> children) {
        this.children = children;
    }

    public synchronized void clearSprites() {
        children.clear();
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
     * Has this {@link SpriteGroup} child sprites?
     *
     * @return true if group has childrens, otherwise false
     */
    public synchronized boolean hasChildren() {
        return children.size() != 0;
    }

    /**
     * Get the index of this sprite in its current hierarchy level.
     *
     * @return index of the child, -1 if this is the root node
     */
    public synchronized int index() {
        if (getParent() != null) {
            for (int i = 0; ; i++) {
                if (getParent() instanceof SpriteGroup) {
                    Object node = ((SpriteGroup) getParent()).getChildren();
                    if (this == node) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

}
