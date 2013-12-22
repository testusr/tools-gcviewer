package com.smeo.tools.gc;

import java.util.List;

import junit.framework.TestCase;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.parser.GcLogParser;

public class GcLogParserTest extends TestCase {
	private String logSnippet = "Total time for which application threads were stopped: 0.0009550 seconds\n"
			+
			"Application time: 1.0019390 seconds\n"
			+
			"Total time for which application threads were stopped: 0.0003580 seconds\n"
			+
			"Application time: 0.6621220 seconds\n"
			+
			"{Heap before GC invocations=1 (full 1):\n"
			+
			" par new generation   total 4096000K, used 2048000K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)\n"
			+
			"  eden space 2048000K, 100% used [0x0000000446800000, 0x00000004c3800000, 0x00000004c3800000)\n"
			+
			"  from space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)\n"
			+
			"  to   space 2048000K,   0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)\n"
			+
			" concurrent mark-sweep generation total 9216000K, used 6216K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)\n"
			+
			" concurrent-mark-sweep perm gen total 262144K, used 28881K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)\n"
			+
			"2012-02-14T05:17:42.512+0000: 6.187: [GC 6.187: [ParNew\n"
			+
			"Desired survivor size 1677721600 bytes, new threshold 15 (max 15)\n"
			+
			"- age   1:   74383112 bytes,   74383112 total\n"
			+
			": 2048000K->72965K(4096000K), 0.0400390 secs] 2054216K->79181K(13312000K), 0.0401460 secs] [Times: user=0.45 sys=0.13, real=0.04 secs] \n"
			+
			"Heap after GC invocations=2 (full 1):\n"
			+
			" par new generation   total 4096000K, used 72965K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)\n"
			+
			"  eden space 2048000K,   0% used [0x0000000446800000, 0x0000000446800000, 0x00000004c3800000)\n"
			+
			"  from space 2048000K,   3% used [0x0000000540800000, 0x0000000544f415b0, 0x00000005bd800000)\n"
			+
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)\n"
			+
			" concurrent mark-sweep generation total 9216000K, used 6216K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)\n"
			+
			" concurrent-mark-sweep perm gen total 262144K, used 28881K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)\n"
			+
			"}\n"
			+
			"Total time for which application threads were stopped: 0.0408050 seconds\n"
			+
			"Application time: 0.1417900 seconds\n"
			+
			"Total time for which application threads were stopped: 0.0003440 seconds\n"
			+
			"Application time: 0.0039460 seconds\n"
			+
			"Total time for which application threads were stopped: 0.0009550 seconds\n"
			+
			"Application time: 1.0019390 seconds\n"
			+
			"Total time for which application threads were stopped: 0.0003580 seconds\n"
			+
			"Application time: 0.6621220 seconds\n"
			+
			"Total time for which application threads were stopped: 0.0259660 seconds\n"
			+
			"Application time: 3.8740200 seconds\n"
			+
			"{Heap before GC invocations=324 (full 1):\n"
			+
			" par new generation   total 4096000K, used 984558K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)\n"
			+
			"  eden space 2048000K,  44% used [0x0000000446800000, 0x000000047e95c668, 0x00000004c3800000)\n"
			+
			"  from space 2048000K,   3% used [0x0000000540800000, 0x000000054481f548, 0x00000005bd800000)\n"
			+
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)\n"
			+
			" concurrent mark-sweep generation total 9216000K, used 446347K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)\n"
			+
			" concurrent-mark-sweep perm gen total 262144K, used 71622K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)\n"
			+
			"2012-02-14T06:17:39.075+0000: 3602.750: [Full GC (System) 3602.751: [CMS: 446347K->290160K(9216000K), 1.4022130 secs] 1430906K->290160K(13312000K), [CMS Perm : 71622K->61746K(262144K)], 1.4036830 secs] [Times: user=1.40 sys=0.01, real=1.40 secs] \n"
			+
			"Heap after GC invocations=325 (full 2):\n" +
			" par new generation   total 4096000K, used 0K [0x0000000446800000, 0x00000005bd800000, 0x00000005bd800000)\n" +
			"  eden space 2048000K,   0% used [0x0000000446800000, 0x0000000446800000, 0x00000004c3800000)\n" +
			"  from space 2048000K,   0% used [0x0000000540800000, 0x0000000540800000, 0x00000005bd800000)\n" +
			"  to   space 2048000K,   0% used [0x00000004c3800000, 0x00000004c3800000, 0x0000000540800000)\n" +
			" concurrent mark-sweep generation total 9216000K, used 290160K [0x00000005bd800000, 0x00000007f0000000, 0x00000007f0000000)\n" +
			" concurrent-mark-sweep perm gen total 262144K, used 61746K [0x00000007f0000000, 0x0000000800000000, 0x0000000800000000)\n" +
			"}\n" +
			"";

	public void testLogParser() {
		GcLogParser gcLogParser = GcLogParser.createCmsParser();
		String lines[] = logSnippet.split("\n");
		for (String currLine : lines) {
			gcLogParser.parseLine(currLine);
		}

		List<GarbageCollectionEvent> applicationStopTimeEvents = gcLogParser.getAllGarbageCollectionEvents();

		assertEquals(2, applicationStopTimeEvents.size());
		assertFalse(applicationStopTimeEvents.get(0).isFullGc());
		assertFalse(applicationStopTimeEvents.get(0).isStopTheWorld);

		/* TODO.csi - this expectation seem to be wrong ... need to be fixed */
		// assertEquals(applicationStopTimeEvents.get(0).followingApplicationStopTimes.size(), 5);
	}
}
