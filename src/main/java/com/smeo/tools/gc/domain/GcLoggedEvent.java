package com.smeo.tools.gc.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class GcLoggedEvent implements Comparable<GcLoggedEvent> {
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }


    @Override
    public int compareTo(GcLoggedEvent o) {
        if (timestamp < o.getTimestamp()){
            return -1;
        }
        return 1;
    }
}
