package com.smeo.tools.gc;

import com.smeo.tools.gc.domain.TenuringEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joachim on 25.12.13.
 */
public class TenuringEventParser {

    private final static Pattern initLinePattern = Pattern.compile("Desired survivor size [0-9]+ bytes, new threshold [0-9]+ \\(max [0-9]+");
    //"- age   1:     832928 bytes,     832928 total"
    private final static Pattern agePattern = Pattern.compile("- age[ ]+[0-9]+: *[0-9]+ bytes, *[0-9]+ *total");

    public static TenuringEvent parseGcEvents(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvents(stringBuilder.toString());
    }

    public static TenuringEvent parseGcEvents(String logFile) {
        Matcher initLineMatcher = initLinePattern.matcher(logFile);

        if (initLineMatcher.find()){
            Matcher agePatternMatcher = agePattern.matcher(logFile);
            int usedSpace[] = new int[TenuringEvent.MAX_AGE];
            int availableSpace[] = new int[TenuringEvent.MAX_AGE];

            for (int i=0; i < TenuringEvent.MAX_AGE; i++){
                if (agePatternMatcher.find()){
                    String ageLogEntry = agePatternMatcher.group();
                    extractSizes(i, usedSpace, availableSpace,ageLogEntry);
                } else {
                    usedSpace[i] = 0;
                    availableSpace[i] = 0;
                }
            }
            return createTenuringEvent(initLineMatcher.group(), usedSpace, availableSpace);
        }
        return null;
    }

    private static TenuringEvent createTenuringEvent(String currLine, int usedSpace[], int availableSpace[] ){
        String[] elements = currLine.split("size|bytes|threshold|max|\\(|\\)");
        int desiredSurvivorSpace = Integer.valueOf(elements[1].trim());
        int newThreshold = Integer.valueOf(elements[3].trim());
        int max = Integer.valueOf(elements[5].trim());

        return new TenuringEvent(newThreshold, max, desiredSurvivorSpace, usedSpace, availableSpace);

    }

    private static void extractSizes(int i, int[] usedSpace, int[] availableSpace, String currLine) {
        String[] elements = currLine.split("- age|:|bytes|total|,");
        usedSpace[Integer.valueOf(elements[1].trim())-1] = Integer.valueOf(elements[2].trim());
        availableSpace[Integer.valueOf(elements[1].trim())-1] = Integer.valueOf(elements[4].trim());
    }

}
