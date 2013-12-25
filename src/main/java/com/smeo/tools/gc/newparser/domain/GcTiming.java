package com.smeo.tools.gc.newparser.domain;

public class GcTiming {
    private double userTimeInSec;
    private double sysTimeInSec;
    private double realTimeInSec;

    public GcTiming(double userTimeInSec, double sysTimeInSec, double realTimeInSec) {
        this.realTimeInSec = realTimeInSec;
        this.userTimeInSec = userTimeInSec;
        this.sysTimeInSec = sysTimeInSec;
    }

    public double getUserTimeInSec() {
        return userTimeInSec;
    }

    public double getSysTimeInSec() {
        return sysTimeInSec;
    }

    public double getRealTimInSec() {
        return realTimeInSec;
    }
}
