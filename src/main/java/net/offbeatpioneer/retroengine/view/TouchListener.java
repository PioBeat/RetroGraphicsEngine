package net.offbeatpioneer.retroengine.view;

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;

import net.offbeatpioneer.retroengine.core.StateManager;

/**
 * {@link TouchListener} nimmt die Eingaben entgegen, die als Touch-Event oder
 * �ber die Tastatur ankommen k�nnen. Diese werden immer an den gerade aktuellen
 * Spielzustand (State) weitergeleitet. Dieser Listener wird an die
 * {@link DrawView} gebunden, um die Eingaben dieser abzufangen und wie erw�hnt
 * weiterzuleiten.
 * 
 * @author Dominik Grzelak
 * 
 */
public class TouchListener implements OnKeyListener, OnTouchListener {

	private net.offbeatpioneer.retroengine.core.states.State currentState = null;
	private StateManager manager = StateManager.getInstance();
	
	public TouchListener() {
	}

	public boolean onKey(View arg0, int arg1, KeyEvent e) {
		currentState = manager.getActiveGameState();
		currentState.onKeyEvent(arg0, arg1, e);
		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {
		currentState = manager.getActiveGameState();
		if(currentState == null) return false;
		currentState.onTouchEvent(v, event);
		return true;
	}
}