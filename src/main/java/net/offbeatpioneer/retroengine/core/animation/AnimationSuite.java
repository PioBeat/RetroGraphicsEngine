package net.offbeatpioneer.retroengine.core.animation;

import android.graphics.Canvas;
import android.graphics.Paint;

import net.offbeatpioneer.retroengine.core.sprites.AbstractSprite;

import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

//TODO: add delay for animation

/**
 * Base class for all animations
 * <p>
 * Subclasses define the concrete animation and overriding the {@code animationLogicTemplate()} method
 * for implementation.
 * <p>
 * Resetting the the animated values is off by default. Call {@code isDoReset(true)} to reset the
 * animated properties to the initial value when the animation was started.
 *
 * @author Dominim Grzelak
 * @since 14.09.2014
 */
public abstract class AnimationSuite {
    private boolean started = false;
    boolean finished = false;
    private AbstractSprite animatedSprite;
    private int time;
    protected IAnimationSuiteListener listener = new EmptyAnimationSuiteListener();

    private boolean loop = false;
    private long now = 0;

    @Deprecated
    //TODO: entfernen, soll einfach in einer Action für ein Sprite festgelegt werden und nicht hier, dazu noch eine extra logic action für ein sprite machen oder sowas
    private int stopAfterMilliseconds;
    private Timer timer;

    /**
     * Der Schalter ermöglicht es anzugeben, ob die Animation wieder durchgeführt werden kann, nachdem diese
     * automatisch gestoppt wurde, wenn die Methode {@code startAnimation()} wieder ausgeführt wird. <br/>
     * Kommt nur zum Einsatz wenn {@code stopAfterMilliseconds} != -1 ist.
     */
    private boolean doReset;

    /**
     * Default Constructor
     * <p>
     * Animation is not repeated and not started immediately. The values are reset to default if
     * animation ends.
     */
    protected AnimationSuite() {
        loop = false;
        started = false;
        finished = false;
        doReset = false;
        setStopAfterMilliseconds(-1);
    }

    //TODO
    public AnimationSuite copyAnimationSuite(AnimationSuite original) {
        if (original instanceof RotationAnimation) {
            RotationAnimation copy = new RotationAnimation();
            copy.setLoop(original.isLoop());
            copy.setCurrentAngle(((RotationAnimation) original).getCurrentAngle());
            copy.setAnimatedSprite(original.getAnimatedSprite());
            copy.setStopAfterMilliseconds(original.getStopAfterMilliseconds());
            copy.setTime(original.getTime());
            return copy;
        }
        return null;
    }

    /**
     * Concrete logic for animations. Subclasses implement this.
     */
    protected abstract void animationLogicTemplate();

    /**
     * Resets the settings for the animation if {@code doReset} is {@code true}. Everthing
     * will be reset to the initial state so that the animation can be started again.
     * <p>
     * The member variables {@code finished} and {@code started} will be set to {@code false}.
     */
    public void reset() {
        finished = false;
        started = false;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public void stop() {
        finished = true;
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }
    }

    public boolean hasStarted() {
        return started;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Startet die Animation, d.h. konkret, ruft die dahinterliegende Logik auf, die für die Animation
     * erforderlich ist. Solll die Animation nach einer bestimmten Zeit automatisch beendet werden, so wird
     * in diesem Fall ein {@link TimerTask} angelegt. <br> Wenn die Animation nach Beendigung wieder neu ausgeführt
     * werden soll ({@code doReset}), so werden die Einstellungen mit {@code reset()} zurückgesetzt.
     */
    public void startAnimation() {
        started = true;
        listener.onAnimationStart(this);
        if (getStopAfterMilliseconds() != -1 && timer == null) { //D.h. animation soll auch wieder aufhören nach paar sekunden
            timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finished = true;
                    if (isDoReset()) //Animation kann nochmal gestartet werden
                        reset();
                }
            }, getStopAfterMilliseconds());
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public boolean isLoop() {
        return loop;
    }

    /**
     * Define if animation can be repeated
     *
     * @param loop if true animation is repeated
     */
    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void animationLogic() {
        if (hasStarted() && !isFinished()) {
            animationLogicTemplate();
        }
        if (isFinished()) {
            listener.onAnimationEnd(this);
        }
    }


    protected void renderTemplate(Canvas canvas, Paint paint, long currentTime) {
        //Do nothing
    }

    public IAnimationSuiteListener getListener() {
        return listener;
    }

    public void setListener(IAnimationSuiteListener listener) {
        this.listener = listener;
    }

    public AbstractSprite getAnimatedSprite() {
        return animatedSprite;
    }

    public void setAnimatedSprite(AbstractSprite animatedSprite) {
        this.animatedSprite = animatedSprite;
    }

    public int getStopAfterMilliseconds() {
        return stopAfterMilliseconds;
    }

    public void setStopAfterMilliseconds(int stopAfterMilliseconds) {
        this.stopAfterMilliseconds = stopAfterMilliseconds;
    }

    public boolean isDoReset() {
        return doReset;
    }

    /**
     * Should the animated values be reset after animation finishes?
     *
     * @param doReset true if animated values should be reset, otherwise false
     */
    public void setDoReset(boolean doReset) {
        this.doReset = doReset;
    }

    protected Timer getTimer() {
        return timer;
    }
}
