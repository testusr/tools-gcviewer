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
import com.smeo.tools.gc.dataset.MemoryDataSetFactory.MemoryInfoDataSet;

public class MemoryInfoDataSetPlotChartFactory extends PlotChartFactory {
	public JFreeChart createChart(MemoryInfoDataSet infoDataSet, String name, boolean usedSpace, boolean incomingMem, boolean totalMemory) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(
				name,
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

        int seriesId = 0;

        if (usedSpace) {
            TimeSeriesCollection percentageGraphCollection = new TimeSeriesCollection();
            percentageGraphCollection.addSeries(createBigDecimalTimeSeries("usedSpace(%)", infoDataSet.beforeGcUsedPerc));
            AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, seriesId, percentageGraphCollection, "usedSpace", false, 100.0, true);
            renderer.setSeriesPaint(seriesId, Color.yellow);
            seriesId++;
        }

        TimeSeriesCollection memoryTrafficCollection = new TimeSeriesCollection();
        AbstractXYItemRenderer memoryTrafficRenderer = null;

        if (incomingMem) {
			memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("incomingMemory(s)", infoDataSet.incomingDataRate));
			memoryTrafficRenderer = addDataSeriesToPlot(plot, seriesId, memoryTrafficCollection, "memoryTraffic(kB)", false, 500000.0, true);
            memoryTrafficRenderer.setSeriesPaint(seriesId, Color.green);
            seriesId++;
		}
        if (totalMemory) {
            memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("maxMemory", infoDataSet.totalMemory));
            if (memoryTrafficRenderer == null){
            memoryTrafficRenderer = addDataSeriesToPlot(plot, seriesId, memoryTrafficCollection, "maxSpace", false, -1, true);
            }
            memoryTrafficRenderer.setSeriesPaint(seriesId, Color.blue);
            seriesId++;
        }


		return chart;
	}

}
