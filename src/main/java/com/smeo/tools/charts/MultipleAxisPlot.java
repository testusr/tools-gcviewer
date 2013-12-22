package com.smeo.tools.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import com.smeo.tools.common.TimeDataSetSeries;

public class MultipleAxisPlot implements ChartCreator {
	private List<TimeDataSetSeries> dataSeries;
	private String timeZone = "GTM";

	public MultipleAxisPlot() {
		dataSeries = new ArrayList<TimeDataSetSeries>();
	}

	public void addDataSetSerie(TimeDataSetSeries dataSerie) {
		dataSeries.add(dataSerie);
	}

	public JFreeChart createChart() {

		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"Multiple Axis Chart",
				"Time of Day (" + timeZone + ")",
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

		int i = 0;
		for (TimeDataSetSeries currDataSeries : dataSeries) {
			TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
			timeSeriesCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(currDataSeries));

			NumberAxis axis = new NumberAxis(currDataSeries.getName());
			axis.setAutoRangeIncludesZero(false);
			System.err.println(i + " /" + (i % 2));
			plot.setRangeAxis(i, axis);
			if ((i % 2) == 0) {
				plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_LEFT);
			} else {
				plot.setRangeAxisLocation(i, AxisLocation.BOTTOM_OR_RIGHT);
			}

			plot.setDataset(i, timeSeriesCollection);
			plot.mapDatasetToRangeAxis(i, new Integer(i));
			((DateAxis) plot.getDomainAxis()).setTimeZone(TimeZone.getTimeZone(timeZone));
			plot.setRenderer(i, new StandardXYItemRenderer());

			i++;
		}

		return chart;
	}
}
