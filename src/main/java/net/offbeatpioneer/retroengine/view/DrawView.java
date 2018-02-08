package net.offbeatpioneer.retroengine.view;

import android.app.Activity;
import android.app.Dialog;
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

import net.offbeatpioneer.retroengine.core.StateManager;
import net.offbeatpioneer.retroengine.core.RetroEngine;

/**
 * {@link DrawView} is a {@link android.view.View} component and is derived from {@link SurfaceView}.
 * It is the drawing surface for the Engine. Put this component in your layout file.
 * It provides a canvas which is used to draw the sprite objects.
 * <p>
 * When the view is created the {@link RenderThread} will be instantiated if not
 * set from outside (in an activity for instance). The canvas will be passed to
 * the {@link RenderThread}. There it will be passed to the current active {@link net.offbeatpioneer.retroengine.core.states.State}.
 *
 * @author Dominik Grzelak
 */
public class DrawView extends SurfaceView implements SurfaceHolder.Callback, SensorEventListener {

    private RenderThread renderThread;
    private TouchListener touchListener;

    private Handler handler;

    //for async loading a state
    private ProgressDialog dialog;

    // Hold reference to parent activity
    private Activity myActivity;

    public Resources res = this.getResources();

    private DrawViewInteraction viewInteraction;
    private final DrawViewInteraction emptyInteraction = new DrawViewInteraction() {
        @Override
        public void onCreated(DrawView drawView) {

        }
    };

    public interface DrawViewInteraction {
        void onCreated(DrawView drawView);
    }

    /**
     * The actual (concrete) drawing of sprites or a scene is done in the {@code render()} method
     * of a {@link net.offbeatpioneer.retroengine.core.states.State}.
     */
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if(tempBmp.isRecycled() || tempBmp.getWidth()!=canvas.getWidth() || tempBmp.getHeight() != canvas.getHeight())
//        {
//            tempBmp.recycle();
//            tempBmp = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Bitmap.Config.ARGB_8888);
//            canvas.setBitmap(tempBmp);
//        }
//        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        paint.setAntiAlias(true);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
//    }
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

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);

        RetroEngine.init(context);
        setParentActivity(findActivity());
        StateManager.getInstance().setParentActivity(getParentActivity());

        // register our interest in hearing about changes to our surface
        SurfaceHolder holder = getHolder();
        holder.addCallback(this);

        handler = new Handler();

        // Intercept key events
        setFocusable(true);
        // Intercept touch events
        setFocusableInTouchMode(true);
        // allow long click events
        setLongClickable(true);

        touchListener = new TouchListener();
        setOnKeyListener(touchListener);
        setOnTouchListener(touchListener);
        viewInteraction = emptyInteraction;
    }

    public void addListener(DrawViewInteraction listener) {
        this.viewInteraction = listener;
    }

    public void removeListener() {
        this.viewInteraction = emptyInteraction;
    }

    public Bundle saveState(Bundle saveState) {
        return saveState;
    }

    public void restoreState(Bundle savedState) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        initSurfaceSize(holder);
    }

    public RenderThread getRenderThread() {
        return renderThread;
    }

    public void setRenderThread(RenderThread renderThread) {
        this.renderThread = renderThread;
    }

    public TouchListener getTouchListener() {
        return touchListener;
    }

    public void setTouchListener(TouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public void initSurfaceSize(final SurfaceHolder holder) {
        Canvas tmp = holder.lockCanvas();
        RetroEngine.H = tmp.getHeight();
        RetroEngine.W = tmp.getWidth();
        holder.unlockCanvasAndPost(tmp);
    }

    /**
     * This method is called when the surface of the is created.
     * Only then the method {@code initializeState()} will be called which
     * starts the {@link RenderThread}.
     * <p>
     * The current {@link net.offbeatpioneer.retroengine.core.states.State} will be fetched
     * and determined if it should be loaded
     * asynchronously with {@link AsyncTask} or directly.
     * After this the {@link RenderThread} is started.
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        initSurfaceSize(holder);

        if (renderThread == null) {
            renderThread = new RenderThread(this);
        }

        this.viewInteraction.onCreated(this);

        Class<?> currentStateTemp = renderThread.getCurrentState();

        //try to get the current state
        if (StateManager.getInstance().getGamestates().size() == 1) {
            net.offbeatpioneer.retroengine.core.states.State state = StateManager.getInstance().getGamestates().get(0);
            currentStateTemp = state.getClass();
        } else if (currentStateTemp == null && StateManager.getInstance().getGamestates().size() > 1 && StateManager.getInstance().getActiveGameState() != null) {
            currentStateTemp = StateManager.getInstance().getActiveGameState().getClass();
        }

        if (currentStateTemp != null && StateManager.getInstance().getStateByClass(currentStateTemp) == null) {
            throw new IllegalStateException("State is not added to the StateManager");
        }

        if (currentStateTemp != null && StateManager.getInstance().getStateByClass(currentStateTemp).isInitAsync()) {
            dialog = ProgressDialog.show(getParentActivity(), "Loading", "Please wait ...", true, false);
            new LoadTask(renderThread, handler, dialog).execute(this);
        } else {
            initializeState();
        }

    }

    /**
     * Everthing is prepared to start a {@link net.offbeatpioneer.retroengine.core.states.State}.
     * The {@link RenderThread} will be started and a canvas from the {@link DrawView} will be passed
     * to the {@link RenderThread}.
     * <p>
     * This method is called within the {@code surfaceCreated} method after the surface from this
     * View is created.
     */
    private void initializeState() {

        renderThread.setHandler(handler);
        renderThread.initState();
        // Thread starten
        if (!renderThread.isAlive() && !RetroEngine.isRunning()) {
            try {
                RetroEngine.changeRunningState(true);
                RetroEngine.resumeRenderThread();
                renderThread.start();
            } catch (Exception e) {
                Log.v("RenderThread", "RenderThread already started." + e.toString());
                try {
                    RetroEngine.pauseRenderThread();
                    renderThread.interrupt();
                    renderThread.join();
                    renderThread.start();
                } catch (Exception e2) {
                    e2.printStackTrace();
                    Log.e("RenderThread", e.toString());
                }
            }
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;

        // Pause RenderThread and wait until closed
        RetroEngine.pauseRenderThread();
        while (retry) {
            try {
                RetroEngine.changeRunningState(false);
                renderThread.join();
                retry = false;
            } catch (InterruptedException ignored) {
            }
        }

        renderThread = null;
    }

    /**
     * Ansynchroner Task führt das Laden des Spielzustandes durch.
     */
    private static class LoadTask extends AsyncTask<DrawView, Void, Object> {

        RenderThread renderThread;
        Handler handler;
        Dialog dialog;

        LoadTask(RenderThread renderThread, Handler handler) {
            this(renderThread, handler, null);
        }

        LoadTask(RenderThread renderThread, Handler handler, Dialog dialog) {
            this.renderThread = renderThread;
            this.handler = handler;
            this.dialog = dialog;
        }

        protected Object doInBackground(final DrawView... v) {
            renderThread.setHandler(handler);
            renderThread.initState();
            // Start thread
            if (!renderThread.isAlive() && !RetroEngine.isRunning()) {
                try {
                    RetroEngine.changeRunningState(true);
                    RetroEngine.resumeRenderThread();
                    renderThread.start();
                } catch (Exception e) {
                    Log.v("RenderThread", "RenderThread already started.");
                    try {
                        renderThread.interrupt();
                        renderThread.join();
                        renderThread.start();
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
     * Set reference to parent activity
     *
     * @param parentActivity the activity where this component is inserted
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
