package com.smeo.tools.common;

import java.math.BigDecimal;

public class DataSetEntry {
	public DataSetEntry(long time, float stopTimeInPercent) {
		this.time = time;
		this.value = new BigDecimal("" + stopTimeInPercent);
	}

	public long time;
	public BigDecimal value;
}
