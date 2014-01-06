package com.smeo.tools.gc.parser;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import org.apache.commons.lang.Validate;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.ChartChangeEvent;
import org.jfree.chart.event.ChartChangeListener;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.Range;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import com.smeo.tools.charts.ChartUtils;
import com.smeo.tools.common.TimeDataSetSeries;

/**
 * Prints the memory pushed in into the garbage collector within a specified time interval
 * 
 * @author smeo
 * 
 */
public class GCLogViewer_IncomingMemory {
	private static class EdenSpace {
		Long edenSizeInK;
		Integer percentage;

		public float getUsedSpace() {
			return ((float) edenSizeInK * ((float) percentage) / 100.0f);
		}

		public boolean isFilled() {
			return (edenSizeInK != null && percentage != null);
		}

		@Override
		public String toString() {
			return "EdenSpace [edenSizeInK=" + edenSizeInK + ", percentage=" + percentage + "]";
		}
	}

	private static class GcFilledEdenMemoryRecord {
		Date time;
		boolean fullGC = false;
		boolean concurrentModeFailure = false;
		EdenSpace edenSpace;
		PermGenSpace permGenSpace;

		public boolean isFilled() {
			return (time != null && edenSpace != null && edenSpace.isFilled() && permGenSpace != null);
		}

		@Override
		public String toString() {
			return "GcFilledEdenMemoryRecord [time=" + time + ", edenSizeInK=" + edenSpace + "]";
		}
	}

	private static class PermGenSpace {
		int total;
		int used;

		@Override
		public String toString() {
			return "PermGenSpace [total=" + total + ", used=" + used + "]";
		}
	}

	private static EdenSpace getEdenSpace(String logLine) {
		// eden space 2048000K, 100%
		EdenSpace edenSpace = new EdenSpace();
		String trimmedLogLine = logLine.trim();
		if (trimmedLogLine.matches("eden space [0-9]+K.*")) {
			String[] lineSegements = trimmedLogLine.split(" ");
			String sizeInfoLineSegment = lineSegements[2];
			Integer percentage = null;
			if (trimmedLogLine.contains("%")) {
				lineSegements = trimmedLogLine.substring(0, trimmedLogLine.indexOf("%")).split(" ");
				percentage = new Integer(lineSegements[lineSegements.length - 1]);
			}
			if (percentage > 0) {
				edenSpace.percentage = percentage;
			}

			if (sizeInfoLineSegment.contains("K,")) {
				edenSpace.edenSizeInK = new Long(sizeInfoLineSegment.replace("K,", ""));
			}
		}
		if (edenSpace.isFilled()) {
			return edenSpace;
		} else {
			return null;
		}
	}

	private static GcFilledEdenMemoryRecord getFilledMemoryRecord(List<String> currLogSnippet) throws IOException, ParseException {
		GcFilledEdenMemoryRecord newMemoryRecord = new GcFilledEdenMemoryRecord();
		String firstLine = currLogSnippet.get(0);
		newMemoryRecord.time = getLoggedTime(firstLine);
		newMemoryRecord.fullGC = firstLine.contains("[Full GC");
		newMemoryRecord.concurrentModeFailure = firstLine.contains("CMS (concurrent mode failure)");
		if (newMemoryRecord.concurrentModeFailure) {
			System.out.println("concurrent mode failure");
		}

		if (newMemoryRecord.time != null) {
			for (int i = 0; i < currLogSnippet.size() && !newMemoryRecord.isFilled(); i++) {
				if (newMemoryRecord.edenSpace == null) {
					newMemoryRecord.edenSpace = getEdenSpace(currLogSnippet.get(i));
				}
				if (newMemoryRecord.permGenSpace == null) {
					newMemoryRecord.permGenSpace = getPermGenSpace(currLogSnippet.get(i));
				}
			}
		}

		if (newMemoryRecord.isFilled()) {
			return newMemoryRecord;
		}
		return null;
	}

