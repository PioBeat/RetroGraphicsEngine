package net.offbeatpioneer.retroengine.core.sprites.eventhandling;

/**
 * Interface for the action event of a sprite in the onAction-method
 *
 * @author Dominik Grzelak
 * @since 15.09.2014
 */
public interface IActionEventCallback<Params> {

    void onAction(Params parameter);
}
