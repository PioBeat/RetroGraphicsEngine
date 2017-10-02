package net.offbeatpioneer.retroengine.core.animation.timeline;

import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;
import net.offbeatpioneer.retroengine.core.sprites.AnimatedSprite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

/**
 * {@link StoryLineSlot} is a slot entry object inside {@link AnimationTimeline}.
 * <p>
 * It stores sprites of type {@link AbstractSprite} in a list.
 *
 * @author Dominik Grzelak
 * @since 22.10.2014
 */
public class StoryLineSlot {
    public List<AbstractSprite> animatedSprites = new ArrayList<>();
    private int duration;
    private boolean active;
    private boolean overwrite;
    TimerTask timerTask;

    public StoryLineSlot(int duration) {
        this(new ArrayList<AbstractSprite>(), duration);
    }

    public StoryLineSlot(List<AbstractSprite> animatedSprites, int duration, boolean active, boolean overwrite) {
        this.animatedSprites = animatedSprites;
        this.duration = duration;
        this.active = active;
        this.overwrite = overwrite;
    }

    /**
     * @param animatedSprite Sprite
     * @param duration       Dauer
     */
    public StoryLineSlot(AnimatedSprite animatedSprite, int duration) {
        this(new ArrayList<AbstractSprite>(Collections.singletonList(animatedSprite)), duration);
    }

    public StoryLineSlot(List<AbstractSprite> animatedSprites, int duration) {
        this(animatedSprites, duration, false, false);
    }

    public void addSprite(AbstractSprite sprite) {
        animatedSprites.add(sprite);
    }

    public List<AbstractSprite> getAnimatedSprites() {
        return animatedSprites;
    }

    public void setAnimatedSprites(List<AbstractSprite> animatedSprites) {
        this.animatedSprites = animatedSprites;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isOverwrite() {
        return overwrite;
    }

    public void setOverwrite(boolean overwrite) {
        this.overwrite = overwrite;
    }
}
