package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class CollectionEvent  extends GcLoggedEvent {
    private final boolean isMinorCollection;
    private final boolean isTriggeredBySystem;

    private final CollectorEvent youngGenCollector;
    private final CollectorEvent oldGenCollector;
    private final CollectorEvent permGenCollector;
    private final CollectorEvent totalCollectionValues;

    private final GcTiming gcTiming;

    public CollectionEvent(boolean isMinorCollection, boolean isTriggeredBySystem, CollectorEvent youngGenCollector,
                           CollectorEvent oldGenCollector, CollectorEvent permGenCollector, CollectorEvent totalCollectionValues, GcTiming gcTiming) {
        this.isMinorCollection = isMinorCollection;
        this.isTriggeredBySystem = isTriggeredBySystem;
        this.youngGenCollector = youngGenCollector;
        this.oldGenCollector = oldGenCollector;
        this.permGenCollector = permGenCollector;
        this.totalCollectionValues = totalCollectionValues;
        this.gcTiming = gcTiming;
    }

    public boolean isMinorCollection() {
        return isMinorCollection;
    }

    public boolean isTriggeredBySystem() {
        return isTriggeredBySystem;
    }

    public CollectorEvent getYoungGenCollector() {
        return youngGenCollector;
    }

    public CollectorEvent getOldGenCollector() {
        return oldGenCollector;
    }

    public CollectorEvent getPermGenCollector() {
        return permGenCollector;
    }

    public CollectorEvent getTotalCollectionValues() {
        return totalCollectionValues;
    }

    public GcTiming getGcTiming() {
        return gcTiming;
    }

}
