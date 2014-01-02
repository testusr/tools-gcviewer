package com.smeo.tools.gc;

import com.smeo.tools.gc.newparser.CollectorEventParser;
import com.smeo.tools.gc.newparser.domain.GarbageCollector;
import com.smeo.tools.gc.newparser.domain.CollectorEvent;
import junit.framework.TestCase;

/**
 * Created by joachim on 25.12.13.
 */
public class CollectorEventParserTest extends TestCase {

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

        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(logFile);
        assertNull(collectorEvents[1]);
        verifyCollection(collectorEvents[0], GarbageCollector.PsYoungGen, 8192, 736, 9216);
    }

    public void testCase2() {
        String[] logFile = {"2013-03-18T08:53:22.616+0100: 2.737: [GC 2.737: [DefNew",
                "Desired survivor size 5242880 bytes, new threshold 1 (max 15)",
                "- age   1:   10485760 bytes,   10485760 total",
                ": 92160K->10240K(92160K), 0.0302569 secs] 159852K->85199K(296960K), 0.0303246 secs] [Times: user=0.03 sys=0.00, real=0.03 secs]" };

        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(logFile);
        assertNull(collectorEvents[1]);
        verifyCollection(collectorEvents[0], GarbageCollector.DefNew, 92160, 10240, 92160);

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
        CollectorEvent[] collectorEvents = CollectorEventParser.parseGcEvents(logFile);

        verifyCollection(collectorEvents[0], GarbageCollector.PsYoungGen, 384, 0, 9728);
        verifyCollection(collectorEvents[1], GarbageCollector.ParOldGen, 20353, 4365, 20480);
        verifyCollection(collectorEvents[2], GarbageCollector.PSPermGen, 2959, 2958, 21504);
    }

    private void verifyCollection(CollectorEvent collectorEvent, GarbageCollector expectedCollector, int before, int after, int available) {
        assertEquals(collectorEvent.getCollector(), expectedCollector);
        assertEquals(collectorEvent.getMemoryBefore().getUsedSpaceInK(), before);
        assertEquals(collectorEvent.getMemoryBefore().getAvailableSpaceInK(), available);

        assertEquals(collectorEvent.getMemoryAfter().getUsedSpaceInK(), after);
        assertEquals(collectorEvent.getMemoryAfter().getAvailableSpaceInK(), available);
    }
}