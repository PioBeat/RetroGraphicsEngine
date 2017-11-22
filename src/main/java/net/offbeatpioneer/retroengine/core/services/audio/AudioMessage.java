package net.offbeatpioneer.retroengine.core.services.audio;

public class AudioMessage {
    private int soundId;
    private float volume;

    /**
     * @param soundId unique id
     * @param volume  From 0 to 1
     */
    public AudioMessage(int soundId, float volume) {
        this.soundId = soundId;
        this.volume = volume;
    }

    public int getSoundId() {
        return soundId;
    }

    /**
     * @param soundId the unique id of the sound
     */
    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    /**
     * From 0 to 1
     *
     * @return return the current volume
     */
    public float getVolume() {
        return volume;
    }

    /**
     * From 0 to 1
     *
     * @param volume the volume between 0 and 1
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }
}