package com.smeo.tools.gc.newparser;

import com.smeo.tools.gc.newparser.domain.CollectionEvent;
import com.smeo.tools.gc.newparser.domain.CollectorEvent;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionEventParser {
    String majorCollectionPatter= "[Full GC";
    String majorSystemCollectionPatter = "[Full GC (System)";
    String minorCollectionPatter= "[GC";
    public CollectionEvent parseGcEvent(String loggedEvent) {
        boolean isMinorCollection = !loggedEvent.contains(majorCollectionPatter);
        boolean isTriggeredBySystem = loggedEvent.contains(majorSystemCollectionPatter);

        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(loggedEvent);
        CollectorEvent youngGenCollector = null;
        CollectorEvent oldGenCollector = null;
        CollectorEvent permGenCollector = null;
        if (!isMinorCollection){
            youngGenCollector = collectorEvents[0];
            oldGenCollector = collectorEvents[1];
            youngGenCollector = collectorEvents[2];
        } else {
            permGenCollector = collectorEvents[0];
        }

        return new CollectionEvent(isMinorCollection,
                isTriggeredBySystem,
                youngGenCollector,
                oldGenCollector,
                permGenCollector);
    }
}
