package net.offbeatpioneer.retroengine.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.offbeatpioneer.retroengine.core.StateManager;
import net.offbeatpioneer.retroengine.core.RetroEngine;

/**
 * {@link RenderThread} implements the main ("game") loop to render all the graphics specified
 * by a {@link net.offbeatpioneer.retroengine.core.states.State}.
 * <p>
 * The class will get the active state from the {@link StateManager} to render all its graphics.
 * All available states should be defined beforehand but can be added afterwards as well. If no states
 * are set when the {@link RenderThread} is started, a {@link IllegalStateException} will be thrown.
 * <p>
 * The {@link RenderThread} will obtain the drawing surface of the {@link DrawView} component which
 * is in this a {@link Canvas} object. The active state will get this {@link Canvas} as argument
 * in its render method.
 *
 * @author Dominik Grzelak
 */
public class RenderThread extends Thread {

    private static final String TAG_LOG = "RenderThread";

    private StateManager manager = StateManager.getInstance();
    private Class<?> currentState = null;

    private Handler handler;
    final private SurfaceHolder mSurfaceHolder;
    private final Object[] lock = new Object[]{};

    /**
     * Constructor of the rendering thread.
     * The priority isn't set.
     *
     * @param view the surface view from which the {@link Canvas} is acquired
     */
    public RenderThread(SurfaceView view) {
        this(view, -1);
    }

    /**
     * Constructor of the rendering thread.
     * You must supply a surface view where the rendering can take place.
     * Set the priority of the rendering thread.
     * Use a priority <strong>only</strong> of {@link java.lang.Thread} class.
     * Setting a very high priority may lead to performance issues if not handled carefully.
     *
     * @param view     the surface view from which the {@link Canvas} is acquired
     * @param priority The value supplied must be from {@link Thread} and not from {@code java.lang.Process}.
     */
    public RenderThread(SurfaceView view, int priority) {
        super("RenderThread");
        this.mSurfaceHolder = view.getHolder();
        ((DrawView) view).setRenderThread(this);
        if (priority > 0)
            setPriority(priority);
    }

    public void addState(net.offbeatpioneer.retroengine.core.states.State state) {
        this.manager.getGamestates().add(state);
    }

    public void addStates(net.offbeatpioneer.retroengine.core.states.State... states) {
        for (net.offbeatpioneer.retroengine.core.states.State each : states) {
            this.manager.addGamestate(each);
        }
    }

    /**
     * Initializes the first state to show.
     * <p>
     * If {@code currentState} is not set ({@code null}) then the first state in the list of the {@link StateManager} is
     * chosen. If there are no states in the list then an {@link IllegalStateException} is thrown.
     * <p>
     * If {@code currentState} is not {@code null} then the {@link StateManager} will switch to this
     * state and activate it.
     */
    public void initState() {
        if (currentState == null) {
            if (this.manager.getGamestates().size() > 0) {
                net.offbeatpioneer.retroengine.core.states.State state = this.manager.getGamestates().get(0);
                manager.changeGameState(state.getClass());
            } else {
                throw new IllegalStateException("No state defined");
            }

        } else {
            manager.changeGameState(currentState);
        }
    }

    /**
     * Main method of the render thread
     */
    @Override
    public void run() {
        //manager.getActiveGameState().initAsAnimation();
        net.offbeatpioneer.retroengine.core.states.State currentStateTmp = manager.getActiveGameState();
        StateManager.IS_CHANGING.set(false);
        RetroEngine.shouldWait = false;
        Paint paint = new Paint();
        long next_game_tick = RetroEngine.getTickCount();
        int loops;
        while (RetroEngine.isRunning) {

            if (RetroEngine.shouldWait) {
                if (RetroEngine.resetStateIfWait) {
                    StateManager.IS_CHANGING.set(true);
                    if (currentStateTmp != null) {
                        currentStateTmp.setActive(false);
                        currentStateTmp.cleanUp();
                    }

                    if ((currentStateTmp = manager.getActiveGameState()) == null) {
                        sleepThread(100);
                        break;
                    } else {
                        RetroEngine.shouldWait = false;
                        StateManager.IS_CHANGING.set(false);
                    }
                } else {
                    sleepThread(250);
                    continue;
                }
            }

            loops = 0;
            Canvas canvas = null;
            try {
                if (!mSurfaceHolder.getSurface().isValid()) continue;
                if (StateManager.IS_CHANGING.get()) continue;

                while (RetroEngine.getTickCount() > next_game_tick && loops < RetroEngine.MAX_FRAMESKIP) {
                    assert currentStateTmp != null;
                    currentStateTmp.updateLogic();
                    next_game_tick += RetroEngine.SKIP_TICKS;
                    loops++;
                }

                synchronized (lock) {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    // Render the current state
                    if (currentStateTmp != null && canvas != null) {
                        canvas.clipRect(0, 0, RetroEngine.W, RetroEngine.H);
                        currentStateTmp.render(canvas, paint, RetroEngine.getTickCount());
                    }
                }
            } finally {
                // If an error occurred release the canvas
                if (canvas != null) {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

        }
        cleanUp();
        Log.v(TAG_LOG, "End of RenderThread");
    }

    private void sleepThread(long duration) {
        try {
            Thread.sleep(duration);
        } catch (InterruptedException e) {
            Log.e(TAG_LOG, e.toString(), e);
        }
    }

    /**
     * Call the cleanUp method of the current active state if available
     */
    public void cleanUp() {
        if (manager.getActiveGameState() != null)
            manager.getActiveGameState().cleanUp();
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler mHandler) {
        this.handler = mHandler;
        manager.setHandler(this.handler);
    }

    public void setCurrentState(Class<?> tmp) {
        this.currentState = tmp;
    }

    public Class<?> getCurrentState() {
        return currentState;
    }
}