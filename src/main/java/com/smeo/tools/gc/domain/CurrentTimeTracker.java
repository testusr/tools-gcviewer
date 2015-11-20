package com.smeo.tools.gc.domain;

import com.smeo.tools.gc.parser.PatternFactory;
import com.sun.corba.se.spi.activation._InitialNameServiceImplBase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * can handle formatted time markers
 *             2013-12-17T14:44:14.637+0100:
 * and relative time markers
 *             121.596:
 * Responsible for extracting the current time of the passed log entry
 */
public class CurrentTimeTracker {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final Pattern formattedTimePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}");
    private static final Pattern relativeTimePattern = Pattern.compile("^[0-9]+\\"+ PatternFactory.decimalMarker()+"[0-9]+:");

    Boolean basedOnFormattedTime = null;

    private long currTime = -1;

    public boolean extractLoggedTime(String line) throws IOException, ParseException {
        if (basedOnFormattedTime == null || basedOnFormattedTime) {
            Matcher timeMatcher = formattedTimePattern.matcher(line);
            if (timeMatcher.find()) {
                currTime = simpleDateFormat.parse(timeMatcher.group()).getTime();
                basedOnFormattedTime = true;
                return true;
            }
        }

        if (basedOnFormattedTime == null || !basedOnFormattedTime) {
            Matcher relativeTimeMatcher = relativeTimePattern.matcher(line);
            if (relativeTimeMatcher.find()){
                basedOnFormattedTime = false;
                String[] relativeTimeElements = relativeTimeMatcher.group().split("\\"+PatternFactory.decimalMarker()+"|:");
                currTime = Long.valueOf(relativeTimeElements[0]) * 1000;
                currTime +=  Long.valueOf(relativeTimeElements[1]);
                return true;
            }
        }
        return false;
    }

    public Boolean isBasedOnFormattedTime() {
        return basedOnFormattedTime;
    }

    public GcLoggedEvent updateGcEventTiming(GcLoggedEvent loggedEvent) {
        loggedEvent.setTimestamp(currTime);
        return loggedEvent;
    }
}
