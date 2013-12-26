package com.smeo.tools.gc.newparser;

import com.smeo.tools.gc.newparser.domain.CollectionEvent;
import com.smeo.tools.gc.newparser.domain.CollectorEvent;
import com.smeo.tools.gc.newparser.domain.GcTiming;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionEventParser {
    private static final String majorCollectionPatter= "[Full GC";
    private static final String majorSystemCollectionPatter = "[Full GC (System)";
    private static final String minorCollectionPatter= "[GC";

    public static CollectionEvent parseGcEvent(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvent(stringBuilder.toString());
    }

    public static CollectionEvent parseGcEvent(String loggedEvent) {
        if (!loggedEvent.contains(majorCollectionPatter)
                && !loggedEvent.contains(majorSystemCollectionPatter)
                && !loggedEvent.contains(minorCollectionPatter)){
            return null;
        }
        boolean isMinorCollection = !loggedEvent.contains(majorCollectionPatter);
        boolean isTriggeredBySystem = loggedEvent.contains(majorSystemCollectionPatter);

        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(loggedEvent);
        CollectorEvent youngGenCollector = null;
        CollectorEvent oldGenCollector = null;
        CollectorEvent permGenCollector = null;
        if (!isMinorCollection){
            permGenCollector = collectorEvents[2];
            oldGenCollector = collectorEvents[1];
            youngGenCollector = collectorEvents[0];
        } else {
            youngGenCollector = collectorEvents[0];
        }

        CollectorEvent totalCollectionValues = CollectorEventParser.parseTotalGcEventValues(loggedEvent);

        GcTiming gcTiming = GcTimingEventParser.parseGcEvent(loggedEvent);

        return new CollectionEvent(isMinorCollection,
                isTriggeredBySystem,
                youngGenCollector,
                oldGenCollector,
                permGenCollector,
                totalCollectionValues,
                gcTiming);
    }
}
