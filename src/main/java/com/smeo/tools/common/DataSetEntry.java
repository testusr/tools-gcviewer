package com.smeo.tools.common;

import java.math.BigDecimal;

public class DataSetEntry {
    public DataSetEntry(long time, float value) {
        this.time = time;
        this.value = getBigDecimal("" + value);
    }

    public DataSetEntry(long time, double value) {
        this.time = time;
        this.value = getBigDecimal("" + value);
    }

    private BigDecimal getBigDecimal(String s) {
        try {
            return new BigDecimal(s);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public long time;
    public BigDecimal value;
}
