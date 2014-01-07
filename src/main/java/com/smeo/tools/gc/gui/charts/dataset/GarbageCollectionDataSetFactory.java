package com.smeo.tools.gc.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.CollectionEvent;
import com.smeo.tools.gc.domain.CollectorEvent;
import com.smeo.tools.gc.domain.GcLoggedEvent;

import java.util.ArrayList;
import java.util.List;

public class GarbageCollectionDataSetFactory {
    public static GarbageCollectionDataSet createGarbageCollectionDataSets(List<GcLoggedEvent> allGarbageCollectionEvents) {
        int majorGcCount = 0;
        int minorGcCount = 0;
        int systemGcCount = 0;

        GarbageCollectionDataSet resultGarbageCollectionDataSet = new GarbageCollectionDataSet();

        long lastTime = -1;
        for (GcLoggedEvent currEvent : allGarbageCollectionEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                long currEventTime = currCollectionEvent.getTimestamp();

                if (currEventTime < lastTime) {
                    System.out.println("wrong " + currEventTime + " last: " + lastTime + " diff: " + (currEventTime - lastTime));
                }
                lastTime = currEventTime;

                if (currCollectionEvent.isMinorCollection()) {
                    resultGarbageCollectionDataSet.minorGc.add(new DataSetEntry(currEventTime, minorGcCount++));
                } else {
                    resultGarbageCollectionDataSet.majorGc.add(new DataSetEntry(currEventTime, majorGcCount++));
                }
                if (currCollectionEvent.isTriggeredBySystem()) {
                    resultGarbageCollectionDataSet.systemTriggered.add(new DataSetEntry(currEventTime, systemGcCount++));
                }
            }
        }
        return resultGarbageCollectionDataSet;
    }

    public static GcDurationDataSet createGcDurationDataSet(List<GcLoggedEvent> allGarbageCollectionEvents){
        GcDurationDataSet gcDurationDataSet = new GcDurationDataSet();
        for (GcLoggedEvent currEvent : allGarbageCollectionEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                DataSetEntry currEventDataSet = null;

                CollectorEvent oldGenCollector = currCollectionEvent.getOldGenCollector();
                CollectorEvent youngGenCollector  = currCollectionEvent.getYoungGenCollector();

                if (currCollectionEvent.isMinorCollection()){
                    if (oldGenCollector != null){
                        if (youngGenCollector.getTimeUsedInSecs() != null){
                            gcDurationDataSet.minorGc.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                    youngGenCollector.getTimeUsedInSecs()));
                        }
                        // this collection was triggered by a minor collection
                        if (oldGenCollector.getTimeUsedInSecs() != null){
                            gcDurationDataSet.majorGcTriggeredByMinor.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                    oldGenCollector.getTimeUsedInSecs()));
                        }
                    } else {
                        if (youngGenCollector.getTimeUsedInSecs() != null){
                            // more accurate and preferable
                            gcDurationDataSet.minorGc.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                    youngGenCollector.getTimeUsedInSecs()));
                        } else {
                            gcDurationDataSet.minorGc.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                    currCollectionEvent.getGcTiming().getRealTimInSec()));
                        }

                    }
                } else {
                    if (oldGenCollector.getTimeUsedInSecs() != null){
                        gcDurationDataSet.majorGc.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                oldGenCollector.getTimeUsedInSecs()));
                    } else {
                        gcDurationDataSet.majorGc.add(new DataSetEntry(currCollectionEvent.getTimestamp(),
                                currCollectionEvent.getGcTiming().getRealTimInSec()));
                    }
                }

            }
        }
        return gcDurationDataSet;
    }

    public static class GcDurationDataSet {
        public List<DataSetEntry> majorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> minorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> majorGcTriggeredByMinor = new ArrayList<DataSetEntry>();

    }

    public static class GarbageCollectionDataSet {
        public List<DataSetEntry> majorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> minorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> systemTriggered = new ArrayList<DataSetEntry>();
    }
}
