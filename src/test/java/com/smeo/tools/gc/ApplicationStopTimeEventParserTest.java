package com.smeo.tools.gc;

import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.ApplicationTimeEvent;
import junit.framework.TestCase;

/**
 * Created by joachim on 25.12.13.
 */
public class ApplicationStopTimeEventParserTest extends TestCase {

    public void test1(){
    String[] loggedLines = {"2013-12-25T08:09:10.769+0100: 0.299: Application time: 0.0129857 seconds",
            "2013-12-25T08:09:10.771+0100: 0.301: Total time for which application threads were stopped: 0.0020686 seconds",
            "2013-12-25T08:09:10.789+0100: 0.319: Application time: 0.0174109 seconds",
            "{Heap before GC invocations=5 (full 0):",
            "PSYoungGen      total 9216K, used 9168K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
            "eden space 8192K, 99% used [0x00000000ff600000,0x00000000ffdfc200,0x00000000ffe00000)",
            "from space 1024K, 96% used [0x00000000fff00000,0x00000000ffff8020,0x0000000100000000)",
            "to   space 1024K, 0% used [0x00000000ffe00000,0x00000000ffe00000,0x00000000fff00000)",
            "ParOldGen       total 20480K, used 618K [0x00000000fe200000, 0x00000000ff600000, 0x00000000ff600000)",
            "object space 20480K, 3% used [0x00000000fe200000,0x00000000fe29a8f0,0x00000000ff600000)",
            "PSPermGen       total 21504K, used 2957K [0x00000000f9000000, 0x00000000fa500000, 0x00000000fe200000)",
            "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e37a0,0x00000000fa500000)",
            "2013-12-25T08:09:10.789+0100: 0.319: [GC",
            "Desired survivor size 2097152 bytes, new threshold 6 (max 15)",
            "[PSYoungGen: 9168K->1008K(9216K)] 9786K->1930K(29696K), 0.0094328 secs] [Times: user=0.00 sys=0.00, real=0.01 secs]",
            "Heap after GC invocations=5 (full 0):",
            "PSYoungGen      total 9216K, used 1008K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
            "eden space 8192K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000ffe00000)",
            "from space 1024K, 98% used [0x00000000ffe00000,0x00000000ffefc010,0x00000000fff00000)",
            "to   space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)",
            "ParOldGen       total 20480K, used 922K [0x00000000fe200000, 0x00000000ff600000, 0x00000000ff600000)",
            "object space 20480K, 4% used [0x00000000fe200000,0x00000000fe2e68f0,0x00000000ff600000)",
            "PSPermGen       total 21504K, used 2957K [0x00000000f9000000, 0x00000000fa500000, 0x00000000fe200000)",
            "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e37a0,0x00000000fa500000)",
            "}",
            "2013-12-25T08:09:10.798+0100: 0.328: Total time for which application threads were stopped: 0.0097112 seconds",
            "2013-12-25T08:09:10.816+0100: 0.346: Application time: 0.0174680 seconds" };

        ApplicationStopTimeEvent[] events = ApplicationStopTimeEventParser.parseGcStopTimeEvents(loggedLines);
        assertEquals(events.length, 2);
        assertEquals(events[0].getStopTimeInSec(), 0.0020686);
        assertEquals(events[1].getStopTimeInSec(), 0.0097112);


        ApplicationTimeEvent[] rtEvents = ApplicationStopTimeEventParser.parseGcRunTimeEvents(loggedLines);
        assertEquals(rtEvents.length, 3);
        assertEquals(rtEvents[0].getRunTimeInSec(), 0.0129857);
        assertEquals(rtEvents[1].getRunTimeInSec(), 0.0174109);
        assertEquals(rtEvents[2].getRunTimeInSec(), 0.0174680);

    }

}
