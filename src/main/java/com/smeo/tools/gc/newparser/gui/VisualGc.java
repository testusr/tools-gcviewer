package com.smeo.tools.gc.newparser.gui;

import com.smeo.tools.charts.ChartUtility;
import com.smeo.tools.charts.MovingDomainMarker;
import com.smeo.tools.charts.PlotChartFactory;

import com.smeo.tools.gc.domain.GarbageCollectionEvent;

import com.smeo.tools.gc.newparser.AllEventsParser;
import com.smeo.tools.gc.newparser.domain.GcLoggedEvent;
import com.smeo.tools.gc.newparser.gui.charts.GarbaceCollectionCountChartFactory;
import com.smeo.tools.gc.newparser.gui.charts.MemoryInfoDataSetPlotChartFactory;
import com.smeo.tools.gc.newparser.gui.charts.TenuringDataSetPlotChartFactory;
import com.smeo.tools.gc.newparser.gui.charts.dataset.*;
import com.smeo.tools.gc.parser.GcLogParser;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joachim on 26.12.13.
 */
public class VisualGc {
    AllEventsParser allEventsParser = new AllEventsParser();

    JPanel allTablesPanel = new JPanel();


    MovingDomainMarker movingDomainMarker;
    GcLogParser gcLogParser;
    MemoryDataSetFactory dataSetFactory = new MemoryDataSetFactory();
    SurvivorInputOutputDataSetFactory survivorInputOutputDataSetFactory = new SurvivorInputOutputDataSetFactory();
    TenuringDataSetPlotChartFactory tenuringDataSetPlotChartFactory = new TenuringDataSetPlotChartFactory();

    List<JFreeChart> charts = new ArrayList<JFreeChart>();
    String filename;
    long intervalInMs = 1000;

    public VisualGc(String filename){
        super();
        this.filename = filename;
    }

    public void readLogFile(){
        System.out.println("..start reading '"+filename+"'");
        BufferedReader reader;
        try {
            reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(filename)));

            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    allEventsParser.parseLine(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("..finished reading " + allEventsParser.getContentSummary());

    }


    public void createAndShowCharts() {
//        MemoryInfoDataSetPlotChartFactory memoryInfoDataSetPlotChart = new MemoryInfoDataSetPlotChartFactory();
//        List<GarbageCollectionEvent> allGarbageCollectionEvents = gcLogParser.getAllGarbageCollectionEvents();
//        int intervalInMs = 500;
//        try {
//            JFreeChart  totalMemoryChart = memoryInfoDataSetPlotChart.createChart(
//                    dataSetFactory.createTotalMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "TotalSpace", false, true, true);
//            addChart(totalMemoryChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            JFreeChart edenSpaceChart = memoryInfoDataSetPlotChart.createChart(
//                    dataSetFactory.createEdenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "EdenSpace", false, false, true);
//            addChart(edenSpaceChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            JFreeChart survivorSpaceChart = memoryInfoDataSetPlotChart.createChart(
//                    dataSetFactory.createUsedSurvivorMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "SurvivorSpace", true, false, false);
//            addChart(survivorSpaceChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        try {
//            JFreeChart oldGenSpaceChart = memoryInfoDataSetPlotChart.createChart(
//                    dataSetFactory.createOldGenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "OldGenSpace", true, true, true);
//            addChart(oldGenSpaceChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        try {
//            JFreeChart permGenSpaceChartTotal = memoryInfoDataSetPlotChart.createChart(
//                    dataSetFactory.createPermGenMemoryDataSets(allGarbageCollectionEvents, intervalInMs), "PermGenSpace(TotalSpace)", true, false, true);
//            addChart(permGenSpaceChartTotal);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//		try {
//			survivorSpaceFlowChart = SurvivorIncomingOutgoingChartFactory.createChart(survivorInputOutputDataSetFactory
//					.createDataSet(allGarbageCollectionEvents));
//			addChart(survivorSpaceChart);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//        try {
//            JFreeChart tenuringChart = TenuringDataSetPlotChartFactory.createTotalAgeDistributionChart(TenuringDataSetFactory
//                    .createDataSet(allGarbageCollectionEvents));
//            addChart(tenuringChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//        try {
//            JFreeChart tenuringChart2 = TenuringDataSetPlotChartFactory.createUsedVsTotalTenuringDistributionChart(TenuringDataSetFactory
//                    .createDataSet(allGarbageCollectionEvents));
//            addChart(tenuringChart2);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//
//
//
//        try {
//            JFreeChart garbageCollectionCountChart = GarbaceCollectionCountChartFactory.createChart(GarbageCollectionDataSetFactory
//                    .createGarbageCollectionDataSets(allGarbageCollectionEvents));
//            addChart(garbageCollectionCountChart);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        JFreeChart[] chartsArray = charts.toArray(new JFreeChart[0]);
//        ChartUtility.linkChartZoom(chartsArray);
//        initMovingDomainMarker(allGarbageCollectionEvents, chartsArray);
//
//        showChart(chartsArray);

    }


    private void addChart(JFreeChart chart) {
        if (chart != null) {
            charts.add(chart);
        }
    }

    private void initMovingDomainMarker(List<GcLoggedEvent> allGarbageCollectionEvents, JFreeChart... charts) {
        long halfRangeTimeInMs = 0;
        if (allGarbageCollectionEvents.size() > 0) {
            halfRangeTimeInMs = allGarbageCollectionEvents.get(allGarbageCollectionEvents.size() / 2).getTimestamp();
        }
        movingDomainMarker = new MovingDomainMarker(halfRangeTimeInMs, charts);
    }

