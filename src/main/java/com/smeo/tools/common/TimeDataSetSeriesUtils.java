package com.smeo.tools.common;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.smeo.tools.common.TimeDataSetSeries.TimeDataSet;

public class TimeDataSetSeriesUtils {
	/**
	 * creates several timeDataSetSeries from one, split by day
	 * 
	 * @param timeDataSetSeries
	 * @param makeTimeStampsRelativeToDayStart
	 *            if set day,month and year are set to 0
	 * @return
	 */
	public static List<TimeDataSetSeries> splitByDay(TimeDataSetSeries timeDataSetSeries, boolean makeTimeStampsRelativeToDayStart) {
		Map<String, TimeDataSetSeries> timeStringTosplitTimeDataSetSeries = new HashMap<String, TimeDataSetSeries>();
		for (TimeDataSet currDataSet : timeDataSetSeries.getTimeDataSets()) {
			Calendar calender = Calendar.getInstance();
			calender.setTimeInMillis(currDataSet.getTimestampMs());
			String timeString = calender.get(Calendar.DAY_OF_MONTH) + "." + calender.get(Calendar.MONTH);
			if (makeTimeStampsRelativeToDayStart) {
				calender.set(calender.YEAR, 2000);
				calender.set(calender.DAY_OF_MONTH, 00);
				calender.set(calender.MONTH, 00);
			}
			if (!timeStringTosplitTimeDataSetSeries.containsKey(timeString)) {
				timeStringTosplitTimeDataSetSeries.put(timeString, new TimeDataSetSeries(timeDataSetSeries.getName() + "-" + timeString));
			}
			timeStringTosplitTimeDataSetSeries.get(timeString).addDataSet(calender.getTimeInMillis(), currDataSet.getValue());
		}

		List<TimeDataSetSeries> splitTimeDataSetSeries = new ArrayList<TimeDataSetSeries>();
		splitTimeDataSetSeries.addAll(timeStringTosplitTimeDataSetSeries.values());
		Collections.sort(splitTimeDataSetSeries);

		return splitTimeDataSetSeries;
	}
}
