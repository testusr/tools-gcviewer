package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.CollectorEvent;
import com.smeo.tools.gc.domain.GarbageCollector;
import com.smeo.tools.gc.domain.MemorySpace;

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
    private static final String optionaltimeTaken = "(, [0-9]+\\.[0-9]+ secs){0,1}";
    private static final String knumbersRegexp = "[0-9]+K->[0-9]+K\\([0-9]+K\\)";
    private static final Pattern valuePattern = Pattern.compile("\\: " + knumbersRegexp + optionaltimeTaken);
    private static final String totalCollection = "(secs\\]|\\)\\]) " + knumbersRegexp + optionaltimeTaken;
    private static final Pattern totalCollectionValuePatter =  Pattern.compile(totalCollection);

    public static CollectorEvent parseTotalGcEventValues(String gcLogLine){
        Matcher valueMatcher = totalCollectionValuePatter.matcher(gcLogLine);
        int i =0;
        while (valueMatcher.find()) {
            String group = valueMatcher.group();
            Integer values[] = LogParseUtils.extractKNumbers(group);
            Double timeInSecs = LogParseUtils.extractTimeInSecs(group);
            return new CollectorEvent(null,
                    new MemorySpace(values[0], values[2]),
                    new MemorySpace(values[1], values[2]),
                    timeInSecs
            );
        }
        return null;


    }
    public static CollectorEvent[] parseGcEvents(String gcLogLine) {
        Pattern gcTypes = getTypesPattern();

        Matcher valueMatcher = valuePattern.matcher(gcLogLine);
        Matcher gcTypeMatcher = gcTypes.matcher(gcLogLine);
        CollectorEvent[] collectorEvents = new CollectorEvent[3];
        int i =0;
        while (valueMatcher.find()&& gcTypeMatcher.find()) {
            String group = valueMatcher.group();
            Integer values[] = LogParseUtils.extractKNumbers(group);
            Double timeInSecs = LogParseUtils.extractTimeInSecs(group);
             collectorEvents[i++] = new CollectorEvent(GarbageCollector.fromString(gcTypeMatcher.group()),
                        new MemorySpace(values[0], values[2]),
                     new MemorySpace(values[1], values[2]),
                     timeInSecs);
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

        return Pattern.compile(patter.toString());
    }
}
