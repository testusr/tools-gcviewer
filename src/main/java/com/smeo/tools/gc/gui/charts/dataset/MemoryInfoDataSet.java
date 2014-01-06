package com.smeo.tools.gc.gui.charts.dataset;

import com.smeo.tools.common.DataSetEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by smeo on 1/6/14.
 */
public class MemoryInfoDataSet {
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
