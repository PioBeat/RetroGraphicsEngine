package net.offbeatpioneer.retroengine.core.sprites.eventhandling;

/**
 * Default action for a sprite for its onAction method. A noop action.
 *
 * @author Dominik Grzelak
 * @since 10.10.2014
 */
public class EmptyAction implements IActionEventCallback<Object> {

    public EmptyAction() {
    }

    public void onAction(Object parameter) {
    }
}
