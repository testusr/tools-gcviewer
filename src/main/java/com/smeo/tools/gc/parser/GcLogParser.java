package com.smeo.tools.gc.parser;

import java.util.ArrayList;
import java.util.List;

import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;

/**
 * Parsing gc.log entries into the internally used data format
 * 
 * @author smeo
 * 
 */
public class GcLogParser {
	private List<GarbageCollectionEvent> allGarbageCollectionEvents = new ArrayList<GarbageCollectionEvent>();
	private List<ApplicationStopTimeEvent> applicationStopTimes = new ArrayList<ApplicationStopTimeEvent>();

	private GarbageCollectionEventParser gcEventParser;
	private ApplicationStopTimeEventParser applicationStopTimeParser = new ApplicationStopTimeEventParser();

	public GcLogParser(GarbageCollectionEventParser gcEventParser) {
		super();
		this.gcEventParser = gcEventParser;
	}

	public void parseLine(String currLine) {
		ApplicationStopTimeEvent applicationStopTimeEvent = applicationStopTimeParser.parse(currLine);
		if (applicationStopTimeEvent != null) {
			applicationStopTimes.add(applicationStopTimeEvent);
		} else {
			GarbageCollectionEvent event = gcEventParser.parseLogLine(currLine);
			if (event != null) {
				event.followingApplicationStopTimes = applicationStopTimes;
				applicationStopTimes = new ArrayList<ApplicationStopTimeEvent>();
				allGarbageCollectionEvents.add(event);
			}

		}
	}

	public List<GarbageCollectionEvent> getAllGarbageCollectionEvents() {
		return allGarbageCollectionEvents;
	}

	public static GcLogParser createCmsParser() {
		return new GcLogParser(new CMSGarbageCollectionEventParser());
	}

	public static GcLogParser createGenericParser() {
		return new GcLogParser(new GenericGCEventParser());
	}
}
