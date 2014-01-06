package com.smeo.tools.gc;

import com.smeo.tools.gc.domain.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joachim on 25.12.13.
 */
public class AllEventsParser {
    private CurrentTimeTracker currentTimeTracker = new CurrentTimeTracker();
    private GcFullMemoryInfoEventParser fullMemoryInfoEventParser = new GcFullMemoryInfoEventParser();
    private StringBuilder stringBuilder = new StringBuilder();

    private List<GcLoggedEvent> loggedEvents = new ArrayList<GcLoggedEvent>();
    private boolean hasTenuringDistributions = false;
    private boolean hasApplicationStopTime = false;
    private boolean hasDateTimeStamp = false;
    private boolean hasGcDetails = false;
    private boolean hasFullGcDetails = false;


    public List<GcLoggedEvent> getLoggedEvents() {
        return loggedEvents;
    }

    public boolean isHasTenuringDistributions() {
        return hasTenuringDistributions;
    }

    public boolean isHasApplicationStopTime() {
        return hasApplicationStopTime;
    }

    public boolean isHasDateTimeStamp() {
        return hasDateTimeStamp;
    }

    public boolean isHasGcDetails() {
        return hasGcDetails;
    }

    public boolean isHasFullGcDetails() {
        return hasFullGcDetails;
    }

    public void parseLine(String loggedLine) {
        try {

            if (currentTimeTracker.extractLoggedTime(loggedLine)) {
                hasDateTimeStamp = true;
                String loggedEvents = stringBuilder.toString();
                ApplicationTimeEvent[] applicationRunTimeEvents = ApplicationStopTimeEventParser.parseGcRunTimeEvents(loggedEvents);
                ApplicationStopTimeEvent[] applicationTimeEvents = ApplicationStopTimeEventParser.parseGcStopTimeEvents(loggedEvents);
                GcFullMemoryInfoEvent gcFullMemoryInfoEvent = fullMemoryInfoEventParser.parseGcEvent(loggedEvents);
                CollectionEvent collectionEvent = CollectionEventParser.parseGcEvent(loggedEvents);
                TenuringEvent tenuringEvent = TenuringEventParser.parseGcEvents(loggedEvents);
                if (tenuringEvent != null) {
                    tenuringEvent.setWrappingGcEvent(collectionEvent);
                }

                addEvent(gcFullMemoryInfoEvent);
                addEvent(applicationTimeEvents);
                addEvent(applicationRunTimeEvents);
                addEvent(collectionEvent);
                addEvent(tenuringEvent);
                stringBuilder = new StringBuilder();
            }
            stringBuilder.append(loggedLine);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void addEvent(GcLoggedEvent applicationTimeEvent) {
        if (applicationTimeEvent != null) {
            if (applicationTimeEvent instanceof TenuringEvent) {
                hasTenuringDistributions = true;
            }
            if (applicationTimeEvent instanceof ApplicationStopTimeEvent) {
                hasApplicationStopTime = true;
            }
            if (applicationTimeEvent instanceof CollectionEvent) {
                hasGcDetails = true;
            }
            if (applicationTimeEvent instanceof GcFullMemoryInfoEvent) {
                hasFullGcDetails = true;
            }

            loggedEvents.add(currentTimeTracker.updateGcEventTiming(applicationTimeEvent));
        }
    }

    private void addEvent(GcLoggedEvent[] applicationTimeEvents) {
        for (GcLoggedEvent currEvent : applicationTimeEvents) {
            addEvent(currEvent);
        }
    }

    public String getContentSummary() {
        return "AllEventsParser{" +
                "\nhasTenuringDistributions=" + hasTenuringDistributions +
                "\n, hasApplicationStopTime=" + hasApplicationStopTime +
                "\n, hasDateTimeStamp=" + hasDateTimeStamp +
                "\n, hasGcDetails=" + hasGcDetails +
                "\n, hasFullGcDetails=" + hasFullGcDetails +
                "\n, readEvents=" + loggedEvents.size() +
                "\n}";
    }
}