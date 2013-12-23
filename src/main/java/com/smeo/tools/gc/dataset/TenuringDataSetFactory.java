package com.smeo.tools.gc.dataset;

import com.smeo.tools.common.DataSetEntry;
import com.smeo.tools.gc.domain.GarbageCollectionEvent;
import com.smeo.tools.gc.domain.Tenuring;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: truehl
 * Date: 12/23/13
 * Time: 9:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class TenuringDataSetFactory {
    public static TenuringDataSet createDataSet(List<GarbageCollectionEvent> allGarbageCollectionEvents) {
        TenuringDataSet survivorInputOutput = new TenuringDataSet();

        for (GarbageCollectionEvent currCollectionEvent : allGarbageCollectionEvents) {
            Tenuring currTenuring = currCollectionEvent.tenuring;
            if (currTenuring != null) {
                long currEventTime = currCollectionEvent.time.getTime();

                survivorInputOutput.desiredSurvivorSize.add(new DataSetEntry(currEventTime, currTenuring.desiredSurvivorSpace/1024));
                survivorInputOutput.maxAges.add(new DataSetEntry(currEventTime, currTenuring.max));
                survivorInputOutput.newThreshold.add(new DataSetEntry(currEventTime, currTenuring.newThreshold));
                survivorInputOutput.sumTotal.add(new DataSetEntry(currEventTime, currTenuring.getSumOfTotals()/1024));
                survivorInputOutput.sumUsed.add(new DataSetEntry(currEventTime, currTenuring.getSumOfUsed()/1024));

                for (int i=0; i< Tenuring.MAX_AGE; i++){
                    survivorInputOutput.agesTotal[i].add(new DataSetEntry(currEventTime, (currTenuring.totalSpace[i]/1024)));
                }
            }
        }
        return survivorInputOutput;
    }

    public static class TenuringDataSet {
        public List<DataSetEntry> desiredSurvivorSize = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> maxAges = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> newThreshold = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> sumTotal = new ArrayList<DataSetEntry>();
        public List<DataSetEntry> sumUsed = new ArrayList<DataSetEntry>();

        public List<DataSetEntry>[] agesTotal;

        public TenuringDataSet(){
            init();
        }

        private void init() {
            agesTotal = new List[15];
            for (int i = 0; i < agesTotal.length; i++){
                agesTotal[i] = new ArrayList<DataSetEntry>();
            }
        }
    }
}
