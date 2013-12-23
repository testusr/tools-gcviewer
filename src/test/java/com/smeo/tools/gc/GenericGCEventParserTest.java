package com.smeo.tools.gc;

import junit.framework.TestCase;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;
import com.smeo.tools.gc.parser.GenericGCEventParser;

public class GenericGCEventParserTest extends TestCase {
	public void testMajorCollectionVerbose() {
		// tenured -> old gen
		String[] majorCollectionVerboseExample = {
				"2013-03-18T15:29:46.176+0100: 23739.875: [Full GC 23739.875: [OldGen: 204799K->204799K(204800K), 0.4304336 secs] 296959K->207580K(296960K), [Perm : 37117K->37117K(37120K)], 0.4305503 secs] [Times: user=0.42 sys=0.02, real=0.43 secs]",
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

    public void testTenuringParsing(){
        String[] minorCollectionExample = { "2013-12-17T14:44:14.637+0100: 1535.308: [GC 1535.308: [DefNew",
        "Desired survivor size 5242880 bytes, new threshold 15 (max 15)",
        "- age   1:     832928 bytes,     832928 total",
                "- age   2:      63312 bytes,     896240 total",
                "- age   3:      41840 bytes,     938080 total",
                "- age   4:      41880 bytes,     979960 total",
                "- age   5:      41392 bytes,    1021352 total",
                "- age   6:      36656 bytes,    1058008 total",
                "- age   7:      34984 bytes,    1092992 total",
                "- age   8:      35168 bytes,    1128160 total",
                "- age   9:      51840 bytes,    1180000 total",
                "- age  10:      38760 bytes,    1218760 total",
                "- age  11:      35312 bytes,    1254072 total",
                "- age  12:      38704 bytes,    1292776 total",
                "- age  13:      40624 bytes,    1333400 total",
                "- age  14:      40744 bytes,    1374144 total",
                "- age  15:      40328 bytes,    1414472 total",
                ": 83239K->1381K(92160K), 0.0120258 secs] 280583K->198759K(296960K), 0.0122362 secs]"};

        GenericGCEventParser parser = new GenericGCEventParser();
        GarbageCollectionEvent event = null;
        for (String s : minorCollectionExample) {
            event = parser.parseLogLine(s);
        }
        assertEquals(15, event.tenuring.max);
        assertEquals(63312, event.tenuring.usedSpace[1]);
        assertEquals(40328, event.tenuring.usedSpace[14]);

        assertEquals(896240, event.tenuring.totalSpace[1]);
        assertEquals(1414472, event.tenuring.totalSpace[14]);


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
