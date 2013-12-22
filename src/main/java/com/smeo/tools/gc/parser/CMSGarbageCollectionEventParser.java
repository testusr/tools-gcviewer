package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapMemorySpace;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;
import com.smeo.tools.gc.domain.HeapState;

/**
 * Parses gc.log lines that represent a garbage collection event
 * 
 * {Heap before GC invocations=0 (full 0):
 * par new generation total 4096000K, used 614409K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)
 * eden space 2048000K, 30% used [0x0000000446800000, 0x000000046c002648, 0x00000004c3800000)
 * from space 2048000K, 0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)
 * to space 2048000K, 0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)
 * concurrent mark-sweep generation total 9216000K, used 0K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)
 * concurrent-mark-sweep perm gen total 262144K, used 23964K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)
 * 2013-03-27T21:05:52.961+0000: 1.845: [Full GC (System) 1.845: [CMS: 0K->7665K(9216000K), 0.1444960 secs] 614409K->7665K(13312000K), [CMS Perm :
 * 23964K->23951K(262144K)], 0.1445800 secs] [Times: user=0.11 sys=0.04, real=0.14 secs]
 * Heap after GC invocations=1 (full 1):
 * par new generation total 4096000K, used 0K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)
 * eden space 2048000K, 0% used [0x0000000446800000, 0x0000000446800000, 0x00000004c3800000)
 * from space 2048000K, 0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)
 * to space 2048000K, 0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)
 * concurrent mark-sweep generation total 9216000K, used 7665K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)
 * concurrent-mark-sweep perm gen total 262144K, used 23951K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)
 * }
 * 
 * 
 * @author smeo
 * 
 */
public class CMSGarbageCollectionEventParser extends GarbageCollectionEventParser {
	private GarbageCollectionEvent currentGarbageCollectionEvent = new GarbageCollectionEvent();
	private HeapEntryParser heapEntryParser = new HeapEntryParser();
	private TenuringParser tenuringParser = new TenuringParser();

	public GarbageCollectionEvent parseLogLine(String currLine) {
		if (currLine.trim().startsWith("{")) {
			currentGarbageCollectionEvent = new GarbageCollectionEvent();
		}
		if (currentGarbageCollectionEvent.heapBeforeGC == null) {
			currentGarbageCollectionEvent.heapBeforeGC = heapEntryParser.parseLogLineAndReturnResultWhenFilled(currLine);
		} else if (currentGarbageCollectionEvent.heapAfterGC == null) {
			currentGarbageCollectionEvent.heapAfterGC = heapEntryParser.parseLogLineAndReturnResultWhenFilled(currLine);
		}
		if (currentGarbageCollectionEvent.time == null) {
			currentGarbageCollectionEvent.time = extractTimeFromLogLined(currLine);
		}

		if (!currentGarbageCollectionEvent.isStopTheWorld) {
			currentGarbageCollectionEvent.isStopTheWorld = isEnforcedGcMarker(currLine);
		}

		if (currentGarbageCollectionEvent.tenuring == null) {
			currentGarbageCollectionEvent.tenuring = tenuringParser.parse(currLine);
		}

		if (currLine.trim().equals("}")) {
			return currentGarbageCollectionEvent;
		}
		return null;
	}

	private boolean isEnforcedGcMarker(String currLine) {
		return currLine.contains("[Full GC");
	}

	public class HeapEntryParser {
		HeapState currHeapState;
		int processedLines = 0;

		public HeapEntryParser() {
			newHeapState();
		}

		private void newHeapState() {
			currHeapState = new HeapState();
			currHeapState.memorySpace.edenSpace = new MemorySpace();
			currHeapState.memorySpace.oldGenSpace = new MemorySpace();
			currHeapState.memorySpace.permGenSpace = new MemorySpace();
		}

		public HeapState parseLogLineAndReturnResultWhenFilled(String currLine) {
			HeapMemorySpace currHeapMemorySpace = currHeapState.memorySpace;
			processedLines++;

			String trimmedLogLine = currLine.trim();
			try {
				currHeapMemorySpace.isFilled = (currHeapMemorySpace.edenSpace.isFilled() && processedLines > 5);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (trimmedLogLine.startsWith("eden")) {
				processedLines = 1;
				currHeapMemorySpace.edenSpace = extractMemorySpace(trimmedLogLine);
			}
			if (trimmedLogLine.startsWith("from")) {
				currHeapMemorySpace.survivorFromSpace = extractMemorySpace(trimmedLogLine);
			}
			if (trimmedLogLine.startsWith("to")) {
				currHeapMemorySpace.survivorToSpace = extractMemorySpace(trimmedLogLine);
			}

			if (trimmedLogLine.contains("generation total") || trimmedLogLine.contains("PSYoungGen      total")) {
				currHeapMemorySpace.oldGenSpace = extractTotalUsedMemorySpace(trimmedLogLine);
			}

			if (trimmedLogLine.contains("perm gen total") || trimmedLogLine.contains("PSPermGen       total")) {
				currHeapMemorySpace.permGenSpace = extractTotalUsedMemorySpace(trimmedLogLine);
			}

			int minorGcCount = extractMinorGcCount(trimmedLogLine);
			if (minorGcCount != -1) {
				currHeapState.minorGcCount = minorGcCount;
			}

			int fullGcCount = extractFullGcCount(trimmedLogLine);
			if (fullGcCount != -1) {
				currHeapState.fullGcCount = fullGcCount;
			}

			if (currHeapState.isFilled()) {
				HeapState returnValue = currHeapState;
				newHeapState();
				return returnValue;
			}

			return null;
		}

		private MemorySpace extractTotalUsedMemorySpace(String trimmedLogLine) {
			// concurrent-mark-sweep perm gen total 262144K, used 71622K
			MemorySpace newMemorySpace = new MemorySpace();
			String[] elements = trimmedLogLine.split("total |used |K");
			Float total = Float.valueOf(elements[1]);
			Float used = Float.valueOf(elements[3]);
			Float percentage = used / (total / 100.0f);

			newMemorySpace.setSpaceValuesWithPercentage(total.intValue(), percentage);
			return newMemorySpace;
		}

		private int extractFullGcCount(String currLine) {
			if (currLine.contains(" (full ")) {
				String[] elements = currLine.split(" |\\)");
				return Integer.valueOf(elements[5]);
			}
			return -1;

		}

		private int extractMinorGcCount(String currLine) {
			if (currLine.contains("GC invocations=")) {
				String[] elements = currLine.split("=| ");
				return Integer.valueOf(elements[4]);
			}
			return -1;
		}

		private MemorySpace extractMemorySpace(String currLine) {
			MemorySpace newMemorySpace = new MemorySpace();

			String trimmedLogLine = currLine.trim();
			String[] lineSegements = null;

			Integer percentage = null;

			if (trimmedLogLine.contains("%")) {
				lineSegements = trimmedLogLine.substring(0, trimmedLogLine.indexOf("%")).split(" ");
				percentage = Integer.valueOf(lineSegements[lineSegements.length - 1]);
			}
			if (percentage != null) {
				// newMemorySpace.getUsedSpaceInPercentage() = percentage;
			}

			lineSegements = trimmedLogLine.split("space |K");
			String sizeInfoLineSegment = lineSegements[1];
			newMemorySpace.totalSpace = Integer.valueOf(sizeInfoLineSegment);
			newMemorySpace.setSpaceValuesWithPercentage(newMemorySpace.totalSpace, percentage);
			return newMemorySpace;
		}

	}
}
