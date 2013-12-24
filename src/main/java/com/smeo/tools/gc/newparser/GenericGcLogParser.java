package com.smeo.tools.gc.newparser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 1:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class GenericGcLogParser {

    List<GcElementParser> activeElementParsers = new ArrayList<GcElementParser>();

    public void parseLine(String loggedLine) {
        List<GcElementParser> newParses = GcElementParserFactory.createParsers(loggedLine);
        activeElementParsers.addAll(newParses);

        for (GcElementParser currParser : activeElementParsers) {
            for (int i = activeElementParsers.size() - 1; i >= 0; i--) {
                if (activeElementParsers.get(i).needsMoreInfo(loggedLine)) {
                    activeElementParsers.remove(i);
                }
            }
        }
    }


}
