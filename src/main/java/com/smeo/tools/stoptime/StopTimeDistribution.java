package com.smeo.tools.stoptime;

import com.smeo.tools.gc.parser.PatternFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Trying to correlate safe point data to application stop time logs with the goal to find out
 * which operations caused how much stop time.
 */
public class StopTimeDistribution {
    //    String toMatch = "2015-11-15T16:05:37.198+0000: 0,160: Total time for which application threads were stopped: 0,0000933 seconds, Stopping threads took: 0,0000211 seconds";
    private final static String decimalRexep = "[0-9]+\\" + PatternFactory.decimalMarker() + "[0-9]+";
    private final static String stopTimeRegexp = "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}\\+[0-9]{4}): (" + decimalRexep + "): .*: (" + decimalRexep + ") seconds, .*";
    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final static String safePointRegexp = "(" + decimalRexep + "): ([A-Za-z]+) +\\[.*\\] +[0-9]+ +";

    private Pattern stopTimePattern = Pattern.compile(stopTimeRegexp);
    private Pattern safePointPatter = Pattern.compile(safePointRegexp);

    private List<StopTimeEntry> stopTimeEntries = new ArrayList<StopTimeEntry>();
    private double oldestRelativeStopTime  =  0.0;
    private int reasonsAssigned = 0;

    private void loadStopTimeEntries(String filename) {
        FileParser fileParser = new FileParser() {
            @Override
            boolean parseLine(String line) {
                Matcher matcher = stopTimePattern.matcher(line);
                if (matcher.matches()) {
                    try {
                        long dateTime = simpleDateFormat.parse(getGroup(matcher, 1)).getTime();
                        double relativeTime = PatternFactory.toDouble(getGroup(matcher, 2));
                        double stopTime = PatternFactory.toDouble(getGroup(matcher, 3));

                        addStopTimeEntry(dateTime, relativeTime, stopTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }
        };
        fileParser.parseFile(filename);
    }

    private void addStopTimeEntry(long dateTime, double relativeTime, double stopTime) {
        stopTimeEntries.add(new StopTimeEntry(dateTime, relativeTime, stopTime));
        oldestRelativeStopTime = relativeTime;

    }

    private String getGroup(Matcher matcher, int groupIndex) {
        String matchedLine = matcher.group();
        return matchedLine.substring(matcher.start(groupIndex), matcher.end(groupIndex));
    }

    private static String num(int digits, char postfix) {
        return "[0-9]{" + digits + "}" + postfix;
    }

    private void enrichWithReason(List<StopTimeEntry> entriesToEnrich, String filename) {
        FileParser fileParser = new FileParser() {
            @Override
            boolean parseLine(String line) {
                Matcher matcher = safePointPatter.matcher(line);
                if (matcher.matches()) {
                    double relativeTime = PatternFactory.toDouble(getGroup(matcher, 1));
                    String reason = getGroup(matcher, 2);

                    if (!youngerThanYoungestStopTimeEntry(relativeTime)){
                    StopTimeEntry potentialMatch = findStopTimeEntryYoungerClosestTo(relativeTime);
                    if (potentialMatch != null){
                        reasonsAssigned++;
                        potentialMatch.setReason(reason.intern());
                    }
                    return !olderThanOldestStopTimeEntry(relativeTime);
                    }
                }
                return true;
            }
        };
        fileParser.parseFile(filename);
    }

    private boolean youngerThanYoungestStopTimeEntry(double relativeTime) {
        return relativeTime < stopTimeEntries.get(0).relativeTime;
    }

    private StopTimeEntry findStopTimeEntryYoungerClosestTo(double relativeTime) {
        StopTimeEntry lastYoungerEntry = null;
        for (int i=this.stopTimeEntries.size()-1; i > 0 ; i--){
            StopTimeEntry currStopTimeEntry = stopTimeEntries.get(i);
            if (currStopTimeEntry.youngerThanRelativeTime(relativeTime)){
                lastYoungerEntry = currStopTimeEntry;
            } else {
                break;
            }
        }
        return lastYoungerEntry;
    }


    private boolean olderThanOldestStopTimeEntry(double relativeTime) {
        return relativeTime >= oldestRelativeStopTime ;
    }

    public void run(String gcLogFilename, String stdoutFilename) {
        long start = System.currentTimeMillis();
        loadStopTimeEntries(gcLogFilename);
        System.out.println("finished processing '"+stopTimeEntries.size()+"' stop time entries in " + ((System.currentTimeMillis() - start)/1000.0) + " seconds");
        if (this.stopTimeEntries == null) {
            System.out.println("stop times entries could not be created ... exiting");
            return;
        }
        enrichWithReason(this.stopTimeEntries, stdoutFilename);
        System.out.println("finished in " + ((System.currentTimeMillis() - start)/1000.0) + " seconds - " + reasonsAssigned + " reasons assigned");
    }


    private static class StopTimeEntry {
        private long time;
        private double relativeTime;
        private double stopTime;

        private String reason = null;

        private StopTimeEntry(long dateTime, double relativeTime, double stopTime) {
            this.time = dateTime;
            this.relativeTime = relativeTime;
            this.stopTime = stopTime;
        }

        public boolean youngerThanRelativeTime(double relativeTime) {
            return this.relativeTime >= relativeTime;
        }

        public void setReason(String reason){
            this.reason = reason;
        }

        @Override
        public String toString() {
            return reason + ", " + time + ", " + relativeTime + ", " + stopTime;
        }
    }

    public static void main(String[] args) {
        StopTimeDistribution stopTimeDistribution = new StopTimeDistribution();
        stopTimeDistribution.run("/tmp/bus/gc.log", "/tmp/bus/stdOut.log");
        //stopTimeDistribution.printStopTimeDistribution();
    }

}
