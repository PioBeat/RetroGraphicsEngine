package net.offbeatpioneer.retroengine.core;

import android.graphics.Color;
import android.graphics.Typeface;

/**
 * A font class to define the font face of {@link net.offbeatpioneer.retroengine.core.sprites.decorator.TextElement} sprites
 *
 * @author Dominik Grzelak
 * @since 2014-09-14
 */
public class GameFont {
    private static final int DEFAULT_FONT_COLOR = Color.parseColor("black");
    private static final String DEFAULT_FONT_FAMILY = Typeface.SANS_SERIF.toString();
    private static final int DEFAULT_FONT_EMPHASIS = Typeface.NORMAL;

    private String fontFamily;
    private int fontColor;
    private int fontSize;
    private Typeface typeface;
    private int emphasis;


    public GameFont(String fontFamily, int fontColor, int fontSize, int emphasis) {
        this.fontFamily = fontFamily;
        this.fontColor = fontColor;
        this.fontSize = fontSize;
        this.emphasis = emphasis;
        typeface = Typeface.create(fontFamily, emphasis);
    }

    public GameFont(String fontFamily, int fontSize) {
        this(fontFamily, DEFAULT_FONT_COLOR, fontSize, DEFAULT_FONT_EMPHASIS);
    }

    public GameFont(int fontSize) {
        this(DEFAULT_FONT_FAMILY, DEFAULT_FONT_COLOR, fontSize, DEFAULT_FONT_EMPHASIS);
    }

    public GameFont(int fontSize, int fontColor) {
        this(DEFAULT_FONT_FAMILY, fontColor, fontSize, DEFAULT_FONT_EMPHASIS);
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public int getFontColor() {
        return fontColor;
    }

    public void setFontColor(int fontColor) {
        this.fontColor = fontColor;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public Typeface getTypeface() {
        return typeface;
    }

    public void setTypeface(Typeface typeface) {
        this.typeface = typeface;
    }

    public int getEmphasis() {
        return emphasis;
    }

    public void setEmphasis(int emphasis) {
        this.emphasis = emphasis;
    }
}
