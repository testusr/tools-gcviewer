package com.smeo.tools.gc.parser;


import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.ApplicationTimeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joachim on 25.12.13.
 */
public class ApplicationStopTimeEventParser {
    final static Pattern applicationRunTimePattern = Pattern.compile("Application time: +[0-9]+\\.[0-9]+ seconds");
    final static Pattern applicationStopPattern = Pattern.compile("Total time for which application threads were stopped: +[0-9]+\\.[0-9]+ seconds");
    final static Pattern doubleValuePatter = Pattern.compile("[0-9]+\\.[0-9]+");

    public static ApplicationStopTimeEvent[] parseGcStopTimeEvents(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcStopTimeEvents(stringBuilder.toString());
    }

    public static ApplicationTimeEvent[] parseGcRunTimeEvents(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcRunTimeEvents(stringBuilder.toString());
    }

    public static ApplicationTimeEvent[] parseGcRunTimeEvents(String loggedEvent) {
        Matcher runTimeMatcher = applicationRunTimePattern.matcher(loggedEvent);
        List<ApplicationTimeEvent> stopTimEvents = new ArrayList<ApplicationTimeEvent>();
        while (runTimeMatcher.find()){
            stopTimEvents.add(createRunTimeEvent(runTimeMatcher.group()));
        }
        return stopTimEvents.toArray(new ApplicationTimeEvent[0]);
    }

    public static ApplicationStopTimeEvent[] parseGcStopTimeEvents(String loggedEvent) {
        Matcher stopTimeMatcher = applicationStopPattern.matcher(loggedEvent);
        List<ApplicationStopTimeEvent> stopTimEvents = new ArrayList<ApplicationStopTimeEvent>();
        while (stopTimeMatcher.find()){
            stopTimEvents.add(createStopTimeEvent(stopTimeMatcher.group()));
        }
        return stopTimEvents.toArray(new ApplicationStopTimeEvent[0]);
    }

    private static ApplicationStopTimeEvent createStopTimeEvent(String loggedStopTime) {
        Matcher valueMatcher = doubleValuePatter.matcher(loggedStopTime);
        if (valueMatcher.find()){
            return new ApplicationStopTimeEvent(Double.valueOf(valueMatcher.group()));
        }
        return null;
    }

    private static ApplicationTimeEvent createRunTimeEvent(String loggedRunTime){
        Matcher valueMatcher = doubleValuePatter.matcher(loggedRunTime);
        if (valueMatcher.find()){
            return new ApplicationTimeEvent(Double.valueOf(valueMatcher.group()));
        }
        return null;

    }
}
