package com.smeo.tools.common;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.Validate;

/**
 * represents a set of <code>TimeDataSet</code> and associates it with a
 * name.
 */

public class TimeDataSetSeries implements Comparable<TimeDataSetSeries>, Serializable {
	private static final long serialVersionUID = 5950315143998319360L;
	private static List<TimeDataSetSeries> allDataSetSeries = new ArrayList<TimeDataSetSeries>();

	private String name;
	private Set<TimeDataSet> timeDataSets;
	private long startTime;
	private long endTime;

	public Set<TimeDataSet> getTimeDataSets() {
		return timeDataSets;
	}

	public TimeDataSetSeries(String name) {
		setName(name);
		timeDataSets = new TreeSet<TimeDataSet>();
		allDataSetSeries.add(this);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if either timeInNanos or value is null
	 */
	public void addDataSet(Long timestampMs, Object value) {
		if (startTime > timestampMs || startTime < 0) {
			startTime = timestampMs;
		}
		if (endTime < timestampMs || endTime < 0) {
			endTime = timestampMs;
		}

		timeDataSets.add(new TimeDataSet(timestampMs, value));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		Validate.notNull(name, "name must not be null");
		this.name = name;
	}

	@Override
	public String toString() {
		return (name + "[entries: " + timeDataSets.size() + "/" + new Date(startTime) + "-" + new Date(endTime) + "]");
	}

	public static void saveAllDataSetSeriesToFile(String filename) {
		try {
			System.out.println("Writing " + allDataSetSeries.size() + " dataSetSeries to file " + filename);
			FileOutputStream fout = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fout);
			oos.writeObject(allDataSetSeries);
			oos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static List<TimeDataSetSeries> getAllDataSetSeries() {
		return allDataSetSeries;
	}

	public static List<TimeDataSetSeries> loadDataSetSeriesFromFile(String filename) {
		List<TimeDataSetSeries> dataSetSeries = null;
		try {
			FileInputStream fin = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fin);
			dataSetSeries = (List<TimeDataSetSeries>) ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dataSetSeries;

	}

	/**
	 * Represents a value at a specific point in time
	 */
	public class TimeDataSet implements Comparable<TimeDataSet>, Serializable {
		private static final long serialVersionUID = 1399913081550533470L;
		private Long timstampMs;
		private Object value;

		public TimeDataSet(Long timestampMs, Object value) {
			Validate.notNull(timestampMs, "timeInNanos must not be null");
			Validate.notNull(value, "value must not be null");
			this.timstampMs = timestampMs;
			this.value = value;
		}

		public Long getTimestampMs() {
			return timstampMs;
		}

		public Object getValue() {
			return value;
		}

		public BigDecimal getBigDecimalValue() {
			try {
				if (value instanceof String) {
					return new BigDecimal((String) value);
				} else if (value instanceof Number) {
					return new BigDecimal(value.toString());
				} else {
					return (BigDecimal) value;
				}

			} catch (NumberFormatException e) {
				System.err.println("problems turning string '" + value + "' into a number");
				return null;
			}
		}

		public String getStringValue() {
			return ("" + value);
		}

		public int compareTo(TimeDataSet timeDataSet) {
			return (this.timstampMs.compareTo(timeDataSet.getTimestampMs()));
		}

		@Override
		public String toString() {
			return "TimeDataSet [timstampMs=" + timstampMs + ", value=" + value + "]";
		}

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (endTime ^ (endTime >>> 32));
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (startTime ^ (startTime >>> 32));
		result = prime * result + ((timeDataSets == null) ? 0 : timeDataSets.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeDataSetSeries other = (TimeDataSetSeries) obj;
		if (endTime != other.endTime)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (startTime != other.startTime)
			return false;
		if (timeDataSets == null) {
			if (other.timeDataSets != null)
				return false;
		} else if (!timeDataSets.equals(other.timeDataSets))
			return false;
		return true;
	}

	@Override
	public int compareTo(TimeDataSetSeries o) {
		int result = 0;
		if (startTime != o.startTime) {
			if (startTime < o.startTime) {
				result = 1;
			} else {
				result = -1;
			}
		}

		result = name.compareTo(o.name);
		if (result != 0) {
			return result;
		}
		return result;
	}

}
