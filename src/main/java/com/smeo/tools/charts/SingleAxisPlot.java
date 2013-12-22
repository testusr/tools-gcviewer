package com.smeo.tools.charts;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import com.smeo.tools.common.TimeDataSetSeries;

public class SingleAxisPlot implements ChartCreator {
	private List<TimeDataSetSeries> dataSeries;
	private String timeZone = "GMT";

	public SingleAxisPlot() {
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

		TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
		for (TimeDataSetSeries currDataSeries : dataSeries) {
			timeSeriesCollection.addSeries(ChartUtils.createBigDecimalTimeSeries(currDataSeries));
		}

		// plot.setRangeAxis(i, axis);
		((DateAxis) plot.getDomainAxis()).setTimeZone(TimeZone.getTimeZone(timeZone));
		plot.setRangeAxisLocation(AxisLocation.BOTTOM_OR_LEFT);
		plot.setDataset(timeSeriesCollection);
		plot.setRenderer(new StandardXYItemRenderer());

		return chart;
	}
}
