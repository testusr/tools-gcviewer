package com.smeo.tools.charts;

import java.util.Date;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.RegularTimePeriod;
import org.jfree.data.time.TimeSeries;

import com.smeo.tools.common.TimeDataSetSeries;
import com.smeo.tools.common.TimeDataSetSeries.TimeDataSet;

public class ChartUtils {
	public static TimeSeries createBigDecimalTimeSeries(TimeDataSetSeries timeDataSeries) {

		TimeSeries timeSeries = new TimeSeries(timeDataSeries.getName(), Millisecond.class);
		for (TimeDataSet currTimeDataSet : timeDataSeries.getTimeDataSets()) {
			RegularTimePeriod regularTimePeriod = new Millisecond(new Date(currTimeDataSet.getTimestampMs()));
			timeSeries.add(regularTimePeriod, currTimeDataSet.getBigDecimalValue());
		}
		return timeSeries;
	}

	public static DefaultCategoryDataset createCategoryDataset(TimeDataSetSeries timeDataSeries) {
		final DefaultCategoryDataset dataset = new DefaultCategoryDataset();

		for (TimeDataSet currTimeDataSet : timeDataSeries.getTimeDataSets()) {
			Long timeInMs = currTimeDataSet.getTimestampMs();
			dataset.setValue(timeInMs, timeDataSeries.getName(), currTimeDataSet.getStringValue());
		}
		return dataset;
	}

}
