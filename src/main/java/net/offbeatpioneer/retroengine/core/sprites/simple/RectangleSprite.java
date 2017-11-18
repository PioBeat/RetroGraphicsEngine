package net.offbeatpioneer.retroengine.core.sprites.simple;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AnimatedSprite;
import net.offbeatpioneer.retroengine.core.sprites.Colorable;

/**
 * A basic shape which represents a rectangle.
 *
 * @author Dominik Grzelak
 * @since 05.03.2017.
 */
public class RectangleSprite extends AnimatedSprite implements Colorable {

    private float width;
    private float height;
    private int color;
    private Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);


    public RectangleSprite() {
        this(new PointF(0, 0), 0, 0, Color.BLACK);
    }

    public RectangleSprite(int color) {
        this(new PointF(0, 0), 0, 0, color);
    }

    public RectangleSprite(PointF position, float width, float height, int color) {
        super();
        this.width = width * RetroEngine.DENSITY + 0.5f;
        this.height = height * RetroEngine.DENSITY + 0.5f;
        this.position.set(position.x, position.y);
        this.color = color;
    }

    public AnimatedSprite init(PointF pos, int width, int height) {
        this.position = pos;
        this.width = width; // * RetroEngine.DENSITY + 0.5f;
        this.height = height; // * RetroEngine.DENSITY + 0.5f;

        //prepare circle image and save it as bitmap
        tempBmp.recycle();
        tempBmp = Bitmap.createBitmap((int) this.width, (int) this.height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas();
        c.setBitmap(tempBmp);
//        c.drawColor(Color.BLUE);
        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(ar);
        paint.setStrokeWidth(1);
        paint.setAntiAlias(true);
//        c.drawRect(position.x, position.y, position.x + this.width, position.y + this.height, paint);
        c.drawRect(0, 0, this.width, this.height, paint);
        return this.init(tempBmp, position, new PointF(0, 0));
    }

    @Override
    public void updateLogicTemplate() {

    }


    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }
}
