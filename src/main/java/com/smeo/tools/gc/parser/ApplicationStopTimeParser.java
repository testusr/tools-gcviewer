package com.smeo.tools.gc.parser;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

public class ApplicationStopTimeParser {
	TimeSeries timeSeries = new TimeSeries("Thread Stop Time Per Interval", Millisecond.class);
	// XYSeries series = new XYSeries("Thread Stop Time Per Interval");
	float interval = 0;

	float appRunTime = 0;
	float appStopTime = 0;

	int gcLineCounter = 0;
	int corruptedLines = 0;

	long lastTime = -1;

	public ApplicationStopTimeParser(float interval) {
		this.interval = interval;
	}

	public void parse(String line) throws IOException, ParseException {
		if (line != null) {
			if (lastTime > 0) {
				try {
					if (line.startsWith("Application time")) {
						gcLineCounter++;
						String[] tokens = line.split(" ");
						appRunTime += Float.valueOf(tokens[tokens.length - 2]);
					} else if (line.startsWith("Total time for")) {
						gcLineCounter++;
						String[] tokens = line.split(" ");
						appStopTime += Float.valueOf(tokens[tokens.length - 2]);
					}
					float totalTime = appRunTime + appStopTime;
					if (totalTime > interval) {
						// end of interval reached

						// record statistics
						RegularTimePeriod currTime = new Millisecond(new Date(lastTime += (long) (totalTime * 1000)));
						timeSeries.add(currTime, appStopTime
								/ totalTime * 100);

						// reset counters
						appStopTime = 0;
						appRunTime = 0;
					}
				} catch (NumberFormatException e) {
					// some lines are corrupted
					// but it can be ignored if it's not too much
					corruptedLines++;
					e.printStackTrace();
				}
			} else {
				lastTime = extractLoggedTime(line);
			}
		}

		// System.out.println("Detected " + corruptedLines + " corrupted lines out of " + gcLineCounter + " gc reporting lines");

	}

	public TimeSeries getTimeSeries() {
		return timeSeries;
	}

	private static long extractLoggedTime(String line) throws IOException, ParseException {
		if (line != null) {
			if (line.matches("[0-9]{4}-[0-9]{2}-.*")) {
				String[] lineSegements = line.split("\\+");
				// 2012-02-01T00:00:19.081+0000
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				return simpleDateFormat.parse(lineSegements[0]).getTime();
			}
		}
		return -1;
	}
}
