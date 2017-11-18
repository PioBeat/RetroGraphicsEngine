package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import net.offbeatpioneer.retroengine.core.RetroEngine;

/**
 * Created by Dome on 15.01.2017.
 */

public class AnimatedFrameUpdate implements IFrameUpdate {
    private AbstractSprite sprite;

    public AnimatedFrameUpdate(AbstractSprite sprite) {
        this.sprite = sprite;
    }

    /**
     * Aktualisierungsschritt. Falls ein Filmstreifen vorliegt, dann wird bei jedem Frame-Update das n�chste Bild ausgew�hlt.
     *
     * @return aktuelle Position im Filmstreifen.
     */
    @Override
    public int updateFrame() {
        long starttime = sprite.getStarttime();
        if (RetroEngine.getTickCount() > starttime + sprite.getFramePeriod()) { //RetroEngine.TICKS_PER_SECOND) {
            starttime = RetroEngine.getTickCount();
            sprite.setFrameNr(sprite.getFrameNr() + 1);

            if (sprite.getFrameNr() >= sprite.getFrameCnt()) {
                sprite.setFrameNr(0);
                if (!sprite.isLoop())
                    sprite.setActive(false);
            }
            sprite.setStarttime(starttime);
        }
        Rect sRectangle = sprite.getsRectangle();
        sRectangle.left = (sprite.getFrameNr() * sprite.getFrameW());
        sRectangle.right = (sRectangle.left + sprite.getFrameW());
        try {
//            sprite.getTexture().recycle();
            Bitmap texture = Bitmap.createBitmap(sprite.getBackupTexture(),
                    sRectangle.left,
                    sRectangle.top,
                    sRectangle.width(),
                    sRectangle.height()
            );
            sprite.setsRectangle(sRectangle);
            sprite.setTexture(texture);
        } catch (Exception e) {
//            e.printStackTrace();
        }
        return sprite.getFrameNr();
    }
}
