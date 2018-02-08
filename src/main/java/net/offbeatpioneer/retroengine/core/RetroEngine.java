package net.offbeatpioneer.retroengine.core;

import android.content.Context;
import android.content.res.Resources;

import net.offbeatpioneer.retroengine.view.RenderThread;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Core class to get access to common resources like canvas size or an application's resources at
 * any state or in every class.
 * <p>
 * All sprite class are using this class and is important.
 * <p>
 * The {@link RenderThread} is using the properties to control
 * the main game loop. Values like the framerate can be set here.
 * <p>
 * This class is automatically initialized by the {@link net.offbeatpioneer.retroengine.view.DrawView} class.
 * It will call the {@code init} method to set the height and width of the current drawing surface which
 * is a canvas.
 *
 * @author Dominik Grzelak
 * @since 2014-09-12
 */
public class RetroEngine {
    public static int TICKS_PER_SECOND = 50;
    public static int SKIP_TICKS = 1000 / TICKS_PER_SECOND;
    public static int MAX_FRAMESKIP = 15;

    private static AtomicBoolean running = new AtomicBoolean(false);
    private static AtomicBoolean shouldWait = new AtomicBoolean(false);

    public static int W;
    public static int H;
    public static float DENSITY;
    public static Resources Resources;

    public static long getTickCount() {
        return System.currentTimeMillis();
    }

    public static void init(Context context) {
        DENSITY = context.getResources().getDisplayMetrics().density;
        Resources = context.getResources();
    }

    public static boolean isRunning() {
        return running.get();
    }

    public static void changeRunningState(boolean value) {
        running.set(value);
    }

    public static boolean isShouldWait() {
        return shouldWait.get();
    }

    public static void resumeRenderThread() {
        shouldWait.set(false);
    }

    public static void pauseRenderThread() {
        shouldWait.set(true);
    }
}
