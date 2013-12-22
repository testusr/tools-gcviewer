package com.smeo.tools.gc.domain;

public class GcSurvivorSpace {
	public Integer desiredSize;
	public Integer currTreshold;
	public Integer maxTreshold;

	public Integer sizeInAges[];

	public void setCurrTresholdAndInitSizeInAges(Integer currTreshold) {
		this.currTreshold = currTreshold;
		sizeInAges = new Integer[currTreshold];
	}
}
