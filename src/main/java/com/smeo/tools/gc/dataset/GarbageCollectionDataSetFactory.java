package com.smeo.tools.gc.dataset;

import java.util.ArrayList;
import java.util.List;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapState;

public class GarbageCollectionDataSetFactory {
	public static GarbageCollectionDataSet createGarbageCollectionDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents) {
		int compactingCollectionCount = 0;
		GarbageCollectionDataSet resultGarbageCollectionDataSet = new GarbageCollectionDataSet();

		for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
			long currEventTime = currCollectionEvent.time.getTime();

			HeapState heapAfterGc = currCollectionEvent.heapBeforeGC;
			resultGarbageCollectionDataSet.fullGc.add(new DataSetEntry(currEventTime, heapAfterGc.fullGcCount));
			resultGarbageCollectionDataSet.minorGc.add(new DataSetEntry(currEventTime, heapAfterGc.minorGcCount));
			if (currCollectionEvent.isStopTheWorld) {
				compactingCollectionCount++;
			}
			resultGarbageCollectionDataSet.compactingGc.add(new DataSetEntry(currEventTime, compactingCollectionCount));
		}
		return resultGarbageCollectionDataSet;
	}

	public static class GarbageCollectionDataSet {
		public List<DataSetEntry> fullGc = new ArrayList<DataSetEntry>();
		public List<DataSetEntry> minorGc = new ArrayList<DataSetEntry>();
		public List<DataSetEntry> compactingGc = new ArrayList<DataSetEntry>();
	}
}
