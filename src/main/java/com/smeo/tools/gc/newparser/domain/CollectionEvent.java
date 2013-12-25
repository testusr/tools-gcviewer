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

    public CollectionEvent(boolean isMinorCollection, boolean isTriggeredBySystem, CollectorEvent youngGenCollector, CollectorEvent oldGenCollector, CollectorEvent permGenCollector) {
        this.isMinorCollection = isMinorCollection;
        this.isTriggeredBySystem = isTriggeredBySystem;
        this.youngGenCollector = youngGenCollector;
        this.oldGenCollector = oldGenCollector;
        this.permGenCollector = permGenCollector;
    }
}
