package com.smeo.tools.charts;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.urls.StandardXYURLGenerator;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

import com.smeo.tools.charts.axis.MappedNumberAxis;
import com.smeo.tools.common.TimeDataSetSeries;
import com.smeo.tools.common.TimeDataSetSeries.TimeDataSet;

public class EventAtTimeChart {
	class EventItemGenerator implements XYItemLabelGenerator {
		SimpleDateFormat dateFormatter = new SimpleDateFormat("ss.SS");

		public String generateLabel(XYDataset dataset, int series, int item) {
			try {
				Long time = (Long) dataset.getX(series, item);
				Date date = new Date(time);
				return dateFormatter.format(date);
			} catch (Exception e) {
				return "???";
			}
		}

	}

	private String timeZone = "GMT";
	private List<TimeDataSetSeries> dataSeries;
	private Map<String, Integer> eventNameValueMapping;
	private Integer lastMappedValue = 0;
	private long lastLogEventTimeInMs = 0;

	public EventAtTimeChart() {
		dataSeries = new ArrayList<TimeDataSetSeries>();
		eventNameValueMapping = new HashMap<String, Integer>();
	}

	public void addDataSetSerie(TimeDataSetSeries dataSerie) {
		dataSeries.add(dataSerie);
	}

	private TimeSeriesCollection getTimeSeriesCollection() {
		TimeSeriesCollection dataset = new TimeSeriesCollection();
		for (TimeDataSetSeries currTimeDataSeries : dataSeries) {
			TimeSeries series = new TimeSeries(currTimeDataSeries.getName(), Millisecond.class);
			for (TimeDataSet currTimeDataSet : currTimeDataSeries.getTimeDataSets()) {
				String eventName = currTimeDataSet.getStringValue();
				Integer mappedEventValue;
				if (!eventNameValueMapping.containsKey(eventName)) {
					lastMappedValue += 1;
					eventNameValueMapping.put(eventName, lastMappedValue);
					mappedEventValue = lastMappedValue;
				} else {
					mappedEventValue = eventNameValueMapping.get(eventName);
				}

				long currLogEventTimeInMs = currTimeDataSet.getTimestampMs();
				if (lastLogEventTimeInMs == currLogEventTimeInMs) {
					currLogEventTimeInMs++;
				}
				series.add(new Millisecond(new Date(currLogEventTimeInMs)), mappedEventValue);
				lastLogEventTimeInMs = currLogEventTimeInMs;
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	public JFreeChart createChart() {
		JFreeChart chart = createMappedTimeSeriesChart(
				"Event@Time",
				"Time (" + timeZone + ")",
				"EventValue",
				getTimeSeriesCollection(),
				true,
				true,
				false
				);
		// chart.getXYPlot().addRangeMarker(new ValueMarker(550.0));

		XYPlot plot = chart.getXYPlot();
		XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
		renderer.setSeriesShapesFilled(0, Boolean.TRUE);
		renderer.setSeriesShapesFilled(1, Boolean.FALSE);
		renderer.setItemLabelsVisible(true);
		renderer.setBaseItemLabelGenerator(new EventItemGenerator());
		renderer.setSeriesShapesVisible(0, true);

		return chart;
	}

	public JFreeChart createMappedTimeSeriesChart(String title,
			String timeAxisLabel,
			String valueAxisLabel,
			XYDataset dataset,
			boolean legend,
			boolean tooltips,
			boolean urls) {
		Map<Double, String> tickValueMapping = new HashMap<Double, String>();

		for (String currKey : eventNameValueMapping.keySet()) {
			tickValueMapping.put(new Double(eventNameValueMapping.get(currKey)), currKey);
		}

		DateAxis timeAxis = new DateAxis(timeAxisLabel);
		timeAxis.setTimeZone(TimeZone.getTimeZone(timeZone));

		timeAxis.setLowerMargin(0.02); // reduce the default margins
		timeAxis.setUpperMargin(0.02);
		NumberAxis valueAxis = new MappedNumberAxis(valueAxisLabel, tickValueMapping);

		valueAxis.setAutoRangeIncludesZero(true); // override default
		valueAxis.setAutoRange(true);
		XYPlot plot = new XYPlot(dataset, timeAxis, valueAxis, null);

		XYToolTipGenerator toolTipGenerator = null;
		if (tooltips) {
			toolTipGenerator = StandardXYToolTipGenerator.getTimeSeriesInstance();
		}

		XYURLGenerator urlGenerator = null;
		if (urls) {
			urlGenerator = new StandardXYURLGenerator();
		}

		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true,
				false);
		renderer.setBaseToolTipGenerator(toolTipGenerator);
		renderer.setURLGenerator(urlGenerator);
		plot.setRenderer(renderer);

		JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT,
				plot, legend);
		// currentTheme.apply(chart);
		return chart;
	}
}
