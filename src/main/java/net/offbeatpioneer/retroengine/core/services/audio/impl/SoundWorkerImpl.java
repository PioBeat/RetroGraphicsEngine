package net.offbeatpioneer.retroengine.core.services.audio.impl;

import android.content.Context;
import android.media.MediaDataSource;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import net.offbeatpioneer.retroengine.core.services.audio.AudioMessage;
import net.offbeatpioneer.retroengine.core.services.audio.AudioService;

/**
 * @author Dominik Grzelak
 * @since 11.09.2017
 */

public class SoundWorkerImpl extends HandlerThread implements AudioService, Handler.Callback {
    private final static String TAG = "SoundWorkerImpl";
    private Handler mWorkerHandler;
    private final static int PLAY_SOUND = 100;
    private final static int PLAY_BG_MUSIC = 101;
    private Context context;
    private MediaPlayer backgroundMusic = null;

    public SoundWorkerImpl(Context context) {
        this("SoundWorkerImpl");
        this.context = context;
    }

    private SoundWorkerImpl(String name) {
        super(name);
        start();
        prepareHandler();
    }

    public void prepareHandler() {
        mWorkerHandler = new Handler(getLooper(), this);
    }

    @Override
    public void playSound(AudioMessage audioMessage) {
        Message msg = mWorkerHandler.obtainMessage(PLAY_SOUND, audioMessage);
        mWorkerHandler.sendMessage(msg);
    }

    @Override
    public void playBackgroundMusic(AudioMessage audioMessage) {
        Message msg = mWorkerHandler.obtainMessage(PLAY_BG_MUSIC, audioMessage);
        mWorkerHandler.sendMessage(msg);
    }

    @Override
    public boolean isPlayingBgMusic() {
        try {
            return backgroundMusic != null && backgroundMusic.isPlaying();
        } catch (IllegalStateException e) {
            Log.e(TAG, e.toString(), e);
            return false;
        }
    }

    @Override
    public void stopBackgroundMusic() {
        if (backgroundMusic != null) {
            try {
                backgroundMusic.stop();
            } catch (IllegalStateException e) {
                Log.e(TAG, e.toString(), e);
            }
            backgroundMusic.release();
        }
    }

    @Override
    public void stopAll() {
        this.stopBackgroundMusic();
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.obj == null || !(message.obj instanceof AudioMessage)) {
            return false;
        }
        switch (message.what) {
            case PLAY_SOUND:
                AudioMessage playMessage = (AudioMessage) message.obj;
                MediaPlayer mp = MediaPlayer.create(this.context, playMessage.getSoundId());
                mp.seekTo(0);
                mp.setVolume(playMessage.getVolume(), playMessage.getVolume());
                mp.start();
                break;
            case PLAY_BG_MUSIC:
                AudioMessage playMessage1 = (AudioMessage) message.obj;
                this.stopBackgroundMusic();
                backgroundMusic = MediaPlayer.create(this.context, playMessage1.getSoundId());
                backgroundMusic.setLooping(true);
                backgroundMusic.start();

                break;
        }
        return true;
    }
}
