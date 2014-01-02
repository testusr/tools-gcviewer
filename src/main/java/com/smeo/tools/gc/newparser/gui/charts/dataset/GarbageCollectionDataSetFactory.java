package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.newparser.domain.CollectionEvent;
import com.smeo.tools.gc.newparser.domain.GcLoggedEvent;

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
                DataSetEntry currEventDataSet = new DataSetEntry(currCollectionEvent.getTimestamp(), currCollectionEvent.getGcTiming().getRealTimInSec());

                if (currCollectionEvent.isMinorCollection()){
                    gcDurationDataSet.minorGc.add(currEventDataSet);
                } else {
                    gcDurationDataSet.majorGc.add(currEventDataSet);
                }

            }
        }
        return gcDurationDataSet;
    }

    public static class GcDurationDataSet {
        public List<DataSetEntry> majorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> minorGc = new ArrayList<DataSetEntry>();
    }

    public static class GarbageCollectionDataSet {
        public List<DataSetEntry> majorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> minorGc = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> systemTriggered = new ArrayList<DataSetEntry>();
    }
}
