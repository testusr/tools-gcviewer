package com.smeo.tools.gc.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by joachim on 25.12.13.
 */
public class LogParseUtils {
    private static final Pattern totalMemoryPatter = Pattern.compile("[0-9]+K");
    private static final Pattern timeInSecsPattern = Pattern.compile(", [0-9]+\\.[0-9]+ secs");
    private static final Pattern doubleNumber = Pattern.compile("[0-9]+\\.[0-9]+");

    public static Integer[] extractKNumbers(String data) {

        Matcher matcher = totalMemoryPatter.matcher(data);
        int filledNumbers = 0;
        if (matcher.find()) {
            Integer[] memNumbers = new Integer[3];
            memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
            if (matcher.find()) {
                memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
            }
            if (matcher.find()) {
                memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
            }

            if (filledNumbers == 3) {
                return memNumbers;
            } else {
                throw new IllegalArgumentException("could not extract 3 values from string: " + data);
            }
        }
        return null;
    }

    public static Integer extractIntFromKNumber(String kbTextData) {
        if (kbTextData.endsWith("K")) {
            return Integer.valueOf(kbTextData.substring(0, kbTextData.length() - 1));
        }
        return null;
    }

    public static Double extractTimeInSecs(String timeInSecs){
        Matcher matcher = timeInSecsPattern.matcher(timeInSecs);
        if (matcher.find()){
            String text = matcher.group();
            if (text.endsWith("secs")){
                Matcher doubleMatcher = doubleNumber.matcher(text);
                if (doubleMatcher.find()){
                    return Double.valueOf(doubleMatcher.group());
                }
            }
        }
        return null;
    }

}
