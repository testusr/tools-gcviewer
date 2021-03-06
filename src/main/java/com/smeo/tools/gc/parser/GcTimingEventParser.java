package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.GcTiming;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smeo.tools.gc.parser.PatternFactory.*;

/**
 * Created by joachim on 25.12.13.
 */
public class GcTimingEventParser {
    public static GcTiming parseGcEvent(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvent(stringBuilder.toString());
    }

    public static GcTiming parseGcEvent(String loggedEvent) {
        Matcher matcher = gcTimingPattern().matcher(loggedEvent);
        if (matcher.find()){
            String[] elements = matcher.group().split(", | |=");
            return new GcTiming(PatternFactory.toDouble(elements[2]),
                    PatternFactory.toDouble(elements[4]),
                    PatternFactory.toDouble(elements[6]));
        }
        return null;
    }
}
