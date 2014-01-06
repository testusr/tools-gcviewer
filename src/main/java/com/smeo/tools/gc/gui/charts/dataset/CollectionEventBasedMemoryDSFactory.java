package com.smeo.tools.gc.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.CollectionEvent;
import com.smeo.tools.gc.domain.CollectorEvent;
import com.smeo.tools.gc.domain.GcLoggedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/26/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectionEventBasedMemoryDSFactory extends AbstractMemoryDataSetFactory {


    public static MemoryInfoDataSet createYoungGenMemoryDataSets(List<GcLoggedEvent> allEvents) {

        List<MemoryInfo> rawData = new ArrayList<MemoryInfo>();
        long lastGcTimeStamp = 0;
        int lastUseSpaceAfterGcInK = 0;

        for (GcLoggedEvent currEvent : allEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                CollectorEvent youngGenCollector = currCollectionEvent.getYoungGenCollector();
                if (youngGenCollector != null) {
                    long afterTimestamp = currCollectionEvent.getTimestamp();
                    long beforeTs = afterTimestamp - (long) (currCollectionEvent.getGcTiming().getRealTimInSec() * 1000);
                    int beforeUsedSpaceInK = youngGenCollector.getMemoryBefore().getUsedSpaceInK();

                    double incoming = calcIncoming(lastGcTimeStamp, lastUseSpaceAfterGcInK, beforeTs, beforeUsedSpaceInK);

                    int freedSpaceInK = beforeUsedSpaceInK - youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                    rawData.add(new MemoryInfo(
                            beforeTs,
                            youngGenCollector.getMemoryBefore().getUsedSpaceInK(),
                            youngGenCollector.getMemoryBefore().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    rawData.add(new MemoryInfo(
                            afterTimestamp,
                            youngGenCollector.getMemoryAfter().getUsedSpaceInK(),
                            youngGenCollector.getMemoryAfter().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));
                    lastGcTimeStamp = afterTimestamp;
                    lastUseSpaceAfterGcInK = youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                }
            }
        }

        return createMemoryInfoDataSet(rawData);
    }

    private static double calcIncoming(long lastGcTimeStamp, int lastUseSpaceAfterGcInK, long beforeTs, int beforeUsedSpaceInK) {
        double incoming = (beforeUsedSpaceInK - lastUseSpaceAfterGcInK) / ((beforeTs - lastGcTimeStamp) / 1000.0);
        if (incoming < 0){
            System.out.println("incoming smaller than 0 ... impossible");
        }
        return incoming;
    }


    public static MemoryInfoDataSet createOldGenMemoryDataSets(List<GcLoggedEvent> allEvents) {

        List<MemoryInfo> rawData = new ArrayList<MemoryInfo>();
        long lastGcTimeStamp = 0;
        int lastUseSpaceAfterGcInK = 0;

        for (GcLoggedEvent currEvent : allEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                CollectorEvent youngGenCollector = currCollectionEvent.getOldGenCollector();
                if (youngGenCollector != null) {
                    long afterTimestamp = currCollectionEvent.getTimestamp();
                    long beforeTs = afterTimestamp - (long) (currCollectionEvent.getGcTiming().getRealTimInSec() * 1000);
                    int beforeUsedSpaceInK = youngGenCollector.getMemoryBefore().getUsedSpaceInK();

                    double incoming = calcIncoming(lastGcTimeStamp, lastUseSpaceAfterGcInK, beforeTs, beforeUsedSpaceInK);
                    int freedSpaceInK = beforeUsedSpaceInK - youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                    rawData.add(new MemoryInfo(
                            beforeTs,
                            youngGenCollector.getMemoryBefore().getUsedSpaceInK(),
                            youngGenCollector.getMemoryBefore().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    rawData.add(new MemoryInfo(
                            afterTimestamp,
                            youngGenCollector.getMemoryAfter().getUsedSpaceInK(),
                            youngGenCollector.getMemoryAfter().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    lastGcTimeStamp = afterTimestamp;
                    lastUseSpaceAfterGcInK = youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                }
            }
        }

        return createMemoryInfoDataSet(rawData);
    }

    public static MemoryInfoDataSet createPermGenMemoryDataSets(List<GcLoggedEvent> allEvents) {
        List<MemoryInfo> rawData = new ArrayList<MemoryInfo>();
        long lastGcTimeStamp = 0;
        int lastUseSpaceAfterGcInK = 0;

        for (GcLoggedEvent currEvent : allEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                CollectorEvent youngGenCollector = currCollectionEvent.getPermGenCollector();
                if (youngGenCollector != null) {
                    long afterTimestamp = currCollectionEvent.getTimestamp();
                    long beforeTs = afterTimestamp - (long) (currCollectionEvent.getGcTiming().getRealTimInSec() * 1000);
                    int beforeUsedSpaceInK = youngGenCollector.getMemoryBefore().getUsedSpaceInK();

                    double incoming = calcIncoming(lastGcTimeStamp, lastUseSpaceAfterGcInK, beforeTs, beforeUsedSpaceInK);
                    int freedSpaceInK = beforeUsedSpaceInK - youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                    rawData.add(new MemoryInfo(
                            beforeTs,
                            youngGenCollector.getMemoryBefore().getUsedSpaceInK(),
                            youngGenCollector.getMemoryBefore().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    rawData.add(new MemoryInfo(
                            afterTimestamp,
                            youngGenCollector.getMemoryAfter().getUsedSpaceInK(),
                            youngGenCollector.getMemoryAfter().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    lastGcTimeStamp = afterTimestamp;
                    lastUseSpaceAfterGcInK = youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                }
            }
        }

        return createMemoryInfoDataSet(rawData);
    }

    public static MemoryInfoDataSet createTotalGenMemoryDataSets(List<GcLoggedEvent> allEvents) {
        List<MemoryInfo> rawData = new ArrayList<MemoryInfo>();
        long lastGcTimeStamp = 0;
        int lastUseSpaceAfterGcInK = 0;

        for (GcLoggedEvent currEvent : allEvents) {
            if (currEvent instanceof CollectionEvent) {
                CollectionEvent currCollectionEvent = (CollectionEvent) currEvent;
                CollectorEvent youngGenCollector = currCollectionEvent.getTotalCollectionValues();
                if (youngGenCollector != null) {
                    long afterTimestamp = currCollectionEvent.getTimestamp();
                    long beforeTs = afterTimestamp - (long) (currCollectionEvent.getGcTiming().getRealTimInSec() * 1000);
                    int beforeUsedSpaceInK = youngGenCollector.getMemoryBefore().getUsedSpaceInK();

                    double incoming = calcIncoming(lastGcTimeStamp, lastUseSpaceAfterGcInK, beforeTs, beforeUsedSpaceInK);
                    int freedSpaceInK = beforeUsedSpaceInK - youngGenCollector.getMemoryAfter().getUsedSpaceInK();
                    rawData.add(new MemoryInfo(
                            beforeTs,
                            youngGenCollector.getMemoryBefore().getUsedSpaceInK(),
                            youngGenCollector.getMemoryBefore().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    rawData.add(new MemoryInfo(
                            afterTimestamp,
                            youngGenCollector.getMemoryAfter().getUsedSpaceInK(),
                            youngGenCollector.getMemoryAfter().getAvailableSpaceInK(),
                            incoming,
                            freedSpaceInK));

                    lastGcTimeStamp = afterTimestamp;
                    lastUseSpaceAfterGcInK = youngGenCollector.getMemoryAfter().getUsedSpaceInK();

                }
            }
        }

        return createMemoryInfoDataSet(rawData);
    }

    private static MemoryInfoDataSet createMemoryInfoDataSet(List<MemoryInfo> rawData) {
        MemoryInfoDataSet memoryInfoDataSet = new MemoryInfoDataSet();
        System.out.println("...creating memory data set " + rawData.size() + " entries");
        for (MemoryInfo currMemoryInfo : rawData) {
            try {
                memoryInfoDataSet.availableSpaceInK.add(new DataSetEntry(currMemoryInfo.timeStamp, currMemoryInfo.availableSpaceKb));
                memoryInfoDataSet.usedSpaceInK.add(new DataSetEntry(currMemoryInfo.timeStamp, currMemoryInfo.usedSpaceKb));
                memoryInfoDataSet.spaceFreeInK.add(new DataSetEntry(currMemoryInfo.timeStamp, currMemoryInfo.freedSpaceInK));
                memoryInfoDataSet.incomingDataInK.add(new DataSetEntry(currMemoryInfo.timeStamp, currMemoryInfo.incomingRateKbpS));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return memoryInfoDataSet;
    }


    private static class MemoryInfo {
        private long timeStamp;
        private double usedSpaceKb;
        private double availableSpaceKb;
        private double incomingRateKbpS;
        private int freedSpaceInK;

        private MemoryInfo(long timeStamp, double usedSpaceKb, double availableSpaceKb, double incomingRateKbpS, int freedSpaceInK) {
            this.timeStamp = timeStamp;
            this.usedSpaceKb = usedSpaceKb;
            this.availableSpaceKb = availableSpaceKb;
            this.incomingRateKbpS = incomingRateKbpS;
            this.freedSpaceInK = freedSpaceInK;
        }
    }
}
