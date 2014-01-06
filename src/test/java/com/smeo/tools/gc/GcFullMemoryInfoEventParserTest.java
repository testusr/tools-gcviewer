package com.smeo.tools.gc;

import junit.framework.TestCase;

/**
 * Created by joachim on 25.12.13.
 */
public class GcFullMemoryInfoEventParserTest extends TestCase {
    public void testCase1() {
        String[] logFileBefore = {
                "{Heap before GC invocations=1 (full 0):",
                "PSYoungGen      total 9216K, used 8192K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
                "eden space 8192K, 100% used [0x00000000ff600000,0x00000000ffe00000,0x00000000ffe00000)",
                "from space 1024K, 0% used [0x00000000fff00000,0x00000000fff00000,0x0000000100000000)",
                "to   space 1024K, 0% used [0x00000000ffe00000,0x00000000ffe00000,0x00000000fff00000)",
                "ParOldGen       total 20480K, used 0K [0x00000000fe200000, 0x00000000ff600000, 0x00000000ff600000)",
                "object space 20480K, 0% used [0x00000000fe200000,0x00000000fe200000,0x00000000ff600000)",
                "PSPermGen       total 21504K, used 2952K [0x00000000f9000000, 0x00000000fa500000, 0x00000000fe200000)",
                "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e2118,0x00000000fa500000)"};
        String[] logFileAfter = {
                "2013-12-25T08:09:10.682+0100: 0.212: [GC",
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
                "object space 21504K, 13% used [0x00000000f9000000,0x00000000f92e2118,0x00000000fa500000)"
        };

        GcFullMemoryInfoEventParser gcFullMemoryInfoEventParser = new GcFullMemoryInfoEventParser();
        gcFullMemoryInfoEventParser.parseGcEvent(logFileBefore);
        gcFullMemoryInfoEventParser.parseGcEvent(logFileAfter);
    }
}
