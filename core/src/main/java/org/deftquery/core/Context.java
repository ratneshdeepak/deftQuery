package org.deftquery.core;

import org.deftquery.core.io.DataRecord;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */

public class Context {

	public Context() {
		// TODO Auto-generated constructor stub
	}

	public static Context getContext() {
		return new Context();
	}

	public DataRecord getRecord() {
		return new DataRecord();
	}

}
