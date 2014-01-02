package com.smeo.tools.gc.dataset;

import java.util.ArrayList;
import java.util.List;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;

public class StopTimeDataSetFactory {
	public static List<DataSetEntry> createApplicationStopTimeDataSet(List<GarbageCollectionEvent> allGarbageCollectionEvents, long intervalInMs) {
		List<DataSetEntry> dataSetEntries = new ArrayList<DataSetEntry>();
		float intervalInSec = (intervalInMs / 1000f);
		long logStartTime = -1;
		int intervalCount = 0;

		float currIntevalStopTimeInSec = 0;
		float currIvervalRunTimeInSec = 0;
		float currCoveredTotalIntervalTimeInSec = 0;

		float summedUpTotalTimesInSec = 0;

		for (GarbageCollectionEvent currGcEvent : allGarbageCollectionEvents) {
			if (logStartTime < 0) {
				logStartTime = currGcEvent.time.getTime();
			}
			for (ApplicationStopTimeEvent currApplicationStopTime : currGcEvent.followingApplicationStopTimes) {
				summedUpTotalTimesInSec += currApplicationStopTime.getTotalTime();

				currCoveredTotalIntervalTimeInSec += currApplicationStopTime.getTotalTime();
				currIntevalStopTimeInSec += currApplicationStopTime.totalTimeApplicationsWhereStoppedInSec;
				currIvervalRunTimeInSec += currApplicationStopTime.totalTimeApplicationsWhereRunningInSec;

				while (currCoveredTotalIntervalTimeInSec >= intervalInSec) {
					intervalCount++;
					float stopTimeRatio = currIntevalStopTimeInSec / currCoveredTotalIntervalTimeInSec;
					float stopTimeRatioInPerc = stopTimeRatio * 100f;
					dataSetEntries.add(new DataSetEntry(logStartTime + (intervalCount * intervalInMs), stopTimeRatioInPerc));

					currCoveredTotalIntervalTimeInSec -= intervalInSec;
					currIntevalStopTimeInSec -= (stopTimeRatio * intervalInSec);
					currIvervalRunTimeInSec -= ((1f - stopTimeRatio) * intervalInSec);
				}
			}
		}
		return dataSetEntries;
	}
}
