package com.smeo.tools.gc.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: smeo
 * Date: 12/26/13
 * Time: 5:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractMemoryDataSetFactory {

    public static class MemoryInfoDataSet {
        public List<DataSetEntry> availableSpaceInK;
        public List<DataSetEntry> usedSpaceInK;
        public List<DataSetEntry> incomingDataInK;
        public List<DataSetEntry> spaceFreeInK;

        MemoryInfoDataSet(){
            availableSpaceInK = new ArrayList<DataSetEntry>();
            usedSpaceInK = new ArrayList<DataSetEntry>();
            incomingDataInK = new ArrayList<DataSetEntry>();
            spaceFreeInK = new ArrayList<DataSetEntry>();
        }
    }
}
