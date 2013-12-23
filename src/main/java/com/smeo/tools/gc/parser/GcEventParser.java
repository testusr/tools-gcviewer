package com.smeo.tools.gc.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.smeo.tools.gc.domain.GcSurvivorSpace;
import com.smeo.tools.gc.domain.GcTiming;
import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;

/**
 * Parsing log lines and extracting "logEvents" which are just a object representation
 * of single elements of a gc.log line. This data can be used to display them graphically.
 * 
 * @author smeo
 * 
 */
public class GcEventParser {

	public GcSurvivorSpace parseSurvivorSpaceStartEntry(String gcLogLine) {
		// Desired survivor size 5242880 bytes, new threshold 15 (max 15)
		GcSurvivorSpace survivorSpace = null;
		String trimmedLine = gcLogLine.trim();
		if (trimmedLine.startsWith("Desired survivor")) {
			String elements[] = trimmedLine.split(" |]");
			survivorSpace.desiredSize = Integer.valueOf(elements[3]);
			survivorSpace.currTreshold = Integer.valueOf(elements[3]);
			survivorSpace.maxTreshold = Integer.valueOf(elements[3]);
		}
		return survivorSpace;
	}

	// [Times: user=0.03 sys=0.00, real=0.03 secs]
	public GcTiming parseGcTiming(String gcLogLine) {
		int startIndex = -1;
		int endIndex = -1;
		GcTiming gcTiming = null;

		if ((startIndex = gcLogLine.indexOf("[Times:")) > 0) {
			endIndex = gcLogLine.indexOf("]", startIndex);
			String timingInfo = gcLogLine.substring(startIndex, endIndex);
			String elements[] = timingInfo.split(" |=|,");
			// [[Times:, user, 0.03, sys, 0.00, , real, 0.03, secs]
			gcTiming = new GcTiming();
			gcTiming.userTimeInSec = Double.valueOf(elements[2].trim());
			gcTiming.sysTimeInSec = Double.valueOf(elements[4].trim());
			gcTiming.totalTimeInSec = Double.valueOf(elements[7].trim());
		}

		String timingData = null;
		if ((endIndex = gcLogLine.indexOf(" secs]")) > 0) {
			for (startIndex = endIndex - 1; startIndex > 0; startIndex--) {
				if (gcLogLine.charAt(startIndex) == ' ') {
					timingData = gcLogLine.substring(startIndex, endIndex);
					break;
				}
			}
			if (timingData != null) {
				if (gcTiming == null) {
					gcTiming = new GcTiming();
					gcTiming.sysTimeInSec = -1.0;
					gcTiming.userTimeInSec = -1.0;
					gcTiming.totalTimeInSec = -1.0;
				}
				gcTiming.totalTimeAccurate = Double.valueOf(timingData.trim());
			}

		}
		return gcTiming;
	}

	public GcType parseGcType(String gcLogLine) {
		for (GcType currType : GcType.values()) {
			if (gcLogLine.contains("[" + currType.getLogPrefix())) {
				return currType;
			}
		}
		return null;
	}

	public GcEvent parseTotalMemoryState(String gcLogLine) {
		Pattern totalMemoryPatter = Pattern.compile("\\] [0-9]+K->[0-9]+K\\([0-9]+K\\)");
		Matcher matcher = totalMemoryPatter.matcher(gcLogLine);
		if (matcher.find()) {
			String totalMemoryData = matcher.group();
			return createGcEvent(GcEventType.TOTAL, totalMemoryData.substring(1));
		}

		return null;
	}

	public List<GcEvent> parseGcEvents(String gcLogLine) {
		List<GcEvent> arrayList = new ArrayList<GcEventParser.GcEvent>();
		Pattern totalMemoryPatter = Pattern.compile("\\[[A-Z|a-z| ]+.[ ]*[0-9]+K->[0-9]+K\\([0-9]+K\\)");
		Matcher matcher = totalMemoryPatter.matcher(gcLogLine);
		while (matcher.find()) {
			GcEvent newGcEvent = createGcEvent(matcher.group());
			if (newGcEvent != null) {
				arrayList.add(newGcEvent);
			}
		}

		GcEvent totalMemoryEvent = parseTotalMemoryState(gcLogLine);
		if (totalMemoryEvent != null) {
			arrayList.add(totalMemoryEvent);
		}
		return arrayList;
	}

