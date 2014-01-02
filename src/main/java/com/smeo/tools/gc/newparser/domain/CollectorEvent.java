package com.smeo.tools.gc.newparser.domain;


/**
 * Created by joachim on 25.12.13.
 */
public class CollectorEvent {
    private GarbageCollector collector;
    private MemorySpace memoryBefore;
    private MemorySpace memoryAfter;

    public CollectorEvent(GarbageCollector collect, MemorySpace memoryBefore, MemorySpace memoryAfter) {
        this.collector = collect;
        this.memoryBefore = memoryBefore;
        this.memoryAfter = memoryAfter;
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
}
