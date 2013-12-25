package com.smeo.tools.gc.newparser;

import com.smeo.tools.gc.domain.MemorySpace;
import com.smeo.tools.gc.newparser.domain.CollectorEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class CollectorEventParser {
    // [OldGen: 204799K->204799K(204800K)

    public static CollectorEvent[] parseGcEvents(String gcLogLine) {
        Pattern valuePattern = Pattern.compile("\\: [0-9]+K->[0-9]+K\\([0-9]+K\\)");
        Pattern gcTypes = getTypesPattern();

        Matcher valueMatcher = valuePattern.matcher(gcLogLine);
        Matcher gcTypeMatcher = gcTypes.matcher(gcLogLine);
        CollectorEvent[] collectorEvents = new CollectorEvent[3];
        int i =0;
        while (valueMatcher.find()&& gcTypeMatcher.find()) {
             Integer values[] = LogParseUtils.extractKNumbers(valueMatcher.group());
             collectorEvents[i++] = new CollectorEvent(GarbageCollector.fromString(gcTypeMatcher.group()),
                        new MemorySpace(values[0], values[2]),
                     new MemorySpace(values[1], values[2])
                     );
        }
        return collectorEvents;
    }

    public static CollectorEvent[] parseGcEvents(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvents(stringBuilder.toString());
    }

    public static Pattern getTypesPattern() {
        StringBuilder patter = new StringBuilder();
        for (int i=0; i < GarbageCollector.values().length; i++){
            patter.append("\\[");
            patter.append(GarbageCollector.values()[i].getLogFilePrefix());
            if (i < (GarbageCollector.values().length-1)){
              patter.append("|");
            }
        }
        System.out.println(patter.toString());

        return Pattern.compile(patter.toString());
    }
}
