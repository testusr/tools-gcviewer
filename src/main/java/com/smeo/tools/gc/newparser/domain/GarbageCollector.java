package com.smeo.tools.gc.newparser.domain;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/24/13
 * Time: 2:16 PM
 * To change this template use File | Settings | File Templates.
 */
public enum GarbageCollector {
    ParNew(MemorySegment.YoungGen, "ParNew"),
    DefNew(MemorySegment.YoungGen, "DefNew"),
    PsYoungGen(MemorySegment.YoungGen,"PSYoungGen"),
    PSOldGen(MemorySegment.OldGen, "PSOldGen"),
    ParOldGen(MemorySegment.OldGen, "ParOldGen"),
    PSPermGen(MemorySegment.PermGen, "PSPermGen"),
    Tenured(MemorySegment.OldGen, "Tenured"),
    CMSTenured(MemorySegment.OldGen, "OldGen"),
    CMSPerm(MemorySegment.PermGen, "CMS Perm "),
    Perm(MemorySegment.PermGen, "Perm "),
    CMS(MemorySegment.OldGen, "CMS");

    private final MemorySegment memorySegment;
    private final String logFilePrefix;

    private GarbageCollector(final MemorySegment memorySegment, final String logFilePrefix) {
        this.memorySegment = memorySegment;
        this.logFilePrefix = logFilePrefix;
    }


    public String getLogFilePrefix() {
        return logFilePrefix;
    }

    public MemorySegment getMemorySegment() {
        return memorySegment;
    }

    public static GarbageCollector fromString(String string) {
        for (GarbageCollector currGc : values()){
            if (string.contains(currGc.getLogFilePrefix())){
                return currGc;
            }
        }
        throw new IllegalArgumentException("could not creat GarbageCollector from string '"+ string + "'");
    }
}
