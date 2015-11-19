package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.FullMemoryInfo;
import com.smeo.tools.gc.domain.GarbageCollector;
import com.smeo.tools.gc.domain.GcFullMemoryInfoEvent;
import com.smeo.tools.gc.domain.MemorySpaceInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.smeo.tools.gc.parser.PatternFactory.*;

/**
 * Created by joachim on 25.12.13.
 */
public class GcFullMemoryInfoEventParser {

    private FullMemoryInfo beforeGcMemoryInfo;
    private FullMemoryInfo afterGcMemoryInfo;

    public GcFullMemoryInfoEvent parseGcEvent(String[] gcLogLines) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String currString : gcLogLines){
            stringBuilder.append(currString);
        }
        return parseGcEvent(stringBuilder.toString());
    }

    public GcFullMemoryInfoEvent parseGcEvent(String loggedEvent) {
        if (beforeGcMemoryInfo == null){
            beforeGcMemoryInfo = parseBeforeGcEvent(loggedEvent);
        } else if (afterGcMemoryInfo == null) {
            afterGcMemoryInfo = parseAfterGcEvent(loggedEvent);
        }

        if (beforeGcMemoryInfo != null && afterGcMemoryInfo != null){
           GcFullMemoryInfoEvent gcFullMemoryInfoEvent = new GcFullMemoryInfoEvent(beforeGcMemoryInfo, afterGcMemoryInfo);
           beforeGcMemoryInfo = afterGcMemoryInfo = null;
           return gcFullMemoryInfoEvent;
        }

        return null;
    }

    private FullMemoryInfo parseAfterGcEvent(String loggedEvent) {
        Matcher infoEventMatcher = infoEventPattern().matcher(loggedEvent);
        int i=0;
        if (infoEventMatcher.find()){
            if (infoEventMatcher.group().contains("after")){
                return createFullMemoryInfo(loggedEvent);
            }
        }
        return null;
    }

    private FullMemoryInfo parseBeforeGcEvent(String loggedEvent) {
        Matcher infoEventMatcher = infoEventPattern().matcher(loggedEvent);
        if (infoEventMatcher.find()){
            if (infoEventMatcher.group().contains("before")){
                return createFullMemoryInfo(loggedEvent);
            }
        }
        return null;
    }

    private FullMemoryInfo createFullMemoryInfo(String loggedEvent) {
        try {
        Matcher infoEventMatcher = infoEventPattern().matcher(loggedEvent);
        String headline = getNext(infoEventMatcher);
        String youngGenHead = getNext(infoEventMatcher);
        String edenSpace = getNext(infoEventMatcher);
        String fromSpace = getNext(infoEventMatcher);
        String toSpace = getNext(infoEventMatcher);
        String oldGenHead = getNext(infoEventMatcher);
        String oldGenObjSpace = getNext(infoEventMatcher);
        String permGenHead = getNext(infoEventMatcher);
        String permGenObjSpace = getNext(infoEventMatcher);

        return new FullMemoryInfo(
                extractCollector(youngGenHead),
                extractTotalMemorySpace(youngGenHead),
                extractMemorySpace(edenSpace),
                extractMemorySpace(fromSpace),
                extractMemorySpace(toSpace),
                extractCollector(oldGenHead),
                extractMemorySpace(oldGenObjSpace),
                extractCollector(permGenHead),
                extractMemorySpace(permGenObjSpace));
        } catch (IllegalArgumentException e){
            System.out.println("Could not read perm gen info for FullMemoryInfo ... most likely CMS Version has to be fixed");
        }
        return null;
    }

    private MemorySpaceInfo extractTotalMemorySpace(String youngGenHead) {
        //"PSYoungGen      total 9728K, used 0K [0x00000000ff600000, 0x0000000100000000, 0x0000000100000000)",
        Matcher numberMatcher = numberPattern().matcher(youngGenHead);
        int total;
        int used;

        total = nextIntNumber(numberMatcher);
        used = nextIntNumber(numberMatcher);
        return MemorySpaceInfo.createFromSize(total, used);
    }

    private static int nextIntNumber(Matcher numberMatcher){
        if (numberMatcher.find()){
            return Integer.valueOf(numberMatcher.group());
        } else {
            throw new IllegalArgumentException("could not extract number");
        }
    }

    private static float nextFloatNumber(Matcher numberMatcher){
        if (numberMatcher.find()){
            return Float.valueOf(numberMatcher.group());
        } else {
            throw new IllegalArgumentException("could not extract number");
        }
    }

    private MemorySpaceInfo extractMemorySpace(String space) {
        // "eden space 9216K, 0% used [0x00000000ff600000,0x00000000ff600000,0x00000000fff00000)",
        Matcher numberMatcher = numberPattern().matcher(space);
        int total;
        float usedPerc;

        total = nextIntNumber(numberMatcher);
        usedPerc = nextFloatNumber(numberMatcher);
        return MemorySpaceInfo.createFromPerc(total, usedPerc);
    }

    private GarbageCollector extractCollector(String headline) {
        return GarbageCollector.fromString(headline.split(" ")[0]);
    }

    private String getNext(Matcher infoEventMatcher) {
        if (infoEventMatcher.find()){
            return infoEventMatcher.group();
        } else {
            throw new IllegalArgumentException(("could not find group"));
        }
    }
}