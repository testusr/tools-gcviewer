package com.smeo.tools.gc.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;

public abstract class GarbageCollectionEventParser {
	abstract public GarbageCollectionEvent parseLogLine(String currLine);

	protected Date extractTimeFromLogLined(String currLine) {
		if (currLine.matches("[0-9]{4}-[0-9]{2}-.*")) {
			String[] lineSegements = currLine.split("\\+");
			// 2012-02-01T00:00:19.081+0000
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			try {
				return simpleDateFormat.parse(lineSegements[0]);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}
