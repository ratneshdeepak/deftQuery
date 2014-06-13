package org.deftquery.core.io;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class InContext {

	public String type;

	public String value;

	private InContext(String type, String value) {
		super();
		this.type = type;
		this.value = value;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
