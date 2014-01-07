package com.smeo.tools.gc.domain;


/**
 * Created by joachim on 25.12.13.
 */
public class CollectorEvent {
    private final Double timeUsedInSecs;
    private GarbageCollector collector;
    private MemorySpace memoryBefore;
    private MemorySpace memoryAfter;

    public CollectorEvent(GarbageCollector collect, MemorySpace memoryBefore, MemorySpace memoryAfter, Double timeInSecs) {
        this.collector = collect;
        this.memoryBefore = memoryBefore;
        this.memoryAfter = memoryAfter;
        this.timeUsedInSecs = timeInSecs;
    }

    public GarbageCollector getCollector() {
        return collector;
    }

    public MemorySpace getMemoryBefore() {
        return memoryBefore;
    }

    public MemorySpace getMemoryAfter() {
        return memoryAfter;
    }

    public Double getTimeUsedInSecs() { return timeUsedInSecs; }
}
