package net.offbeatpioneer.retroengine.core.services.audio;

public class AudioMessage {
    private int soundId;
    private float volume;

    /**
     * @param soundId
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
     * @param soundId
     */
    public void setSoundId(int soundId) {
        this.soundId = soundId;
    }

    /**
     * From 0 to 1
     *
     * @return
     */
    public float getVolume() {
        return volume;
    }

    /**
     * From 0 to 1
     *
     * @param volume
     */
    public void setVolume(float volume) {
        this.volume = volume;
    }
}