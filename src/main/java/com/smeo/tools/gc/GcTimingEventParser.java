package com.smeo.tools.gc;

import com.smeo.tools.gc.domain.GcTiming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joachim on 25.12.13.
 */
public class GcTimingEventParser {
    //[Times: user=0.02 sys=0.00, real=0.03 secs]
    private static final Pattern gcTimingPattern = Pattern.compile("\\[Times: user=[0-9]+\\.[0-9]+ sys=[0-9]+\\.[0-9]+, real=[0-9]+\\.[0-9]+ secs");
    public static GcTiming parseGcEvent(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvent(stringBuilder.toString());
    }

    public static GcTiming parseGcEvent(String loggedEvent) {
        Matcher matcher = gcTimingPattern.matcher(loggedEvent);
        if (matcher.find()){
            String[] elements = matcher.group().split(",| |=");
            return new GcTiming(Double.valueOf(elements[2]),
                    Double.valueOf(elements[4]),
                    Double.valueOf(elements[7]));
        }
        return null;
    }
}
