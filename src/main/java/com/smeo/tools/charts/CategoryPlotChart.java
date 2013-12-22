package com.smeo.tools.charts;

import java.awt.Color;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.CategoryToolTipGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import com.smeo.tools.common.TimeDataSetSeries;

public class CategoryPlotChart {

	private List<TimeDataSetSeries> dataSeries;

	public CategoryPlotChart() {
		dataSeries = new ArrayList<TimeDataSetSeries>();
	}

	public void addDataSetSerie(TimeDataSetSeries dataSerie) {
		dataSeries.add(dataSerie);
	}

	public JFreeChart createChart() {
		final DefaultCategoryDataset dataset = ChartUtils.createCategoryDataset(this.dataSeries.get(0));

		final JFreeChart chart = ChartFactory.createLineChart(
				"Event Frequency Demo", // title
				"Category", // domain axis label
				"Value", // range axis label
				dataset, // dataset
				PlotOrientation.HORIZONTAL, // orientation
				true, // include legend
				true, // tooltips
				false // URLs
				);

		// NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

		// set the background color for the chart...
		chart.setBackgroundPaint(new Color(0xFF, 0xFF, 0xCC));

		final CategoryPlot plot = chart.getCategoryPlot();
		// plot.getDomainAxis().setMaxCategoryLabelWidthRatio(10.0f);
		plot.setRangeAxis(new DateAxis("Date"));
		final CategoryToolTipGenerator toolTipGenerator = new StandardCategoryToolTipGenerator(
				"", DateFormat.getDateInstance()
				);

		// final StandardLegend legend = (StandardLegend) chart.getLegend();
		// legend.setDisplaySeriesShapes(true);

		return chart;
	}

}
