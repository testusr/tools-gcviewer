package com.smeo.tools.charts;

import org.jfree.chart.JFreeChart;

import com.smeo.tools.common.TimeDataSetSeries;

public interface ChartCreator {

	public abstract JFreeChart createChart();

	public abstract void addDataSetSerie(TimeDataSetSeries currTimeDataSetSeries);

}