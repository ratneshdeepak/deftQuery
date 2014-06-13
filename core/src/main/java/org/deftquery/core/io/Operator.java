package org.deftquery.core.io;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public enum Operator {

	eq("EQUALS"),

	ne("DOES_NOT_EQUALS"),

	like("CONTAINS"),

	begins("BEGINS_WITH"),

	ends("ENDS_WITH"),

	lt("LESS_THAN"),

	lte("LESS_THAN_EQUAL"),

	gt("GREATER_THAN"),

	gte("GREATER_THAN_EQUAL"),

	in("VALUES_IN_COLLECTION"),

	not_in("VALUES_NOT_IN_COLLECTION");

	private Operator(String name) {
		this.name = name;
	}

	private String name;

	public String getName() {
		return name;
	}

	public static Operator getFilterOperator(String optr) {
		if (optr != null) {
			for (Operator o : Operator.values()) {
				if (optr.equalsIgnoreCase(o.toString())) {
					return o;
				}
			}
		}
		return null;
	}

	public String getMySqlPatten() {
		switch (this) {
		case begins:
			return " %s like '%s%' ";
		case ends:
			return " %s like '%%s' ";
		case eq:
			return " %s = '%s' ";
		case gt:
			return " %s > %s ";
		case gte:
			return " %s >= %s ";
		case in:
			return " %s in (%s) ";
		case like:
			return " %s like '%%s%' ";
		case lt:
			return " %s < %s ";
		case lte:
			return " %s <= %s ";
		case ne:
			return " %s <> %s ";
		case not_in:
			return " %s not in (%s) ";
		default:
			throw new RuntimeException("Operator Undefined");
		}
	}

	public String getVSqlPatten() {
		switch (this) {
		case begins:
			return " %s ilike '%s%' ";
		case ends:
			return " %s ilike '%%s' ";
		case eq:
			return " %s = '%s' ";
		case gt:
			return " %s > %s ";
		case gte:
			return " %s >= %s ";
		case in:
			return " %s in (%s) ";
		case like:
			return " %s ilike '%%s%' ";
		case lt:
			return " %s < %s ";
		case lte:
			return " %s <= %s ";
		case ne:
			return " %s <> %s ";
		case not_in:
			return " %s not in (%s) ";
		default:
			throw new RuntimeException("Operator Undefined");
		}
	}

}
