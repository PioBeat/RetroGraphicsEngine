package net.offbeatpioneer.retroengine.view;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import net.offbeatpioneer.retroengine.core.GamestateManager;
import net.offbeatpioneer.retroengine.core.RetroEngine;

/**
 * DrawView ist eine abgeleitet View-Komponente (erbt von {@link SurfaceView}),
 * die in der XXXXX verwendet wird. Diese stellt über eine
 * Handler das Canvas bereit, welches zum Zeichnen von Grafiken verwendet wird. <br/>
 * <br/>
 * Ist die Oberfläche der View erstellt worden, so werden die in der
 * XXXXXX definierten Threads (GameThread und TouchThread)
 * instantiiert. Dabei wird der Canvas-Handler übergeben, damit diese Zugriff
 * auf das Canvas-Objekt haben. Der {@link GameThread} gibt das Canvas dann
 * immer an den aktuellen Spielzustand weiter.
 *
 * @author Dominik Grzelak
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback,
        SensorEventListener {

    // Paint-Objekt stellt eine Art "Zeichenstift" dar
    public static Paint myPaint;

    // Surface manager, used for drawing etc
    // private SurfaceHolder mSurfaceHolder;
//    private Class<?> currentStateTemp;

    // Referenz zur Eltern-Activity halten
    private GameThread gameThread;
    private TouchListener touchListener;

    private Handler handler;

    private ProgressDialog dialog;

    private Activity myActivity;

//    Bitmap tempBmp = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);

    /**
     * Das eigentliche Zeichnen der Spielumgebung wird in der render()-Methode
     * des gerade aktuellen Spielzustandes geregelt. Dabei wird das
     * Canvas-Objekt �ber einen Handler weiter unten an die Spielzust�nde
     * weitergereicht.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        if(tempBmp.isRecycled() || tempBmp.getWidth()!=canvas.getWidth() || tempBmp.getHeight() != canvas.getHeight())
//        {
//            tempBmp.recycle();
//            tempBmp = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//            canvas.setBitmap(tempBmp);
//        }
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        paint.setAntiAlias(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
    }

    private Activity findActivity() {
        Context context = getContext();
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    public Resources res = this.getResources();

    // public GestureDetector gestureDetector;

    private Activity activity;

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        RetroEngine.init(context);
        setParentActivity(findActivity());
        GamestateManager.getInstance().setParentActivity(getParentActivity());

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        handler = new Handler();
        myPaint = new Paint();


        // Key-Events abfangen
        setFocusable(true);
        // Touch-Events abfangen
        setFocusableInTouchMode(true);

        setLongClickable(true);

        touchListener = new TouchListener();
        setOnKeyListener(touchListener);
        setOnTouchListener(touchListener);
    }

    public Bundle saveState(Bundle saveState) {
        return saveState;
    }

    public void restoreState(Bundle savedState) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    public GameThread getGameThread() {
        return gameThread;
    }

    public void setGameThread(GameThread gameThread) {
        this.gameThread = gameThread;
//        this.gameThread.setHandler(handler);

    }

    public TouchListener getTouchListener() {
        return touchListener;
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    /**
     * Callback-Methode wird aufgerufen, wenn die Oberfl�che der View erstellt
     * worden ist. Erst dann wird die Hilfsmethode
     * <code>initializeState()</code> aufgerufen, die den GameThread und den
     * TouchListener startet. Dies wird �ber einen AsyncTask gemacht, damit bei
     * langen Ladezeiten ein Ladebalken angezeigt wird.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Canvas tmp = holder.lockCanvas();
        RetroEngine.H = tmp.getHeight();
        RetroEngine.W = tmp.getWidth();
        holder.unlockCanvasAndPost(tmp);

        if (gameThread == null) {
            gameThread = new GameThread(this);
        }
        Class<?> currentStateTemp = gameThread.getCurrentState();

        if (GamestateManager.getInstance().getGamestates().size() == 1) {
            net.offbeatpioneer.retroengine.core.states.State state = GamestateManager.getInstance().getGamestates().get(0);
            currentStateTemp = state.getClass();
        } else if(currentStateTemp == null && GamestateManager.getInstance().getGamestates().size() > 1 && GamestateManager.getInstance().getActiveGameState() != null) {
            currentStateTemp = GamestateManager.getInstance().getActiveGameState().getClass();
        }

        if (currentStateTemp != null && GamestateManager.getInstance().getStateByClass(currentStateTemp).isInitAsync()) {
            dialog = ProgressDialog.show(getParentActivity(), "Loading", "Loading game ...", true, false);
            new LoadTask().execute(this);
        } else {
            initializeState();
        }
    }

    /**
     * Bereitet alles f�r die Erstellung eines Spielzustandes vor. Dabei wird
     * GameThread gestartet und der {@link TouchListener} instanziiert und dem
     * {@link GameThread} wird ein Handler �bergeben, damit dieser Zugriff auf
     * das Canvas dieser View hat.
     */
    private void initializeState() {

        gameThread.setHandler(handler);
//        gameThread.initStates();
//        gameThread.setCurrentState(currentStateTemp); //active one
        gameThread.initState();
        // Thread starten
        if (!gameThread.isAlive() && !RetroEngine.isRunning) {
            try {
                RetroEngine.isRunning = true;
                RetroEngine.shouldWait = false;
                gameThread.start();
            } catch (Exception e) {
                Log.v("GameThread", "GameThread already started.");
                e.printStackTrace();
                try {

                    gameThread.interrupt();
                    gameThread.join();
                    gameThread.start();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }

            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        // Auf gameThread warten bis dieser beendet wird.
        RetroEngine.isRunning = false;
        while (retry) {
            try {

                gameThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }

        gameThread = null;
    }

    /**
     * Ansynchroner Task führt das Laden des Spielzustandes durch.
     */
    private class LoadTask extends AsyncTask<DrawView, Void, Object> {

        protected Object doInBackground(final DrawView... v) {
            gameThread.setHandler(handler);
//            gameThread.setCurrentState(currentStateTemp); //active one
            gameThread.initState();
            // Thread starten
            if (!gameThread.isAlive() && !RetroEngine.isRunning) {
                try {
                    RetroEngine.isRunning = true;
                    RetroEngine.shouldWait = false;
                    gameThread.start();
                } catch (Exception e) {
                    Log.v("GameThread", "GameThread already started.");
                    e.printStackTrace();
                    try {

                        gameThread.interrupt();
                        gameThread.join();
                        gameThread.start();
                        return "OK";
                    } catch (Exception e2) {
                        e2.printStackTrace();
                        return "ERROR";
                    }

                }
            }
            return "";
        }

        protected void onPostExecute(Object result) {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    /**
     * Referenz zur Eltern-Activity behalten.
     *
     * @param parentActivity
     */
    public void setParentActivity(Activity parentActivity) {
        myActivity = parentActivity;
    }

    public Activity getParentActivity() {
        return myActivity;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
    }

}
