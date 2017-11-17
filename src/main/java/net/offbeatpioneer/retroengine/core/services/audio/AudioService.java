package net.offbeatpioneer.retroengine.core.services.audio;

/**
 * @author Dominik Grzelak
 * @since 11.09.2017
 */

public interface AudioService {

    /**
     * Do some initialization
     */
    void initialize() throws Exception;
    /**
     * Plays a simple short sound. This should be used for simple non looping
     * sounds
     *
     * @param audioMessage
     */
    void playSound(AudioMessage audioMessage);

    /**
     * Plays a background sound. The specified raw resource id in {@link AudioMessage} will be
     * looped.
     *
     * @param audioMessage
     */
    void playBackgroundMusic(AudioMessage audioMessage);

    /**
     * Checks if any background music is playing (a looping sound)
     *
     * @return true if a looping sound is playing
     */
    boolean isPlayingBgMusic();

    /**
     * Stops all background music
     */
    void stopBackgroundMusic();

    /**
     * Stops a sound with the specific raw id
     * The concrete class has to keep a map of which sound is and was playing or
     * a similiar cache mechanism.
     *
     * @param resId raw resource id
     */
    void stopSound(int resId);

    /**
     * Stops all currently playing sounds
     */
    void stopAll();
}
