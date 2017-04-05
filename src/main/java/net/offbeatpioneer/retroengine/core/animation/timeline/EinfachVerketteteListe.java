package net.offbeatpioneer.retroengine.core.animation.timeline;

/**
 * edited source from: https://javabeginners.de/Sammlungen_und_Listen/Einfach_verkettete_Liste.php
 */
public class EinfachVerketteteListe {

    TimelineNode startElem = null;//new ListElement(new StoryLineSlot(ItemGenerator.generateRandomItem(new Point(0,0)), 10));

    public EinfachVerketteteListe() {
    }

    public void addLast(StoryLineSlot o) {
        TimelineNode newElem = new TimelineNode(o);
        if(startElem == null) {
            startElem = newElem;
            return;
        }
        TimelineNode lastElem = getLastElem();
        lastElem.setNextElem(newElem);
    }

    public void insertAfter(StoryLineSlot prevItem, StoryLineSlot newItem) {
        TimelineNode newElem, nextElem, pointerElem;
        pointerElem = startElem.getNextElem();
        while (pointerElem != null && !pointerElem.getObj().equals(prevItem)) {
            pointerElem = pointerElem.getNextElem();
        }
        newElem = new TimelineNode(newItem);
        nextElem = pointerElem.getNextElem();
        pointerElem.setNextElem(newElem);
        newElem.setNextElem(nextElem);
    }

    public void delete(StoryLineSlot o) {
        TimelineNode le = startElem;
        while (le.getNextElem() != null && !le.getObj().equals(o)) {
            if (le.getNextElem().getObj().equals(o)) {
                if (le.getNextElem().getNextElem() != null)
                    le.setNextElem(le.getNextElem().getNextElem());
                else {
                    le.setNextElem(null);
                    break;
                }
            }
            le = le.getNextElem();
        }
    }

    public boolean find(StoryLineSlot o) {
        TimelineNode le = startElem;
        while (le != null) {
            if (le.getObj().equals(o))
                return true;
            le = le.nextElem;
        }
        return false;
    }

    public TimelineNode getFirstElem() {
        return startElem;
    }

    public TimelineNode getLastElem() {
        TimelineNode le = startElem;
        while (le.getNextElem() != null) {
            le = le.getNextElem();
        }
        return le;
    }

    public void writeList() {
        TimelineNode le = startElem;
        while (le != null) {
            System.out.println(le.getObj());
            le = le.getNextElem();
        }
    }
}