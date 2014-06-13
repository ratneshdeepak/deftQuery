package org.deftquery.core;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public interface DataCharacteristic {

	long min();

	long max();

	TimeGranularity granularity();

}
