package com.smeo.tools.gc.newparser.domain;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joachim on 25.12.13.
 */
public class CurrentTimeTracker {
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private static final Pattern timePattern = Pattern.compile("[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}");

    private long currTime = -1;

    public boolean extractLoggedTime(String line) throws IOException, ParseException {
        Matcher timeMatcher = timePattern.matcher(line);
        if (timeMatcher.find()){
            currTime = simpleDateFormat.parse(timeMatcher.group()).getTime();
            return true;
        }
        return false;
    }

    public GcLoggedEvent updateGcEventTiming(GcLoggedEvent loggedEvent){
        loggedEvent.setTimestamp(currTime);
        return loggedEvent;
    }
}
