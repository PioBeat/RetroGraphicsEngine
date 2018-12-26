package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.animation.AnimationSuite;

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
public class SpriteListGroup extends IterableSpriteGroup<AbstractSprite> {
    private final List<AbstractSprite> children = new ArrayList<>();
    private final List<AbstractSprite> childrenInactive = new ArrayList<>();
    private final List<AbstractSprite> childrenDisabled = new ArrayList<>(); //no groups, only sprites
    private final Object[] lock = new Object[]{};

    public List<AbstractSprite> getChildrenInactive() {
        return childrenInactive;
    }

    public List<AbstractSprite> getChildrenDisabled() {
        return childrenDisabled;
    }

    public SpriteListGroup() {
        active = true;
        position = new PointF(0, 0);
        speed = new PointF(0, 0);
    }

    protected void moveSprite(List<AbstractSprite> from, List<AbstractSprite> to, int indexOfSprite) {
        synchronized (lock) {
            AbstractSprite spriteAt = getSpriteAt(from, indexOfSprite);
            if (spriteAt != null) {
                from.remove(indexOfSprite);
                to.add(spriteAt);
            }
        }
    }

    protected void moveSprite(List<AbstractSprite> from, List<AbstractSprite> to, AbstractSprite sprite) {
        synchronized (lock) {
            int ix = from.indexOf(sprite);
            if (ix != -1) {
                from.remove(ix);
                to.add(sprite);
            }
        }
    }

    /**
     * Return an active sprite at the give index {@code indexOfSprite}.
     *
     * @param list          the collection to search
     * @param indexOfSprite index of the sprite in the collection {@code list}
     * @return the sprite at the specified index, otherwise {@code null}.
     */
    private AbstractSprite getSpriteAt(List<AbstractSprite> list, int indexOfSprite) {
        synchronized (lock) {
            if (indexOfSprite < 0 || indexOfSprite > list.size()) {
                return null;
            }
            return list.get(indexOfSprite);
        }
    }


    @Override
    public void draw(Canvas canvas, long currentTime) {
        synchronized (lock) {
//            System.out.println("Active: " + children.size() + " // Disabled: " + childrenDisabled.size());
            draw(children, canvas, currentTime);
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
        synchronized (lock) {
            childrenInactive.clear();
//            removeInActive(children);
        }
    }


//    private void removeInActive(final List<AbstractSprite> children) {
//        for (int i = children.size() - 1; i >= 0; i--) {
//            AbstractSprite eachSprite = children.get(i);
//            if (eachSprite.hasChildren()) {
//                if (!eachSprite.isActive()) {
//                    children.remove(i);
//                } else {
//                    removeInActive(((SpriteListGroup) eachSprite).getChildren()); //safe case because only groups have children
//                }
//            } else {
//                if (!eachSprite.isActive()) {
//                    children.remove(i);
//                }
//            }
//        }
//    }

    /**
     * A group calls the base method {@code updateLogic} to update the animation logic if any.
     */
    @Override
    public void updateLogicHook() {
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

    private RectF checkBoundsRect = new RectF();

    @Override
    public void updateLogic() {
        synchronized (lock) {
            update(children, childrenDisabled, childrenInactive);
        }
    }

    protected void update(final List<AbstractSprite> childsActive, final List<AbstractSprite> childsDisabled, final List<AbstractSprite> childsInactive) {
        //check if sprites can be actived again
        PointF o = getViewportOrigin();
        checkBoundsRect.set(o.x - (int) (RetroEngine.W * bufferZoneFactor),
                o.y - (int) (RetroEngine.H * bufferZoneFactor),
                o.x + (int) (RetroEngine.W * (1.0 + bufferZoneFactor)),
                o.y + (int) (RetroEngine.H * (1.0 + bufferZoneFactor))
        );
//        System.out.println("Check within " + checkBoundsRect);

        for (int n = childsDisabled.size() - 1, i = n; i >= 0; i--) {
            AbstractSprite each2 = childsDisabled.get(i);
            // only for sprites (no groups) with auto hide enabled (i.e.,, auto destroy is set to false)
            if (!(each2 instanceof IterableSpriteGroup) && !each2.isAutoDestroy()) {
                try {
                    if (checkBoundsRect.contains(each2.getPosition().x, each2.getPosition().y)) {
                        each2.setDisabled(false);
                        moveSprite(childsDisabled, childsActive, each2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        //TODO doppel check verhindern, wenn gerade wieder in active liste aufgenommen
        //check if sprites must be disabled or destroyed
        //this doesn't affect groups at all, only the children
        for (int n = childsActive.size() - 1, i = n; i >= 0; i--) {
            AbstractSprite each = childsActive.get(i);

            if (each instanceof IterableSpriteGroup) { //each.hasChildren() &&
                each.updateLogicHook();
                update(((SpriteListGroup) each).getChildren(), ((SpriteListGroup) each).getChildrenDisabled(), ((SpriteListGroup) each).getChildrenInactive()); //safe case because only groups have children
            } else {
                if(!each.isActive()) {
                    moveSprite(childsActive, childsDisabled, each);
                    continue;
                }
//                else {
                //checky
//                if (true) { //!(each instanceof SpriteListGroup) && !each.hasChildren()
//                System.out.println("Checking " + each.getClass() + " with Parent " + each.getParent());
                if (!checkBoundsRect.contains(each.getPosition().x, each.getPosition().y)) {
//                    System.out.println("Setting sprite " + each.toString() + " disable/inactive");
                    if (each.isAutoDestroy()) {
                        each.setActive(false);
                        moveSprite(childsActive, childsInactive, each);
//                        moveActiveToInactive(i);
                        continue;
                    } else { //otherwise autoHide
                        each.setDisabled(true);
//                        moveActiveToDisabled(i);
                        moveSprite(childsActive, childsDisabled, each);
                        continue;
                    }
                }
//                }
                //checky end
                each.updateLogic();
//                }
            }
        }
    }

    public void add(AbstractSprite child) {
        add(child, -1);
    }

    // Add the child to the list of children
    public void add(AbstractSprite child, int index) {
        if (child == null) return;
        int n = children.size();
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
    public int getChildrenSize() {
        synchronized (lock) {
            return getChildren().size();
        }
    }

    public void setChildren(List<AbstractSprite> children) {
        synchronized (lock) {
            this.children.clear();
            this.children.addAll(children);
        }
    }

    public void clearSprites() {
        synchronized (lock) {
            children.clear();
            childrenDisabled.clear();
            childrenInactive.clear();
        }
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
