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

    public CircleSprite() {
        this(0, Color.BLACK);
    }

    public CircleSprite(int color) {
        this(0, color);
    }

    public CircleSprite(float radius, int color) {
        super();
        this.radius = radius;
        this.color = color;
    }

    public AnimatedSprite init(float x, float y) {
        return this.initWithRadius(this.radius, x, y);
    }

    public AnimatedSprite initWithRadius(float rds, float x, float y) {
        this.radius = rds; // * RetroEngine.DENSITY + 0.5f;
        position = new PointF(x - this.radius, y - this.radius);

        //prepare circle image and save it as bitmap
        tempBmp.recycle();
        tempBmp = Bitmap.createBitmap((int) radius * 2, (int) radius * 2, Bitmap.Config.ARGB_8888);
        c.setBitmap(tempBmp);
//        c.drawColor(Color.BLUE);
        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(ar);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
        c.drawCircle((int) radius, (int) radius, this.radius, paint);

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
