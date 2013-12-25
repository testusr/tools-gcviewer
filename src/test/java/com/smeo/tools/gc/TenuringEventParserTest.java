package com.smeo.tools.gc;

import com.smeo.tools.gc.newparser.TenuringEventParser;
import com.smeo.tools.gc.newparser.domain.TenuringEvent;
import junit.framework.TestCase;

/**
 * Created by joachim on 25.12.13.
 */
public class TenuringEventParserTest extends TestCase {
    public void test1(){
        String[] minorCollectionExample = { "2013-12-17T14:44:14.637+0100: 1535.308: [GC 1535.308: [DefNew",
                "Desired survivor size 5242880 bytes, new threshold 14 (max 15)",
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
                ": 83239K->1381K(92160K), 0.0120258 secs] 280583K->198759K(296960K), 0.0122362 secs]"};

        TenuringEvent tenuringEvent = TenuringEventParser.parseGcEvents(minorCollectionExample);
        assertEquals(tenuringEvent.max, 15);
        assertEquals(tenuringEvent.newThreshold, 14);
        assertEquals(tenuringEvent.desiredSurvivorSpace, 5242880);

        assertEquals(tenuringEvent.getTotalSpace(15), 0);
        assertEquals(tenuringEvent.getTotalSpace(14), 1374144);
        assertEquals(tenuringEvent.getTotalSpace(6), 1058008);

        assertEquals(tenuringEvent.getUsedSpace(15), 0);
        assertEquals(tenuringEvent.getUsedSpace(14), 40744);
        assertEquals(tenuringEvent.getUsedSpace(6), 36656);

    }
}
