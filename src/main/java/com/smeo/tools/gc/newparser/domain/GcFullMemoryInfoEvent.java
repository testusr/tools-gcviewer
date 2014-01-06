package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class GcFullMemoryInfoEvent  extends GcLoggedEvent {
    private final FullMemoryInfo beforeCollection;
    private final FullMemoryInfo afterCollection;
    private GcTiming gcDuration;

    public GcFullMemoryInfoEvent(FullMemoryInfo beforeCollection, FullMemoryInfo afterCollection) {
        this.beforeCollection = beforeCollection;
        this.afterCollection = afterCollection;
    }

    public FullMemoryInfo getAfterCollection() {
        return afterCollection;
    }

    public FullMemoryInfo getBeforeCollection() {
        return beforeCollection;
    }

    public GcTiming getGcDuration() {
        return gcDuration;
    }

    
}
