package com.smeo.tools.stoptime;

import com.smeo.tools.gc.parser.PatternFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Trying to correlate safe point data to application stop stopTimeEntryDateTime logs with the goal to find out
 * which operations caused how much stop stopTimeEntryDateTime.
 */
public class StopTimeDistribution {
    //    String toMatch = "2015-11-15T16:05:37.198+0000: 0,160: Total stopTimeEntryDateTime for which application threads were stopped: 0,0000933 seconds, Stopping threads took: 0,0000211 seconds";
    private final static String decimalRegexp = "[0-9]+\\" + PatternFactory.decimalMarker() + "[0-9]+";
    private final static String gcDateTimeRelativeRegexp = "([0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}\\+[0-9]{4}): (" + decimalRegexp + "):";
    private final static String stopTimeRegexp = ""+gcDateTimeRelativeRegexp + " .*: (" + decimalRegexp + ") seconds, .*";
    // 2015-11-22T07:48:26.438+0200: 2,857: Application stopTimeEntryDateTime: 0,1547989 seconds
    private final static String runTimeRegexp = gcDateTimeRelativeRegexp + " Application time: (" + decimalRegexp + ") seconds.*";

    private final static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final static String safePointRegexp = "(" + decimalRegexp + "): ([A-Za-z|_]+) +\\[.*\\] +[0-9]+ +";

    private Pattern stopTimePattern = Pattern.compile(stopTimeRegexp);
    private Pattern runTimePattern = Pattern.compile(runTimeRegexp);
    private Pattern safePointPatter = Pattern.compile(safePointRegexp);

    private List<StopRunTimeEntry> stopTimeEntries = new ArrayList<StopRunTimeEntry>();
    private double oldestRelativeStopTime = 0.0;
    private int safePointDataEntriesAssigned = 0;
    private int safePointDataEntryCount = 0;

