package net.offbeatpioneer.retroengine.core.sprites;

/**
 * Interface for sprite interaction.
 * Defines a common callback function if sprites need some action after an interaction
 *
 * @author Dominik Grzelak
 * @since 07.02.2015
 */
//TODO: mit IActionEventCallback zusammenfügen...
public interface ISpriteLogic {
    AbstractSprite onAction();
}
