package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class ApplicationStopTimeEvent extends GcLoggedEvent{
    double stopTimeInSec;

    public ApplicationStopTimeEvent(double stopTimeInSec) {
        this.stopTimeInSec = stopTimeInSec;
    }

    public double getStopTimeInSec() {
        return stopTimeInSec;
    }
}
