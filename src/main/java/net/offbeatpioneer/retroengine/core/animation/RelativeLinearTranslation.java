package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.PointF;

import net.offbeatpioneer.retroengine.auxiliary.struct.quadtree.QuadTree;
import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.ISpriteGroup;
import net.offbeatpioneer.retroengine.core.sprites.SpriteListGroup;
import net.offbeatpioneer.retroengine.core.sprites.SpriteQuadtreeGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dominik Grzelak
 * @since 12.09.2014
 */
public class RelativeLinearTranslation extends AnimationSuite {

    private PointF[] values;
    private int N;
    private int counter;
    private PointF end;
    private PointF currentPosition;
    //Typ mitgeben welche art von end-Vektor: zB absolute angabe oder als "speed"-Vektor
    //Speed-vektor kann man auch ausrechnen: v=s/t
    public RelativeLinearTranslation(PointF end, int time) {
        super();

        setEnd(end);
        setTime(time);
        currentPosition = new PointF(0, 0);
        counter = 0;
        float timeReal = ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS) * (1.0f));
        float vx = (end.x / timeReal);
        float vy = (end.y / (timeReal));
        N = (int) ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        values = new PointF[N];
        for (int i = 0; i < N; i++) {
            values[i] = new PointF(vx, vy);
        }
    }

    @Override
    protected void animationLogicTemplate() {
        if (counter >= N) {

            counter = 0;
            if (!isLoop()) {
                finished = true;
                getListener().onAnimationEnd(this);
                if (isDoReset()) {
                    if (getAnimatedSprite().hasChildren()) {
                        animateResetPosition(((ISpriteGroup) getAnimatedSprite()));
                    }
                    getAnimatedSprite().resetPosition();
                } else {
                    //Do nothing because sprite will end in the last position set
                }
                if (getTimer() != null)
                    getTimer().cancel();
                return;
            } else {
                if (getAnimatedSprite() instanceof ISpriteGroup) {
                    animateResetPosition((ISpriteGroup) getAnimatedSprite());
                } else {
                    getAnimatedSprite().resetPosition();
                }
                getListener().onAnimationRepeat(this);
            }
        }

        float xValue = values[counter].x;
        float yValue = values[counter].y;
        currentPosition.set(xValue, yValue);
        counter++;
        if (getAnimatedSprite() instanceof ISpriteGroup) {
            animateSetPosition((ISpriteGroup) getAnimatedSprite(), currentPosition);
        } else {
            getAnimatedSprite().translate(currentPosition);
        }
    }

    private void animateSetPosition(ISpriteGroup group, PointF position) {
        List<AbstractSprite> childs = getChildrensFromSpriteGroup(group);
        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                animateSetPosition((ISpriteGroup) child, position);
            } else {
                child.translate(position);
            }
        }
    }

    private void animateResetPosition(ISpriteGroup group) {
        final List<AbstractSprite> childs = getChildrensFromSpriteGroup(group);

        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                animateResetPosition(((ISpriteGroup) child));
            } else {
                child.resetPosition();
            }
        }
    }

    public List<AbstractSprite> getChildrensFromSpriteGroup(ISpriteGroup group) {
        List<AbstractSprite> childs = new ArrayList<>();
        if (group instanceof SpriteListGroup) {
            childs = ((SpriteListGroup) group).getChildren();
        } else if (group instanceof SpriteQuadtreeGroup) {
            List<QuadTree<AbstractSprite>.CoordHolder> items = ((SpriteQuadtreeGroup) group).getChildren();
            for (QuadTree<AbstractSprite>.CoordHolder each : items) {
                childs.add(each.o);
            }
        }
        return childs;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }
}
