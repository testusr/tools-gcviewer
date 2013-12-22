package com.smeo.tools.gc.domain;

public class GcTiming {
	public double totalTimeAccurate;
	public double totalTimeInSec;
	public double userTimeInSec;
	public double sysTimeInSec;

	public Double getMostAccurateTotalTime() {
		if (totalTimeAccurate > 0) {
			return totalTimeAccurate;
		}
		if (totalTimeInSec > 0) {
			return totalTimeInSec;
		}
		return null;
	}

	public Long getMostAccurateTotalTimeInMs() {
		if (getMostAccurateTotalTime() != null) {
			return (long) (getMostAccurateTotalTime() * 1000.0);
		}
		return null;
	}

	@Override
	public String toString() {
		return "GcTiming [totalTimeInSec=" + totalTimeInSec + ", userTimeInSec=" + userTimeInSec + ", sysTimeInSec=" + sysTimeInSec + "]";
	}

}
