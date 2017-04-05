package net.offbeatpioneer.retroengine.core;

import java.util.ArrayList;

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

    private final ArrayList<State> gamestateList = new ArrayList<State>();

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
        for (State each : gamestateList) {
            if (each.isActive())
                return each;
        }
        return null;
    }

    public synchronized void render(Canvas canvas, Paint paint, long currentTime) {
        getActiveGameState().render(canvas, paint, currentTime);

    }

    public void addGamestate(State state) {
        synchronized (gamestateList) {
            gamestateList.add(state);
        }
    }

    public void addGameState(State state, boolean active) {
        synchronized (gamestateList) {
            gamestateList.add(state);


            if (active) {
                state.setActive(true);
                state.init();
            }
        }
    }

    public synchronized void changeGameState(Class<?> c) {
        IS_CHANGING = true;
        for (State each : gamestateList) {
            if (each.getClass().equals(c)) {
                State oldState = getActiveGameState();
                if (oldState != null) {
                    oldState.setActive(false);
                    oldState.cleanUp();
                }
                each.setActive(true);
                each.init();
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
        for (State each : gamestateList) {
            if (each.getStateName().equalsIgnoreCase(name))
                return each;
        }
        return null;
    }

    public State getStateByClass(Class name) {
        if (name != null) {
            for (State each : gamestateList) {
                if (each.getClass() == name)
                    return each;
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

    public ArrayList<State> getGamestates() {
        return gamestateList;
    }
}
