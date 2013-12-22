package com.smeo.tools.gc;

import junit.framework.TestCase;

import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;
import com.smeo.tools.gc.domain.HeapState;
import com.smeo.tools.gc.parser.ApplicationStopTimeEventParser;
import com.smeo.tools.gc.parser.CMSGarbageCollectionEventParser;

public class CMSGarbageCollectionEventParserTest extends TestCase {
	private String[] secondSnippet = {
			"{Heap before GC invocations=324 (full 1):",
			" par new generation   total 4096000K, used 984558K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)",
			"  eden space 2048000K,  44% used [0x0000000446800000, 0x000000047e95c668, 0x00000004c3800000)",
			"  from space 2048000K,   3% used [0x0000000540800000, 0x000000054481f548, 0x00000005bd800000)",
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)",
			" concurrent mark-sweep generation total 9216000K, used 446347K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)",
			" concurrent-mark-sweep perm gen total 262144K, used 71622K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)",
			"2012-02-14T06:17:39.075+0000: 3602.750: [Full GC (System) 3602.751: [CMS: 446347K->290160K(9216000K), 1.4022130 secs] 1430906K->290160K(13312000K), ",
			"[CMS Perm : 71622K->61746K(262144K)], 1.4036830 secs] [Times: user=1.40 sys=0.01, real=1.40 secs] ",
			"Heap after GC invocations=325 (full 2):",
			" par new generation   total 4096000K, used 0K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)",
			"  eden space 2048000K,   0% used [0x0000000446800000, 0x0000000446800000, 0x00000004c3800000)",
			"  from space 2048000K,   0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)",
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)",
			" concurrent mark-sweep generation total 9216000K, used 290160K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)",
			" concurrent-mark-sweep perm gen total 262144K, used 61746K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)",
			"}",
			"" };
	private String[] logSnippet = { "Total time for which application threads were stopped: 0.0009550 seconds",
			"Application time: 1.0019390 seconds",
			"{Heap before GC invocations=1 (full 1):",
			" par new generation   total 4096000K, used 2048000K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)",
			"  eden space 2048000K, 100% used [0x0000000446800000, 0x00000004c3800000, 0x00000004c3800000)",
			"  from space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)",
			"  to   space 2048000K,   0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)",
			" concurrent mark-sweep generation total 9216000K, used 6216K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)",
			" concurrent-mark-sweep perm gen total 262144K, used 28881K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)",
			"2012-02-14T05:17:42.512+0000: 6.187: [GC 6.187: [ParNew",
			"Desired survivor size 1677721600 bytes, new threshold 15 (max 15)",
			"- age   1:   74383112 bytes,   74383112 total",
			": 2048000K->72965K(4096000K), 0.0400390 secs] 2054216K->79181K(13312000K), 0.0401460 secs] [Times: user=0.45 sys=0.13, real=0.04 secs] ",
			"Heap after GC invocations=2 (full 1):",
			" par new generation   total 4096000K, used 72965K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)",
			"  eden space 2048000K,   0% used [0x0000000446800000, 0x0000000446800000, 0x00000004c3800000)",
			"  from space 2048000K,   3% used [0x0000000540800000, 0x0000000544f415b0, 0x00000005bd800000)",
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)",
			" concurrent mark-sweep generation total 9216000K, used 6216K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)",
			" concurrent-mark-sweep perm gen total 262144K, used 28881K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)",
			"}",
			"Total time for which application threads were stopped: 0.0408050 seconds",
			"Application time: 0.1417900 seconds",
			"Total time for which application threads were stopped: 0.0003440 seconds",
			"Application time: 0.0039460 seconds",
			"" };

	private String[] applicationStopTimeLog = { "Total time for which application threads were stopped: 0.0408050 seconds",
			"Application time: 0.1417900 seconds",
			"", "" };

	public void testApplicationStopParser() {
		ApplicationStopTimeEventParser applicationStopTimeEventParser = new ApplicationStopTimeEventParser();
		ApplicationStopTimeEvent applicationStopTimeEvent = null;

		for (String currLine : applicationStopTimeLog) {
			applicationStopTimeEvent = applicationStopTimeEventParser.parse(currLine);
			if (applicationStopTimeEvent != null) {
				break;
			}
		}
		assertTrue((0.0408050f - applicationStopTimeEvent.totalTimeApplicationsWhereStoppedInSec) == 0.0f);
		assertTrue((0.1417900f - applicationStopTimeEvent.totalTimeApplicationsWhereRunningInSec) == 0.0f);

	}

	public void testGarbageCollectionEventParser() {
		CMSGarbageCollectionEventParser garbageCollectionEventParser = new CMSGarbageCollectionEventParser();
		GarbageCollectionEvent garbageCollectionEvent = null;
		for (String currLine : logSnippet) {
			garbageCollectionEvent = garbageCollectionEventParser.parseLogLine(currLine);
			if (garbageCollectionEvent != null) {
				break;
			}
		}

		HeapState heapBeforeGC = garbageCollectionEvent.heapBeforeGC;

		validateMemorySpace(heapBeforeGC.memorySpace.edenSpace, 2048000, 100);
		validateMemorySpace(heapBeforeGC.memorySpace.survivorFromSpace, 2048000, 0);
		validateMemorySpace(heapBeforeGC.memorySpace.survivorToSpace, 2048000, 0);
		validateMemorySpace(heapBeforeGC.memorySpace.oldGenSpace, 9216000, (6216f / (9216000f / 100f)));
		validateMemorySpace(heapBeforeGC.memorySpace.permGenSpace, 262144, (28881f / (262144f / 100f)));

		assertEquals(1, heapBeforeGC.fullGcCount);
		assertEquals(1, heapBeforeGC.minorGcCount);

		HeapState heapAfterGC = garbageCollectionEvent.heapAfterGC;

		validateMemorySpace(heapAfterGC.memorySpace.edenSpace, 2048000, 0);
		validateMemorySpace(heapAfterGC.memorySpace.survivorFromSpace, 2048000, 3);
		validateMemorySpace(heapAfterGC.memorySpace.survivorToSpace, 2048000, 0);
		validateMemorySpace(heapAfterGC.memorySpace.oldGenSpace, 9216000, (6216f / (9216000f / 100f)));
		validateMemorySpace(heapAfterGC.memorySpace.permGenSpace, 262144, (28881f / (262144f / 100f)));

		assertEquals(1, heapAfterGC.fullGcCount);
		assertEquals(2, heapAfterGC.minorGcCount);

		assertFalse(garbageCollectionEvent.isFullGc());
	}

	public void testGarbageCollectionEventParserB() {
		CMSGarbageCollectionEventParser garbageCollectionEventParser = new CMSGarbageCollectionEventParser();
		GarbageCollectionEvent garbageCollectionEvent = null;
		for (String currLine : secondSnippet) {
			garbageCollectionEvent = garbageCollectionEventParser.parseLogLine(currLine);
			if (garbageCollectionEvent != null) {
				break;
			}
		}

		assertTrue(garbageCollectionEvent.isFullGc());
		assertTrue(garbageCollectionEvent.isStopTheWorld);
	}

	public static void validateHeapState(HeapState heapState, int fullGcCount, int minorGcCount) {
		assertEquals(heapState.fullGcCount, fullGcCount);
		assertEquals(heapState.minorGcCount, minorGcCount);
	}

	public static void validateMemorySpace(MemorySpace memorySpace, int totalSize, float usedPerc) {
		assertEquals(memorySpace.totalSpace.intValue(), totalSize);
		assertTrue((memorySpace.getUsedSpaceInPercentage() - usedPerc) == 0);
	}
}
