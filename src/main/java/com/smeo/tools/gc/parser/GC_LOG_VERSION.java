package com.smeo.tools.gc.parser;

/**
 * Created by truehl on 11/19/15.
 */
public enum GC_LOG_VERSION {
    V1_6('.'),
    V1_7_plus(',');


    private final char decimalMarker;

    GC_LOG_VERSION(char decimalMarker) {
        this.decimalMarker = decimalMarker;
    }

    public char decimalMarker() {
        return decimalMarker;
    }
}
