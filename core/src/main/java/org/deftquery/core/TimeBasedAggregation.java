package org.deftquery.core;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TimeZone;
import java.util.TreeMap;

import org.deftquery.core.annotations.Source;
import org.deftquery.core.annotations.Sources;
import org.deftquery.core.io.TimePeriod;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public enum TimeBasedAggregation {
	NONE,

	HALF_HOURLY,

	HOURLY,

	DAILY,

	MONTHLY;

	static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	static final int MILLIS = 1000;
	static final int HOUR_IN_MILLIS = 60 * 60 * MILLIS;
	static final int HALF_HOUR_IN_MILLIS = 30 * 60 * MILLIS;

	/**
	 * Returns the start position of time based on archival policy
	 */
	public long min() {
		switch (this) {
		case NONE:
			return 0L;
		case HALF_HOURLY:

			break;
		case HOURLY:

			break;
		case DAILY:

			break;
		case MONTHLY:

			break;

		default:
			throw new RuntimeException("Undefined");
		}
		return 0L;
	}

	/**
	 * Returns the end position of time based on computation interval
	 */
	public long max() {
		return 0L;
	}

	public TimeGranularity granularity() {
		switch (this) {
		case NONE:
			return new TimeGranularity(null, 0);
		case HALF_HOURLY:
			return new TimeGranularity(TimeGranularity.Unit.MINUTE, 30);
		case HOURLY:
			return new TimeGranularity(TimeGranularity.Unit.HOUR, 1);
		case DAILY:
			return new TimeGranularity(TimeGranularity.Unit.DAY, 1);
		case MONTHLY:
			return new TimeGranularity(TimeGranularity.Unit.MONTH, 1);
		default:
			throw new RuntimeException("Undefined");
		}
	}

	/**
	 * 
	 * @param factClass
	 * @param startTimeInMillis
	 * @param endTimeInMillis
	 * @param fieldsRequired
	 *            union of all the fields requirement from select, predicate,
	 *            group by and order by
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static TimeBasedQueryPlan getQueryPlan(Class factClass,
			long startTimeInMillis, long endTimeInMillis,
			TimeGranularity granularity, String... fieldsRequired) {
		Sources sources = (Sources) factClass.getAnnotation(Sources.class);
		startTimeInMillis = startTimeInMillis > 0 ? startTimeInMillis
				: 1325376000 * 1000L;
		endTimeInMillis = endTimeInMillis > 0 ? endTimeInMillis
				: TimeBasedAggregation.HALF_HOURLY.getServesBefore(System
						.currentTimeMillis());
		TimeBasedQueryPlan plan = new TimeBasedQueryPlan();
		if (sources == null) {
			// Check if Union or Choice is defined
			// In case of union need to get data from all the classes in union
			// In case of choice it should be treated as another data source
		} else {
			SortedMap<String, TimeBasedAggregation> map = new TreeMap<String, TimeBasedAggregation>();
			for (Source source : sources.value()) {
				TimeBasedAggregation aggregation = TimeBasedAggregation
						.valueOf(source.parseAs());
				String key = (1000 - source.priority()) + ":"
						+ aggregation.name();
				map.put(key, aggregation);
			}
			List<TimePeriod> predicate = new ArrayList<TimePeriod>();
			predicate.add(new TimePeriod(startTimeInMillis, endTimeInMillis));
			for (String key : map.keySet()) {
				TimeBasedAggregation aggregation = map.get(key);
				List<TimePeriod> serves = new ArrayList<TimePeriod>();
				List<TimePeriod> remaining = new ArrayList<TimePeriod>();
				for (TimePeriod period : predicate) {
					long servableFrom = aggregation.getServesFrom(period
							.getFrom());
					long servableBefore = aggregation.getServesBefore(period
							.getTo());
					// System.out.println(aggregation + " "
					// + new TimePeriod(servableFrom, servableBefore));
					if (servableFrom >= servableBefore) {
						remaining.add(period);
					} else {
						serves.add(new TimePeriod(servableFrom, servableBefore));
						if (servableFrom != period.getFrom()) {
							remaining.add(new TimePeriod(period.getFrom(),
									servableFrom));
						}
						if (servableBefore != period.getTo()) {
							remaining.add(new TimePeriod(servableBefore, period
									.getTo()));
						}
					}

				}
				if (serves.size() > 0)
					plan.put(aggregation, serves);
				predicate = remaining;
				if (remaining.size() == 0)
					break;
			}
			if (predicate.size() != 0)
				throw new RuntimeException(
						"No source available to fetch remaining data: "
								+ predicate);
		}
		return plan;
	}

	public long getServesFrom(long from) {
		DateTime d = new DateTime(from, DateTimeZone.UTC);
		switch (this) {
		case DAILY: {
			if (d.getMillisOfDay() == 0)
				return from;
			else
				return d.minus(d.getMillisOfDay()).plusDays(1).getMillis();
		}
		case MONTHLY: {
			if (d.getDayOfMonth() == 1 && d.getMillisOfDay() == 0)
				return from;
			else {
				return d.minus(d.getMillisOfDay())
						.minusDays(d.getDayOfMonth() - 1).plusMonths(1)
						.getMillis();
			}
		}
		case HALF_HOURLY: {
			if (d.getMillisOfDay() % HALF_HOUR_IN_MILLIS == 0)
				return from;
			else {
				return d.minus(d.getMillisOfDay() % HALF_HOUR_IN_MILLIS)
						.plusMinutes(30).getMillis();
			}
		}
		case HOURLY: {
			if (d.getMillisOfDay() % HOUR_IN_MILLIS == 0)
				return from;
			else {
				return d.minus(d.getMillisOfDay() % HOUR_IN_MILLIS)
						.plusMinutes(60).getMillis();
			}
		}
		case NONE:
			return from;
		default:
			return -1;
		}
	}

	public long getServesBefore(long before) {
		DateTime d = new DateTime(before, DateTimeZone.UTC);
		switch (this) {
		case DAILY: {
			if (d.getMillisOfDay() == 0)
				return before;
			else
				return d.minus(d.getMillisOfDay()).getMillis();
		}
		case MONTHLY: {
			if (d.getMillisOfDay() == 0 && d.getDayOfMonth() == 1)
				return before;
			else {
				return d.minus(d.getMillisOfDay())
						.minusDays(d.getDayOfMonth() - 1).getMillis();
			}
		}
		case HALF_HOURLY: {
			if (d.getMillisOfDay() % HALF_HOUR_IN_MILLIS == 0)
				return before;
			else {
				return d.minus(d.getMillisOfDay() % HALF_HOUR_IN_MILLIS)
						.getMillis();
			}
		}
		case HOURLY: {
			if (d.getMillisOfDay() % HOUR_IN_MILLIS == 0)
				return before;
			else {
				return d.minus(d.getMillisOfDay() % HOUR_IN_MILLIS).getMillis();
			}
		}
		case NONE:
			return before;
		default:
			return -1;
		}
	}

	public String getPredicate(long from, long to) {
		switch (this) {
		case HALF_HOURLY:
			return String
					.format(" ts_utc_day >= %s and ts_utc_day < %s and timestamp >= %s and timestamp < %s ",
							DAILY.getServesBefore(from) / MILLIS,
							DAILY.getServesFrom(to) / MILLIS, from / MILLIS, to
									/ MILLIS);
		case DAILY:
			return String
					.format(" ts_utc_month >= %s and ts_utc_month < %s and timestamp >= %s and timestamp < %s ",
							MONTHLY.getServesBefore(from) / MILLIS,
							MONTHLY.getServesFrom(to) / MILLIS, from / MILLIS,
							to / MILLIS);
		case MONTHLY:
			return String.format(" timestamp >= %s and timestamp < %s ", from
					/ MILLIS, to / MILLIS);
		default:
			return "";
		}
	}
}
