package org.deftquery.core.io;

import java.io.Serializable;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class Predicate implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3751439873525143574L;

	public static final String NAME_COLUMN = "name";

	private String column;

	private Operator operator;

	private Object value;

	public Predicate() {
		super();
	}

	public Predicate(String column, Operator operator, Object value) {
		super();
		this.column = column;
		this.operator = operator;
		this.value = value;
	}

	public String getColumn() {
		return column;
	}

	public void setColumn(String column) {
		this.column = column;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object object) {
		if (object == this) {
			return true;
		}
		if (object == null || object.getClass() != this.getClass()) {
			return false;
		}

		Predicate filter = (Predicate) object;
		return this.column.equals(filter.column)
				&& this.operator.equals(filter.operator)
				&& this.value.equals(filter.value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((column == null) ? 0 : column.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return "Predicate [column=" + column + ", operator=" + operator
				+ ", value=" + value + "]";
	}

}
