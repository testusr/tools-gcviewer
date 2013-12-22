package com.smeo.tools.gc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFrame;

import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class GCViewer {

	public static void main(String[] args) throws IOException, ParseException {
		TimeSeries timeSeries = new TimeSeries("Thread Stop Time Per Interval", Millisecond.class);
		// XYSeries series = new XYSeries("Thread Stop Time Per Interval");
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				new FileInputStream(args[0])));
		float interval = Float.valueOf(args[1]);

		float appRunTime = 0;
		float appStopTime = 0;

		int gcLineCounter = 0;
		int corruptedLines = 0;

		long lastTime = getStartTime(reader).getTime();

		String line = reader.readLine();
		while (line != null) {
			try {
				if (line.startsWith("Application time")) {
					gcLineCounter++;
					String[] tokens = line.split(" ");
					appRunTime += Float.valueOf(tokens[tokens.length - 2]);
				} else if (line.startsWith("Total time for")) {
					gcLineCounter++;
					String[] tokens = line.split(" ");
					appStopTime += Float.valueOf(tokens[tokens.length - 2]);
				}
				float totalTime = appRunTime + appStopTime;
				if (totalTime > interval) {
					// end of interval reached

					// record statistics
					RegularTimePeriod currTime = new Millisecond(new Date(lastTime += (long) (totalTime * 1000)));
					timeSeries.add(currTime, appStopTime
							/ totalTime * 100);

					// reset counters
					appStopTime = 0;
					appRunTime = 0;
				}
			} catch (NumberFormatException e) {
				// some lines are corrupted
				// but it can be ignored if it's not too much
				corruptedLines++;
				e.printStackTrace();
			}
			line = reader.readLine();
		}

		System.out.println("Detected " + corruptedLines + " corrupted lines out of " + gcLineCounter + " gc reporting lines");

		// Add the series to your data set
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		dataset.addSeries(timeSeries);
		createChart(dataset);
	}

	private static void createChart(TimeSeriesCollection dataset) {
		DateAxis timeAxis = new DateAxis("time");

		// timeAxis.setLowerMargin(0.02); // reduce the default margins
		// timeAxis.setUpperMargin(0.02);

		ValueAxis valueAxis = new NumberAxis();
		valueAxis.setRange(0.0, 110.0);
		XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

		// Generate the graph
		// JFreeChart chart = ChartFactory.createXYLineChart(
		// "Thread Stop Chart for " + args[0], // Title
		// args[1] + " Seconds Time Interval Number", // x-axis Label
		// "Application Threads Stop Time (%)", // y-axis Label
		// dataset, // Dataset
		// PlotOrientation.VERTICAL, // Plot Orientation
		// true, // Show Legend
		// true, // Use tooltips
		// false // Configure chart to generate URLs?
		// );

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		plot.setRenderer(renderer);

		JFreeChart chart = new JFreeChart("gcviewer", JFreeChart.DEFAULT_TITLE_FONT,
				plot, false);

		JFrame chartFrame = new ChartFrame("gcviewer", chart);
		chartFrame.setSize(800, 500);
		chartFrame.setVisible(true);
	}

	private static Date getStartTime(BufferedReader reader) throws IOException, ParseException {
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.matches("[0-9]{4}-[0-9]{2}-.*")) {
				String[] lineSegements = line.split("\\+");
				// 2012-02-01T00:00:19.081+0000
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
				return simpleDateFormat.parse(lineSegements[0]);
			}
		}
		return null;
	}
}