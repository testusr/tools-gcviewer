package com.smeo.tools.gc.gui.charts;

import com.smeo.tools.charts.PlotChartFactory;
import com.smeo.tools.gc.gui.charts.dataset.MemoryInfoDataSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.AbstractXYItemRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;

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
		chart.addSubtitle(new TextTitle(""));
		XYPlot plot = chart.getXYPlot();
		plot.setOrientation(PlotOrientation.VERTICAL);
		plot.setBackgroundPaint(Color.lightGray);
		plot.setDomainGridlinePaint(Color.white);
		plot.setRangeGridlinePaint(Color.white);

        int seriesId = 0;



        TimeSeriesCollection memoryTrafficCollection = new TimeSeriesCollection();
        AbstractXYItemRenderer memoryTrafficRenderer = null;

        if (totalMemory) {
            memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("availableSpaceInK", infoDataSet.availableSpaceInK));
            if (memoryTrafficRenderer == null){
            memoryTrafficRenderer = addDataSeriesToPlot(plot, seriesId, memoryTrafficCollection, "maxSpace", false, -1, true);
            }
            memoryTrafficRenderer.setSeriesPaint(seriesId, Color.green);
            seriesId++;
        }
        if (usedSpace) {
            memoryTrafficCollection.addSeries(createBigDecimalTimeSeries("usedSpace(K)", infoDataSet.usedSpaceInK));
            if (memoryTrafficRenderer == null){
                memoryTrafficRenderer = addDataSeriesToPlot(plot, seriesId, memoryTrafficCollection, "maxSpace", false, -1, true);
            }
            memoryTrafficRenderer.setSeriesPaint(seriesId, Color.blue);
            seriesId++;
        }


        if (incomingMem) {
            TimeSeriesCollection incomingMemCollection = new TimeSeriesCollection();
            incomingMemCollection.addSeries(createBigDecimalTimeSeries("incomingDataInK(s)", infoDataSet.incomingDataInK));
            AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 1, incomingMemCollection, "incoming(kB/s)", false, 10000, true);
            renderer.setSeriesPaint(0, Color.yellow);
            seriesId++;
        }

		return chart;
	}

}