	private GcEvent createGcEvent(String eventData) {
		// [CMS Perm : 23964K->23951K(262144K)
		for (GcEventType currType : GcEventType.values()) {
			String logFilePrefix = currType.getLogFilePrefix();
			if (logFilePrefix != null) {
				if (eventData.contains(logFilePrefix)) {
					return createGcEvent(currType, eventData.split(":")[1]);
				}
			}
		}
		return null;
	}

	private GcEvent createGcEvent(GcEventType type, String gcEventData) {
		Integer[] numbers = extractKNumbers(gcEventData);
		MemorySpace before = new MemorySpace();
		MemorySpace after = new MemorySpace();

		before.setSpaceValues(numbers[0], numbers[2]);
		after.setSpaceValues(numbers[1], numbers[2]);

		return new GcEvent(type, before, after);
	}

	private Integer[] extractKNumbers(String data) {
		Pattern totalMemoryPatter = Pattern.compile("[0-9]+K");
		Matcher matcher = totalMemoryPatter.matcher(data);
		int filledNumbers = 0;
		if (matcher.find()) {
			Integer[] memNumbers = new Integer[3];
			memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
			if (matcher.find()) {
				memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
			}
			if (matcher.find()) {
				memNumbers[filledNumbers++] = extractIntFromKNumber(matcher.group());
			}

			if (filledNumbers == 3) {
				return memNumbers;
			} else {
				throw new IllegalArgumentException("could not extract 3 values from string: " + data);
			}
		}
		return null;
	}

	private Integer extractIntFromKNumber(String kbTextData) {
		if (kbTextData.endsWith("K")) {
			return Integer.valueOf(kbTextData.substring(0, kbTextData.length() - 1));
		}
		return null;
	}

	public class GcEvent {
		public GcEventType gcVersion;
		public MemorySpace before;
		public MemorySpace after;

		public GcEvent(GcEventType gcType, MemorySpace before, MemorySpace after) {
			super();
			this.gcVersion = gcType;
			this.before = before;
			this.after = after;
		}

		@Override
		public String toString() {
			return "GcEvent [gcType=" + gcVersion + ", before=" + before + ", after=" + after + "]";
		}

	}

	public enum GcType {
		// be careful... order is important here
		FULL_SYSTEM("Full GC (System)", true),
		FULL("Full GC", true),
		MINOR("GC", false);

		private final String logPrefix;
		private boolean isMajor;

		private GcType(String logPrefix, boolean isMajor) {
			this.logPrefix = logPrefix;
			this.isMajor = isMajor;
		}

		public String getLogPrefix() {
			return logPrefix;
		}

		public boolean isMajor() {
			return isMajor;
		}

	}

	public enum GcMemory {
		PermGen,
        OldGen,
        YoungGen,
        Eden,
        Survivor,
		TotalHeap
	}

	public enum GcEventType {
		TOTAL(GcMemory.TotalHeap, null),
        DefNew(GcMemory.Eden, "DefNew"),
		PsYoungGen(GcMemory.Eden, "PSYoungGen"),
		PSOldGen(GcMemory.OldGen, "PSOldGen"),
		PSPermGen(GcMemory.PermGen, "PSPermGen"),
		Tenured(GcMemory.OldGen, "OldGen:"),
		CMSPerm(GcMemory.PermGen, "CMS Perm :"),
		Perm(GcMemory.PermGen, "Perm :"),
		CMS(GcMemory.Eden, "CMS");

		private final GcMemory memoryType;
		private final String logFilePrefix;

		private GcEventType(final GcMemory memoryType, final String logFilePrefix) {
			this.memoryType = memoryType;
			this.logFilePrefix = logFilePrefix;
		}

		public GcMemory getMemoryType() {
			return memoryType;
		}

		public String getLogFilePrefix() {
			return logFilePrefix;
		}

		public boolean isStopTheWorld() {
			// TODO Auto-generated method stub
			return false;
		}

	}
}
