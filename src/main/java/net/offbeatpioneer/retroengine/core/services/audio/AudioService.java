package net.offbeatpioneer.retroengine.core.services.audio;

/**
 * @author Dominik Grzelak
 * @since 11.09.2017
 */

public interface AudioService {
    void playSound(AudioMessage audioMessage);

    void playBackgroundMusic(AudioMessage audioMessage);

    boolean isPlayingBgMusic();

    void stopBackgroundMusic();

    void stopAll();
}
