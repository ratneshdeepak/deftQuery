package org.deftquery.core;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class TimeGranularity {

	public static enum Unit {
		MINUTE, HOUR, DAY, WEEK, MONTH, YEAR
	}

	Unit unit;

	int value;

	public TimeGranularity(Unit unit, int value) {
		super();
		this.unit = unit;
		this.value = value;
	}

}