    private void loadStopTimeEntries(String filename) {
        System.out.println(stopTimeRegexp);
        System.out.println(runTimeRegexp);

        FileParser fileParser = new FileParser() {
            StopRunTimeEntry currStopTimeEntry = null;

            @Override
            boolean parseLine(String line) {
                Matcher stopTimeMatcher = stopTimePattern.matcher(line);
                if (stopTimeMatcher.matches()) {
                    try {
                        if (currStopTimeEntry != null) {
                            addStopTimeEntry(currStopTimeEntry);
                        }
                        long dateTime = simpleDateFormat.parse(getGroup(stopTimeMatcher, 1)).getTime();
                        double relativeTime = PatternFactory.toDouble(getGroup(stopTimeMatcher, 2));
                        double stopTime = PatternFactory.toDouble(getGroup(stopTimeMatcher, 3));
                        this.currStopTimeEntry = new StopRunTimeEntry(dateTime, relativeTime, stopTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    Matcher runTimeMatcher = runTimePattern.matcher(line);
                    if (runTimeMatcher.matches()) {
                        long dateTime;
                        try {
                            dateTime = simpleDateFormat.parse(getGroup(runTimeMatcher, 1)).getTime();

                            double relativeTime = PatternFactory.toDouble(getGroup(runTimeMatcher, 2));
                            double runtime = PatternFactory.toDouble(getGroup(runTimeMatcher, 3));
                            if (currStopTimeEntry != null) {
                                currStopTimeEntry.addRuntimeInfo(dateTime, relativeTime, runtime);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }


                return true;

            }
        };
        fileParser.parseFile(filename);
    }

    private void addStopTimeEntry(StopRunTimeEntry stopTimeEntry) {
        stopTimeEntries.add(stopTimeEntry);
        oldestRelativeStopTime = stopTimeEntry.runTimeEntryRelativeTime;

    }

    private String getGroup(Matcher matcher, int groupIndex) {
        String matchedLine = matcher.group();
        return matchedLine.substring(matcher.start(groupIndex), matcher.end(groupIndex));
    }

    private void enrichWithSafePointData(String filename) {
        FileParser fileParser = new FileParser() {
            @Override
            boolean parseLine(String line) {
                Matcher matcher = safePointPatter.matcher(line);
                if (matcher.matches()) {
                    double relativeTime = PatternFactory.toDouble(getGroup(matcher, 1));
                    String reason = getGroup(matcher, 2);
                    safePointDataEntryCount++;

                    if (!youngerThanYoungestStopTimeEntry(relativeTime)) {
                        StopRunTimeEntry potentialMatch = findStopTimeEntryYoungerClosestTo(relativeTime);
                        if (potentialMatch != null) {
                            safePointDataEntriesAssigned++;
                            potentialMatch.setSafePointData(relativeTime, reason.intern());
                        }
                        return true;// !olderThanOldestStopTimeEntry(relativeTime);
                    }
                }
                return true;
            }
        };
        fileParser.parseFile(filename);
    }

    private boolean youngerThanYoungestStopTimeEntry(double relativeTime) {
        return relativeTime < stopTimeEntries.get(0).runTimeEntryRelativeTime;
    }

    private StopRunTimeEntry findStopTimeEntryYoungerClosestTo(double relativeTime) {
        StopRunTimeEntry prevEntry;
        StopRunTimeEntry currStopTimeEntry = stopTimeEntries.get(0);
        for (int i = 0; i < stopTimeEntries.size();  i++) {
            prevEntry = currStopTimeEntry;
            currStopTimeEntry = stopTimeEntries.get(i);
            if (currStopTimeEntry.matchesRelativeTime(relativeTime)){
                return currStopTimeEntry;
            }  else if (currStopTimeEntry.olderThan(relativeTime)) {
                double diffPrev = prevEntry.matchTimeDiff(relativeTime);
                double diffCurr = currStopTimeEntry.matchTimeDiff(relativeTime);
                if (diffPrev < diffCurr){
                    return prevEntry;
                } else {
                    return currStopTimeEntry;
                }
            }
        }
        return null;
    }

    public void run(String gcLogFilename, String stdoutFilename) {
        long start = System.currentTimeMillis();
        loadStopTimeEntries(gcLogFilename);
        System.out.println("finished processing '" + stopTimeEntries.size() + "' stop stopTimeEntryDateTime entries in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds");
        if (this.stopTimeEntries == null) {
            System.out.println("stop times entries could not be created ... exiting");
            return;
        }
        enrichWithSafePointData(stdoutFilename);
        System.out.println("finished in " + ((System.currentTimeMillis() - start) / 1000.0) + " seconds - " + safePointDataEntriesAssigned + " reasons assigned");

        printConsistencyInformation();
        //printEntries();
        printSummedUpStopTimes();
    }

    private void printEntries(){
        for (int i=0; i < stopTimeEntries.size(); i++){
            System.out.println(stopTimeEntries.get(i));
        }
    }

    private void printSummedUpStopTimes(){
        Map<String, Double> summedStopTimes = new HashMap<String, Double>();
        Map<String, Integer> entryCounts = new HashMap<String, Integer>();
        int notCountendEntries = 0;

        for (StopRunTimeEntry currEntry : stopTimeEntries){
            if (currEntry.hasSavePointData()){
                Double currStopTime = summedStopTimes.get(currEntry.safePoint());
                Integer currEntryCounts = entryCounts.get(currEntry.safePoint());
                if (currStopTime == null){
                    currStopTime = 0.0;
                    currEntryCounts = 0;
                }
                summedStopTimes.put(currEntry.safePoint(), currStopTime + currEntry.stopTime);
                entryCounts.put(currEntry.safePoint(), ++currEntryCounts);
            } else {
                notCountendEntries++;
            }
        }

        System.out.println("\n########### SUMMED UP STOP TIME  ");
        for (Map.Entry<String, Double> currEntry : summedStopTimes.entrySet()){
            String savePoint = currEntry.getKey();
            System.out.println(savePoint + " ["+entryCounts.get(savePoint)+"] : " + currEntry.getValue());
        }
        System.out.println("not counted entries: " + notCountendEntries);
        System.out.println("entries total: " + stopTimeEntries.size());
    }
    private void printConsistencyInformation() {
        int stopTimeEntries = this.stopTimeEntries.size();
        System.out.println("## Printing consistency information for " + stopTimeEntries );
        int fullyConsistentEntries = 0;
        int inconsistentEntries = 0;
        int safePointInconsistencies = 0;
        int runTimeInconsistencies = 0;
        int entriesWithSafePointData = 0;
        int entriesWithPerfectlyMatchedSafePointData = 0;
        for (int i=0; i < this.stopTimeEntries.size(); i++){
            StopRunTimeEntry currEntry = this.stopTimeEntries.get(i);
            boolean runTimeStopTimeConsistent = currEntry.runTimeStopTimeConsistent();
            boolean safePointConsistent = currEntry.safePointConsistent();
            if (!runTimeStopTimeConsistent) runTimeInconsistencies++;
            if (!safePointConsistent) safePointInconsistencies++;
            if (!runTimeStopTimeConsistent || !safePointConsistent) {
                inconsistentEntries++;
            } else {
                fullyConsistentEntries++;
            }
            if (currEntry.hasSavePointData()){
                entriesWithSafePointData++;
                if (currEntry.isExactSafePointMatch().get()){
                    entriesWithPerfectlyMatchedSafePointData++;
                }
            }
        }
        System.out.println("\n########### CONSISTENCY INFO ");
        System.out.println("fullyConsistentEntries: " + fullyConsistentEntries);
        System.out.println("inconsistentEntries: " + inconsistentEntries);
        System.out.println("safePointInconsistencies: " + safePointInconsistencies);
        System.out.println("runTimeInconsistencies: " + runTimeInconsistencies);
        System.out.println("safePointDataEntries: " + safePointDataEntryCount);
        System.out.println("entriesWithSafePointData: " + entriesWithSafePointData);
        System.out.println("entriesWithPerfectlyMatchedSafePointData: " + entriesWithPerfectlyMatchedSafePointData);
        System.out.println("entriesWithoutSafePointData: " + (stopTimeEntries - entriesWithSafePointData));

    }


    private static class StopRunTimeEntry {
        private long stopTimeEntryDateTime;
        private double stopTimeEntryRelativeTime;
        private double stopTime;
        private long runTimeEntryDateTime;
        private double runTimeEntryRelativeTime;
        private double runTime;

        private double safePointRelativeTime;
        private String reason = null;

        private StopRunTimeEntry(long dateTime, double relativeTime, double stopTime) {
            this.stopTimeEntryDateTime = dateTime;
            this.stopTimeEntryRelativeTime = relativeTime;
            this.stopTime = stopTime;
        }

        public boolean runTimeStopTimeConsistent(){
            if (!(runTimeEntryRelativeTime > 0 && stopTimeEntryRelativeTime > 0)) return false;
            double expectedStopTimeEntryRelativeTime = runTimeEntryRelativeTime - runTime;
            return (expectedStopTimeEntryRelativeTime - stopTimeEntryRelativeTime) < 1.0;
        }
        public boolean safePointConsistent(){
            if (!hasSavePointData()) return true;
            return (Math.abs(runTimeEntryRelativeTime - safePointRelativeTime) < 10.0);
        }

        private boolean hasSavePointData() {
            if (safePointRelativeTime > 0){
                return true;
            }
            return false;
        }

        public Optional<Boolean> isExactSafePointMatch(){
            if (!hasSavePointData()) return Optional.empty();
            return Optional.of(safePointRelativeTime == runTimeEntryRelativeTime);
        }

        public boolean olderThan(double relativeTime) {
            return this.runTimeEntryRelativeTime > relativeTime;
        }

        public void setSafePointData(double relativeTime, String reason) {
            this.reason = reason;
            this.safePointRelativeTime = relativeTime;
        }

        public void addRuntimeInfo(long dateTime, double relativeTime, double runTime) {
            this.runTimeEntryDateTime = dateTime;
            this.runTimeEntryRelativeTime = relativeTime;
            this.runTime = runTime;
        }

        private Double matchTimeDiff(double relativeTime){
                return Math.abs(runTimeEntryRelativeTime  - relativeTime);
        }

        @Override
        public String toString() {
            return toString(runTimeEntryRelativeTime, safePointRelativeTime, reason, hasSavePointData() ? matchTimeDiff(safePointRelativeTime) : "null");
        }

        private String toString(Object... objects){
            StringBuilder builder = new StringBuilder();
            for  (int i=0; i < objects.length; i++){
                builder.append(String.valueOf(objects[i]));
                builder.append(", ");
            }
            return builder.toString();
        }

        public boolean matchesRelativeTime(double relativeTime) {
            return this.runTimeEntryRelativeTime == relativeTime;
        }

        public String safePoint() {
            return reason;
        }
    }

    public static void main(String[] args) {
        StopTimeDistribution stopTimeDistribution = new StopTimeDistribution();
        stopTimeDistribution.run("/tmp/bus/gc.log", "/tmp/bus/stdOut.log");
        //stopTimeDistribution.printStopTimeDistribution();
    }

}
