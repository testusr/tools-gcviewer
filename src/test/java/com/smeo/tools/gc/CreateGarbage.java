package com.smeo.tools.gc;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/22/13
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class CreateGarbage {
    private ArrayList<GarbageCreator> garbageCreators = new ArrayList<GarbageCreator>();

    public void addGarbageCreator(double objectLiveTimeMs, long bytesPerSecond) {
        addTempGarbageCreator(0, 0, objectLiveTimeMs, bytesPerSecond);
    }

    private void addTempGarbageCreator(long startTime, int runTime, double objectLiveTimeMs, long bytesPerSecond) {
        garbageCreators.add(new GarbageCreator(garbageCreators.size(), startTime, runTime, objectLiveTimeMs, bytesPerSecond));
    }

    public void run() {
        while (true) {
            for (GarbageCreator currGarbageCreator : garbageCreators) {
                currGarbageCreator.update();
            }
        }
    }


    private static class GarbageCreator {
        long objectLiveTimeNs;
        int bytesPerCycle;

        ArrayList<MemoryBlock> memoryBlockList = new ArrayList<MemoryBlock>(1000);

        long nextObjectCreationCycleNanoTime = -1;
        long objectCreationFrequencyInNs = 1;
        long startTime;
        long endTime = Long.MAX_VALUE;

        boolean active = false;
        int id;

        private GarbageCreator(int id, long startTime, long runtime, double objectLiveTimeMs, long bytesPerSecond) {
            this.objectCreationFrequencyInNs = TimeUnit.SECONDS.toNanos(1) / (bytesPerSecond / 2048);
            this.nextObjectCreationCycleNanoTime = System.nanoTime();
            this.objectLiveTimeNs = (long) (objectLiveTimeMs * 1000000);
            this.bytesPerCycle = 1024;
            this.id = id;

            this.startTime = System.currentTimeMillis() + startTime;
            if (runtime > 0) {
                this.endTime = this.startTime + runtime;
            }

            System.out.println("[" + id + "] cycle: " + objectCreationFrequencyInNs + "ns bytes: " + bytesPerCycle);
        }

        public void update() {
            if (System.currentTimeMillis() > startTime && System.currentTimeMillis() < endTime) {
                if (!active) {
                    System.out.println("[" + id + "] getting active");
                    active = true;
                }
                createObjectsIfNecessary();
            }
            else {
                if (active){
                System.out.println("[" + id + "] getting inactive");
                }
                active = false;
            }
            cleanExpiredObjects();
        }

        private void createObjectsIfNecessary() {
            if (System.nanoTime() >= nextObjectCreationCycleNanoTime) {
                memoryBlockList.add(new MemoryBlock(System.nanoTime() + objectLiveTimeNs, bytesPerCycle));
                this.nextObjectCreationCycleNanoTime += objectCreationFrequencyInNs;
            }
        }

        private void cleanExpiredObjects() {
            for (int i = (memoryBlockList.size() - 1); i > 0; i--) {
                if (System.nanoTime() >= memoryBlockList.get(i).expirationTimeInNs) {
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
                for (int i = 0; i < bytesPerCycle; i++) {
                    content[i] = 2;
                }
            }
        }
    }

    public static void main(String[] args) {
        CreateGarbage garbageCreator = new CreateGarbage();
        garbageCreator.addTempGarbageCreator(0, 20000, 10, (1024 * 1024 * 1024));
        garbageCreator.addTempGarbageCreator(5000, 15000, 100, (1024 * 1024 * 1024));
        garbageCreator.addTempGarbageCreator(10000, 10000, 150, (1024 * 1024 * 1024));

        garbageCreator.addTempGarbageCreator(30000, 30000, 2000, (1024 * 1024 * 10));
        garbageCreator.addTempGarbageCreator(30000, 30000, 10, (1024 * 1024 * 1024));
        garbageCreator.addTempGarbageCreator(35000, 15000, 100, (1024 * 1024 * 1024));
        garbageCreator.addTempGarbageCreator(40000, 10000, 150, (1024 * 1024 * 1024));

        garbageCreator.addTempGarbageCreator(50000, 50000, 10, (1024 * 1024 * 1024));



        garbageCreator.run();
    }


}
