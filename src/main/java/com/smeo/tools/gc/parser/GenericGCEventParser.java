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
    TenuringParser tenuringParser;
	GarbageCollectionEvent currGarbageCollectionEvent = null;
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

				currGarbageCollectionEvent = new GarbageCollectionEvent();
				currGarbageCollectionEvent.time = date;

				HeapState heapAfterGC = new HeapState();
				HeapState heapBeforeGC = new HeapState();

				currGarbageCollectionEvent.heapAfterGC = heapAfterGC;
				currGarbageCollectionEvent.heapBeforeGC = heapBeforeGC;

				heapBeforeGC.fullGcCount = getMajorGcCountBefore();
				heapBeforeGC.minorGcCount = getMinorGcCountBefore();

				heapAfterGC.fullGcCount = majorGcCount;
				heapAfterGC.minorGcCount = minorGcCount;

                this.tenuringParser = new TenuringParser();
			}
		}

		if (currGarbageCollectionEvent != null) {
			List<GcEvent> gcEvents = gcEventParser.parseGcEvents(currLine);

			for (GcEvent currEvent : gcEvents) {
				GcEventType gcVersion = currEvent.gcVersion;

				switch (gcVersion.getMemoryType()) {
				case Eden: {
					currGarbageCollectionEvent.heapBeforeGC.setEdenSpace(currEvent.before);
					currGarbageCollectionEvent.heapAfterGC.setEdenSpace(currEvent.after);
					break;
				}
				case PermGen: {
					currGarbageCollectionEvent.heapBeforeGC.setPermGenSpace(currEvent.before);
					currGarbageCollectionEvent.heapAfterGC.setPermGenSpace(currEvent.after);
					break;
				}
				case OldGen: {
					currGarbageCollectionEvent.heapBeforeGC.setOldGenSpace(currEvent.before);
					currGarbageCollectionEvent.heapAfterGC.setOldGenSpace(currEvent.after);
					break;
				}
				case TotalHeap: {
					currGarbageCollectionEvent.heapBeforeGC.setTotalMemSpace(currEvent.before);
					currGarbageCollectionEvent.heapAfterGC.setTotalMemSpace(currEvent.after);
				}
				}

			}
			GcTiming gcTiming = null;
			if ((gcTiming = gcEventParser.parseGcTiming(currLine)) != null) {
				GarbageCollectionEvent toReturn = currGarbageCollectionEvent;
				toReturn.gcTiming = gcTiming;
				currGarbageCollectionEvent = null;
				gcType = null;
				return toReturn;
			}

            this.currGarbageCollectionEvent.tenuring = tenuringParser.parse(currLine);
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
