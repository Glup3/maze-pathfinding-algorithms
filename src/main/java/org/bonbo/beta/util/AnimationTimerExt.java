package org.bonbo.beta.util;

import javafx.animation.AnimationTimer;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class AnimationTimerExt extends AnimationTimer {

    private long sleepNs;
    private long skipSteps;
    private long prevTime;

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

        //TODO BUG: executes too often
        for (int i = 0; i <= skipSteps; i++) {
            handle();
        }

        renderCanvas();
    }

    public abstract void handle();

    public abstract void renderCanvas();

}
