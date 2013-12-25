package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class ApplicationTimeEvent  extends GcLoggedEvent {
    double runTimeInSec;

    public ApplicationTimeEvent(double runTimeInSec) {
        this.runTimeInSec = runTimeInSec;
    }

    public double getRunTimeInSec() {
        return runTimeInSec;
    }
}
