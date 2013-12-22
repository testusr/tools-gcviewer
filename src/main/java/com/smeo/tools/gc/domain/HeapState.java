package com.smeo.tools.gc.domain;

import com.smeo.tools.gc.domain.HeapMemorySpace.MemorySpace;

public class HeapState {
	public int fullGcCount = -1;
	public int minorGcCount = -1;
	public HeapMemorySpace memorySpace = new HeapMemorySpace();

	public boolean isFilled() {
		return (memorySpace.isFilled());
	}

	public void setEdenSpace(MemorySpace edenSpace) {
		memorySpace.edenSpace = edenSpace;
	}

	public MemorySpace getEdenSpace() {
		return memorySpace.edenSpace;
	}

	public MemorySpace getUsedSurvivorSpace() {
		if (memorySpace.survivorFromSpace.getUsedSpaceInPercentage() > 0) {
			return memorySpace.survivorFromSpace;
		} else {
			return memorySpace.survivorToSpace;
		}
	}

	public void setOldGenSpace(MemorySpace oldGenSpace) {
		memorySpace.oldGenSpace = oldGenSpace;
	}

	public MemorySpace getOldGenSpace() {
		return memorySpace.oldGenSpace;
	}

	public MemorySpace getPermGenSpace() {
		return memorySpace.permGenSpace;
	}

	public void setPermGenSpace(MemorySpace permGenSpace) {
		memorySpace.permGenSpace = permGenSpace;
	}

	public void setTotalMemSpace(MemorySpace memSpace) {
		memorySpace.totalMemSpace = memSpace;
	}

	public MemorySpace getTotalMemSpace() {
		return memorySpace.totalMemSpace;
	}

	@Override
	public String toString() {
		return "HeapState [fullGcCount=" + fullGcCount + ", minorGcCount=" + minorGcCount + ", memorySpace=" + memorySpace + "]";
	}

}
