package com.smeo.tools.gc.newparser.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapState;

import java.util.ArrayList;
import java.util.List;

public class GarbageCollectionDataSetFactory {
	public static GarbageCollectionDataSet createGarbageCollectionDataSets(List<GarbageCollectionEvent> allGarbageCollectionEvents) {
		int compactingCollectionCount = 0;
		GarbageCollectionDataSet resultGarbageCollectionDataSet = new GarbageCollectionDataSet();

        long lastTime = -1;
		for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
			long currEventTime = currCollectionEvent.time.getTime();

            if (currEventTime < lastTime){
                System.out.println("wrong " + currEventTime + " last: " + lastTime + " diff: " + (currEventTime - lastTime));
            }
            lastTime = currEventTime;

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