package com.smeo.tools.gc;

import java.util.ArrayList;

/**
 * Just an attempt to write an application simulating a certain memory situation
 * User: smeo
 * Date: 12/22/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateGarbage {
    private ArrayList<GarbageCreator> garbageCreators = new ArrayList<GarbageCreator>();

    public void addGarbageCreator(GarbageCreator garbageCreator) {
        garbageCreators.add(garbageCreator);
    }

    public void run() {
        while (true) {
            for (GarbageCreator currGarbageCreator : garbageCreators) {
                currGarbageCreator.update();
            }
        }
    }



    private static class GarbageCreator {
        private static int noOfInstances = 0;
        private final long bytesAtEndOfRuntime;
        private final long objLiveTimeMs;
        private long currBytes = 0;
        final long startTimeStamp;
        final long endTimeStamp;

        int minBytesPerCycle;
        double bytesPerMs;


        ArrayList<MemoryBlock> memoryBlockList = new ArrayList<MemoryBlock>(1000);


        boolean done = false;
        int id;
        private final long runtime;


        public static GarbageCreator createWithBytesPerSecond(long startTime, long runtimeMs, long objectLiveTimeMs, long bytesPerSecond) {
            long bytesAtEndOfRuntime = (long) ((runtimeMs / 1000.0) * bytesPerSecond);
            return new GarbageCreator(noOfInstances++, bytesAtEndOfRuntime, objectLiveTimeMs, startTime, runtimeMs, 1024);
        }

        public static GarbageCreator createWithTotalBytes(long startTime, long runtimeMs, long objectLiveTimeMs, long totalBytes) {
            return new GarbageCreator(noOfInstances++,totalBytes, objectLiveTimeMs, startTime, runtimeMs, 1024);
        }

        public static String humanReadableByteCount(long bytes, boolean si) {
            int unit = si ? 1000 : 1024;
            if (bytes < unit) return bytes + " B";
            int exp = (int) (Math.log(bytes) / Math.log(unit));
            String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
            return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
        }

        private GarbageCreator(int id, long bytesAtEndOfRuntime, long objLiveTimeMs, long relativeStartTimeInMs, long runtime, int minBytesPerCycle) {
            this.id = id;
            this.bytesAtEndOfRuntime = bytesAtEndOfRuntime;
            this.objLiveTimeMs = objLiveTimeMs;
            this.startTimeStamp = System.currentTimeMillis() + relativeStartTimeInMs;
            this.runtime = runtime;
            this.minBytesPerCycle = minBytesPerCycle;

            this.bytesPerMs = bytesAtEndOfRuntime / runtime;

            if (runtime > 0) {
                this.endTimeStamp = this.startTimeStamp + runtime;
            } else {
                endTimeStamp = Long.MAX_VALUE;
            }
            System.out.printf("[%d] created runtime[%d ms] totalBytes [%s] bytesPerSecond [%s] objLivetime[%d ms]\n",
                    id,
                    runtime,
                    humanReadableByteCount(bytesAtEndOfRuntime,true),
                    humanReadableByteCount((long)(bytesPerMs * 1000), true),
                    objLiveTimeMs);
        }

        public void update() {
            createObjectsIfNecessary();
            cleanExpiredObjects();
        }

        private void createObjectsIfNecessary() {
            if (!done && System.currentTimeMillis() > startTimeStamp) {
                int bytesToCreate = calcBytesToCreate();
                if (bytesToCreate > 0) {
                    memoryBlockList.add(new MemoryBlock((System.currentTimeMillis() + objLiveTimeMs), bytesToCreate));
//                    System.out.println("create memory[" + memoryBlockList.size() + "] lt: " + objLiveTimeMs + " bytes: " + bytesToCreate +
//                            " currBytes: " + currBytes + " /" + bytesAtEndOfRuntime);
                    currBytes += bytesToCreate;
                }
            }
            if (currBytes >= bytesAtEndOfRuntime) {
                done = true;
            }
        }

        private int calcBytesToCreate() {
            long bytesLeft = bytesAtEndOfRuntime - currBytes;
            if (bytesLeft < minBytesPerCycle) {
                minBytesPerCycle = 0;
            }

            long passedRuntime = System.currentTimeMillis() - startTimeStamp;
            long shouldCreateByTime = (long) ((passedRuntime * bytesPerMs) - currBytes);
            if (shouldCreateByTime > minBytesPerCycle) {
                return (int) shouldCreateByTime;
            }

            return 0;

        }

        private void cleanExpiredObjects() {
            for (int i = (memoryBlockList.size() - 1); i > 0; i--) {
                if (System.currentTimeMillis() >= memoryBlockList.get(i).expirationTimeInNs) {
                    memoryBlockList.remove(i);
                }
            }
        }

        private static class MemoryBlock {
            long expirationTimeInNs;
            byte[] content;

            public MemoryBlock(long expirationTimeInNs, int bytesPerCycle) {
                this.expirationTimeInNs = expirationTimeInNs;
                content = new byte[bytesPerCycle];
            }
        }
    }

    public static void main(String[] args) {
        CreateGarbage garbageCreator = new CreateGarbage();
        garbageCreator.addGarbageCreator(GarbageCreator.createWithTotalBytes(0, 10000, 30000, (1024*1024*10)));
        garbageCreator.addGarbageCreator(GarbageCreator.createWithBytesPerSecond(5000, 30000, 10, (1024*1024*200)));

        garbageCreator.run();
    }


}
