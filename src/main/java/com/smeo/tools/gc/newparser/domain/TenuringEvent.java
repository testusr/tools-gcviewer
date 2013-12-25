package com.smeo.tools.gc.newparser.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class TenuringEvent  extends GcLoggedEvent {
    public static int MAX_AGE = 15;
    public int newThreshold;
    public int max;

    public int desiredSurvivorSpace;
    public int[] usedSpace = createEmptyAges();
    public int[] totalSpace = createEmptyAges();

    public TenuringEvent(int newThreshold, int max, int desiredSurvivorSpace, int[] usedSpace, int[] totalSpace) {
        this.newThreshold = newThreshold;
        this.max = max;
        this.desiredSurvivorSpace = desiredSurvivorSpace;
        this.usedSpace = usedSpace;
        this.totalSpace = totalSpace;
    }

    private static int[] createEmptyAges() {
        int[] ages = new int[MAX_AGE];
        for (int i = 0; i < MAX_AGE; i++) {
            ages[i] = 0;
        }
        return ages;
    }

    public int getSumOfTotals(){
        int sum = 0;
        for (int i = 0; i < MAX_AGE; i++){
            sum += totalSpace[i];
        }
        return sum;
    }
    public int getSumOfUsed(){
        int sum = 0;
        for (int i = 0; i < MAX_AGE; i++){
            sum += usedSpace[i];
        }
        return sum;
    }

    public Integer getYoungGenPromotion() {
        return usedSpace[0];
    }

    public Integer getOldGenPromotion() {
        return usedSpace[MAX_AGE-1];
    }

    @Override
    public String toString() {
        return "Tenuring [max=" + max + ", usedSpace=" + usedSpace + "]";
    }

    public int getTotalSpace(int age) {
        return this.totalSpace[age-1];
    }

    public int getUsedSpace(int age) {
        return usedSpace[age-1];
    }
}
