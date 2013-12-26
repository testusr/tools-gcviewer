package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class FullMemoryInfo {
    private final GarbageCollector youngGenCollector;
    private final MemorySpaceInfo youngTotalSpace;
    private final MemorySpaceInfo edenSize;
    private final MemorySpaceInfo fromSurvivor;
    private final MemorySpaceInfo toSurvivor;

    private final GarbageCollector oldGenCollector;
    private final MemorySpaceInfo oldObjectSpace;

    private final GarbageCollector permGenCollector;
    private final MemorySpaceInfo perObjectSpace;

    public FullMemoryInfo(GarbageCollector youngGenCollector, MemorySpaceInfo youngTotalSpace, MemorySpaceInfo edenSize, MemorySpaceInfo fromSurvivor, MemorySpaceInfo toSurvivor, GarbageCollector oldGenCollector, MemorySpaceInfo oldObjectSpace, GarbageCollector permGenCollector, MemorySpaceInfo perObjectSpace) {
        this.youngGenCollector = youngGenCollector;
        this.youngTotalSpace = youngTotalSpace;
        this.edenSize = edenSize;
        this.fromSurvivor = fromSurvivor;
        this.toSurvivor = toSurvivor;
        this.oldGenCollector = oldGenCollector;
        this.oldObjectSpace = oldObjectSpace;
        this.permGenCollector = permGenCollector;
        this.perObjectSpace = perObjectSpace;
    }

    public GarbageCollector getYoungGenCollector() {
        return youngGenCollector;
    }

    public MemorySpaceInfo getYoungTotalSpace() {
        return youngTotalSpace;
    }

    public MemorySpaceInfo getEdenSize() {
        return edenSize;
    }

    public MemorySpaceInfo getFromSurvivor() {
        return fromSurvivor;
    }

    public MemorySpaceInfo getToSurvivor() {
        return toSurvivor;
    }

    public GarbageCollector getOldGenCollector() {
        return oldGenCollector;
    }

    public MemorySpaceInfo getOldObjectSpace() {
        return oldObjectSpace;
    }

    public GarbageCollector getPermGenCollector() {
        return permGenCollector;
    }

    public MemorySpaceInfo getPerObjectSpace() {
        return perObjectSpace;
    }
}
