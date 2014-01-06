package com.smeo.tools.gc;

import com.smeo.tools.gc.domain.CollectionEvent;
import com.smeo.tools.gc.domain.GarbageCollector;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.parser.CollectionEventParser;
import junit.framework.TestCase;

/**
 * Created by joachim on 25.12.13.
 */
public class CollectionEventParserTest extends TestCase {

    public void testCase1() {
        String logFile[] = {"2013-12-25T08:09:10.682+0100: 0.212: [GC",
                "Desired survivor size 1048576 bytes, new threshold 7 (max 15)",
                "[PSYoungGen: 8192K->736K(9216K)] 8192K->736K(29696K), 0.0345537 secs] [Times: user=0.02 sys=0.00, real=0.03 secs]",
                "Heap after GC invocations=1 (full 0):",
                "PSYoungGen      total 9216K, used 736K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
                "eden space 8192K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000ffe00000)",
                "from space 1024K, 71% used [0x00000000ffe00000,0x00000000ffeb8020,0x00000000fff00000)",
                "to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)",
                "ParOldGen       total 20480K, used 0K [0x00000000fe200000, 0x00000000ff600000, 0x00000000ff600000)",
                "object space 20480K, 0% used [0x00000000fe200000,0x00000000fe200000,0x00000000ff600000)",
                "PSPermGen       total 21504K, used 2952K [0x00000000f9000000, 0x00000000fa500000, 0x00000000fe200000)",
                "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e2118,0x00000000fa500000)" };

        CollectionEvent collectionEvent = CollectionEventParser.parseGcEvent(logFile);
        verifyCollection(collectionEvent, true, false, GarbageCollector.PsYoungGen, null, null);

        //[Times: user=0.02 sys=0.00, real=0.03 secs]
        validateTiming(collectionEvent, 0.02, 0.00, 0.03);
    }

    private void validateTiming(CollectionEvent collectionEvent, double usr, double sys, double real) {
        GcTiming gcTiming = collectionEvent.getGcTiming();
        assertEquals(gcTiming.getUserTimeInSec(), usr);
        assertEquals(gcTiming.getSysTimeInSec(), sys);
        assertEquals(gcTiming.getRealTimInSec(), real);
    }

    public void testCase2() {
        String[] logFile = {"2013-03-18T08:53:22.616+0100: 2.737: [GC 2.737: [DefNew",
                "Desired survivor size 5242880 bytes, new threshold 1 (max 15)",
                "- age   1:   10485760 bytes,   10485760 total",
                ": 92160K->10240K(92160K), 0.0302569 secs] 159852K->85199K(296960K), 0.0303246 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]" };

        CollectionEvent collectionEvent = CollectionEventParser.parseGcEvent(logFile);
        verifyCollection(collectionEvent, true, false, GarbageCollector.DefNew, null, null);
        validateTiming(collectionEvent, 0.03, 0.00, 0.03);

    }

    public void testCase3() {

        String[] logFile = {"2013-12-25T08:09:14.731+0100: 4.261: [Full GC [PSYoungGen: 384K->0K(9728K)] [ParOldGen: 20353K->4365K(20480K)] 20737K->4365K(30208K) [PSPermGen: 2959K->2958K(21504K)], 0.0360476 secs] [Times: user=0.03 sys=0.00, real=0.04 secs]",
                "Heap after GC invocations=67 (full 1):",
                "PSYoungGen      total 9728K, used 0K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
                "eden space 9216K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000fff00000)",
                "from space 512K, 0% used [0x00000000fff80000,0x00000000fff80000,0x0000000100000000)",
                "to   space 512K, 0% used [0x00000000fff00000,0x00000000fff00000,0x00000000fff80000)",
                "ParOldGen       total 20480K, used 4365K [0x00000000fe200000, 0x00000000ff600000, 0x00000000ff600000)",
                "object space 20480K, 21% used [0x00000000fe200000,0x00000000fe643458,0x00000000ff600000)",
                "PSPermGen       total 21504K, used 2958K [0x00000000f9000000, 0x00000000fa500000, 0x00000000fe200000)",
                "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e3960,0x00000000fa500000)",
                "}" };
        CollectionEvent collectionEvent = CollectionEventParser.parseGcEvent(logFile);
        verifyCollection(collectionEvent, false, false, GarbageCollector.PsYoungGen, GarbageCollector.ParOldGen, GarbageCollector.PSPermGen);
        validateTiming(collectionEvent, 0.03, 0.00, 0.04);

    }

    private void verifyCollection(CollectionEvent collectionEvent,
                                  boolean isMinor, boolean isSystem,
                                  GarbageCollector youngC, GarbageCollector oldC, GarbageCollector permC){
        assertEquals(collectionEvent.isMinorCollection(), isMinor);
        assertEquals(collectionEvent.isTriggeredBySystem(), isSystem);
        if (isMinor){
            assertNull(collectionEvent.getOldGenCollector());
            assertNull(collectionEvent.getPermGenCollector());
        } else{
            assertEquals(youngC, collectionEvent.getYoungGenCollector().getCollector());
            assertEquals(oldC, collectionEvent.getOldGenCollector().getCollector());
            assertEquals(permC, collectionEvent.getPermGenCollector().getCollector());
        }
    }
}