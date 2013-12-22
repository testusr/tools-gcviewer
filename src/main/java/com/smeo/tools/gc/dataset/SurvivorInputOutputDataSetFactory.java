package com.smeo.tools.gc.dataset;

import java.util.ArrayList;
import java.util.List;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.Tenuring;

public class SurvivorInputOutputDataSetFactory {

	public static SurvivorInputOutputDataSet createDataSet(List<GarbageCollectionEvent> allGarbageCollectionEvents) {
		SurvivorInputOutputDataSet survivorInputOutput = new SurvivorInputOutputDataSet();

		for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
			Tenuring currTenuring = currCollectionEvent.tenuring;
			if (currTenuring != null) {
				long currEventTime = currCollectionEvent.time.getTime();
				Integer bytesPromotedToOldGen = currTenuring.getOldGenPromotion();
				Integer bytesPromotedToSurvivorSpace = currTenuring.getYoungGenPromotion();

				if (bytesPromotedToOldGen != null) {
					survivorInputOutput.outgoingData.add(new DataSetEntry(currEventTime, bytesPromotedToOldGen));
				}
				if (bytesPromotedToSurvivorSpace != null) {
					survivorInputOutput.incomingDataRate.add(new DataSetEntry(currEventTime, bytesPromotedToSurvivorSpace));
				}

			}
		}
		return survivorInputOutput;
	}

	public static class SurvivorInputOutputDataSet {
		public List<DataSetEntry> incomingDataRate = new ArrayList<DataSetEntry>();
		public List<DataSetEntry> outgoingData = new ArrayList<DataSetEntry>();

	}
}
