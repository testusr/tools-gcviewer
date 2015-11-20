package com.smeo.tools.stoptime;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by truehl on 11/20/15.
 */
public class MatcherExperiment {


    public static void main(String[] args) {
//        String toMatch = "2015-11-15T16:05:37.198+0000: 0,160: Total time for which application threads were stopped: 0,0000933 seconds, Stopping threads took: 0,0000211 seconds";
//        String regexp = "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}\\+[0-9]{4}): ([0-9]+\\,[0-9]+): .*: ([0-9]+\\,[0-9]+) seconds";
//
//        Matcher matcher = Pattern.compile(regexp).matcher(toMatch);
//        System.out.println(matcher.matches());
//        System.out.println(matcher.group());
//        System.out.println(matcher.groupCount());
//        System.out.println(toMatch.substring(matcher.start(0), matcher.end(0)));
//        System.out.println(toMatch.substring(matcher.start(1), matcher.end(1)));
//        System.out.println(toMatch.substring(matcher.start(2), matcher.end(2)));
//        System.out.println(toMatch.substring(matcher.start(3), matcher.end(3)));


        String spToMatch = "2,733: CollectForMetadataAllocation       [     213          0              2    ]      [     0     0     0     0     0    ]  0 ";
        String spRegexp = "([0-9]+\\,[0-9]+): ([A-Za-z]+) +\\[.*\\] +[0-9]+ +";
        Matcher spMatcher = Pattern.compile(spRegexp).matcher(spToMatch);
        System.out.println(spMatcher.matches());
        System.out.println(spMatcher.group());
        System.out.println(spMatcher.groupCount());
        System.out.println(spMatcher.group().substring(spMatcher.start(1), spMatcher.end(1)));
        System.out.println(spMatcher.group().substring(spMatcher.start(2), spMatcher.end(2)));

    }

}
