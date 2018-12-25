package net.offbeatpioneer.retroengine.core.sprites;

import java.util.Collection;

/**
 * Interface for sprite groups that are iterable and not in any way spatially distributed
 */
public abstract class IterableSpriteGroup<T> extends AnimatedSprite implements ISpriteGroup {
    
    public abstract Collection<T> getChildren();

    @Override
    public boolean isRoot() {
        return getParent() == null;
    }


}
