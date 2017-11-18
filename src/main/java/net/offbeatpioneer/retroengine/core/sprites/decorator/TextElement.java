package net.offbeatpioneer.retroengine.core.sprites.decorator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import net.offbeatpioneer.retroengine.R;
import net.offbeatpioneer.retroengine.core.GameFont;
import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.Decorator;
import net.offbeatpioneer.retroengine.core.sprites.EmptySprite;

/**
 * Text sprite as decorator for any {@link net.offbeatpioneer.retroengine.core.sprites.AnimatedSprite}.
 * There exists constructors and init method to create a pure text sprite not using it as decorator (will be
 * handled internally as that)
 *
 * @author Dominik Grzelak
 * @since 14.09.2014
 */
public class TextElement extends Decorator {

    private GameFont font = new GameFont(12);
    private String text = "";
    private Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
    private Canvas c = new Canvas();
    private int textWidth = 0, textHeight = 0;
    private float descent = 0f;
    private int bgColor = -1;

    /**
     * Constructor for decorating an existing sprite with text.
     *
     * @param text   the text for the sprite
     * @param sprite Sprite to apply text
     */
    public TextElement(String text, AbstractSprite sprite) {
        super(sprite);
        this.text = text;
    }

    /**
     * Constructor for decorating an existing sprite with text. Use setText or initWithText method
     * to set the text which should be drawn onto the sprite.
     *
     * @param sprite Sprite to apply text
     */
    public TextElement(AbstractSprite sprite) {
        this("", sprite);
    }

    /**
     * Use this constructor if you only want a text sprite. An empty sprite will be automatically created,
     * at the defined position, where the text is drawn on.
     *
     * @param text     the text
     * @param position the position of the text. Underlying "placeholder" sprite will get this position
     */
    public TextElement(String text, PointF position) {
        this(text, new EmptySprite().init(BitmapFactory.decodeResource(RetroEngine.Resources, R.drawable.empty), position));
    }

    /**
     * Constructor for creating a text element. Set the position later with the init method.
     * Default position is at (0,0) on the canvas.
     *
     * @param text the text
     */
    public TextElement(String text) {
        this(text, new EmptySprite().init(BitmapFactory.decodeResource(RetroEngine.Resources, R.drawable.empty), new PointF(0, 0)));
    }


    /**
     * Initialize text sprite. If text is not set it will be empty. Otherwise
     * call constructor with text argument or use the initWithText method.
     * <p>
     * The position is inherited from the sprite always
     */
    public TextElement init() {
        if (text == null) text = "";
        return this.initWithText(this.text);
    }

    /**
     * Initialize text sprite. If text is not set it will be empty. Otherwise
     * call constructor with text argument or use the initWithText method.
     * <p>
     *
     * @param pos position of the text
     */
    public TextElement init(PointF pos) {
        if (text == null) text = "";
        return this.initWithText(this.text, pos);
    }

    public TextElement initWithText(String text) {
        return this.initWithText(text, getSprite().getPosition());
    }

    //https://chris.banes.me/2014/03/27/measuring-text/

    /**
     * Initializes the text element at the specified position. The sprite where the text is drawn onto
     * will change its position.
     *
     * @param text the text
     * @param pos  position of the text. Will be assigned to the underlying sprite
     */
    @SuppressWarnings("all")
    public TextElement initWithText(String text, PointF pos) {
        getSprite().setPosition(pos);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(font.getFontSize());
        paint.setTypeface(font.getTypeface());

//        position = pos;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        textWidth = (int) paint.measureText(text); // Use measureText to calculate width
        textHeight = textBounds.height(); // Use height from getTextBounds()
        //prepare circle image and save it as bitmap
        tempBmp.recycle();
        descent = paint.descent();
        tempBmp = Bitmap.createBitmap(textWidth, (int) (textHeight + descent), Bitmap.Config.ARGB_8888);
        c.setBitmap(tempBmp);
//        c.drawPaint(paint);
//        c.drawColor(Color.BLUE);
        if (bgColor != -1)
            c.drawColor(bgColor);
//        int ar = Color.argb(getAlphaValue(), Color.red(color), Color.green(color), Color.blue(color));
        paint.setColor(font.getFontColor());
        paint.setAntiAlias(true);
//        paint.setColor(Color.argb(getAlphaValue(), 255, 255, 255));
        c.drawText(text, 0, textHeight + 0.5f, paint);
        return (TextElement) this.init(tempBmp, getSprite().getPosition());
    }


    @Override
    public void updateLogic() {
        getSprite().updateLogic();

        super.updateLogic();
    }

    @Override
    public void draw(Canvas canvas, long currentTime) {
        getSprite().draw(canvas, currentTime);

        PointF spritePos = getSprite().getPosition();
        pivotPoint.set(
                spritePos.x + frameW / 2,
                spritePos.y + frameH / 2);

        paint.setAlpha(getAlphaValue());
        transformationMatrix.reset();
        transformationMatrix.postScale(getScale(), getScale(), pivotPoint.x, pivotPoint.y);
        transformationMatrix.postRotate(getAngle(), pivotPoint.x, pivotPoint.y);
        transformationMatrix.preTranslate(spritePos.x, spritePos.y);
        canvas.drawBitmap(texture, transformationMatrix, paint);
    }

    /**
     * Get the text width in pixels. Only available if sprite was initialised by an init method.
     *
     * @return width of the text in pixels
     */
    @SuppressWarnings("unused")
    public int getTextWidth() {
        return textWidth;
    }

    /**
     * Get the text height in pixels. Only available if sprite was initialised by an init method.
     *
     * @return text height in pixels
     */
    @SuppressWarnings("unused")
    public int getTextHeight() {
        return (int) (textHeight + descent);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    @SuppressWarnings("unused")
    public GameFont getFont() {
        return font;
    }

    public void setFont(GameFont font) {
        this.font = font;
    }
}
