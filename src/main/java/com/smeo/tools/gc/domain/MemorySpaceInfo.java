package com.smeo.tools.gc.domain;

/**
 * Created by joachim on 25.12.13.
 */
public class MemorySpaceInfo {
    public static final MemorySpaceInfo UNDEFINED = new MemorySpaceInfo(-1,-1,0.0f);
    private final int size;
    private final int used;
    private final float usedPerc;

    public MemorySpaceInfo(int size, int used, float usedPerc) {
        this.size = size;
        this.used = used;
        this.usedPerc = usedPerc;
    }

    public static MemorySpaceInfo createFromPerc(int size, float usedPerc){
        return new MemorySpaceInfo(size, (int) ((size / 100.0)*usedPerc), usedPerc);
    }

    public static MemorySpaceInfo createFromSize(int size, int used){
        return new MemorySpaceInfo(size, used, ((used/(float)size)*100.0f));
    }

    public int getSize() {
        return size;
    }

    public int getUsed() {
        return used;
    }

    public float getUsedPerc() {
        return usedPerc;
    }
}
