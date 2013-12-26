
Anotomy GcLog Events

Timestamp (entry1)
 Application Stop Events
 Application Run time event
 HeapState events
 Tenuring information
 Collection events
   Major
   Minor Collection
Timestamp (entry2)

TimeStamp 
  format:
    2013-10-23T16:25:00.610+0000: 3.642: <event data> (with -XX:+PrintGCDateStamps)
  or
    3.642: <eventData>

 First part only exists if  -XX:+PrintGCDateStamps is set. 
 Second part is the relative runtime from VM start in Ms


Application Stop Time Events (-XX:+PrintGCApplicationStoppedTime): 
    format:
      Total time for which application threads were stopped: 0.0032960 seconds  	<-- stop time
      Application time: 0.3531030 seconds  					 	<-- runtime
      
    The can appear between every line. 
    
    
Heap state events:
 {Heap before GC invocations=235 (full 3):
 PSYoungGen      total 98304K, used 97792K [0x00000000f9c00000, 0x0000000100000000, 0x0000000100000000)
  eden space 94208K, 100% used [0x00000000f9c00000,0x00000000ff800000,0x00000000ff800000)
  from space 4096K, 87% used [0x00000000ff800000,0x00000000ffb80000,0x00000000ffc00000)
  to   space 4096K, 0% used [0x00000000ffc00000,0x00000000ffc00000,0x0000000100000000)
 ParOldGen       total 204800K, used 15056K [0x00000000ed400000, 0x00000000f9c00000, 0x00000000f9c00000)
  object space 204800K, 7% used [0x00000000ed400000,0x00000000ee2b4328,0x00000000f9c00000)
 PSPermGen       total 21504K, used 2882K [0x00000000e8200000, 0x00000000e9700000, 0x00000000ed400000)
  object space 21504K, 13% used [0x00000000e8200000,0x00000000e84d0bf0,0x00000000e9700000)

 ... other events 

 Heap after GC invocations=235 (full 3):
 PSYoungGen      total 98304K, used 3552K [0x00000000f9c00000, 0x0000000100000000, 0x0000000100000000)
  eden space 94208K, 0% used [0x00000000f9c00000,0x00000000f9c00000,0x00000000ff800000)
  from space 4096K, 86% used [0x00000000ffc00000,0x00000000fff78000,0x0000000100000000)
  to   space 4096K, 0% used [0x00000000ff800000,0x00000000ff800000,0x00000000ffc00000)
 ParOldGen       total 204800K, used 17776K [0x00000000ed400000, 0x00000000f9c00000, 0x00000000f9c00000)
  object space 204800K, 8% used [0x00000000ed400000,0x00000000ee55c328,0x00000000f9c00000)
 PSPermGen       total 21504K, used 2882K [0x00000000e8200000, 0x00000000e9700000, 0x00000000ed400000)
  object space 21504K, 13% used [0x00000000e8200000,0x00000000e84d0bf0,0x00000000e9700000)
 }

 The only events i have seen so far using curly braces 

Collections:
 Minor collection 
 2013-12-24T12:23:23.080+0100: 136.276: [GC
  ...
 [PSYoungGen: 97824K->3584K(98304K)] 157336K->65880K(303104K), 0.0031030 secs] [Times: user=0.01 sys=0.00, real=0.00 secs] 

 Major collection
   2013-12-24T12:21:51.878+0100: 45.074: [Full GC [PSYoungGen: 3552K->0K(98304K)] [ParOldGen: 203686K->6794K(204800K)] 207238K->6794K(303104K) [PSPermGen: 2883K->2882K(21504K)], 0.0123180 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 

 The indicator of a collection information block is allways "[<type> where type can be "GC|Full GC|Full GC(system)"
 Then the single collector information. On a major collection this contains two collections the major and the minor collection. 
 After the two collector information a overall heap information.
 
 Collector information 
  [<collectory> <spaceInfoSet>] bsp: 
    spaceInfoSet: <usedSpaceBeforCollection>K-><usedSpaceAfter>K(<totalAvailableSpace>K)
      bsp: 97792K->3584K(98304K)
  
   <type>          <younGenCollector>           <oldGenCollector>                 <totalSpace>                  <PermGenCollector>      <collectionTime>              <timeInfo>
 [Full GC [PSYoungGen: 3552K->0K(98304K)] [ParOldGen: 203686K->6794K(204800K)] 207238K->6794K(303104K) [PSPermGen: 2883K->2882K(21504K)], 0.0123180 secs] [Times: user=0.03 sys=0.00, real=0.01 secs] 

 
 -XX:+UseParallelGC  -XX:MaxTenuringThreshold=15 -XX:InitialTenuringThreshold=15  -XX:+PrintGCApplicationStoppedTime  -XX:+PrintGCDetails -XX:+PrintGCDateStamps -XX:-PrintAdaptiveSizePolicy -XX:+PrintTenuringDistribution -XX:+PrintHeapAtGC -Xloggc:/tmp/gc.log -Xms300m -Xmx300m


Interesting numbers:
    Allocation Rate: the size of the young generation divided by the time between young generation collections

    The Promotion Rate: the change in usage of the old gen over time (excluding collections)

    The Survivor Death Ratio: when looking at a log, the size of survivors in age N divided by the size of survivors in age N-1 in the previous collection

    Old Gen collection times: the total time between a CMS-initial-mark and the next CMS-concurrent-reset. You'll want both your 'normal' and the maximum observed

    Young Gen collection times: both normal and maximum. These are just the "total collection time" entries in the logs

    Old Gen Buffer: the promotion rate * the maximum Old Gen collection time * (1 + a little bit)

