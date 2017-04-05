package net.offbeatpioneer.retroengine.core.animation.timeline;

class TimelineNode {

    StoryLineSlot obj;

    TimelineNode nextElem;

    public TimelineNode(StoryLineSlot obj) {
        this.obj = obj;
        nextElem = null;
    }

    public void setNextElem(TimelineNode nextElem) {
        this.nextElem = nextElem;
    }

    public TimelineNode getNextElem() {
        return nextElem;
    }

    public StoryLineSlot getObj() {
        return obj;
    }
} 