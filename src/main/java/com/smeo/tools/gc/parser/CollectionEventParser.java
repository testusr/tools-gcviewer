package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.CollectionEvent;
import com.smeo.tools.gc.domain.CollectorEvent;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.MemorySegment;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionEventParser {
    private static final String majorCollectionPatter = "[Full GC";
    private static final String majorSystemCollectionPatter = "[Full GC (System)";
    private static final String minorCollectionPatter = "[GC";

    public static CollectionEvent parseGcEvent(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines) {
            stringBuilder.append(currString);
        }
        return parseGcEvent(stringBuilder.toString());
    }

    public static CollectionEvent parseGcEvent(String loggedEvent) {
        if (!loggedEvent.contains(majorCollectionPatter)
                && !loggedEvent.contains(majorSystemCollectionPatter)
                && !loggedEvent.contains(minorCollectionPatter)) {
            return null;
        }
        boolean isMinorCollection = !loggedEvent.contains(majorCollectionPatter);
        boolean isTriggeredBySystem = loggedEvent.contains(majorSystemCollectionPatter);

        CollectorEvent totalCollectionValues = CollectorEventParser.parseTotalGcEventValues(loggedEvent);
        GcTiming gcTiming = GcTimingEventParser.parseGcEvent(loggedEvent);

        return getCollectionEvent(loggedEvent, isMinorCollection, isTriggeredBySystem, totalCollectionValues, gcTiming);
    }

    private static CollectionEvent getCollectionEvent(String loggedEvent, boolean minorCollection, boolean triggeredBySystem, CollectorEvent totalCollectionValues, GcTiming gcTiming) {
        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(loggedEvent);
        CollectorEvent youngGenCollector = null;
        CollectorEvent oldGenCollector = null;
        CollectorEvent permGenCollector = null;

        for (CollectorEvent currCollectorEvent : collectorEvents) {
            if (currCollectorEvent != null) {
                if (MemorySegment.PermGen == currCollectorEvent.getCollector().getMemorySegment()) {
                    if (permGenCollector != null) {
                        throw new IllegalArgumentException("two 'PermGen' collectors detected in one gc event");
                    }
                    permGenCollector = currCollectorEvent;
                }
                if (MemorySegment.YoungGen == currCollectorEvent.getCollector().getMemorySegment()) {
                    if (youngGenCollector != null) {
                        throw new IllegalArgumentException("two 'YoungGen' collectors detected in one gc event");
                    }
                    youngGenCollector = currCollectorEvent;
                }
                if (MemorySegment.OldGen == currCollectorEvent.getCollector().getMemorySegment()) {
                    if (oldGenCollector != null) {
                        throw new IllegalArgumentException("two 'OldGen' collectors detected in one gc event");
                    }
                    oldGenCollector = currCollectorEvent;
                }
            }
        }


        return new CollectionEvent(minorCollection,
                triggeredBySystem,
                youngGenCollector,
                oldGenCollector,
                permGenCollector,
                totalCollectionValues,
                gcTiming);
    }
}
