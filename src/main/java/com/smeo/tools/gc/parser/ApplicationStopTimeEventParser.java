package com.smeo.tools.gc.parser;


import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.ApplicationTimeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smeo.tools.gc.parser.PatternFactory.*;


/**
 * Created by joachim on 25.12.13.
 */
public class ApplicationStopTimeEventParser {

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
        Matcher runTimeMatcher = applicationRunTimePattern().matcher(loggedEvent);
        List<ApplicationTimeEvent> stopTimEvents = new ArrayList<ApplicationTimeEvent>();
        while (runTimeMatcher.find()){
            stopTimEvents.add(createRunTimeEvent(runTimeMatcher.group()));
        }
        return stopTimEvents.toArray(new ApplicationTimeEvent[0]);
    }

    public static ApplicationStopTimeEvent[] parseGcStopTimeEvents(String loggedEvent) {
        Matcher stopTimeMatcher = PatternFactory.applicationStopPattern().matcher(loggedEvent);
        List<ApplicationStopTimeEvent> stopTimEvents = new ArrayList<ApplicationStopTimeEvent>();
        while (stopTimeMatcher.find()){
            stopTimEvents.add(createStopTimeEvent(stopTimeMatcher.group()));
        }
        return stopTimEvents.toArray(new ApplicationStopTimeEvent[0]);
    }

    private static ApplicationStopTimeEvent createStopTimeEvent(String loggedStopTime) {
        Matcher valueMatcher = PatternFactory.doubleValuePatter().matcher(loggedStopTime);
        if (valueMatcher.find()){
            return new ApplicationStopTimeEvent(toDouble(valueMatcher));
        }
        return null;
    }

    private static ApplicationTimeEvent createRunTimeEvent(String loggedRunTime){
        Matcher valueMatcher = PatternFactory.doubleValuePatter().matcher(loggedRunTime);
        if (valueMatcher.find()){
            return new ApplicationTimeEvent(toDouble(valueMatcher));
        }
        return null;

    }
}
