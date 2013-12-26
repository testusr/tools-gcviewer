package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;

import java.util.ArrayList;
import java.util.List;

public class MemoryDataSetFactory extends AbstractMemoryDataSetFactory {

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

}
