package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class CollectionEvent {
    private boolean isMinorCollection;
    private boolean isTriggeredBySystem;

    private CollectorEvent youngGenCollector;
    private CollectorEvent oldGenCollector;
    private CollectorEvent permGenCollector;

    private GcTiming gcTiming;

    public CollectionEvent(boolean isMinorCollection, boolean isTriggeredBySystem, CollectorEvent youngGenCollector,
                           CollectorEvent oldGenCollector, CollectorEvent permGenCollector, GcTiming gcTiming) {
        this.isMinorCollection = isMinorCollection;
        this.isTriggeredBySystem = isTriggeredBySystem;
        this.youngGenCollector = youngGenCollector;
        this.oldGenCollector = oldGenCollector;
        this.permGenCollector = permGenCollector;
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

    public GcTiming getGcTiming() {
        return gcTiming;
    }
}
