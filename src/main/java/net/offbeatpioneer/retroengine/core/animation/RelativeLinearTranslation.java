package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.PointF;
import android.graphics.RectF;

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

    private PointF currentPosition;
    private PointF[] values;
    private int N;
    private int counter;
    private PointF start;
    private PointF end;
    private float amountX = 0;
    private float amountY = 0;

    //Typ mitgeben welche art von end-Vektor: zB absolute angabe oder als "speed"-Vektor
    //Speed-vektor kann man auch ausrechnen: v=s/t
    public RelativeLinearTranslation(PointF end, int time) {
        super();
        this.counter = 0;
        //milliseconds = time;
        setTime(time);
        setStart(start);
        setEnd(end);
        currentPosition = start;
        float timeReal = ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS) * (1.0f));
        float vx = (end.x / timeReal);
        float vy = (end.y / (timeReal));
        N = (int) ((time * 1.0f) / (RetroEngine.TICKS_PER_SECOND - RetroEngine.SKIP_TICKS));
        amountX = vx; //(float)(N*vx);
        amountY = vy; //(float)(N*vy);
        values = new PointF[N];
        for (int i = 0; i < N; i++) {
//            values[i] = new Point(end.x, end.y);
            values[i] = new PointF(amountX, amountY);
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

        PointF start = new PointF(getAnimatedSprite().getPosition().x, getAnimatedSprite().getPosition().y);
        PointF pp = new PointF(start.x + amountX, start.y + amountY);
//        float xValue = InterpolationHelper.linearPointBetween(start.x, pp.x, counter, N);
//        float yValue = InterpolationHelper.linearPointBetween(start.y, pp.y, counter, N);
        float xValue = values[counter].x;
        float yValue = values[counter].y;
        currentPosition = new PointF(xValue, yValue);
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

            }
            child.translate(position);
//            child.setPosition(new PointF(position.x, position.y));
        }
    }

    private void animateResetPosition(ISpriteGroup group) {
        List<AbstractSprite> childs = getChildrensFromSpriteGroup(group);

        for (AbstractSprite child : childs) {
            if (child.hasChildren()) {
                animateResetPosition(((ISpriteGroup) child));
            }
            child.resetPosition();
        }
    }

    public List<AbstractSprite> getChildrensFromSpriteGroup(ISpriteGroup group) {
        List<AbstractSprite> childs = new ArrayList<>();
        if (group instanceof SpriteListGroup) {
            childs = ((SpriteListGroup) group).getChildren();
        } else if (group instanceof SpriteQuadtreeGroup) {
//            List<QuadTree<AbstractSprite>.CoordHolder> items = ((SpriteQuadtreeGroup) group).getChildren().root.items;
            RectF qr = ((SpriteQuadtreeGroup) group).getQueryRange();
            List<QuadTree<AbstractSprite>.CoordHolder> items = ((SpriteQuadtreeGroup) group).getChildren().findAll(qr.left, qr.top, qr.right, qr.bottom);
            for (QuadTree<AbstractSprite>.CoordHolder each : items) {
                childs.add(each.o);
            }
        }
        return childs;
    }

    @Deprecated
    private List<QuadTree<AbstractSprite>.CoordHolder> collectAllItems(QuadTree<AbstractSprite> childs) {
        List<QuadTree<AbstractSprite>.CoordHolder> list = new ArrayList<>();
        list.addAll(childs.root.LL.items);
        list.addAll(childs.root.LR.items);
        list.addAll(childs.root.UL.items);
        list.addAll(childs.root.UR.items);
        return list;
    }

    public PointF getStart() {
        return start;
    }

    public void setStart(PointF start) {
        this.start = start;
    }

    public PointF getEnd() {
        return end;
    }

    public void setEnd(PointF end) {
        this.end = end;
    }
}
