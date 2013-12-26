package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.HeapMemorySpace;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/26/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractMemoryDataSetFactory {
    protected MemoryInfoDataSet createMemoryDataSet(List<BeforeAfterGcMemorySpace> rawData) {
        MemoryInfoDataSet memoryInfoDataSet = new MemoryInfoDataSet();

        memoryInfoDataSet.usedSpaceInK = getTotalMemoryUsageInPercDataSet(rawData);
        memoryInfoDataSet.incomingDataInK = getIncomingRateDataSet(rawData, 1000);
        memoryInfoDataSet.availableSpaceInK = getTotalMemoryDataSet(rawData);
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

    protected static class BeforeAfterGcMemorySpace {
        long gcLoggedTime;
        long gcTookTimeInMs = -1;
        HeapMemorySpace.MemorySpace beforeGc = HeapMemorySpace.MemorySpace.UNDEFINED;
        HeapMemorySpace.MemorySpace afterGc = HeapMemorySpace.MemorySpace.UNDEFINED;

        public BeforeAfterGcMemorySpace(long time, HeapMemorySpace.MemorySpace beforeGc, HeapMemorySpace.MemorySpace afterGc) {
            this.gcLoggedTime = time;
            this.beforeGc = beforeGc == null ? HeapMemorySpace.MemorySpace.UNDEFINED : beforeGc;
            this.afterGc = afterGc == null ? HeapMemorySpace.MemorySpace.UNDEFINED : afterGc;
        }

        public BeforeAfterGcMemorySpace(GcTiming gcTiming, long time, HeapMemorySpace.MemorySpace beforeGc, HeapMemorySpace.MemorySpace afterGc) {
            this.gcLoggedTime = time;
            this.beforeGc = beforeGc == null ? HeapMemorySpace.MemorySpace.UNDEFINED : beforeGc;
            this.afterGc = afterGc == null ? HeapMemorySpace.MemorySpace.UNDEFINED : afterGc;
            this.gcTookTimeInMs = gcTiming.getMostAccurateTotalTimeInMs();
        }

        public long getGcStartedTime() {
            return gcLoggedTime - gcTookTimeInMs;
        }

        public long getGcFinishedTime() {
            return gcLoggedTime;
        }

        public boolean isValid() {
            return (beforeGc != HeapMemorySpace.MemorySpace.UNDEFINED && afterGc != HeapMemorySpace.MemorySpace.UNDEFINED);
        }
    }

    public static class MemoryInfoDataSet {
        public List<DataSetEntry> availableSpaceInK;
        public List<DataSetEntry> usedSpaceInK;
        public List<DataSetEntry> incomingDataInK;
        public List<DataSetEntry> spaceFreeInK;

        MemoryInfoDataSet(){
            availableSpaceInK = new ArrayList<DataSetEntry>();
            usedSpaceInK = new ArrayList<DataSetEntry>();
            incomingDataInK = new ArrayList<DataSetEntry>();
            spaceFreeInK = new ArrayList<DataSetEntry>();
        }
    }
}
