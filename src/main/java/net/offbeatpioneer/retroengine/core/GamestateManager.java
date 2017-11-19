package net.offbeatpioneer.retroengine.core;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;

import net.offbeatpioneer.retroengine.core.states.State;

/**
 * {@link GamestateManager} is a manger for all {@link State} objects
 * This class is implemented as singleton.
 * <p>
 * It keeps a {@link java.util.List} of all available {@link State} instances.
 * The {@link GamestateManager} is used to switch between states.
 * <p>
 * Each {@link State} instance keeps a reference to the {@link GamestateManager} to get access
 * to its methods and with that also to the calling {@link Activity}.
 *
 * @author Dominik Grzelak
 */
public class GamestateManager {

    public static volatile boolean IS_CHANGING = false;

    private Activity mParentActivity = null;

    private final List<State> gamestateList = new ArrayList<State>();

    private static GamestateManager instance = null;

    private Handler handler;

    public static synchronized GamestateManager getInstance() {
        if (instance == null)
            instance = new GamestateManager();
        return instance;
    }

    private GamestateManager() {
    }

    /**
     * Hole gerade aktuellen State.
     *
     * @return
     */
    public synchronized State getActiveGameState() {
        synchronized (gamestateList) {
            for (int i = 0, n = gamestateList.size(); i < n; i++) {
                if (gamestateList.get(i).isActive())
                    return gamestateList.get(i);
            }
            return null;
        }
    }

    public synchronized void render(Canvas canvas, Paint paint, long currentTime) {
        getActiveGameState().render(canvas, paint, currentTime);

    }

    /**
     * Adds a new state to the managed list of {@link GamestateManager}. If State
     * is from the same class then it will not be added.
     *
     * @param state state to add
     */
    public void addGamestate(State state) {
        synchronized (gamestateList) {
            State tmp = getStateByClass(state.getClass());
            if (tmp == null)
                gamestateList.add(state);
            else {
                gamestateList.remove(tmp);
                gamestateList.add(state);
            }
        }
    }

    /**
     * Adds a new state to the managed list of {@link GamestateManager}. If State
     * is from the same class then it will not be added.
     * <p>
     * If the active status is set to true all other states active status will be set
     * to false. Otherwise use {@code addGamestate()}.
     *
     * @param state  state to add
     * @param active active status for the state, preferably true
     */
    public void addGameState(State state, boolean active) {
        synchronized (gamestateList) {
            this.addGamestate(state);

            if (active) {
                setDeactivateAllStates();
                state.setActive(true);
                state.init();
            }
        }
    }

    /**
     * Sets the active status to false for all added states in {@code gamestateList}.
     */
    private void setDeactivateAllStates() {
        synchronized (gamestateList) {
            for (int i = 0, n = gamestateList.size(); i < n; i++) {
                gamestateList.get(i).setActive(false);
            }
        }
    }

    public synchronized void changeGameState(Class<?> c) {
        IS_CHANGING = true;
        for (int i = 0, n = gamestateList.size(); i < n; i++) {
            if (gamestateList.get(i).getClass().equals(c)) {
                State oldState = getActiveGameState();
                if (oldState != null) {
                    oldState.setActive(false);
                    oldState.cleanUp();
                }
                gamestateList.get(i).setActive(true);
                gamestateList.get(i).init();
                break;
            }
        }
    }

    public void clearStates() {
        synchronized (gamestateList) {
            gamestateList.clear();
        }
    }

    public State getStateByName(String name) {
        for (int i = 0, n = gamestateList.size(); i < n; i++) {
            if (gamestateList.get(i).getStateName().equalsIgnoreCase(name))
                return gamestateList.get(i);
        }
        return null;
    }

    public State getStateByClass(Class name) {
        if (name != null) {
            for (int i = 0, n = gamestateList.size(); i < n; i++) {
                if (gamestateList.get(i).getClass() == name)
                    return gamestateList.get(i);
            }
        }
        return null;
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
        return gamestateList;
    }
}
