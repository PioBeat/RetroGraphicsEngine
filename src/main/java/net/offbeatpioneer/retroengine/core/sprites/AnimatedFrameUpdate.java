package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;

import net.offbeatpioneer.retroengine.core.RetroEngine;

/**
 * Frame update function for animated sprites, for sprites that have a film stripe as texture.
 * The original bitmap is clipped. A window will slide over the stripe to select the next segment of it.
 * The {@link AbstractSprite#sRectangle} of the sprite gets updated.
 * The init method of {@link AnimatedSprite} has to be called first so that the frame width and height
 * can be set in order to make this frame update work correctly.
 *
 * @author Dominik Grzelak
 * @since 15.01.2017.
 */
public class AnimatedFrameUpdate implements IFrameUpdate {
    private AbstractSprite sprite;
    private Bitmap tempBmp;
    private Canvas c;
    private Rect source;

    public AnimatedFrameUpdate(AbstractSprite sprite) {
        this.sprite = sprite;
        tempBmp = Bitmap.createBitmap(sprite.frameW, sprite.frameH, Bitmap.Config.ARGB_8888);
        c = new Canvas(tempBmp);
        source = new Rect(0, 0, tempBmp.getWidth(), tempBmp.getHeight());
    }

    /**
     * Update step. If a film strips is available the next part of the stripe is selected and the
     * texture of the sprite is updated.
     *
     * @return current position of the film stripe
     */
    @Override
    public int updateFrame() {
        long starttime = sprite.getStarttime();
        if (RetroEngine.getTickCount() > starttime + sprite.getFramePeriod()) {
            starttime = RetroEngine.getTickCount();
            sprite.setFrameNr(sprite.getFrameNr() + 1);

            if (sprite.getFrameNr() >= sprite.getFrameCnt()) {
                sprite.setFrameNr(0);
                if (!sprite.isLoop())
                    sprite.setActive(false);
            }
            sprite.setStarttime(starttime);
        }

        int left = (sprite.getFrameNr() * sprite.getFrameW());
        int right = (left + sprite.getFrameW());
        sprite.updateSRectangle(left,
                sprite.getsRectangle().top,
                right,
                sprite.getsRectangle().bottom);
        try {
            tempBmp.recycle();
            c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
            c.drawBitmap(sprite.getBackupTexture(), sprite.getsRectangle(), source, null);
            sprite.setTexture(tempBmp);
        } catch (Exception ignored) {
        }
        return sprite.getFrameNr();
    }
}
