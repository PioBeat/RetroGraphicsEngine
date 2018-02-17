package net.offbeatpioneer.retroengine.core.services.audio.impl;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import net.offbeatpioneer.retroengine.core.services.audio.AudioMessage;
import net.offbeatpioneer.retroengine.core.services.audio.AudioService;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Basic implementation of the {@link AudioService}.
 * The {@link MediaPlayer} instances are created in the {@link HandlerThread} and messages are used to indicate
 * whether and which kind of sound (background music or sound effect) should be played.
 * <p>
 * Allows to play one background music, but more than one sound effects which don't loop.
 * A maximum number of sounds that can be played simultaneously has to be specified. This value
 * shouldn't be set to a high number to save resources of the device.
 *
 * @author Dominik Grzelak
 * @since 11.09.2017
 */
public class SoundWorkerImpl extends HandlerThread implements AudioService, Handler.Callback, MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {
    private final static String TAG = "SoundWorkerImpl";
    private final int MAX_SOUNDS;
    private Handler mWorkerHandler;
    private final static int PLAY_SOUND = 100;
    private final static int PLAY_BG_MUSIC = 101;
    private MediaPlayer backgroundMusic = null;

    private AtomicInteger cnt = new AtomicInteger(0);
    private Context context;
    private final int maxSounds;
    private boolean debug = false;

    /**
     * Is called by the client
     *
     * @param context   context for the {@link MediaPlayer}
     * @param maxSounds maximum allowed sounds that can be played
     */
    public SoundWorkerImpl(Context context, int maxSounds) {
        this("SoundWorkerImpl", maxSounds);
        this.context = context;
    }

    private SoundWorkerImpl(String name, int maxSounds) {
        super(name, Process.THREAD_PRIORITY_AUDIO);
        this.MAX_SOUNDS = maxSounds;
        this.maxSounds = maxSounds;
    }

    @Override
    protected void onLooperPrepared() {
        super.onLooperPrepared();
        prepareHandler();
    }

    private void prepareHandler() {
        if (mWorkerHandler == null)
            mWorkerHandler = new Handler(getLooper(), this);
    }

    /**
     * Start the {@link HandlerThread} in the background.
     *
     * @throws Exception is only thrown if thread already started, but the Looper is null. This is
     *                   the signal to provide a new audio service of this kind
     */
    @Override
    public void initialize() throws Exception {
        try {
            start();
        } catch (IllegalThreadStateException e) {
            if (getLooper() == null) {
                throw e;
            }
        }
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
            if (debug)
                Log.e(TAG, e.toString(), e);
            return false;
        }
    }

    @Override
    public void stopBackgroundMusic() {
        stopAndReleaseMediaPlayer(backgroundMusic, true);
    }

    private void stopAndReleaseMediaPlayer(MediaPlayer mp, boolean stop) {
        if (mp != null) {
            try {
                mp.stop();
            } catch (IllegalStateException e) {
                if (debug)
                    Log.e(TAG, e.toString(), e);
            }
            mp.release();
            mp = null;
        }
    }

    @Override
    public void stopSound(int resId) {

    }

    /**
     * Calling stopAll() will stop all currently playing sounds and will also
     * destroy this thread. In case you want to access this audio service again you have
     * to create a new one.
     */
    @Override
    public void stopAll() {
        cnt.set(maxSounds);
        this.stopBackgroundMusic();
        if (mWorkerHandler != null) {
            mWorkerHandler.removeMessages(PLAY_SOUND);
            mWorkerHandler.removeMessages(PLAY_BG_MUSIC);
            mWorkerHandler.removeCallbacks(this);
        }
        try {
            this.quit();
        } catch (Exception e) {
            if (debug)
                Log.e(TAG, e.toString(), e);
        }
        mWorkerHandler = null;
    }

    @Override
    public void enableDebugOutput() {
        debug = true;
    }

    @Override
    public boolean handleMessage(Message message) {
        if (message.obj == null || !(message.obj instanceof AudioMessage)) {
            return false;
        }
        if (cnt.get() >= MAX_SOUNDS) {
            if (debug)
                Log.d(TAG, "Maximum number of sounds playing reached. Maximum=" + MAX_SOUNDS);
            return true;
        }
        switch (message.what) {
            case PLAY_SOUND:
                AudioMessage playMessage = (AudioMessage) message.obj;
                MediaPlayer mp = MediaPlayer.create(this.context, playMessage.getSoundId());
                mp.setVolume(playMessage.getVolume(), playMessage.getVolume());
                try {
                    mp.seekTo(0);
                    mp.setOnPreparedListener(this);
                    mp.setOnCompletionListener(this);
                } catch (IllegalStateException e) {
                    if (debug)
                        Log.e(TAG, e.toString(), e);
                }
                break;
            case PLAY_BG_MUSIC:
                AudioMessage playMessage1 = (AudioMessage) message.obj;
                this.stopBackgroundMusic();
                backgroundMusic = MediaPlayer.create(this.context, playMessage1.getSoundId());
                backgroundMusic.setLooping(true);
                backgroundMusic.setVolume(playMessage1.getVolume(), playMessage1.getVolume());
                try {
                    backgroundMusic.setOnPreparedListener(this);
                } catch (IllegalStateException e) {
                    if (debug)
                        Log.e(TAG, e.toString(), e);
                }
                break;
        }
        return true;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        int c = cnt.incrementAndGet();
        if (debug)
            Log.d(TAG, "Current sound counter=" + c);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        stopAndReleaseMediaPlayer(mediaPlayer, false);
        int c = cnt.getAndDecrement();
        if (c < 0) cnt.set(0);
        if (debug)
            Log.d(TAG, "Current sound counter=" + c);
    }
}
