package com.smeo.tools.gc.parser;

import java.util.Date;
import java.util.List;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.HeapState;
import com.smeo.tools.gc.parser.GcEventParser.GcEvent;
import com.smeo.tools.gc.parser.GcEventParser.GcEventType;
import com.smeo.tools.gc.parser.GcEventParser.GcType;

public class GenericGCEventParser extends GarbageCollectionEventParser {
	GcEventParser gcEventParser = new GcEventParser();
	GarbageCollectionEvent garbageCollectionEvent = null;
	GcType gcType = null;
	int minorGcCount = 0;
	int majorGcCount = 0;
	Boolean wasMajorGc = null;

	@Override
	public GarbageCollectionEvent parseLogLine(String currLine) {

		if ((gcType = gcEventParser.parseGcType(currLine)) != null) {
			Date date = extractTimeFromLogLined(currLine);
			if (date != null) {
				wasMajorGc = gcType.isMajor();
				if (gcType.isMajor()) {
					majorGcCount++;
				} else {
					minorGcCount++;
				}

				garbageCollectionEvent = new GarbageCollectionEvent();
				garbageCollectionEvent.time = date;

				HeapState heapAfterGC = new HeapState();
				HeapState heapBeforeGC = new HeapState();

				garbageCollectionEvent.heapAfterGC = heapAfterGC;
				garbageCollectionEvent.heapBeforeGC = heapBeforeGC;

				heapBeforeGC.fullGcCount = getMajorGcCountBefore();
				heapBeforeGC.minorGcCount = getMinorGcCountBefore();

				heapAfterGC.fullGcCount = majorGcCount;
				heapAfterGC.minorGcCount = minorGcCount;
			}
		}

		if (garbageCollectionEvent != null) {
			List<GcEvent> gcEvents = gcEventParser.parseGcEvents(currLine);

			for (GcEvent currEvent : gcEvents) {
				GcEventType gcVersion = currEvent.gcVersion;

				switch (gcVersion.getMemoryType()) {
				case Eden: {
					garbageCollectionEvent.heapBeforeGC.setEdenSpace(currEvent.before);
					garbageCollectionEvent.heapAfterGC.setEdenSpace(currEvent.after);
					break;
				}
				case PermGen: {
					garbageCollectionEvent.heapBeforeGC.setPermGenSpace(currEvent.before);
					garbageCollectionEvent.heapAfterGC.setPermGenSpace(currEvent.after);
					break;
				}
				case Tenured: {
					garbageCollectionEvent.heapBeforeGC.setOldGenSpace(currEvent.before);
					garbageCollectionEvent.heapAfterGC.setOldGenSpace(currEvent.after);
					break;
				}
				case TotalHeap: {
					garbageCollectionEvent.heapBeforeGC.setTotalMemSpace(currEvent.before);
					garbageCollectionEvent.heapAfterGC.setTotalMemSpace(currEvent.after);
				}
				}

			}
			GcTiming gcTiming = null;
			if ((gcTiming = gcEventParser.parseGcTiming(currLine)) != null) {
				GarbageCollectionEvent toReturn = garbageCollectionEvent;
				toReturn.gcTiming = gcTiming;
				garbageCollectionEvent = null;
				gcType = null;
				return toReturn;
			}
		}
		return null;
	}

	int getMajorGcCountBefore() {
		if (wasMajorGc != null) {
			if (wasMajorGc) {
				return majorGcCount - 1;
			} else {
				return majorGcCount;
			}
		}
		return majorGcCount;
	}

	int getMinorGcCountBefore() {
		if (wasMajorGc != null) {
			if (!wasMajorGc) {
				return minorGcCount - 1;
			} else {
				return minorGcCount;
			}
		}
		return minorGcCount;
	}

}
