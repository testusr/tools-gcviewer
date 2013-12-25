package com.smeo.tools.gc.newparser.domain;

import com.smeo.tools.gc.domain.MemorySpace;
import com.smeo.tools.gc.newparser.GarbageCollector;

/**
 * Created by joachim on 25.12.13.
 */
public class CollectorEvent {
    private GarbageCollector collect;
    private MemorySpace memoryBefore;
    private MemorySpace memoryAfter;

    public CollectorEvent(GarbageCollector collect, MemorySpace memoryBefore, MemorySpace memoryAfter) {
        this.collect = collect;
        this.memoryBefore = memoryBefore;
        this.memoryAfter = memoryAfter;
    }

    public GarbageCollector getCollect() {
        return collect;
    }

    public MemorySpace getMemoryBefore() {
        return memoryBefore;
    }

    public MemorySpace getMemoryAfter() {
        return memoryAfter;
    }
}
