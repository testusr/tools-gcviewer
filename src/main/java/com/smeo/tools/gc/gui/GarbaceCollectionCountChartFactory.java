package com.smeo.tools.gc.gui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import com.smeo.tools.charts.PlotChartFactory;
import com.smeo.tools.gc.dataset.GarbageCollectionDataSetFactory.GarbageCollectionDataSet;

public class GarbaceCollectionCountChartFactory extends PlotChartFactory {
	public static JFreeChart createChart(GarbageCollectionDataSet infoDataSet) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"GC Counts",
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

		TimeSeriesCollection fullGcTimeSeriesCollection = new TimeSeriesCollection();
		fullGcTimeSeriesCollection.addSeries(createBigDecimalTimeSeries("fullGc", infoDataSet.fullGc));
		fullGcTimeSeriesCollection.addSeries(createBigDecimalTimeSeries("compactingGc", infoDataSet.compactingGc));
		AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, fullGcTimeSeriesCollection, "full gc count", false, -1, true);
		renderer.setSeriesPaint(0, Color.green);
		renderer.setSeriesPaint(1, Color.red);

		TimeSeriesCollection minorGcTimeSeriesCollection = new TimeSeriesCollection();
		minorGcTimeSeriesCollection.addSeries(createBigDecimalTimeSeries("minorGc", infoDataSet.minorGc));
		renderer = addDataSeriesToPlot(plot, 1, minorGcTimeSeriesCollection, "minor gc count", false, -1, true);
		renderer.setSeriesPaint(0, Color.blue);

		return chart;
	}
}
