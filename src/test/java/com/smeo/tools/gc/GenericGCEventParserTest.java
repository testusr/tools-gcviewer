package com.smeo.tools.gc;

import junit.framework.TestCase;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;
import com.smeo.tools.gc.parser.GenericGCEventParser;

public class GenericGCEventParserTest extends TestCase {
	public void testMajorCollectionVerbose() {
		// tenured -> old gen
		String[] majorCollectionVerboseExample = {
				"2013-03-18T15:29:46.176+0100: 23739.875: [Full GC 23739.875: [Tenured: 204799K->204799K(204800K), 0.4304336 secs] 296959K->207580K(296960K), [Perm : 37117K->37117K(37120K)], 0.4305503 secs] [Times: user=0.42 sys=0.02, real=0.43 secs]",
				".........." };

		GenericGCEventParser parses = new GenericGCEventParser();
		GarbageCollectionEvent majorGcEvent = parses.parseLogLine(majorCollectionVerboseExample[0]);

		assertNotNull(majorGcEvent);
		// assertTrue(majorGcEvent.isStopTheWorld);

		verifyMemorySpace(204799, 204800, majorGcEvent.heapBeforeGC.getOldGenSpace());
		verifyMemorySpace(204799, 204800, majorGcEvent.heapAfterGC.getOldGenSpace());

		verifyMemorySpace(37117, 37120, majorGcEvent.heapBeforeGC.getPermGenSpace());
		verifyMemorySpace(37117, 37120, majorGcEvent.heapAfterGC.getPermGenSpace());

		// 296959K->207580K(296960K), [

		verifyMemorySpace(296959, 296960, majorGcEvent.heapBeforeGC.getTotalMemSpace());
		// verifyMemorySpace(207580, 296960, majorGcEvent.heapAfterGC.getTotalMemSpace());

	}

	public void testMajorCollection() {
		String[] majorCollectionExample = {
				"2013-03-28T12:13:38.605-0700: 79.213: [Full GC (System) [PSYoungGen: 928K->0K(100288K)] [PSOldGen: 38547K->27906K(204800K)] 39475K->27906K(305088K)",
				"[PSPermGen: 42401K->42401K(42560K)], 0.1750890 secs]", "..." };

		GenericGCEventParser parses = new GenericGCEventParser();
		assertNull(parses.parseLogLine(majorCollectionExample[0]));
		GarbageCollectionEvent majorGcEvent = parses.parseLogLine(majorCollectionExample[1]);

		assertNotNull(majorGcEvent);
		// assertTrue(majorGcEvent.isCompactingGc);
		//
		// assertEquals(0, majorGcEvent.heapBeforeGC.minorGcCount);
		// assertEquals(0, majorGcEvent.heapBeforeGC.fullGcCount);
		//
		// assertEquals(0, majorGcEvent.heapAfterGC.minorGcCount);
		// assertEquals(1, majorGcEvent.heapAfterGC.fullGcCount);

		verifyMemorySpace(928, 100288, majorGcEvent.heapBeforeGC.getEdenSpace());
		verifyMemorySpace(38547, 204800, majorGcEvent.heapBeforeGC.getOldGenSpace());
		verifyMemorySpace(42401, 42560, majorGcEvent.heapBeforeGC.getPermGenSpace());

		verifyMemorySpace(0, 100288, majorGcEvent.heapAfterGC.getEdenSpace());
		verifyMemorySpace(27906, 204800, majorGcEvent.heapAfterGC.getOldGenSpace());
		verifyMemorySpace(42401, 42560, majorGcEvent.heapBeforeGC.getPermGenSpace());

	}

	public void testMinorCollection() {

		String[] minorCollectionExample = { "2013-03-28T12:12:23.746-0700: 4.355: [GC",
				" Desired survivor size 13107200 bytes, new threshold 7 (max 15)",
				" [PSYoungGen: 77856K->1520K(89600K)] 77856K->1520K(294400K), 0.0056452 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]" };

		GenericGCEventParser parses = new GenericGCEventParser();
		assertNull(parses.parseLogLine(minorCollectionExample[0]));
		assertNull(parses.parseLogLine(minorCollectionExample[1]));
		GarbageCollectionEvent minorEvent = parses.parseLogLine(minorCollectionExample[2]);

		assertNotNull(minorEvent);
		assertFalse(minorEvent.isStopTheWorld);
		assertEquals(0, minorEvent.heapBeforeGC.minorGcCount);
		assertEquals(0, minorEvent.heapBeforeGC.fullGcCount);

		assertEquals(1, minorEvent.heapAfterGC.minorGcCount);
		assertEquals(0, minorEvent.heapAfterGC.fullGcCount);

		verifyMemorySpace(77856, 89600, minorEvent.heapBeforeGC.getEdenSpace());
		verifyMemorySpace(1520, 89600, minorEvent.heapAfterGC.getEdenSpace());

		verifyMemorySpace(1520, 294400, minorEvent.heapAfterGC.getTotalMemSpace());
		verifyMemorySpace(77856, 294400, minorEvent.heapBeforeGC.getTotalMemSpace());

	}

	public void testIgnoreLinesOfNoConcern() {
		String[] randomEntriesToBeIgnored = {
				"Total time for which application threads were stopped: 0.3201015 seconds",
				"Total time for which application threads were stopped: 0.3201015 seconds"
		};

		GenericGCEventParser parses = new GenericGCEventParser();
		for (String currLine : randomEntriesToBeIgnored) {
			assertNull(parses.parseLogLine(currLine));
		}
	}

	private void verifyMemorySpace(int usedSpaceInK, int totalSpaceInK, MemorySpace space) {
		assertEquals(totalSpaceInK, space.totalSpace.intValue());
		assertEquals(usedSpaceInK, (int) space.getUsedSpaceInK());

	}

}
