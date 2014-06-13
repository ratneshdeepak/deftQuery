package org.deftquery.core;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.deftquery.core.io.TimePeriod;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class TimeBasedQueryPlan {

	private SortedMap<TimeBasedAggregation, List<TimePeriod>> sourcePredicate = new TreeMap<TimeBasedAggregation, List<TimePeriod>>();

	public void put(TimeBasedAggregation aggregation, List<TimePeriod> periods) {
		sourcePredicate.put(aggregation, periods);
	}

	public void print() {
		for (Map.Entry<TimeBasedAggregation, List<TimePeriod>> entry : sourcePredicate
				.entrySet()) {
			System.out.println(entry.getKey() + ":\n\t" + entry.getValue());
		}
	}

	public Set<TimeBasedAggregation> keySet() {
		return sourcePredicate.keySet();
	}

	public List<TimePeriod> get(TimeBasedAggregation aggregation) {
		return sourcePredicate.get(aggregation);
	}
}
