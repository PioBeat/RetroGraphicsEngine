package net.offbeatpioneer.retroengine.core.services.audio;

import net.offbeatpioneer.retroengine.core.services.audio.impl.EmptyAudioProvider;

/**
 * Audio service locator
 *
 * @author Dominik Grzelak
 * @since 11.09.2017
 */
public class AudioServiceLocator {
    private static EmptyAudioProvider emptyAudio = new EmptyAudioProvider();
    private static AudioService audioService = null;

    static {
        initialize();
    }

    private static void initialize() {
        audioService = emptyAudio;
    }

    /**
     * Provide a new audio service
     *
     * @param service new audio service to provide
     */
    public static void provideService(AudioService service) {
        if (service == null) {
            service = emptyAudio;
        }
        AudioServiceLocator.audioService = service;
    }

    /**
     * Get the current audio service. If no audio service was provided the
     * null device (empty audio device) will be returned.
     *
     * @return current audio service
     */
    public static AudioService getAudioService() {
        return audioService;
    }
}
