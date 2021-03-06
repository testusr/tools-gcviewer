package com.smeo.tools.gc;

import com.smeo.tools.charts.ChartUtility;
import com.smeo.tools.charts.MovingDomainMarker;
import com.smeo.tools.charts.PlotChartFactory;

import com.smeo.tools.gc.parser.AllEventsParser;
import com.smeo.tools.gc.domain.GcLoggedEvent;
import com.smeo.tools.gc.gui.charts.GarbaceCollectionCountChartFactory;
import com.smeo.tools.gc.gui.charts.MemoryInfoDataSetPlotChartFactory;
import com.smeo.tools.gc.gui.charts.TenuringDataSetPlotChartFactory;
import com.smeo.tools.gc.gui.charts.dataset.*;
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
    TenuringDataSetPlotChartFactory tenuringDataSetPlotChartFactory = new TenuringDataSetPlotChartFactory();

    List<JFreeChart> charts = new ArrayList<JFreeChart>();
    String filename;

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



    public void start(){
        readLogFile();
        createAndAddRegularCollectionCharts(allEventsParser.getLoggedEvents());

        if (allEventsParser.isHasFullGcDetails()){
            createAnddAddGcDetailsCharts(allEventsParser.getLoggedEvents());
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
                    CollectionEventBasedMemoryDSFactory.createYoungGenMemoryDataSets(loggedEvents), "YoungGen-incoming", false, true, false);
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

        try {
            JFreeChart oldGenChart = GarbaceCollectionCountChartFactory.createGcDurationChart(
                    GarbageCollectionDataSetFactory.createGcDurationDataSet(loggedEvents));
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
                        "ApplicationStopTime", "ApplicationStopTime(%)", "Stop time per 1 sec", Color.black);
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
            JFreeChart memoryDimesionChart = tenuringDataSetPlotChartFactory.createMemoryDimensionChart(
                    TenuringDataSetFactory.createMemoryDimensionDataSet(loggedEvents));
            addChart(memoryDimesionChart);
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
