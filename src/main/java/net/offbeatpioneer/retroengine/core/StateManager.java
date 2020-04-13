package net.offbeatpioneer.retroengine.core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

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

    private final ReentrantLock lock = new ReentrantLock();
    private AtomicBoolean changingState = new AtomicBoolean(false);

    private Activity mParentActivity = null;

    private final List<State> states = new ArrayList<>();

    private static StateManager instance = null;
    private State currentActiveState = null;

    private Handler handler;

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
    public State getActiveGameState() {
        synchronized (lock) {
            if (currentActiveState != null) return currentActiveState;
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).isActive()) {
                    currentActiveState = states.get(i);
                    return currentActiveState;
                }
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
        synchronized (lock) {
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
        synchronized (lock) {
            this.addGamestate(state);
            if (active) {
                deactivateAllStates();
                state.setActive(true);
                state.init();
            }
        }
    }

    public void activateState(Class<? extends State> state) {
        synchronized (lock) {
            deactivateAllStates();
            State stateByClass = getStateByClass(state);
            if (stateByClass != null) {
                stateByClass.setActive(true);
            }
        }
    }

    /**
     * Sets the active status to false for all added states in {@code states}.
     */
    private void deactivateAllStates() {
        synchronized (lock) {
            for (int i = 0, n = states.size(); i < n; i++) {
                states.get(i).setActive(false);
                currentActiveState = null;
            }
        }
    }

    /**
     * Begin a state change
     *
     * @param c the class of the state to switch
     */
    public void changeGameState(Class<? extends net.offbeatpioneer.retroengine.core.states.State> c) {
        synchronized (lock) {
            changingState.set(true);
            RetroEngine.pauseRenderThread(); // pause the render thread
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).getClass().equals(c)) {
                    State oldState = getActiveGameState();
                    if (this.currentActiveState != null && oldState != null) {
                        oldState.setActive(false);
                        oldState.cleanUp();
                    }
                    states.get(i).setActive(true);
                    currentActiveState = states.get(i);
                    states.get(i).init();
                    break;
                }
            }
            changingState.set(false);
        }
    }

    /**
     * Check if a state change event is occurring
     *
     * @return true, if a state change is currently active, otherwise false
     */
    public boolean isChangingState() {
        return changingState.get();
    }

    /**
     * Set the changing state flag to {@code false}
     */
    public void endStateChange() {
        changingState.set(false);
    }

    public void clearStates() {
        synchronized (lock) {
            states.clear();
        }
    }

    public State getStateByName(String name) {
        synchronized (lock) {
            for (int i = 0, n = states.size(); i < n; i++) {
                if (states.get(i).getStateName().equalsIgnoreCase(name))
                    return states.get(i);
            }
            return null;
        }
    }

    public State getStateByIndex(int ix) {
        synchronized (lock) {
            if (ix < 0 || ix >= states.size()) return null;
            return states.get(ix);
        }
    }

    public State getStateByClass(Class name) {
        synchronized (lock) {
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

    public int getStateCount() {
        synchronized (lock) {
            return states.size();
        }
    }

    List<State> getGamestates() {
        return states;
    }
}
