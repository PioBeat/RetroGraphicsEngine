package net.offbeatpioneer.retroengine.core.animation.timeline;

import android.util.Log;

import net.offbeatpioneer.retroengine.core.RetroEngine;
import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * AnimationTimeline manages a list of {@link StoryLineSlot} objects.
 * Allows you to set animation sequences similar to a video program.
 *
 * @author Dominik Grzelak
 * @since 22.10.2014
 */
public class AnimationTimeline {
    private List<StoryLineSlot> slots = new ArrayList<>();
    //private Iterator<StoryLineSlot> spriteIterator;
    private int counter = 0;
    public static boolean CHANGED = false;
    private Timer timer = new Timer();
    private TimerTask timerTask;
    private long currentTime;
    private StoryLineSlot oldElem;
    //private StoryLineSlot currentElement;

    public AnimationTimeline() {
        reset();
    }

    private void reset() {
        if (slots == null)
            slots = new ArrayList<StoryLineSlot>();
        this.counter = 0;
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                CHANGED = true;
//                Log.d("TL", "AnimationTimeline: Slot soll gewechselt werden nach " + element.getDuration());
            }
        };
    }

    public AnimationTimeline(List<StoryLineSlot> slots) {
        this.slots = slots;
        this.counter = 0;
    }

    public void finalizeTimeline() {
        if (slots != null)
            oldElem = slots.get(0);
        currentTime = RetroEngine.getTickCount();
    }

    public boolean isChanged() {
        return CHANGED;
    }

    public void setNoChange() {
        CHANGED = false;
    }

    //Bereitet die Timeline vor
    public void initCurrentSlot() {
        final StoryLineSlot element = getCurrentElement();
        Log.d("TL", "AnimationTimeline: Timer wird gestartet für " + element.getDuration());
        //TODO: damit es nicht zur verzögerungen kommt, darüber nachdenken die zu einer gruppe zuzuordnen, die sich ein attribut teilen...
        for (AbstractSprite s : element.getAnimatedSprites()) {
            s.beginAnimation();
        }
        //timer.schedule(this.timerTask, element.getDuration());
    }

    public List<AbstractSprite> update() {
        if (RetroEngine.getTickCount() >= currentTime + getCurrentElement().getDuration()) {
            currentTime = RetroEngine.getTickCount();
            CHANGED = true;
            StoryLineSlot nextElem;
            oldElem = getCurrentElement();
            if ((nextElem = getNext()) != null) {
                setNoChange();
                //storyLine.setNoChange();
                initCurrentSlot();
                return nextElem.getAnimatedSprites();
            }
        }
        return null;
    }

    public void add(StoryLineSlot slot) {
        slots.add(slot);
    }

    public StoryLineSlot getFirst() {
        return slots.get(0);
    }

    public StoryLineSlot getNext() {
        if (slots == null) {
            CHANGED = false;
            return null;
        }
        if ((counter + 1) >= slots.size()) {
            CHANGED = false;
            return null;
        }
        counter++;
        return slots.get(counter);
    }

    public StoryLineSlot getLast() {
        return slots.get(slots.size() - 1);
    }

    public StoryLineSlot getCurrentElement() {
        return slots.get(counter);
    }

    public boolean isOverwrite() {
        if (oldElem != null) {
            return oldElem.isOverwrite();
        }
        return getCurrentElement().isOverwrite();
    }
}
