package com.smeo.tools.gc.parser;

import com.smeo.tools.gc.domain.ApplicationStopTimeEvent;

//"Total time for which application threads were stopped: 0.0408050 seconds\n" +
//"Application time: 0.1417900 seconds\n" +

public class ApplicationStopTimeEventParser {
	private ApplicationStopTimeEvent currApplicationStopTimeEvent = new ApplicationStopTimeEvent();

	public ApplicationStopTimeEvent parse(String line) {
		if (line.startsWith("Total time for which application threads were stopped:")) {
			String[] elements = line.split(":")[1].split(" ");
			currApplicationStopTimeEvent.totalTimeApplicationsWhereStoppedInSec = (Float.valueOf(elements[1]));
		}
		if (line.startsWith("Application time:")) {
			String[] elements = line.split(":")[1].split(" ");
			currApplicationStopTimeEvent.totalTimeApplicationsWhereRunningInSec = (Float.valueOf(elements[1]));
		}
		if (currApplicationStopTimeEvent.isFilled()) {
			ApplicationStopTimeEvent value = currApplicationStopTimeEvent;
			currApplicationStopTimeEvent = new ApplicationStopTimeEvent();
			return value;
		}
		return null;
	}
}