    private void showChart(JFreeChart... charts) {
        allTablesPanel.setLayout(new BoxLayout(allTablesPanel, BoxLayout.X_AXIS));

        JPanel currTablePairPanel = null;
        for (int i = 0; i < charts.length; i++) {
            if (currTablePairPanel == null) {
                currTablePairPanel = new JPanel();
                currTablePairPanel.setLayout(new BoxLayout(currTablePairPanel, BoxLayout.Y_AXIS));
            }
            ChartPanel currChartPanel = new ChartPanel(charts[i]);
            currTablePairPanel.add(currChartPanel);
            if (movingDomainMarker != null) {
                movingDomainMarker.addMouseListener(currChartPanel);
            }

            if (i % 2 == 0) {
                allTablesPanel.add(currTablePairPanel);
                currTablePairPanel = null;
            }
        }

        if (currTablePairPanel != null) {
            currTablePairPanel.add(new JPanel());
            allTablesPanel.add(currTablePairPanel);
        }

        JFrame frame = new JFrame("GC - log graphical");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JScrollPane scrollPane = new JScrollPane(allTablesPanel);

        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

    }

    private void parseLine(String line) {
        gcLogParser.parseLine(line);
    }

    public void start(){
        readLogFile();
        if (allEventsParser.isHasFullGcDetails()){
            createAnddAddGcDetailsCharts(allEventsParser.getLoggedEvents());
            createAndAddRegularCollectionCharts(allEventsParser.getLoggedEvents());
        } else {
            createAndAddRegularCollectionCharts(allEventsParser.getLoggedEvents());
        }
        if (allEventsParser.isHasApplicationStopTime()){
            createAndAddApplicationStopTimeChart(allEventsParser.getLoggedEvents());
        }
        if (allEventsParser.isHasTenuringDistributions()){
            createAndAddTenuringDistributionChart(allEventsParser.getLoggedEvents());
        }
        JFreeChart[] chartsArray = charts.toArray(new JFreeChart[0]);
        ChartUtility.linkChartZoom(chartsArray);
        initMovingDomainMarker(allEventsParser.getLoggedEvents(), chartsArray);

        showChart(chartsArray);

    }

    private void createAnddAddGcDetailsCharts(List<GcLoggedEvent> loggedEvents) {
    }

    private void createAndAddRegularCollectionCharts(List<GcLoggedEvent> loggedEvents) {
        System.out.println("...creating plots based on regular collections events");
        MemoryInfoDataSetPlotChartFactory memoryInfoDataSetPlotChart = new MemoryInfoDataSetPlotChartFactory();

        try {
            JFreeChart oldGenChart = memoryInfoDataSetPlotChart.createChart(
                    CollectionEventBasedMemoryDSFactory.createTotalGenMemoryDataSets(loggedEvents), "AllGen", true, false, true);
            addChart(oldGenChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart oldGenChart = memoryInfoDataSetPlotChart.createChart(
                    CollectionEventBasedMemoryDSFactory.createOldGenMemoryDataSets(loggedEvents), "OldGen", true, true, true);
            addChart(oldGenChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart oldGenChart = memoryInfoDataSetPlotChart.createChart(
                    CollectionEventBasedMemoryDSFactory.createYoungGenMemoryDataSets(loggedEvents), "YoungGen", true, false, true);
            addChart(oldGenChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart oldGenChart = memoryInfoDataSetPlotChart.createChart(
                    CollectionEventBasedMemoryDSFactory.createPermGenMemoryDataSets(loggedEvents), "PermGen", true, true, true);
            addChart(oldGenChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart oldGenChart = GarbaceCollectionCountChartFactory.createChart(
                    GarbageCollectionDataSetFactory.createGarbageCollectionDataSets(loggedEvents));
            addChart(oldGenChart);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void createAndAddApplicationStopTimeChart(List<GcLoggedEvent> loggedEvents) {
        System.out.println("...creating application stop time chart");
        try {
            if (loggedEvents.size() > 0) {
                JFreeChart applicationStopTimeChart = PlotChartFactory.createChart(
                        StopTimeDataSetFactory.createApplicationStopTimeDataSet(loggedEvents, 1000),
                        "ApplicationStopTime", "ApplicationStopTime(%)", Color.black);
                addChart(applicationStopTimeChart);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createAndAddTenuringDistributionChart(List<GcLoggedEvent> loggedEvents) {
        System.out.println("...creating tenuring distribution chart");
        try {
            JFreeChart tenuringChart = tenuringDataSetPlotChartFactory.createChartAgeSettings(
                    TenuringDataSetFactory.createDataSet(loggedEvents));
            addChart(tenuringChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart tenuringAgesChart = tenuringDataSetPlotChartFactory.createTotalAgeDistributionChart(
                    TenuringDataSetFactory.createDataSet(loggedEvents));
            addChart(tenuringAgesChart);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            JFreeChart tenuringAgesChart = tenuringDataSetPlotChartFactory.createTotalAllocationDemographyChart(
                    TenuringDataSetFactory.createDemographyDataSet(loggedEvents));
            addChart(tenuringAgesChart);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        VisualGc visualGc = new VisualGc(args[0]);
        visualGc.start();


    }

}
