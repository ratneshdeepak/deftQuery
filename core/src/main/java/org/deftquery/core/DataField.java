package org.deftquery.core;

import java.util.HashMap;

import org.deftquery.core.annotations.Metric;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
@SuppressWarnings("rawtypes")
public class DataField {

	private static long l = 1;

	private static synchronized long genNextId() {
		return l++;
	}

	private final String id = "f" + genNextId();

	public DataField(DataTable parent, String name, String dbName,
			String jsonName, Class type, Metric.Type metricType) {
		this(parent, name, dbName, jsonName, type);
		this.metricType = metricType;
	}

	public DataField(DataTable parent, String name, String dbName,
			String jsonName, Class type) {
		super();
		this.parent = parent;
		this.name = name;
		this.dbName = dbName;
		this.jsonName = jsonName;
		this.type = type;
	}

	protected HashMap<String, String> contextField = new HashMap<String, String>();
	protected String contextKey;

	public void setContext(String contextKey,
			HashMap<String, String> contextField) {
		this.contextKey = contextKey;
		this.contextField = contextField;
	}

	public String getDbExpresion(HashMap<String, String> context) {
		return getExpresion(getDbName(context));
	}

	public String getUnionExpresion(HashMap<String, String> context) {
		return getExpresion(getId());
	}

	private String getExpresion(String dbName) {
		if (metricType == null)
			return dbName;
		switch (metricType) {
		case MAX:
			return String.format("max(%s)", dbName);
		case MIN:
			return String.format("min(%s)", dbName);
		case SUM:
			return String.format("sum(%s)", dbName);
		default:
			return dbName;
		}
	}

	public String getDbName(HashMap<String, String> context) {
		String contextName;
		if (contextKey == null)
			return getDbName();
		else if ((contextName = context.get(contextKey)) != null) {
			if (contextField.containsKey(contextName)) {
				return contextField.get(contextName);
			} else {
				throw new RuntimeException(String.format(
						"%s not available in %s %s context", name, contextName,
						contextKey));
			}
		} else
			throw new RuntimeException(String.format(
					"%s cannot be accessed without %s context", name,
					contextKey));
	}

	protected final DataTable parent;
	protected final String name;
	protected final String dbName;
	protected final String jsonName;
	protected final Class type;
	protected Metric.Type metricType;

	public String getName() {
		return name;
	}

	public String getJsonName() {
		return jsonName;
	}

	public String getDbName() {
		return (parent == null ? "" : parent.getId() + ".") + dbName;
	}

	public Metric.Type getMetricType() {
		return metricType;
	}

	public boolean isMetric() {
		return metricType != null;
	}

	public Class getType() {
		return type;
	}

	public String getContextKey() {
		return contextKey;
	}

	public DataTable fetchParent() {
		return parent;
	}

	public String getId() {
		return id;
	}

	@Override
	public String toString() {
		return "DataField [name=" + name + ", dbName=" + dbName + ", jsonName="
				+ jsonName + ", type=" + type + "]";
	}

}
