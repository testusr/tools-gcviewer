package com.smeo.tools.gc.domain;

import java.math.BigDecimal;

/**
 * Created by joachim on 25.12.13.
 */
public class MemorySpace {
    public static final MemorySpace UNDEFINED = new MemorySpace();
    public int availableSpace;  // total available space
    public int usedSpace;
    public Float usedPercentage;




    public MemorySpace(){};
    public MemorySpace(Integer usedSpace, Integer availabeSpace) {
        setSpaceValues(usedSpace, availabeSpace);
    }

    public float getUsedSpaceInPercentage() {
        if (usedPercentage != null){
            return usedPercentage;
        }
        return -1;
    }

    public boolean isFilled() {
       return true;
    }

    public void setSpaceValues(int usedInK, int totalSpaceInK) {
        this.availableSpace = totalSpaceInK;
        this.usedSpace = usedInK;

        this.usedPercentage = ((float) usedSpace) / (availableSpace / 100.0f);
        if (usedSpace > 0 && totalSpaceInK > 0) {
            this.usedPercentage = new BigDecimal(usedPercentage).setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
        } else {
            totalSpaceInK = 0;
        }
    }

    public void setSpaceValuesWithPercentage(int totalSpace, float usedPercentage) {
        this.availableSpace = totalSpace;
        this.usedPercentage = usedPercentage;

        this.usedSpace = new BigDecimal((totalSpace / 100.0f) * (float) usedPercentage).setScale(2, BigDecimal.ROUND_HALF_UP).intValue();
    }

    @Override
    public String toString() {
        return "MemorySpace [availableSpace=" + availableSpace + ", usedSpace=" + getUsedSpaceInK() + " usedSpaceInPercentage=" + getUsedSpaceInPercentage() + "]";
    }

    public int getAvailableSpaceInK() {
        return availableSpace;
    }

    public int getUsedSpaceInK() {
        return usedSpace;
    }

}