	private static Date getLoggedTime(String currLogSnippet) throws IOException, ParseException {
		if (currLogSnippet.matches("[0-9]{4}-[0-9]{2}-.*")) {
			String[] lineSegements = currLogSnippet.split("\\+");
			// 2012-02-01T00:00:19.081+0000
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
			return simpleDateFormat.parse(lineSegements[0]);
		}
		return null;
	}

	private static PermGenSpace getPermGenSpace(String logline) {
		// concurrent-mark-sweep perm gen total 38264K, used 23300K [0...
		PermGenSpace permGenSpace = null;
		String trimmedLogLine = logline.trim();
		if (trimmedLogLine.startsWith("concurrent-mark-sweep perm gen total ")) {
			permGenSpace = new PermGenSpace();
			String[] lineSegments = trimmedLogLine.split("[K| ]");
			permGenSpace.total = new Integer(lineSegments[4]);
			permGenSpace.used = new Integer(lineSegments[7]);
		}
		return permGenSpace;
	}

	public static void main(String[] args) throws IOException, ParseException {
		GCLogViewer_IncomingMemory incomingMemoryViewer = new GCLogViewer_IncomingMemory();
		incomingMemoryViewer.run(args);
	}

	private static void showChart(JFreeChart... charts) {
		JPanel allTablesPanel = new JPanel();
		allTablesPanel.setLayout(new BoxLayout(allTablesPanel, BoxLayout.X_AXIS));

		JPanel currTablePairPanel = null;
		for (int i = 0; i < charts.length; i++) {
			if (currTablePairPanel == null) {
				currTablePairPanel = new JPanel();
				currTablePairPanel.setLayout(new BoxLayout(currTablePairPanel, BoxLayout.Y_AXIS));
			}
			currTablePairPanel.add(new ChartPanel(charts[i]));

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

	long intervalInMs;

	int intervalNumber;

	long startTime;

	TimeDataSetSeries fullGcDataSet;

	TimeDataSetSeries minorGcDataSet;

	TimeDataSetSeries concurrentModeFailureDataSet;

	TimeDataSetSeries rawMemoryTrafficDataSet;

	ApplicationStopTimeParser applicationStopTimeParser;

	private AbstractXYItemRenderer addDataSeriesToPlot(XYPlot plot, int dataSetIndex, TimeSeriesCollection collection, String name, boolean showSymbol,
			double maxRange, boolean autoRange) {

		NumberAxis axis = new NumberAxis(name);
		axis.setAutoRangeIncludesZero(false);
		if (maxRange > 0) {
			axis.setRange(new Range(0.0, maxRange));
		}
		axis.setVisible(autoRange);

		plot.setRangeAxis(dataSetIndex, axis);
		if ((dataSetIndex % 2) == 0) {
			plot.setRangeAxisLocation(dataSetIndex, AxisLocation.BOTTOM_OR_LEFT);
		} else {
			plot.setRangeAxisLocation(dataSetIndex, AxisLocation.BOTTOM_OR_RIGHT);
		}

		plot.setDataset(dataSetIndex, collection);
		plot.mapDatasetToRangeAxis(dataSetIndex, new Integer(dataSetIndex));
		if (!showSymbol) {
			StandardXYItemRenderer standardRenderer = new StandardXYItemRenderer();
			plot.setRenderer(dataSetIndex, standardRenderer);
			return standardRenderer;
		} else {
			XYLineAndShapeRenderer renderer2006 = new XYLineAndShapeRenderer();
			renderer2006.setUseFillPaint(true);
			renderer2006.setBaseFillPaint(Color.white);
			plot.setRenderer(dataSetIndex, renderer2006);
			return renderer2006;
		}
	}

	public JFreeChart createApplicationStopTimeChart() {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"ApplicationStopTime",
				"Time of Day",
				"Primary Range Axis",
				null,
				true,
				true,
				false
				);

		chart.setBackgroundPaint(Color.white);
		chart.addSubtitle(new TextTitle("different values shown in one timeline"));
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		TimeSeriesCollection applicatioStopTimeCollection = new TimeSeriesCollection();
		applicatioStopTimeCollection.addSeries(applicationStopTimeParser.getTimeSeries());
		AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 1, applicatioStopTimeCollection, "applicatioStopTime", false, 110, true);
		renderer.setSeriesPaint(0, Color.black);

		TimeSeriesCollection eventOccuranceCollection = new TimeSeriesCollection();
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(fullGcDataSet));
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(minorGcDataSet));
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(concurrentModeFailureDataSet));

		renderer = addDataSeriesToPlot(plot, 0, eventOccuranceCollection, "events", true, 10.0, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.blue);
		renderer.setSeriesPaint(2, Color.green);

		return chart;
	}

	public JFreeChart createPermGenChart(List<GcFilledEdenMemoryRecord> memoryRecords) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Perm Gen",
				"Time of Day",
				"Primary Range Axis",
				null,
				true,
				true,
				false
				);

		chart.setBackgroundPaint(Color.white);
		chart.addSubtitle(new TextTitle("different values shown in one timeline"));
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		TimeSeriesCollection applicatioStopTimeCollection = new TimeSeriesCollection();
		applicatioStopTimeCollection.addSeries(applicationStopTimeParser.getTimeSeries());
		AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 1, createPermGenTimeSeriesCollection(memoryRecords), "permGen", false, -1, true);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(0, Color.yellow);

		TimeSeriesCollection eventOccuranceCollection = new TimeSeriesCollection();
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(concurrentModeFailureDataSet));

		renderer = addDataSeriesToPlot(plot, 0, eventOccuranceCollection, "events", true, 10.0, false);
		renderer.setSeriesPaint(0, Color.green);

		return chart;
	}

	private TimeSeriesCollection createPermGenTimeSeriesCollection(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeDataSetSeries totalPermGenSpaceSeries = new TimeDataSetSeries("totalPermGen");
		TimeDataSetSeries usdPermGenSpaceSeries = new TimeDataSetSeries("usedPermGen");

		for (GcFilledEdenMemoryRecord currMemoryRecord : memoryRecords) {
			PermGenSpace currPermGenSpace = currMemoryRecord.permGenSpace;
			if (currPermGenSpace != null) {
				totalPermGenSpaceSeries.addDataSet(currMemoryRecord.time.getTime(), currPermGenSpace.total);
				usdPermGenSpaceSeries.addDataSet(currMemoryRecord.time.getTime(), currPermGenSpace.used);
			}
		}

		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		timeSeriesCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(totalPermGenSpaceSeries));
		timeSeriesCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(usdPermGenSpaceSeries));
		return timeSeriesCollection;
	}

	private TimeSeriesCollection createTimeSeriesWithSpecifiedTimeFrame(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeSeries timeSeries = new TimeSeries("Incoming memory traffic per time frame in K", Millisecond.class);
		long intervalStartTime = startTime;
		long intervalEndTime = intervalStartTime + intervalInMs;
		boolean endReached = false;
		while (!endReached) {
			int currMemoryEntryIndex = 0;
			long collectedMemoryInK = 0;

			long recordedIntervalStartTime = 0;
			long recordedIntervalEndTime = 0;

			while (memoryRecords.get(currMemoryEntryIndex + 1).time.getTime() < intervalStartTime) {
				currMemoryEntryIndex++;
			}
			recordedIntervalStartTime = memoryRecords.get(currMemoryEntryIndex).time.getTime();

			if (memoryRecords.get(memoryRecords.size() - 1).time.getTime() > intervalEndTime) {

				while (memoryRecords.get(currMemoryEntryIndex).time.getTime() < intervalEndTime) {
					GcFilledEdenMemoryRecord currMemoryRecord = memoryRecords.get(currMemoryEntryIndex);
					collectedMemoryInK += currMemoryRecord.edenSpace.getUsedSpace();
					currMemoryEntryIndex++;
				}
				recordedIntervalEndTime = memoryRecords.get(currMemoryEntryIndex).time.getTime();

				long realIntervalLengthInMs = recordedIntervalEndTime - recordedIntervalStartTime;
				float kByteInMs = ((float) collectedMemoryInK) / ((float) realIntervalLengthInMs);
				float collectedMemoryInInterval = kByteInMs * intervalInMs;
				timeSeries.add(new Millisecond(new Date(intervalEndTime)), collectedMemoryInInterval);

				intervalStartTime = intervalEndTime;
				intervalEndTime = intervalStartTime + intervalInMs;
			} else {
				endReached = true;
			}
		}

		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		timeSeriesCollection.addSeries(timeSeries);
		return timeSeriesCollection;
	}

	public JFreeChart createTrafficAndGcChart() {

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Memory Traffice And GC chart",
				"Time of Day",
				"Primary Range Axis",
				null,
				true,
				true,
				false
				);

		chart.setBackgroundPaint(Color.white);
		chart.addSubtitle(new TextTitle("different values shown in one timeline"));
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

		// plot.setAxisOffset(new Spacer(Spacer.ABSOLUTE, 5.0, 5.0, 5.0, 5.0));

		// StandardXYItemRenderer renderer = (StandardXYItemRenderer) plot.getRenderer();
		// renderer.setPaint(Color.black);

		TimeSeriesCollection eventOccuranceCollection = new TimeSeriesCollection();
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(fullGcDataSet));
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(minorGcDataSet));
		eventOccuranceCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(concurrentModeFailureDataSet));

		AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, eventOccuranceCollection, "events", true, 10.0, false);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.blue);
		renderer.setSeriesPaint(2, Color.green);

		TimeSeriesCollection memoryTraffic = new TimeSeriesCollection();
		memoryTraffic.addSeries(ChartUtils.createBigDecimalTimeSeries(rawMemoryTrafficDataSet));
		renderer = addDataSeriesToPlot(plot, 1, memoryTraffic, "memoryTraffic", false, 5000000, true);
		renderer.setSeriesPaint(0, Color.yellow);
		return chart;
	}

	private TimeDataSetSeries getConcurrentModeFailure(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeDataSetSeries timeDataSetSeries = new TimeDataSetSeries("concurrentModeFailure");
		for (GcFilledEdenMemoryRecord currRecord : memoryRecords) {
			if (currRecord.concurrentModeFailure) {
				timeDataSetSeries.addDataSet(currRecord.time.getTime(), 3);
			}
		}
		return timeDataSetSeries;
	}

	private TimeDataSetSeries getFullGcs(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeDataSetSeries timeDataSetSeries = new TimeDataSetSeries("fullGc");
		for (GcFilledEdenMemoryRecord currRecord : memoryRecords) {
			if (currRecord.fullGC) {
				timeDataSetSeries.addDataSet(currRecord.time.getTime(), 1);
			}
		}
		return timeDataSetSeries;
	}

	private TimeDataSetSeries getMinorGcs(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeDataSetSeries timeDataSetSeries = new TimeDataSetSeries("minorGc");
		for (GcFilledEdenMemoryRecord currRecord : memoryRecords) {
			if (!currRecord.fullGC) {
				timeDataSetSeries.addDataSet(currRecord.time.getTime(), 2);
			}
		}
		return timeDataSetSeries;
	}

	private TimeDataSetSeries getRawMemoryTraffic(List<GcFilledEdenMemoryRecord> memoryRecords) {
		TimeDataSetSeries timeDataSetSeries = new TimeDataSetSeries("Memory Traffic in K");

		GcFilledEdenMemoryRecord pastRecord = null;
		for (GcFilledEdenMemoryRecord currRecord : memoryRecords) {
			if (pastRecord == null) {
				pastRecord = currRecord;
				continue;
			}

			long intervalInMs = currRecord.time.getTime() - pastRecord.time.getTime();
			float intervalTimeInSec = ((float) intervalInMs) / 1000.0f;
			float averageMemoryTroughput = currRecord.edenSpace.getUsedSpace() / intervalTimeInSec;
			Date date = new Date(pastRecord.time.getTime() + (intervalInMs / 2));
			timeDataSetSeries.addDataSet(date.getTime(), averageMemoryTroughput);

			pastRecord = currRecord;
			System.out.println(date + " : " + averageMemoryTroughput);
		}
		return timeDataSetSeries;
	}

	private void linkCharts(JFreeChart... charts) {
		for (JFreeChart currChartA : charts) {
			for (JFreeChart currChartB : charts) {
				linkCharts(currChartA, currChartB);
			}
		}
	}

	private void linkCharts(final JFreeChart trafficAndGcChart, final JFreeChart applicationStopTimeChart) {
		if (trafficAndGcChart != applicationStopTimeChart) {
			trafficAndGcChart.addChangeListener(new ChartChangeListener() {

				@Override
				public void chartChanged(ChartChangeEvent event) {
					ValueAxis srcDomainAxis = event.getChart().getXYPlot().getDomainAxis();
					Range srcDomainRange = srcDomainAxis.getRange();
					Range dstDomainRange = applicationStopTimeChart.getXYPlot().getDomainAxis().getRange();
					if (srcDomainRange.toString().compareTo(dstDomainRange.toString()) != 0) {
						applicationStopTimeChart.getXYPlot().getDomainAxis().setRange(srcDomainRange);
					}

				}
			});

			applicationStopTimeChart.addChangeListener(new ChartChangeListener() {

				@Override
				public void chartChanged(ChartChangeEvent event) {
					ValueAxis srcDomainAxis = event.getChart().getXYPlot().getDomainAxis();
					Range srcDomainRange = srcDomainAxis.getRange();
					Range dstDomainRange = trafficAndGcChart.getXYPlot().getDomainAxis().getRange();
					if (srcDomainRange.toString().compareTo(dstDomainRange.toString()) != 0) {
						trafficAndGcChart.getXYPlot().getDomainAxis().setRange(srcDomainRange);
					}

				}
			});
		}

	}

	public void run(String[] args) throws IOException, ParseException {
		// XYSeries series = new XYSeries("Thread Stop Time Per Interval");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		float interval = Float.valueOf(args[1]);
		intervalInMs = (long) (interval * 1000);
		Validate.isTrue(intervalInMs > 0, " interval in ms has to be bigger than 0");
		this.applicationStopTimeParser = new ApplicationStopTimeParser(interval);

		intervalNumber = 0;

		List<String> currLogSnippet = new ArrayList<String>();
		String line = reader.readLine();

		List<GcFilledEdenMemoryRecord> memoryRecords = new ArrayList<GcFilledEdenMemoryRecord>();
		while ((line = reader.readLine()) != null) {
			applicationStopTimeParser.parse(line);

			if (getLoggedTime(line) != null) {
				GcFilledEdenMemoryRecord memoryRecord = getFilledMemoryRecord(currLogSnippet);
				if (memoryRecord != null) {
					memoryRecords.add(memoryRecord);
				}
				currLogSnippet = new ArrayList<String>();
				currLogSnippet.add(line);
			}
			currLogSnippet.add(line);
		}

		startTime = memoryRecords.get(0).time.getTime();
		System.out.println("Detected " + memoryRecords.size() + " logSnippets ");

		fullGcDataSet = getFullGcs(memoryRecords);
		minorGcDataSet = getMinorGcs(memoryRecords);
		concurrentModeFailureDataSet = getConcurrentModeFailure(memoryRecords);
		rawMemoryTrafficDataSet = getRawMemoryTraffic(memoryRecords);

		JFreeChart trafficAndGcChart = createTrafficAndGcChart();
		JFreeChart applicationStopTimeChart = createApplicationStopTimeChart();
		JFreeChart permGenChart = createPermGenChart(memoryRecords);

		linkCharts(trafficAndGcChart, applicationStopTimeChart, permGenChart);
		showChart(trafficAndGcChart, applicationStopTimeChart, permGenChart);
	}

}
