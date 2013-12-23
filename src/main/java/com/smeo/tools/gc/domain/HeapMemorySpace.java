package com.smeo.tools.gc.domain;

import java.math.BigDecimal;

public class HeapMemorySpace {
	public MemorySpace totalMemSpace = null;
	public MemorySpace edenSpace = null;
	public MemorySpace survivorFromSpace = null;
	public MemorySpace survivorToSpace = null;

	public MemorySpace oldGenSpace = null;
	public MemorySpace permGenSpace = null;

	public boolean isFilled = false;

	public boolean isFilled() {
		return isFilled;
	}

	@Override
	public String toString() {
		return "HeapMemorySpace [totalMemSpace=" + totalMemSpace + ", edenSpace=" + edenSpace + ", survivorFromSpace=" + survivorFromSpace
				+ ", survivorToSpace=" + survivorToSpace + ", oldGenSpace=" + oldGenSpace + ", permGenSpace=" + permGenSpace + ", isFilled=" + isFilled + "]";
	}

	public static class MemorySpace {
        public static final MemorySpace UNDEFINED = new MemorySpace();
        public Integer totalSpace;  // total available space
		public Integer usedSpace;
		public Float usedPercentage;

		public Integer getUsedSpaceInK() {
			return usedSpace;
		}

		public float getUsedSpaceInPercentage() {
            if (usedPercentage != null){
			return usedPercentage;
            }
            return -1;
		}

		public boolean isFilled() {
			return (totalSpace != null && usedSpace != null && usedPercentage != null);
		}

		public void setSpaceValues(int usedInK, int totalSpaceInK) {
			this.totalSpace = totalSpaceInK;
			this.usedSpace = usedInK;

			this.usedPercentage = ((float) usedSpace) / (totalSpace / 100.0f);
			if (usedSpace > 0 && totalSpaceInK > 0) {
				this.usedPercentage = new BigDecimal(usedPercentage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
			} else {
				totalSpaceInK = 0;
			}
		}

		public void setSpaceValuesWithPercentage(int totalSpace, float usedPercentage) {
			this.totalSpace = totalSpace;
			this.usedPercentage = usedPercentage;

			this.usedSpace = new BigDecimal((totalSpace / 100.0f) * (float) usedPercentage).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
		}

		@Override
		public String toString() {
			return "MemorySpace [totalSpace=" + totalSpace + ", usedSpace=" + getUsedSpaceInK() + " usedSpaceInPercentage=" + getUsedSpaceInPercentage() + "]";
		}

	}
}
