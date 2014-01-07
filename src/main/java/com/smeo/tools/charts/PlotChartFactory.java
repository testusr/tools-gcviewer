package com.smeo.tools.charts;

import java.awt.Color;
import java.util.Date;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.NumberAxis;
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

import com.smeo.tools.common.DataSetEntry;

public class PlotChartFactory {

    public static JFreeChart createChart(List<DataSetEntry> dataSetEnryList, String chartName, String lineName, String textTitle, Color color){
        JFreeChart chart = ChartFactory.createTimeSeriesChart(
                chartName,
                "Time of Day",
                "Primary Range Axis",
                null,
                true,
                true,
                false
        );
        chart.setBackgroundPaint(Color.white);
        chart.addSubtitle(new TextTitle(textTitle));
        XYPlot plot = chart.getXYPlot();
        plot.setOrientation(PlotOrientation.VERTICAL);
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);

        TimeSeriesCollection memoryTrafficCollection = new TimeSeriesCollection();
        memoryTrafficCollection.addSeries(createBigDecimalTimeSeries(lineName, dataSetEnryList));

        AbstractXYItemRenderer renderer = addDataSeriesToPlot(plot, 0, memoryTrafficCollection, chartName, false, -1, true);
        renderer.setSeriesPaint(0, Color.red);

        return chart;
    }
    public static JFreeChart createChart(List<DataSetEntry> dataSetEnryList, String chartName, String lineName, Color color) {
        return createChart(dataSetEnryList, chartName, lineName, "", color);
	}

	protected static TimeSeries createBigDecimalTimeSeries(String name, List<DataSetEntry> dataSetEntries) {
		TimeSeries timeSeries = new TimeSeries(name, Millisecond.class);
		for (DataSetEntry currDataSetEntry : dataSetEntries) {
			timeSeries.addOrUpdate(new Millisecond(new Date(currDataSetEntry.time)), currDataSetEntry.value);
		}
		return timeSeries;
	}

    protected static AbstractXYItemRenderer addDataSeriesToPlot(XYPlot plot, int dataSetIndex, TimeSeriesCollection collection, String name,
                                                                boolean showSymbol,
                                                                double maxRange, boolean axisVisible) {
        return addDataSeriesToPlot(plot, dataSetIndex, collection, name, showSymbol, maxRange, axisVisible, true);
    }

	protected static AbstractXYItemRenderer addDataSeriesToPlot(XYPlot plot, int dataSetIndex, TimeSeriesCollection collection, String name,
			boolean showSymbol,
			double maxRange, boolean axisVisible, boolean baseLineVisible) {

		NumberAxis axis = new NumberAxis(name);
		axis.setAutoRangeIncludesZero(false);
		if (maxRange > 0) {
			axis.setRange(new Range(0.0, maxRange));
		}
		axis.setVisible(axisVisible);

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
            standardRenderer.setPlotLines(baseLineVisible);
			plot.setRenderer(dataSetIndex, standardRenderer);
			return standardRenderer;
		} else {
			XYLineAndShapeRenderer renderer2006 = new XYLineAndShapeRenderer();
			renderer2006.setUseFillPaint(true);
            renderer2006.setBaseLinesVisible(baseLineVisible);
			renderer2006.setBaseFillPaint(Color.white);
			plot.setRenderer(dataSetIndex, renderer2006);
			return renderer2006;
		}
	}
}
