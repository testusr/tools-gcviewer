package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.Tenuring;
import com.smeo.tools.gc.newparser.domain.CollectionEvent;
import com.smeo.tools.gc.newparser.domain.CollectorEvent;
import com.smeo.tools.gc.newparser.domain.GcLoggedEvent;
import com.smeo.tools.gc.newparser.domain.TenuringEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: truehl
 * Date: 12/23/13
 * Time: 9:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class TenuringDataSetFactory {
    public static TenuringDataSet createDataSet(List<GcLoggedEvent> allGarbageCollectionEvents) {
        TenuringDataSet survivorInputOutput = new TenuringDataSet();

        for (GcLoggedEvent currEvent : allGarbageCollectionEvents) {
            if (currEvent instanceof TenuringEvent) {
                TenuringEvent currTenuring = (TenuringEvent) currEvent;
                if (currTenuring != null) {
                    long currEventTime = currTenuring.getTimestamp();

                    survivorInputOutput.desiredSurvivorSize.add(new DataSetEntry(currEventTime, currTenuring.desiredSurvivorSpace / 1024));
                    survivorInputOutput.maxAges.add(new DataSetEntry(currEventTime, currTenuring.max));
                    survivorInputOutput.newThreshold.add(new DataSetEntry(currEventTime, currTenuring.newThreshold));
                    survivorInputOutput.sumTotal.add(new DataSetEntry(currEventTime, currTenuring.getSumOfTotals() / 1024));
                    survivorInputOutput.sumUsed.add(new DataSetEntry(currEventTime, currTenuring.getSumOfUsed() / 1024));

                    for (int i = 0; i < Tenuring.MAX_AGE; i++) {
                        int currTotalSpace = currTenuring.totalSpace[i] / 1024;
                        survivorInputOutput.agesTotal[i].add(new DataSetEntry(currEventTime, currTotalSpace));
                    }
                }
            }
        }
        return survivorInputOutput;
    }

    public static MemoryDimensioning createMemoryDimensionDataSet(List<GcLoggedEvent> allGarbageCollectionEvents) {
        MemoryDimensioning memoryDimensioning = new MemoryDimensioning();

        for (GcLoggedEvent currEvent : allGarbageCollectionEvents) {
            if (currEvent instanceof TenuringEvent) {
                TenuringEvent currTenuringEvent = (TenuringEvent) currEvent;
                memoryDimensioning.edenSize.add(new DataSetEntry(currEvent.getTimestamp(), (currTenuringEvent.getEdenSize() / 1024)));
                memoryDimensioning.survivorSize.add(new DataSetEntry(currEvent.getTimestamp(), (currTenuringEvent.desiredSurvivorSpace / 1024)));
            } else if (currEvent instanceof CollectionEvent){
                CollectionEvent collectionEvent = (CollectionEvent) currEvent;
                CollectorEvent oldGenCollector = collectionEvent.getOldGenCollector();
                if (oldGenCollector != null){
                    memoryDimensioning.oldGen.add(new DataSetEntry(currEvent.getTimestamp(), oldGenCollector.getMemoryAfter().getAvailableSpaceInK()));
                }
            }
        }
        return memoryDimensioning;
    }

    public static TenuringDataSetFactory.AllocationDemography createDemographyDataSet(List<GcLoggedEvent> allGarbageCollectionEvents) {
        AllocationDemography allocationDemography = null;

        TenuringEvent prevTenuringEvent = null;
        allocationDemography = new AllocationDemography();

        for (GcLoggedEvent currEvent : allGarbageCollectionEvents) {
            if (currEvent instanceof TenuringEvent) {
                TenuringEvent currTenuringEvent = (TenuringEvent) currEvent;
                if (prevTenuringEvent != null && currTenuringEvent != null) {
                    float shortLivedObjAllocRate = calcShortLivedObjAllocRate(prevTenuringEvent, currTenuringEvent);
                    float longLivedObjAllocRate = calcLongLivedObjAllocRate(prevTenuringEvent, currTenuringEvent);
                    float totalAllocRate = calcTotalObjAllocRate(prevTenuringEvent, currTenuringEvent);

                    long currEventTimestamp = currEvent.getTimestamp();

                    if (shortLivedObjAllocRate >= 0) {
                        allocationDemography.shortLivedObjAllocationRate.add(new DataSetEntry(
                                currEventTimestamp,
                                shortLivedObjAllocRate));
                    }
                    if (longLivedObjAllocRate >= 0) {
                        allocationDemography.longLivedObjAllocationRate.add(new DataSetEntry(
                                currEventTimestamp,
                                longLivedObjAllocRate));
                    }
                    if (totalAllocRate >= 0) {
                        allocationDemography.totalAllocationRate.add(new DataSetEntry(
                                currEventTimestamp,
                                totalAllocRate));
                    }

                    if (shortLivedObjAllocRate >= 0 && longLivedObjAllocRate >= 0 && totalAllocRate >= 0) {
                        float midAgedAllocRate = totalAllocRate - shortLivedObjAllocRate - longLivedObjAllocRate;
                        allocationDemography.midAgedObjAllocationRate.add(new DataSetEntry(
                                currEventTimestamp, midAgedAllocRate
                        ));
                        if (midAgedAllocRate < 0){
                            System.out.println("totalAllocRate: " + totalAllocRate + "shortLivedObjAllocRate: " + shortLivedObjAllocRate + "longLivedObjAllocRate: " + longLivedObjAllocRate );
                        }
                    }
                }
                prevTenuringEvent = currTenuringEvent;

            }
        }
        return allocationDemography;
    }

    private static float calcTotalObjAllocRate(TenuringEvent prevTenuringEvent, TenuringEvent currTenuringEvent) {
        CollectionEvent wrappingGcEvent = currTenuringEvent.getWrappingGcEvent();
        int oldestAge = currTenuringEvent.getOldestAge();
        int edenSize = currTenuringEvent.getEdenSize();
        if (edenSize >= 0 && oldestAge >= 0) {
            float youngCollectionTimePeriod = wrappingGcEvent.getTimestamp() - prevTenuringEvent.getTimestamp();
            return (edenSize - currTenuringEvent.getUsedSpace(oldestAge)) / youngCollectionTimePeriod;
        }

        return -1;
    }

    private static float calcLongLivedObjAllocRate(TenuringEvent prevTenuringEvent, TenuringEvent currTenuringEvent) {
        // Eden size (young space size – 2 * survivor space size) – size of
        // age 1 reported by collector divided by collection period.
        float youngCollectionTimePeriod = currTenuringEvent.getWrappingGcEvent().getTimestamp() - prevTenuringEvent.getTimestamp();
        if (youngCollectionTimePeriod > -1) {
            int oldestAge = currTenuringEvent.getOldestAge();
            if (oldestAge > 1){ // if this is equal one, there are no "old objects"
            return currTenuringEvent.getUsedSpace(oldestAge) / youngCollectionTimePeriod;
            }
        }
        return -1;
    }

    private static float calcShortLivedObjAllocRate(TenuringEvent prevTenuringEvent, TenuringEvent currTenuringEvent) {
        CollectionEvent wrappingGcEvent = currTenuringEvent.getWrappingGcEvent();
        int edenSize = currTenuringEvent.getEdenSize();
        if (edenSize >= 0) {
            float youngCollectionTimePeriod = wrappingGcEvent.getTimestamp() - prevTenuringEvent.getTimestamp();
            return (edenSize - currTenuringEvent.getUsedSpace(1)) / youngCollectionTimePeriod;
        }

        return -1;
    }

    public static class MemoryDimensioning {
        public List<DataSetEntry> survivorSize = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> edenSize = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> oldGen = new ArrayList<DataSetEntry>();
    }

    public static class AllocationDemography {
        // http://java.dzone.com/articles/how-tame-java-gc-pauses
        public List<DataSetEntry> shortLivedObjAllocationRate = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> midAgedObjAllocationRate = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> longLivedObjAllocationRate = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> totalAllocationRate = new ArrayList<DataSetEntry>();

    }


    public static class TenuringDataSet {
        public List<DataSetEntry> desiredSurvivorSize = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> maxAges = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> newThreshold = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> sumTotal = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> sumUsed = new ArrayList<DataSetEntry>();

        public List<DataSetEntry>[] agesTotal;

        public TenuringDataSet() {
            init();
        }

        private void init() {
            agesTotal = new List[15];
            for (int i = 0; i < agesTotal.length; i++) {
                agesTotal[i] = new ArrayList<DataSetEntry>();
            }
        }
    }
}

