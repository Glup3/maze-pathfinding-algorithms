package org.bonbo.beta.util;

import javafx.animation.AnimationTimer;

public abstract class AnimationTimerExt extends AnimationTimer {

    private long sleepNs;
    private long skipSteps = 1;

    private long prevTime = 0;
    private long step;

    public AnimationTimerExt(long sleepMs) {
        this.sleepNs = sleepMs * 1_000_000;
    }

    public AnimationTimerExt(long sleepMs, long skipSteps) {
        this.sleepNs = sleepMs * 1_000_000;
        this.skipSteps = skipSteps;
    }

    @Override
    public void handle(long now) {

        if ((now - prevTime) < sleepNs) {
            return;
        }

        prevTime = now;

        handle();
        step++;

        if (step % skipSteps == 0) {
            renderCanvas();
        }
    }

    public abstract void handle();

    public abstract void renderCanvas();

}
