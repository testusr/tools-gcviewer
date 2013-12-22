package com.smeo.tools.gc;

import java.util.List;

import junit.framework.TestCase;

import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.parser.GcEventParser;
import com.smeo.tools.gc.parser.GcEventParser.GcEvent;
import com.smeo.tools.gc.parser.GcEventParser.GcEventType;
import com.smeo.tools.gc.parser.GcEventParser.GcType;

public class GcEventParserTest extends TestCase {

	public void testCmsAndCmsPerm() {
		String gcLogLine = "2013-03-27T21:05:53.584+0000: 3.125: [Full GC (System) 3.125: [CMS: 0K->7662K(9216000K), 0.2428940 secs] 614419K->7662K(13312000K), [CMS Perm : 23964K->23951K(262144K)], 0.2430100 secs] [Times: user=0.17 sys=0.07, real=0.25 secs] ";
		GcEventParser memorySpaceParser = new GcEventParser();
		List<GcEvent> result = memorySpaceParser.parseGcEvents(gcLogLine);
		assertEquals(3, result.size());

		assertGcEvent(GcEventType.CMS, 0, 7662, 9216000, result.get(0));
		assertGcEvent(GcEventType.CMSPerm, 23964, 23951, 262144, result.get(1));
		assertGcEvent(GcEventType.TOTAL, 614419, 7662, 13312000, result.get(2));
	}

	// public void testGcTimings() {
	// String gcLogLine =
	// "2013-03-28T12:13:38.605-0700: 79.213: [Full GC (System) [PSYoungGen: 928K->0K(100288K)] [PSOldGen: 38547K->27906K(204800K)] 39475K->27906K(305088K) [PSPermGen: 42401K->42401K(42560K)], 0.1750890 secs]";
	// GcEventParser memorySpaceParser = new GcEventParser();
	// GcTiming gcTiming = memorySpaceParser.parseGcTiming(gcLogLine);
	// assertEquals(0.1750890, gcTiming.totalTimeInSec);
	// }

	public void testTotalMemoryParsing() {
		String gcLogLine = ": 92160K->10240K(92160K), 0.0302569 secs] 159852K->85199K(296960K), 0.0303246 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]";
		GcEventParser memorySpaceParser = new GcEventParser();
		GcEvent totalMemory = memorySpaceParser.parseTotalMemoryState(gcLogLine);
		assertGcEvent(GcEventType.TOTAL, 159852, 85199, 296960, totalMemory);
	}

	public void testGcTmingsDetailed() {
		String[] gcLogLines = { "2013-03-18T08:53:22.616+0100: 2.737: [GC 2.737: [DefNew",
				"Desired survivor size 5242880 bytes, new threshold 1 (max 15)",
				"- age   1:   10485760 bytes,   10485760 total",
				": 92160K->10240K(92160K), 0.0302569 secs] 159852K->85199K(296960K), 0.0303246 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]" };

		GcEventParser memorySpaceParser = new GcEventParser();
		GcTiming gcTiming = memorySpaceParser.parseGcTiming(gcLogLines[3]);
		assertEquals(0.03, gcTiming.userTimeInSec);
		assertEquals(0.00, gcTiming.sysTimeInSec);
		assertEquals(0.03, gcTiming.totalTimeInSec);

	}

	public void testGcVersions() {
		String gcLogLine = "2013-03-28T12:13:38.605-0700: 79.213: [Full GC (System) [PSYoungGen: 928K->0K(100288K)] [PSOldGen: 38547K->27906K(204800K)] 39475K->27906K(305088K) [PSPermGen: 42401K->42401K(42560K)], 0.1750890 secs]";
		GcEventParser memorySpaceParser = new GcEventParser();
		List<GcEvent> result = memorySpaceParser.parseGcEvents(gcLogLine);

		assertEquals(4, result.size());

		assertGcEvent(GcEventType.PsYoungGen, 928, 0, 100288, result.get(0));
		assertGcEvent(GcEventType.PSOldGen, 38547, 27906, 204800, result.get(1));
		assertGcEvent(GcEventType.PSPermGen, 42401, 42401, 42560, result.get(2));
		assertGcEvent(GcEventType.TOTAL, 39475, 27906, 305088, result.get(3));

		assertEquals(GcType.FULL_SYSTEM, memorySpaceParser.parseGcType(gcLogLine));
	}

	public void assertGcEvent(GcEventType gcVersion, int usedBefore, int usedAfter, int total, GcEvent gcEvent) {
		assertEquals(gcVersion, gcEvent.gcVersion);
		assertEquals(total, gcEvent.before.totalSpace.intValue());
		assertEquals(usedBefore, gcEvent.before.getUsedSpaceInK().intValue());

		assertEquals(total, gcEvent.after.totalSpace.intValue());
		assertEquals(usedAfter, gcEvent.after.getUsedSpaceInK().intValue());

	}
}
