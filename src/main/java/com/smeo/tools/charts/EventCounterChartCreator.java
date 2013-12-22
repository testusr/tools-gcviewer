package com.smeo.tools.charts;

import java.awt.BorderLayout;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartPanel;

import com.smeo.tools.common.TimeDataSetSeries;

public class EventCounterChartCreator {

	private Map<String, Map<Date, BigDecimal>> values;
	private boolean useMultipleAxis = true;

	public EventCounterChartCreator(Map<String, Map<Date, BigDecimal>> values) {
		this.values = values;
	}

	public EventCounterChartCreator(Map<String, Map<Date, BigDecimal>> values, boolean useMultipleAxis) {
		this.values = values;
		this.useMultipleAxis = useMultipleAxis;
	}

	public void showChart() {
		showChart(createTimeDataSeries());
	}

	private List<TimeDataSetSeries> createTimeDataSeries() {
		List<TimeDataSetSeries> result = new ArrayList<TimeDataSetSeries>();
		for (Map.Entry<String, Map<Date, BigDecimal>> entry : values.entrySet()) {
			result.add(createTimeDataSeries(entry.getKey(), entry.getValue()));
		}
		return result;
	}

	private void showChart(List<TimeDataSetSeries> timeDataSetSeries) {
		final ChartCreator plotChart;
		if (useMultipleAxis) {
			plotChart = new MultipleAxisPlot();
		} else {
			plotChart = new SingleAxisPlot();
		}
		for (TimeDataSetSeries currTimeDataSetSeries : timeDataSetSeries) {
			plotChart.addDataSetSerie(currTimeDataSetSeries);
		}

		JFrame frame = new JFrame("CategoryPlotChart");
		final ChartPanel panel = new ChartPanel(plotChart.createChart(), true, true, true, false, true);
		JScrollPane scrollPane = new JScrollPane(panel);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.setSize(1200, 800);
		frame.setVisible(true);
	}

	private TimeDataSetSeries createTimeDataSeries(String dataSetName, Map<Date, BigDecimal> timeStatistics) {

		TimeDataSetSeries series = new TimeDataSetSeries(dataSetName);
		for (Map.Entry<Date, BigDecimal> entry : timeStatistics.entrySet()) {
			Date dataPointDate = entry.getKey();
			// dataPointDate = adjustDataPointData(dataPointDate);
			series.addDataSet(dataPointDate.getTime(), entry.getValue());
		}
		return series;
	}

	// private Date adjustDataPointData(Date dataPointDate) {
	// Calendar cal = new GregorianCalendar();
	// cal.setTime(dataPointDate);
	// cal.set(Calendar.DAY_OF_MONTH, 1 );
	// cal.set(Calendar.MONTH, 0);
	// cal.set(Calendar.YEAR, 2001);
	// return cal.getTime();
	// }
}
