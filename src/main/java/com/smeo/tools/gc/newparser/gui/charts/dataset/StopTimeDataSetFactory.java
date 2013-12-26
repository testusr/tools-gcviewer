package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.newparser.domain.GcLoggedEvent;

import java.util.ArrayList;
import java.util.List;

public class StopTimeDataSetFactory {
    public static List<DataSetEntry> createApplicationStopTimeDataSet(List<GcLoggedEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<DataSetEntry> dataSetEntries = new ArrayList<DataSetEntry>();
        float intervalInSec = (intervalInMs / 1000f);
        long logStartTime = -1;
        int intervalCount = 0;

        float currIntevalStopTimeInSec = 0;
        float currIvervalRunTimeInSec = 0;
        float currCoveredTotalIntervalTimeInSec = 0;

        float summedUpTotalTimesInSec = 0;

        for (GcLoggedEvent currGcEvent : allGarbageCollectionEvents) {
            if (logStartTime < 0) {
                logStartTime = currGcEvent.getTimestamp();
            }
            if (currGcEvent instanceof com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent) {
                com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent stopTimeEvent = (com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent) currGcEvent;
                double stopTime = stopTimeEvent.getStopTimeInSec();
                summedUpTotalTimesInSec += stopTime;
                currIntevalStopTimeInSec += stopTime;
                currCoveredTotalIntervalTimeInSec += stopTime;
            }
            if (currGcEvent instanceof com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent) {
                com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent runTimeEvent = (com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent) currGcEvent;
                double runTime = runTimeEvent.getRunTimeInSec();
                summedUpTotalTimesInSec += runTime;
                currIvervalRunTimeInSec += runTime;
                currCoveredTotalIntervalTimeInSec += runTime;
            }


            while (currCoveredTotalIntervalTimeInSec > intervalInSec) {
                intervalCount++;
                float stopTimeRatio = currIntevalStopTimeInSec / currCoveredTotalIntervalTimeInSec;
                float stopTimeRatioInPerc = stopTimeRatio * 100f;
                dataSetEntries.add(new DataSetEntry(logStartTime + (intervalCount * intervalInMs), stopTimeRatioInPerc));

                currCoveredTotalIntervalTimeInSec -= intervalInSec;
                currIntevalStopTimeInSec -= (stopTimeRatio * intervalInSec);
                currIvervalRunTimeInSec -= ((1f - stopTimeRatio) * intervalInSec);
            }
        }

        return dataSetEntries;
    }

    public static List<DataSetEntry> ncreateApplicationStopTimeDataSet(List<GcLoggedEvent> allGarbageCollectionEvents, long intervalInMs) {
        List<DataSetEntry> dataSetEntries = new ArrayList<DataSetEntry>();
        float intervalInSec = (intervalInMs / 1000f);
        long logStartTime = -1;
        int intervalCount = 0;

        float currIntevalStopTimeInSec = 0;
        float currIvervalRunTimeInSec = 0;
        float currCoveredTotalIntervalTimeInSec = 0;

        float summedUpTotalTimesInSec = 0;


        for (GcLoggedEvent currGcEvent : allGarbageCollectionEvents) {
            if (logStartTime < 0) {
                logStartTime = currGcEvent.getTimestamp();
            }
            com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent currStopTimeEvent = null;
            com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent currRuntTimeEvent = null;
            if (currGcEvent instanceof com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent) {
                currStopTimeEvent = (com.smeo.tools.gc.newparser.domain.ApplicationStopTimeEvent) currGcEvent;
                summedUpTotalTimesInSec += currStopTimeEvent.getStopTimeInSec();
                currCoveredTotalIntervalTimeInSec += currStopTimeEvent.getStopTimeInSec();
                currIntevalStopTimeInSec += currStopTimeEvent.getStopTimeInSec();
            }
            if (currGcEvent instanceof com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent) {
                currRuntTimeEvent = (com.smeo.tools.gc.newparser.domain.ApplicationTimeEvent) currGcEvent;
                summedUpTotalTimesInSec += currRuntTimeEvent.getRunTimeInSec();
                currCoveredTotalIntervalTimeInSec += currRuntTimeEvent.getRunTimeInSec();
                currIvervalRunTimeInSec += currRuntTimeEvent.getRunTimeInSec();
            }

            while (currCoveredTotalIntervalTimeInSec > intervalInSec) {
                intervalCount++;
                float stopTimeRatio = currIntevalStopTimeInSec / currCoveredTotalIntervalTimeInSec;
                float stopTimeRatioInPerc = stopTimeRatio * 100f;
                dataSetEntries.add(new DataSetEntry(logStartTime + (intervalCount * intervalInMs), stopTimeRatioInPerc));

                currCoveredTotalIntervalTimeInSec -= intervalInSec;
                currIntevalStopTimeInSec -= (stopTimeRatio * intervalInSec);
                currIvervalRunTimeInSec -= ((1f - stopTimeRatio) * intervalInSec);
            }
        }
        return dataSetEntries;
    }
}
