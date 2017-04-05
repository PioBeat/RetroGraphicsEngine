package net.offbeatpioneer.retroengine.core.sprites;

import android.graphics.Bitmap;
import android.graphics.PointF;

/**
 * Simple sprite with no visual appearance. Serves as a placeholder.
 * For example to set a text with {@link net.offbeatpioneer.retroengine.core.sprites.decorator.TextElement}
 *
 * @author Dominik Grzelak
 * @since 14.09.2014
 */
public class EmptySprite extends AnimatedSprite {

    public EmptySprite() {
        super();
        frameUpdate = new NoFrameUpdate();
    }

    @Override
    public AnimatedSprite initAsAnimation(Bitmap bitmap, int height, int width, int fps, int frameCount, PointF pos, boolean loop) {
        super.initAsAnimation(bitmap, height, width, fps, frameCount, pos, loop);
        if (texture != null) {
            frameW = texture.getWidth();
            frameH = texture.getHeight();
        } else {
            frameW = 0;
            frameH = 0;
        }
        return this;
    }

    @Override
    public void updateLogic() {
    }

}
