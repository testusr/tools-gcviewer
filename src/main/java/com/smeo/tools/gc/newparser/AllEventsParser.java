package com.smeo.tools.gc.newparser;

import com.smeo.tools.gc.newparser.domain.*;

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
    boolean gotTimeStamp = false;

    public void parseLine(String loggedLine){
        try {

            if (currentTimeTracker.extractLoggedTime(loggedLine)){
                String loggedEvents = stringBuilder.toString();
                addEvent(ApplicationStopTimeEventParser.parseGcRunTimeEvents(loggedEvents));
                addEvent(ApplicationStopTimeEventParser.parseGcStopTimeEvents(loggedEvents));
                addEvent(fullMemoryInfoEventParser.parseGcEvent(loggedEvents));
                addEvent(TenuringEventParser.parseGcEvents(loggedEvents));
                addEvent(CollectionEventParser.parseGcEvent(loggedEvents));
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
        if (applicationTimeEvent != null){
          loggedEvents.add(currentTimeTracker.updateGcEventTiming(applicationTimeEvent));
        }
    }
    private void addEvent(GcLoggedEvent[] applicationTimeEvents) {
        for (GcLoggedEvent currEvent : applicationTimeEvents){
            addEvent(currEvent);
        }
    }
}
