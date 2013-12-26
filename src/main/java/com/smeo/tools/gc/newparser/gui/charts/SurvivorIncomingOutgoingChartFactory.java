package com.smeo.tools.gc.newparser.gui.charts;

import com.smeo.tools.charts.PlotChartFactory;

import com.smeo.tools.gc.newparser.gui.charts.dataset.SurvivorInputOutputDataSetFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;

public class SurvivorIncomingOutgoingChartFactory extends PlotChartFactory {
	public static JFreeChart createChart(SurvivorInputOutputDataSetFactory.SurvivorInputOutputDataSet survivorInputOutputDataSet) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				"SurvivorSpace-MemoryFlow",
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

		TimeSeriesCollection memoryTrafficCollection = new TimeSeriesCollection();
		memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("incoming(B)", survivorInputOutputDataSet.incomingDataRate));
		memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("outgoing(B)", survivorInputOutputDataSet.outgoingData));

		AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, memoryTrafficCollection, "incomingMemory", false, -1, true);
		renderer.setSeriesPaint(0, Color.red);
		renderer.setSeriesPaint(1, Color.green);

		return chart;
	}
}
