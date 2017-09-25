package net.offbeatpioneer.retroengine.core.services.audio.impl;

import net.offbeatpioneer.retroengine.core.services.audio.AudioMessage;
import net.offbeatpioneer.retroengine.core.services.audio.AudioService;

/**
 * @author Dominik Grzelak
 * @since 11.09.2017
 */

public class EmptyAudioProvider implements AudioService {
    @Override
    public void playSound(AudioMessage audioMessage) {

    }

    @Override
    public void playBackgroundMusic(AudioMessage audioMessage) {

    }

    @Override
    public void stopBackgroundMusic() {

    }

    @Override
    public void stopAll() {

    }
}