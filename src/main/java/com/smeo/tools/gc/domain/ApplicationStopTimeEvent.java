package com.smeo.tools.gc.domain;

public class ApplicationStopTimeEvent {
	public float totalTimeApplicationsWhereStoppedInSec = -1f;
	public float totalTimeApplicationsWhereRunningInSec = -1f;

	public float getStopTimeInPercent() {
		return (totalTimeApplicationsWhereStoppedInSec / getTotalTime()) * 100.0f;
	}

	public float getTotalTime() {
		return totalTimeApplicationsWhereStoppedInSec + totalTimeApplicationsWhereRunningInSec;
	}

	public boolean isFilled() {
		return (totalTimeApplicationsWhereStoppedInSec > 0 && totalTimeApplicationsWhereRunningInSec > 0);
	}

	@Override
	public String toString() {
		return "ApplicationStopTimeEvent [totalTimeApplicationsWhereStopped=" + totalTimeApplicationsWhereStoppedInSec + ", applicationTime="
				+ totalTimeApplicationsWhereRunningInSec
				+ "]";
	}
}
