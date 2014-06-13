package org.deftquery.core;

import java.util.HashMap;
import java.util.LinkedHashSet;

import org.deftquery.core.annotations.Sources;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class DataTable extends DataField {

	private static long l = 1;

	private static synchronized long genNextId() {
		return l++;
	}

	private final String id = "t" + genNextId();

	protected HashMap<String, DataField> fields;
	protected LinkedHashSet<String> keys;

	protected LinkedHashSet<String> getDisplay() {
		return keys;
	}

	protected String catalog;
	protected String schema;
	protected String table;
	protected Sources sources;

	@SuppressWarnings("rawtypes")
	public DataTable(DataTable parent, String name, String dbName,
			String jsonName, Class type) {
		super(parent, name, dbName, jsonName, type);
		fields = new HashMap<String, DataField>();
		keys = new LinkedHashSet<String>();
	}

	public void updateAsTable(String catalog, String schema, String tableName) {
		this.catalog = catalog;
		this.schema = schema;
		this.table = tableName;
	}

	public void updateAsSources(Sources sources) {
		this.sources = sources;
		this.catalog = sources.defaultCatalog();
		this.schema = sources.defaultSchema();
	}

	public void addField(DataField dataField, boolean isKey) {
		this.fields.put(dataField.getName(), dataField);
		if (isKey)
			this.keys.add(dataField.getName());
	}

	public DataField getField(String name) {
		return fields.get(name);
	}

	@Override
	public String toString() {
		return "DataTable [fields=" + fields + ", keys=" + keys + ", catalog="
				+ catalog + ", schema=" + schema + ", sources=" + sources + "]";
	}

	public HashMap<String, DataField> getFields() {
		return fields;
	}

	public LinkedHashSet<String> getKeys() {
		return keys;
	}

	public String getCatalog() {
		return catalog;
	}

	public String getSchema() {
		return schema;
	}

	public String getTable() {
		return table;
	}

	@Override
	public String getId() {
		return id;
	}

	public String getTableWithAlias() {
		return String.format("%s as t%s", table, id);
	}

	public String getTableAlias() {
		return String.format("%s%s", table, id);
	}

	public Sources fetchSources() {
		return sources;
	}

}
