package com.smeo.tools.gc.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by truehl on 11/19/15.
 */
public class PatternFactory {
    private final static String doubleValue = "[0-9]+\\.[0-9]+";
    private final static Pattern applicationRunTimePattern = Pattern.compile("Application time: +"+doubleValue+" seconds");
    private static final String majorCollectionPatter = "[Full GC";
    private static final String majorSystemCollectionPatter = "[Full GC (System)";
    private static final String minorCollectionPatter = "[GC";

    // CollectorEventParser
    private static final String optionaltimeTaken = "(, [0-9]+\\.[0-9]+ secs){0,1}";
    private static final String knumbersRegexp = "[0-9]+K->[0-9]+K\\([0-9]+K\\)";
    private static final Pattern valuePattern = Pattern.compile("\\: " + knumbersRegexp + optionaltimeTaken);
    private static final String totalCollection = "(secs\\]|\\)\\]) " + knumbersRegexp + optionaltimeTaken;
    private static final Pattern totalCollectionValuePatter = Pattern.compile(totalCollection);

    // GcFullMemoryInfoEventParser
    private final static String headerLine = "Heap (before|after) GC invocations=[0-9]+ \\(full [0-9]+";
    private final static String collectorStartLine = "[A-Za-z]+ +total [0-9]+K, used [0-9]+K";
    private final static String spaceInfoLine = "(eden|from|to|object) +space [0-9]+K, [0-9]*";

    private final static Pattern infoEventPattern = Pattern.compile("(" + headerLine + "|" + collectorStartLine + "|" + spaceInfoLine + ")");
    private final static Pattern numberPattern = Pattern.compile("[0-9]+\\.{0,1}[0-9]*");

    // GcTimingEventParser
        //[Times: user=0.02 sys=0.00, real=0.03 secs]
    private static final Pattern gcTimingPattern = Pattern.compile("\\[Times: user=[0-9]+\\.[0-9]+ sys=[0-9]+\\.[0-9]+, real=[0-9]+\\.[0-9]+ secs");


    // ApplicationStopTimeEventParser

    private final static Pattern applicationStopPattern = Pattern.compile("Total time for which application threads were stopped: +[0-9]+\\.[0-9]+ seconds");
    private final static Pattern doubleValuePatter = Pattern.compile("[0-9]+\\.[0-9]+");


    public static Pattern applicationRunTimePattern() {
        return applicationRunTimePattern;
    }

    public static double toDouble(Matcher matcher) {
        return Double.valueOf(matcher.group());
    }

    public static CharSequence majorCollectionPatter() {
        return majorCollectionPatter;
    }

    public static CharSequence majorSystemCollectionPatter() {
        return majorSystemCollectionPatter;
    }

    public static CharSequence minorCollectionPatter() {
        return minorCollectionPatter;
    }

    public static Pattern totalCollectionValuePatter() {
        return totalCollectionValuePatter;
    }

    public static Pattern valuePattern() {
        return valuePattern;
    }

    public static Pattern numberPattern() {
        return numberPattern;
    }

    public static Pattern infoEventPattern() {
        return infoEventPattern;
    }

    public static Pattern gcTimingPattern() {
        return gcTimingPattern;
    }

    public static Pattern applicationStopPattern() {
        return applicationStopPattern;
    }

    public static Pattern doubleValuePatter() {
        return doubleValuePatter;
    }
}
