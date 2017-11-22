package net.offbeatpioneer.retroengine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;

import net.offbeatpioneer.retroengine.core.states.State;

/**
 * {@link StateManager} is a manger for all {@link State} objects
 * This class is implemented as singleton.
 * <p>
 * It keeps a {@link java.util.List} of all available {@link State} instances.
 * The {@link StateManager} is used to switch between states.
 * <p>
 * Each {@link State} instance keeps a reference to the {@link StateManager} to get access
 * to its methods and with that also to the calling {@link Activity}.
 *
 * @author Dominik Grzelak
 */
public class StateManager {

    public static AtomicBoolean IS_CHANGING = new AtomicBoolean(false);

    private Activity mParentActivity = null;

    private final List<State> states = new ArrayList<>();

    private static StateManager instance = null;

    private Handler handler;

    private final Object[] lock = new Object[]{};

    public static synchronized StateManager getInstance() {
        if (instance == null)
            instance = new StateManager();
        return instance;
    }

    private StateManager() {
    }

    /**
     * Get the currently active state
     *
     * @return active state or null, if no state is active
     */
    public synchronized State getActiveGameState() {
        synchronized (states) {
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).isActive())
                    return states.get(i);
            }
            return null;
        }
    }

    /**
     * Calls the render method of the current active state.
     * This method will loop every time through all registered states to find the active
     * one before calling the render method. It is recommended to get the active state first with
     * {@link StateManager#getActiveGameState()} and call the render method directly.
     *
     * @param canvas      the canvas
     * @param paint       the paint
     * @param currentTime the time
     */
    public synchronized void render(Canvas canvas, Paint paint, long currentTime) {
        getActiveGameState().render(canvas, paint, currentTime);
    }

    /**
     * Adds a new state to the managed list of {@link StateManager}. If State
     * is from the same class then it will be removed and the given state will be added to
     * the end of the list.
     *
     * @param state the state to add
     */
    public void addGamestate(State state) {
        synchronized (states) {
            State tmp = getStateByClass(state.getClass());
            if (tmp == null)
                states.add(state);
            else {
                states.remove(tmp);
                states.add(state);
            }
        }
    }

    /**
     * Adds a new state to the managed list of {@link StateManager}. If State
     * is from the same class then it will not be added.
     * <p>
     * If the active status is set to true all other states active status will be set
     * to false. Otherwise use {@code addGamestate()}.
     *
     * @param state  state to add
     * @param active active status for the state, preferably true
     */
    public void addGameState(State state, boolean active) {
        synchronized (states) {
            this.addGamestate(state);
            if (active) {
                deactivateAllStates();
                state.setActive(true);
                state.init();
            }
        }
    }

    /**
     * Sets the active status to false for all added states in {@code states}.
     */
    private void deactivateAllStates() {
        synchronized (states) {
            for (int i = 0, n = states.size(); i < n; i++) {
                states.get(i).setActive(false);
            }
        }
    }

    public void changeGameState(Class<?> c) {
        synchronized (states) {
            IS_CHANGING.set(true);
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).getClass().equals(c)) {
                    State oldState = getActiveGameState();
                    if (oldState != null) {
                        oldState.setActive(false);
                        oldState.cleanUp();
                    }
                    states.get(i).setActive(true);
                    states.get(i).init();
                    break;
                }
            }
            IS_CHANGING.set(false);
        }
    }

    public void clearStates() {
        synchronized (states) {
            states.clear();
        }
    }

    public State getStateByName(String name) {
        synchronized (states) {
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).getStateName().equalsIgnoreCase(name))
                    return states.get(i);
            }
            return null;
        }
    }

    public State getStateByClass(Class name) {
        synchronized (states) {
            if (name != null) {
                for (int i = 0, n = states.size(); i < n; i++) {
                    if (states.get(i).getClass() == name)
                        return states.get(i);
                }
            }
            return null;
        }
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public Activity getParentActivity() {
        return mParentActivity;
    }

    public void setParentActivity(Activity mParentActivity) {
        this.mParentActivity = mParentActivity;
    }

    public List<State> getGamestates() {
        return states;
    }
}
