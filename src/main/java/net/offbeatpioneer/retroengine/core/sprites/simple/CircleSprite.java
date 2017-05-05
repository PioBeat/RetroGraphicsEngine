package net.offbeatpioneer.retroengine.core.sprites.simple;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.sprites.AnimatedSprite;
import net.offbeatpioneer.retroengine.core.sprites.Colorable;

/**
 * A circle as basic sprite.
 *
 * @author Dominik Grzelak
 */
public class CircleSprite extends AnimatedSprite implements Colorable {

    private float radius = 0f;
    protected int color;
    private Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Canvas c = new Canvas();

    /**
     * Default constructor. The default color is black, and the default radius is 0.
     */
    public CircleSprite() {
        this(0, Color.BLACK);
    }

    /**
     * The default radius is 0.
     *
     * @param color Color of the circle
     */
    public CircleSprite(int color) {
        this(0, color);
    }

    /**
     * Constructor to initialise the circle with a radius and a colour
     *
     * @param radius radius of the circle in pixels
     * @param color  color of the circle
     */
    public CircleSprite(float radius, int color) {
        super();
        this.radius = radius;
        this.color = color;
    }

    /**
     * Create the circle at position (x,y).
     * This method calls the {@code initWithRadius()} and uses the default radius of 0 pixels.
     * <p>
     * If the default constructor was used the default color of the circle was set to black.
     *
     * @param x x coordinate of the circle
     * @param y y coordinate of the circle
     * @return a circle sprite
     */
    public AnimatedSprite init(float x, float y) {
        return this.initWithRadius(this.radius, x, y);
    }

    /**
     * Create the circle with a specified radius at position (x,y) on the drawing surface.
     *
     * @param rds radius of the circle in pixels
     * @param x   x coordinate of the circle
     * @param y   y coordinate of the circle
     * @return circle sprite
     */
    public AnimatedSprite initWithRadius(float rds, float x, float y) {
        this.radius = rds; // * RetroEngine.DENSITY + 0.5f;
        position = new PointF(x - this.radius, y - this.radius);

        //prepare circle image and save it as bitmap
//        tempBmp.recycle();
//        tempBmp = Bitmap.createBitmap((int) radius * 2, (int) radius * 2, Bitmap.Config.ARGB_8888);
//        c.setBitmap(tempBmp);
////        c.drawColor(Color.BLUE);
//        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
//        paint.setColor(ar);
//        paint.setStrokeWidth(1);
//        paint.setAntiAlias(true);
//        c.drawCircle((int) radius, (int) radius, this.radius, paint);


        return redraw(); //this.init(tempBmp, position, new PointF(0, 0));
    }

    public AnimatedSprite redraw() {
        tempBmp.recycle();
        tempBmp = Bitmap.createBitmap((int) (radius * 2 * getScale()), (int) (radius * 2 * getScale()),
                Bitmap.Config.ARGB_8888);
        c = new Canvas();
        c.setBitmap(tempBmp);
//        c.drawColor(Color.BLUE);
        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(ar);
        paint.setStrokeWidth(1);
//        paint.setAlpha(getAlphaValue());
        paint.setAntiAlias(true);
        c.drawCircle((int) radius * getScale(), (int) radius * getScale(), this.radius * getScale(), paint);
//        position = new PointF(position.x, position.y);
        return this.init(tempBmp, position, new PointF(0, 0));
    }

    @Override
    public int getColor() {
        return color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }
}
