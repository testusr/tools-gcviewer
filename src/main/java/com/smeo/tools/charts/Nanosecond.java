package com.smeo.tools.charts;

import java.util.Date;

import org.jfree.data.time.Millisecond;

public class Nanosecond extends Millisecond {
	Integer nanosecond;

	public Nanosecond(int nanoseconds, int millisecond, Date date) {
		this(nanoseconds, millisecond, date.getSeconds(), date.getMinutes(), date.getHours(), date.getDay(), date.getMonth(), date.getYear());
	}

	public Nanosecond(int nanoseconds, int milliseconds, int second, int minute, int hour, int day, int month, int year) {
		super(milliseconds, second, minute, hour, day, month, year);
		this.nanosecond = nanoseconds;
	}

	public Integer getNanosecond() {
		return nanosecond;
	}

	@Override
	public int compareTo(Object obj) {
		int result = super.compareTo(obj);
		if (result == 0) {
			Nanosecond nanosecond = (Nanosecond) obj;
			result = getNanosecond().compareTo(nanosecond.getNanosecond());
		}
		return result;
	}

	public int hashCode() {
		int result = 37 * super.hashCode() + nanosecond;
		return result;
	}

	public static Nanosecond createWithNanosSeconds(long nanoseconds) {
		nanoseconds = ((new Date()).getTime() * 1000000) + 453534;
		long milliseconds = nanoseconds / 1000000;

		Date date = new Date(milliseconds);
		long upToSecondsInMilliSeconds = (new Date(date.getYear(), date.getMonth(), date.getDay(), date.getHours(), date.getMinutes(), date.getSeconds()))
				.getTime();

		int nanosecondPart = (int) nanoseconds % 1000000;
		int millisecondPart = (int) (milliseconds - upToSecondsInMilliSeconds);

		return new Nanosecond(nanosecondPart, millisecondPart, date);
	}

}
