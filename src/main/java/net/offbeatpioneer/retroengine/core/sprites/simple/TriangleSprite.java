package net.offbeatpioneer.retroengine.core.sprites.simple;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;

import net.offbeatpioneer.retroengine.core.sprites.AnimatedSprite;
import net.offbeatpioneer.retroengine.core.sprites.Colorable;

/**
 * Basic shape represents an equilateral triangle.
 *
 * @author Dominik Grzelak
 * @since 05.03.2017.
 */
public class TriangleSprite extends AnimatedSprite implements Colorable {

    private int color;
    private Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private float length; //length of one side

    public TriangleSprite(int color) {
        this(0, color);
    }

    @SuppressWarnings("all")
    public TriangleSprite(float length, int color) {
        super();
        this.length = length;
        this.color = color;
    }

    public AnimatedSprite initWithLength(float length, PointF pos) {
        return this.initWithLength(length, pos, this.color);
    }

    @SuppressWarnings("all")
    public AnimatedSprite initWithLength(float length, PointF pos, int color) {
        this.length = length;
        this.color = color;
        this.position = pos;

        //prepare circle image and save it as bitmap
        tempBmp.recycle();
        tempBmp = Bitmap.createBitmap((int) this.length, (int) this.length, Bitmap.Config.ARGB_8888);
        Canvas c =  new Canvas();
        c.setBitmap(tempBmp);
//        c.drawColor(Color.BLUE);
        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(ar);
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        Point point1_draw = new Point((int) length / 2, 0);
        Point point2_draw = new Point(0, (int) length);
        Point point3_draw = new Point((int) length, (int) length);
        path.moveTo(point1_draw.x, point1_draw.y);
        path.lineTo(point2_draw.x, point2_draw.y);
        path.lineTo(point3_draw.x, point3_draw.y);
        path.lineTo(point1_draw.x, point1_draw.y);
        path.close();

        c.drawPath(path, paint);

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
}
