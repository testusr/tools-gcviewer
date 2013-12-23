package com.smeo.tools.gc.dataset;

import java.util.ArrayList;
import java.util.List;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.HeapMemorySpace;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;

public class MemoryDataSetFactory {

    public MemoryInfoDataSet createEdenMemoryDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<BeforeAfterGcMemorySpace> rawData = new ArrayList<MemoryDataSetFactory.BeforeAfterGcMemorySpace>();
        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            rawData.add(new BeforeAfterGcMemorySpace(
                    currCollectionEvent.gcTiming,
                    currCollectionEvent.time.getTime(),
                    currCollectionEvent.heapBeforeGC.getEdenSpace(),
                    currCollectionEvent.heapAfterGC.getEdenSpace()));
        }

        return createMemoryDataSet(rawData);
    }

    public MemoryInfoDataSet createUsedSurvivorMemoryDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<BeforeAfterGcMemorySpace> rawData = new ArrayList<MemoryDataSetFactory.BeforeAfterGcMemorySpace>();
        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            MemorySpace usedSurvivorSpaceBefore = currCollectionEvent.heapBeforeGC.getUsedSurvivorSpace();
            MemorySpace usedSurvivorSpaceAfter = currCollectionEvent.heapAfterGC.getUsedSurvivorSpace();
            if (usedSurvivorSpaceAfter != MemorySpace.UNDEFINED && usedSurvivorSpaceBefore != MemorySpace.UNDEFINED) {
                rawData.add(new BeforeAfterGcMemorySpace(
                        currCollectionEvent.gcTiming,
                        currCollectionEvent.time.getTime(),
                        usedSurvivorSpaceBefore,
                        usedSurvivorSpaceAfter));
            }
        }

        return createMemoryDataSet(rawData);
    }

    public MemoryInfoDataSet createOldGenMemoryDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<BeforeAfterGcMemorySpace> rawData = new ArrayList<MemoryDataSetFactory.BeforeAfterGcMemorySpace>();
        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            MemorySpace oldGenSpace = currCollectionEvent.heapBeforeGC.getOldGenSpace();
            if (oldGenSpace != null) {
                rawData.add(new BeforeAfterGcMemorySpace(
                        currCollectionEvent.gcTiming,
                        currCollectionEvent.time.getTime(),
                        oldGenSpace,
                        currCollectionEvent.heapAfterGC.getOldGenSpace()));
            }
        }

        return createMemoryDataSet(rawData);
    }

    public MemoryInfoDataSet createPermGenMemoryDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<BeforeAfterGcMemorySpace> rawData = new ArrayList<MemoryDataSetFactory.BeforeAfterGcMemorySpace>();
        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            MemorySpace permGenSpaceBefore = currCollectionEvent.heapBeforeGC.getPermGenSpace();
            if (permGenSpaceBefore != null) {
                rawData.add(new BeforeAfterGcMemorySpace(
                        currCollectionEvent.gcTiming,
                        currCollectionEvent.time.getTime(),
                        permGenSpaceBefore,
                        currCollectionEvent.heapAfterGC.getPermGenSpace()));
            }
        }

        return createMemoryDataSet(rawData);
    }

    public MemoryInfoDataSet createTotalMemoryDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<BeforeAfterGcMemorySpace> rawData = new ArrayList<MemoryDataSetFactory.BeforeAfterGcMemorySpace>();
        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            MemorySpace totalMemSpaceBefore = currCollectionEvent.heapBeforeGC.getTotalMemSpace();
            if (totalMemSpaceBefore != null) {
                rawData.add(new BeforeAfterGcMemorySpace(
                        currCollectionEvent.gcTiming,
                        currCollectionEvent.time.getTime(),
                        totalMemSpaceBefore,
                        currCollectionEvent.heapAfterGC.getTotalMemSpace()));
            }
        }

        return createMemoryDataSet(rawData);
    }

    private MemoryInfoDataSet createMemoryDataSet(List<BeforeAfterGcMemorySpace> rawData) {
        MemoryInfoDataSet memoryInfoDataSet = new MemoryInfoDataSet();

        memoryInfoDataSet.beforeGcUsedPerc = getTotalMemoryUsageInPercDataSet(rawData);
        memoryInfoDataSet.incomingDataRate = getIncomingRateDataSet(rawData, 1000);
        memoryInfoDataSet.totalMemory = getTotalMemoryDataSet(rawData);
        return memoryInfoDataSet;
    }

    private List<DataSetEntry> getTotalMemoryDataSet(List<BeforeAfterGcMemorySpace> rawData) {
        List<DataSetEntry> resultDataSet = new ArrayList<DataSetEntry>();
        for (BeforeAfterGcMemorySpace currBeforeAfterGcMemorySpace : rawData) {
            if (currBeforeAfterGcMemorySpace.isValid()) {
                resultDataSet.add(new DataSetEntry(currBeforeAfterGcMemorySpace.getGcStartedTime(), currBeforeAfterGcMemorySpace.beforeGc.usedSpace));
                resultDataSet.add(new DataSetEntry(currBeforeAfterGcMemorySpace.getGcFinishedTime(), currBeforeAfterGcMemorySpace.afterGc.usedSpace));
            }
        }

        return resultDataSet;
    }

    private List<DataSetEntry> getIncomingRateDataSet(List<BeforeAfterGcMemorySpace> rawData, long perTimeInMs) {
        List<DataSetEntry> resultDataSet = new ArrayList<DataSetEntry>();
        BeforeAfterGcMemorySpace previousDataEntry = null;

        for (BeforeAfterGcMemorySpace currBeforeAfterGcMemorySpace : rawData) {
            if (previousDataEntry == null) {
                previousDataEntry = currBeforeAfterGcMemorySpace;
            } else {
                if (currBeforeAfterGcMemorySpace.isValid() && previousDataEntry.isValid()) {
                    float rawDataTimeInterval = currBeforeAfterGcMemorySpace.gcLoggedTime - previousDataEntry.gcLoggedTime;
                    float grownDataAmountInK = currBeforeAfterGcMemorySpace.beforeGc.getUsedSpaceInK() - previousDataEntry.afterGc.getUsedSpaceInK();
                    float growRateInK = 0.0F;
                    if (grownDataAmountInK > 0) {
                        growRateInK = (grownDataAmountInK / rawDataTimeInterval) * ((float) perTimeInMs);
                    }
                    resultDataSet.add(new DataSetEntry(currBeforeAfterGcMemorySpace.gcLoggedTime, growRateInK));

                }
                previousDataEntry = currBeforeAfterGcMemorySpace;
            }
        }
        return resultDataSet;
    }

    private List<DataSetEntry> getTotalMemoryUsageInPercDataSet(List<BeforeAfterGcMemorySpace> rawData) {
        List<DataSetEntry> resultDataSet = new ArrayList<DataSetEntry>();

        for (BeforeAfterGcMemorySpace currBeforeAfterGcMemorySpace : rawData) {
            if (currBeforeAfterGcMemorySpace.beforeGc != null && currBeforeAfterGcMemorySpace.afterGc != null) {
                resultDataSet.add(new DataSetEntry(currBeforeAfterGcMemorySpace.getGcStartedTime(), currBeforeAfterGcMemorySpace.beforeGc
                        .getUsedSpaceInPercentage()));
                resultDataSet.add(new DataSetEntry(currBeforeAfterGcMemorySpace.getGcFinishedTime(), currBeforeAfterGcMemorySpace.afterGc
                        .getUsedSpaceInPercentage()));
            }
        }
        return resultDataSet;
    }

    private static class BeforeAfterGcMemorySpace {
        long gcLoggedTime;
        long gcTookTimeInMs = -1;
        MemorySpace beforeGc = MemorySpace.UNDEFINED;
        MemorySpace afterGc = MemorySpace.UNDEFINED;

        public BeforeAfterGcMemorySpace(long time, MemorySpace beforeGc, MemorySpace afterGc) {
            this.gcLoggedTime = time;
            this.beforeGc = beforeGc == null ? MemorySpace.UNDEFINED : beforeGc;
            this.afterGc = afterGc == null ? MemorySpace.UNDEFINED : afterGc;
        }

        public BeforeAfterGcMemorySpace(GcTiming gcTiming, long time, MemorySpace beforeGc, MemorySpace afterGc) {
            this.gcLoggedTime = time;
            this.beforeGc = beforeGc == null ? MemorySpace.UNDEFINED : beforeGc;
            this.afterGc = afterGc == null ? MemorySpace.UNDEFINED : afterGc;
            this.gcTookTimeInMs = gcTiming.getMostAccurateTotalTimeInMs();
        }

        public long getGcStartedTime() {
            return gcLoggedTime - gcTookTimeInMs;
        }

        public long getGcFinishedTime() {
            return gcLoggedTime;
        }

        public boolean isValid() {
            return (beforeGc != MemorySpace.UNDEFINED && afterGc != MemorySpace.UNDEFINED);
        }
    }

    public static class MemoryInfoDataSet {
        public List<DataSetEntry> totalMemory;
        public List<DataSetEntry> incomingDataRate;
        public List<DataSetEntry> beforeGcUsedPerc;
    }
}
