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

    public void addGarbageCreator(double objectLiveTimeMs, long bytesPerSecond){
        garbageCreators.add(new GarbageCreator(objectLiveTimeMs, bytesPerSecond, bytesPerSecond/1024));
    }

    public void run(){
        while (true){
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

        private GarbageCreator(double objectLiveTimeMs, long bytesPerSecond, long objectCreationFrequencyInNs) {
            this.objectCreationFrequencyInNs = objectCreationFrequencyInNs;
            this.nextObjectCreationCycleNanoTime = System.nanoTime();
            this.objectLiveTimeNs = (long)(objectLiveTimeMs * 1000000);
            this.bytesPerCycle = (int)((bytesPerSecond / 1000000.0) * objectCreationFrequencyInNs);

            System.out.println("cycle: " + objectCreationFrequencyInNs + "ns bytes: " + bytesPerCycle);
        }

        public void update(){
            cleanExpiredObjects();
            createObjectsIfNecessary();
        }

        private void createObjectsIfNecessary() {
            if (System.nanoTime() >= nextObjectCreationCycleNanoTime){
                memoryBlockList.add(new MemoryBlock(System.nanoTime() + objectLiveTimeNs, bytesPerCycle));
            }
        }

        private void cleanExpiredObjects() {
            for (int i=(memoryBlockList.size()-1); i > 0 ; i--){
                if (System.nanoTime() >=  memoryBlockList.get(i).expirationTimeInNs){
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
                for (int i = 0; i < bytesPerCycle; i++){
                    content[i] = 2;
                }
            }
        }
    }

    public static void main(String[] args) {
        CreateGarbage garbageCreator = new CreateGarbage();
        garbageCreator.addGarbageCreator(1000, (1024 * 1024));
        garbageCreator.addGarbageCreator(0.001, (1024 * 1024));
        garbageCreator.addGarbageCreator(0.01, (1024 * 1024));
        garbageCreator.addGarbageCreator(1, (1024 * 1024));
        garbageCreator.run();
    }

}
