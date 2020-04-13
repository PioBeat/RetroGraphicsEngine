package net.offbeatpioneer.retroengine.view;

import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import net.offbeatpioneer.retroengine.core.StateManager;

/**
 * {@link SimpleStateRedirectedTouchListener} nimmt die Eingaben entgegen, die als Touch-Event oder
 * �ber die Tastatur ankommen k�nnen. Diese werden immer an den gerade aktuellen
 * Spielzustand (State) weitergeleitet. Dieser Listener wird an die
 * {@link DrawView} gebunden, um die Eingaben dieser abzufangen und wie erw�hnt
 * weiterzuleiten.
 *
 * @author Dominik Grzelak
 */
public class SimpleStateRedirectedTouchListener implements OnKeyListener, OnTouchListener {

    private net.offbeatpioneer.retroengine.core.states.State currentState = null;
    private StateManager manager = StateManager.getInstance();
    private GestureDetector gestureDetector;

    public SimpleStateRedirectedTouchListener(GestureDetector gestureDetector) {
        this.gestureDetector = gestureDetector;
    }

    public boolean onKey(View arg0, int arg1, KeyEvent e) {
        currentState = manager.getActiveGameState();
        currentState.onKeyEvent(arg0, arg1, e);
        return false;
    }

    public boolean onTouch(View v, MotionEvent event) {
        currentState = manager.getActiveGameState();
        if (currentState == null) return false;
        boolean swipe = gestureDetector.onTouchEvent(event);
        boolean touch = currentState.onTouchEvent(v, event);
        return swipe || touch;
    }
}