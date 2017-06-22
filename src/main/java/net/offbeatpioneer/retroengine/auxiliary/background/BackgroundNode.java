package net.offbeatpioneer.retroengine.auxiliary.background;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.core.RetroEngine;

import java.util.ArrayList;
import java.util.List;

/**
 * Node object which holds any background layer type and
 * manages them.
 *
 * @author Dominim Grzelak
 * @since 13.01.2017
 */
public class BackgroundNode {

    private PointF referencePoint = new PointF();
    private RectF referenceRect = new RectF();
    private final List<BackgroundLayer> backgrounds = new ArrayList<>();
    private int width;
    private int height;
    private float xt = 0, yt = 0;
    private PointF viewportOrigin = new PointF(-xt, -yt);
    public int offsetX = 0;
    public int offsetY = 0;

    public BackgroundNode() {
    }

    public static class Builder {
        public static BackgroundNode create() {
            BackgroundNode background = new BackgroundNode();
            background.setHeight(RetroEngine.H);
            background.setWidth(RetroEngine.W);
            return background;
        }

        //ToDo add more create methods
    }

    public List<BackgroundLayer> getBackgrounds() {
        return backgrounds;
    }

    public void setBackgrounds(List<BackgroundLayer> backgrounds) {
        this.backgrounds.clear();
        this.backgrounds.addAll(backgrounds);
    }

    public float getXt() {
        return xt;
    }

    public void setXt(float xt) {
        this.xt = xt;
    }

    public float getYt() {
        return yt;
    }

    public void setYt(float yt) {
        this.yt = yt;
    }


    public void scrollWorld(Canvas canvas, boolean scrollWorld) {
        initTranslation();
        if (scrollWorld) {
            canvas.translate(xt, yt);
        }

        for (BackgroundLayer layer : backgrounds) {
            layer.setViewportOrigin(viewportOrigin);
            layer.setReferencePoint(referencePoint);
            layer.draw(canvas, 0);
        }
    }


    private void initTranslation() {
        xt = width / 2 + offsetX - referenceRect.width() / 2 - referencePoint.x;
        yt = height / 2 + offsetY - referenceRect.height() / 2 - referencePoint.y;
        setViewportOrigin(new PointF(-xt, -yt));
    }

    /**
     * Top-Left corner of the canvas (drawing surface)
     *
     * @return viewport origin, the top-left corner, of the canvas
     */
    public PointF getViewportOrigin() {
//        return new PointF(viewportOrigin.x - offsetX, viewportOrigin.y - offsetY);
        return viewportOrigin;
    }

    public void setViewportOrigin(PointF viewportOrigin) {
        this.viewportOrigin = viewportOrigin;
    }

    /**
     * Add a background layer to the background node
     *
     * @param layer background
     */
    public void addLayer(BackgroundLayer layer) {
        this.backgrounds.add(layer);
    }

    /**
     * Get background layer at the specified position in the list.
     *
     * @param position Index of a background layer
     * @return Background layer, otherwise {@code null}
     */
    public BackgroundLayer getLayer(int position) {
        if (position < 0 || position > backgrounds.size() - 1) {
            return null;
        }
        return backgrounds.get(position);
    }

    public PointF getReferencePoint() {
        return referencePoint;
    }

    /**
     * Set the reference point necessary for some special background layers.
     * Will be transferred to every layer in the list by the {@code scrollWorld} method.
     * <p>
     * Normally this method will be called from the {@code setReferenceSprite()} method within
     * the {@link net.offbeatpioneer.retroengine.core.states.State} class.
     *
     * @param referencePoint Reference point e.g. of a sprite
     */
    public void setReferencePoint(PointF referencePoint) {
        this.referencePoint = referencePoint;
    }

    public RectF getReferenceRect() {
        return referenceRect;
    }

    public void setReferenceRect(RectF referenceRect) {
        this.referenceRect = referenceRect;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
