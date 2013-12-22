package com.smeo.tools.gc.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GarbageCollectionEvent {
	public Date time;
	public boolean isStopTheWorld;
	public HeapState heapBeforeGC;
	public HeapState heapAfterGC;
	public Tenuring tenuring;
	public GcTiming gcTiming;

	public List<ApplicationStopTimeEvent> followingApplicationStopTimes = new ArrayList<ApplicationStopTimeEvent>();

	public boolean isFullGc() {
		return (heapBeforeGC.fullGcCount < heapAfterGC.fullGcCount);
	}

	@Override
	public String toString() {
		return "GarbageCollectionEvent [time=" + time + ", isEnforcedGc=" + isStopTheWorld + ", heapBeforeGC=" + heapBeforeGC + ", heapAfterGC=" + heapAfterGC
				+ ", followingApplicationStopTimes=" + followingApplicationStopTimes + "]";
	}
}
