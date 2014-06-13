package org.deftquery.core.io;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class TimePeriod {

	private long from;
	private long before;

	public long getFrom() {
		return from;
	}

	public long getTo() {
		return before;
	}

	public TimePeriod(long from, long before) {
		super();
		this.from = from;
		this.before = before;
	}

	@Override
	public String toString() {
		return new DateTime(from, UTC) + " to " + new DateTime(before, UTC);
	}

	DateTimeZone UTC = DateTimeZone.UTC;

}
