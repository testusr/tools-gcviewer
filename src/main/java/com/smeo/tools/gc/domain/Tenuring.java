package com.smeo.tools.gc.domain;

import java.util.ArrayList;
import java.util.List;

public class Tenuring {
    public int newThreshold;
    public int max;

    public int desiredSurvivorSpace;
    public int[] usedSpace = createEmptyAges();
    public int[] totalSpace = createEmptyAges();

    private static int[] createEmptyAges() {
        int[] ages = new int[15];
        for (int i = 0; i < 15; i++) {
            ages[i] = 0;
        }
        return ages;
    }

    public Integer getYoungGenPromotion() {
        return usedSpace[0];
    }

    public Integer getOldGenPromotion() {
        return usedSpace[14];
    }

    @Override
    public String toString() {
        return "Tenuring [max=" + max + ", usedSpace=" + usedSpace + "]";
    }
}
