package org.deftquery.core.io;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataRecord extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4970726625514967814L;

	public DataRecord() {
	}

	public DataRecord(int initialCapacity) {
		super(initialCapacity);
	}

	public DataRecord(Map m) {
		super(m);
	}

	public DataRecord(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

}
