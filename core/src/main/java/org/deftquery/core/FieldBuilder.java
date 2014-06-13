package org.deftquery.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

import org.deftquery.core.annotations.Alias;
import org.deftquery.core.annotations.ColumnContext;
import org.deftquery.core.annotations.ColumnName;
import org.deftquery.core.annotations.Duraton;
import org.deftquery.core.annotations.Metric;
import org.deftquery.core.annotations.Source;
import org.deftquery.core.annotations.Sources;
import org.deftquery.core.io.DataRequest;
import org.deftquery.core.io.Operator;
import org.deftquery.core.io.Predicate;
import org.deftquery.core.io.ResultData;
import org.deftquery.core.io.TimePeriod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public class FieldBuilder {
	private static final Logger logger = LoggerFactory
			.getLogger(FieldBuilder.class);

	DataTable dataTable;
	HashMap<String, ExtendedDataField> baseFields = new HashMap<String, ExtendedDataField>();
	ExtendedDataField durationField = null;
	Duraton duration = null;

	public final DataTable getDataTable() {
		return dataTable;
	}

	public final Set<String> getBaseKeys() {
		return baseFields.keySet();
	}

	public class AmbiguousDataField extends DataField {
		protected final int depth;

		HashSet<DataField> fields = new HashSet<DataField>();

		public AmbiguousDataField(int depth) {
			super(null, null, null, null, null);
			this.depth = depth;
		}

		public void add(DataField field) {
			fields.add(field);
		}
	}

	public boolean exists(String key) {
		return getDataField(key) != null;
	}

	public DataField getDataField(String key) {
		if (!baseFields.containsKey(key))
			return null;
		DataField field = baseFields.get(key).field;
		if (field instanceof AmbiguousDataField)
			return null;
		else
			return field;
	}

	public void addDataField(ExtendedDataField dataField) {
		DataField field = dataField.field;
		int depth = dataField.depth;
		String jsonKey = field.getJsonName();
		// baseFields.put(dataField.getJsonName(), );
		baseFields.put(jsonKey, dataField);
		String[] parts = jsonKey.split("\\.");
		int length = parts.length;
		if (length > 0) {
			String baseKey = parts[length - 1];
			for (int i = parts.length - 2; i >= 0; i--) {
				baseKey = parts[i] + "_" + baseKey;
				ExtendedDataField current = baseFields.get(baseKey);
				if (current == null) {
					baseFields.put(baseKey, dataField.clone());
				} else {
					if (current.clone == false) {
						continue;
					} else if (current.field instanceof AmbiguousDataField) {
						AmbiguousDataField af = (AmbiguousDataField) current.field;
						if (af.depth < depth)
							continue;
						else if (af.depth > depth)
							baseFields.put(baseKey, dataField.clone());
						else {
							af.add(current.field);
						}
					} else {
						if (current.depth < depth)
							continue;
						else if (current.depth > depth)
							baseFields.put(baseKey, dataField.clone());
						else {
							AmbiguousDataField af = new AmbiguousDataField(
									depth);
							af.add(current.field);
							af.add(field);
						}
					}
				}
			}
		}
	}

	public static class ExtendedDataField {
		protected DataField field;
		protected int depth;
		protected final boolean clone;

		public ExtendedDataField(DataField field, int depth) {
			super();
			this.field = field;
			this.depth = depth;
			this.clone = false;
		}

		private ExtendedDataField(DataField field, int depth, boolean clone) {
			super();
			this.field = field;
			this.depth = depth;
			this.clone = clone;
		}

		public ExtendedDataField clone() {
			return new ExtendedDataField(field, depth, true);
		}
	}

	public void appendNodes(DataField field) {
		while (field.fetchParent() != null) {
			addNodes(field.fetchParent());
			field = field.fetchParent();
		}
	}

	// TODO: If we can maintain the tree then join on can be used
	private SortedSet<DataTable> nodes1 = new TreeSet<DataTable>(
			new Comparator<DataTable>() {

				@Override
				public int compare(DataTable o1, DataTable o2) {
					ExtendedDataField f1 = baseFields.get(o1.getJsonName());
					ExtendedDataField f2 = baseFields.get(o2.getJsonName());
					if (f1 == null && f2 == null)
						return 0;
					else if (f1 == null)
						return -1;
					else if (f2 == null)
						return 1;
					return Integer.compare(f1.depth, f2.depth);
				}
			});

	public Set<DataTable> getNodes() {
		return nodes1;
	}

	private void addNodes(DataTable table) {
		nodes1.add(table);
	}

	public String getJoinTables() {
		StringBuilder builder = new StringBuilder();
		int i;
		i = 0;
		for (DataTable table : getNodes()) {
			if (i++ != 0)
				builder.append(", ");
			builder.append(table.getTable()).append(" as ")
					.append(table.getId());
		}
		return builder.toString();
	}

	public String getJoinClause() {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for (DataTable table : getNodes()) {
			// Assuming its single key
			if (table.fetchParent() != null) {
				if (i++ > 0)
					builder.append(" and ");
				DataField key = table.getField(table.keys
						.toArray(new String[] {})[0]);
				builder.append(table.getDbName()).append("=")
						.append(key.getDbName());
			}
		}
		return builder.toString();
	}

	public String getValueAccessKey(String jsonName) {
		return baseFields.get(jsonName).field.getId();
	}

	// http://docs.jboss.org/hibernate/core/3.6/reference/en-US/html_single/#events
	// http://docs.jboss.org/hibernate/orm/4.3/manual/en-US/html/ch20.html#cacheproviders
	// http://hibernate.org/orm/contribute/
	public Map<String, String> validationErrors(DataRequest request) {
		LinkedHashMap<String, String> errorMessages = new LinkedHashMap<String, String>();
		// Select
		for (String s : request.getColumns()) {
			if (!exists(s)) {
				errorMessages.put("columns." + s,
						String.format("%s used in select doesn't exist", s));
			}
		}
		// Predicate
		for (Predicate p : request.getFilters()) {
			String s = p.getColumn();
			if (!exists(s)) {
				errorMessages.put("filters." + s,
						String.format("%s used in predicate doesn't exist", s));
			}
		}
		// Group
		for (String s : request.getGroupBy()) {
			if (!exists(s)) {
				errorMessages.put("groupBy." + s,
						String.format("%s used in grouping doesn't exist", s));
			}
		}
		// Order
		for (String s : request.getSortBy()) {
			s = s.replaceFirst("[+-]", "");
			if (!exists(s)) {
				errorMessages.put("sortBy." + s,
						String.format("%s used in ordering doesn't exist", s));
			}
		}
		if (request.getDuration() != null) {
			if (duration == null)
				errorMessages.put("duration",
						"no timestamp field found to support time range");
			else {
				/*
				 * if (false) { // TODO: Time range validation should go here. }
				 * else
				 */
			}
		}

		System.out.println(errorMessages.toString().replace(",", "\n"));
		return errorMessages;
	}

	public String getQuery(DataRequest request) {
		DataTable table = this.dataTable;
		Sources sources = table.fetchSources();
		String baseQuery;
		if (sources != null) {
			TimePeriod period = request.getDuration();
			long startTime = period == null ? 1357043400 * 1000L : period
					.getFrom();
			long endTime = period == null ? 1398403800 * 1000L : period.getTo();
			TimeBasedQueryPlan plan = TimeBasedAggregation.getQueryPlan(
					table.getType(), startTime, endTime, null);
			String innerQueryTemplate = getQuery(request, table, false);
			String outerQueryTemplate = getQuery(request, table, true);
			StringBuilder innerQuery = new StringBuilder("(\n\t(");
			int j = 0;
			for (TimeBasedAggregation aggregation : plan.keySet()) {
				Source aggSource = null;
				for (Source source : sources.value()) {
					if (aggregation.name().equals(source.parseAs()))
						aggSource = source;
				}
				StringBuilder subQueryPredicate = new StringBuilder("(");
				List<TimePeriod> periods = plan.get(aggregation);
				int i = 0;
				for (TimePeriod tp : periods) {
					if (i++ != 0)
						subQueryPredicate.append(") or (");
					subQueryPredicate.append(aggregation.getPredicate(
							tp.getFrom(), tp.getTo()));
				}
				subQueryPredicate.append(")");
				if (j++ != 0)
					innerQuery.append(") \n\tUNION \n\t(");
				innerQuery.append(String.format(innerQueryTemplate,
						aggSource.table(), subQueryPredicate));
			}
			innerQuery.append(")\n) A" + System.currentTimeMillis());
			// System.out.println(outerQueryTemplate);
			baseQuery = String
					.format(outerQueryTemplate, innerQuery, " (1=1) ");
		} else {
			// /table.getTableWithAlias()
			String queryTemplate = getQuery(request, table, null);
			String joinClause = getJoinClause();
			baseQuery = String.format(queryTemplate, getJoinTables(),
					joinClause.length() == 0 ? " (1 = 1) " : joinClause);

		}
		if (request.getPageSize() > 0 && request.getStartIndex() > -1) {
			return String.format("%s limit %s, %s", baseQuery,
					request.getStartIndex(), request.getPageSize());
		} else
			return baseQuery;

	}

	private String getQuery(DataRequest request, DataTable table,
			Boolean outerQuery) {
		LinkedHashSet<String> groupNames = new LinkedHashSet<String>(
				request.getGroupBy());
		updateGroupSelects(request, table, groupNames);
		updateSelectGroups(request, table, groupNames);
		String selects = getSelectFields(request, table, outerQuery);
		String predicates = getPredicates(request, table);
		String groups = getGroups(groupNames, table);
		String orders = getOrders(request, table);
		StringBuilder query = new StringBuilder("select ");
		query.append(selects).append(" from ").append(" %s ");
		if (predicates.length() > 0)
			query.append(" where ").append(" %s and ").append(predicates);
		else
			query.append(" where ").append(" %s ");
		if (groups.length() > 0)
			query.append(" group by ").append(groups);
		if (!Boolean.FALSE.equals(outerQuery) && orders.length() > 0)
			query.append(" order by ").append(orders);
		return query.toString();
	}

	public String getSelectFields(DataRequest request, DataTable table,
			Boolean outerQuery) {
		StringBuilder builder = new StringBuilder();
		for (String column : request.getColumns()) {
			// Assume same level fields
			DataField field = getDataField(column);
			if (field == null) {
				// Do exceptions
			} else {
				String selectString = Boolean.TRUE.equals(outerQuery) ? field
						.getUnionExpresion(request.getContext()) : field
						.getDbExpresion(request.getContext());
				builder.append(String.format(", %s as %s", selectString,
						field.getId()));// field.getJsonName()
				appendNodes(field);
			}
		}
		if (builder.length() > 0)
			return builder.substring(1);
		return builder.toString();
	}

	public void updateGroupSelects(DataRequest request, DataTable table,
			LinkedHashSet<String> groupNames) {
		for (String column : groupNames) {
			// Assume same level fields
			DataField field = getDataField(column);
			if (field == null) {
				// Do exceptions
			} else if (field instanceof DataTable) {
				for (String key : ((DataTable) field).keys)
					request.getColumns().add(key);
			} else if (!field.isMetric()) {
				request.getColumns().add(column);
			}
		}
	}

	public void updateSelectGroups(DataRequest request, DataTable table,
			LinkedHashSet<String> groupNames) {
		for (String column : request.getColumns()) {
			// Assume same level fields
			DataField field = getDataField(column);
			if (field == null) {
				// Do exceptions
			} else if (!field.isMetric()) {
				groupNames.add(column);
			}
		}
	}

	public String getGroups(LinkedHashSet<String> groupbys, DataTable table) {
		StringBuilder builder = new StringBuilder();
		System.out.println(groupbys);
		for (String column : groupbys) {
			// Assume same level fields
			DataField field = getDataField(column);
			if (field == null) {
				// Do exceptions
			} else if (field instanceof DataTable) {
				DataTable ft = (DataTable) field;
				for (String key : ft.keys) {
					DataField kf = getDataField(key);
					if (kf != null) {
						builder.append(", ").append(kf.getId());
						appendNodes(kf);
					}
				}
			} else {
				builder.append(", ").append(field.getId());
				appendNodes(field);
			}

			System.out.println(column + " : " + builder);
		}
		if (builder.length() > 0)
			return builder.substring(1);
		return builder.toString();
	}

	public String getOrders(DataRequest request, DataTable table) {
		StringBuilder builder = new StringBuilder();
		for (String column : request.getSortBy()) {
			// Assume same level fields
			boolean ascending = true;
			String fieldName = column;
			if (fieldName.startsWith("-")) {
				fieldName = fieldName.substring(1);
				ascending = false;
			} else if (fieldName.startsWith("+")) {
				fieldName = fieldName.substring(1);
				ascending = true;
			}
			
			DataField field = getDataField(fieldName);
			if (field == null) {
				// Do exceptions
			} else if (field instanceof DataTable) {
				DataTable ft = (DataTable) field;
				for (String key : ft.getDisplay()) {
					DataField kf = getDataField(key);
					if (kf != null) {
						builder.append(", ").append(kf.getId())
								.append(ascending ? " ASC " : " DESC ");
						appendNodes(kf);
					}
				}
			} else {
				builder.append(", ").append(field.getId())
						.append(ascending ? " ASC " : " DESC ");
				appendNodes(field);
			}
		}
		if (builder.length() > 0)
			return builder.substring(1);
		return builder.toString();
	}

	public String getPredicates(DataRequest request, DataTable table) {
		StringBuilder builder = new StringBuilder();
		LinkedHashSet<Predicate> predicates = new LinkedHashSet<Predicate>(
				request.getFilters());
		if (request.getDuration() != null) {
			TimePeriod period = request.getDuration();
			if (period.getFrom() != 0)
				predicates.add(new Predicate(durationField.field.jsonName,
						Operator.gte, period.getFrom()));
			if (period.getTo() != 0)
				predicates.add(new Predicate(durationField.field.jsonName,
						Operator.lt, period.getTo()));
		}
		for (Predicate predicate : predicates) {
			// Assume same level fields
			DataField field = getDataField(predicate.getColumn());
			if (field == null) {
				// Do exceptions
			} else {
				if (builder.length() > 0)
					builder.append(" and ");
				builder.append(String.format(predicate.getOperator()
						.getVSqlPatten(), field.getDbName(), predicate
						.getValue()));
				appendNodes(field);
			}
		}
		if (builder.length() > 0)
			return builder.substring(1);
		return builder.toString();
	}

	public void printQueryPlan() throws Exception {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		long startTime = format.parse("2014-05-22 18:30").getTime();
		long endTime = format.parse("2014-08-22 18:00").getTime();
		System.out.println(new TimePeriod(startTime, endTime));
		TimeBasedQueryPlan plan = TimeBasedAggregation.getQueryPlan(
				dataTable.getClass(), startTime, endTime, null);
		plan.print();
	}

	public ResultData getResult(DataRequest request) {
		validateRequest();
		preprocessRequest();
		String query = generateQuery();
		fetchQueryData(query);
		return new ResultData();
	}

	private boolean validateRequest() {
		return true;
	}

	private void preprocessRequest() {

	}

	private String generateQuery() {
		return null;
	}

	private HashMap<String, Object> fetchQueryData(String query) {
		return null;
	}

	@SuppressWarnings({ "rawtypes", "unused" })
	private final HashSet<Class> processed;
	private final int MAX_DEPTH;
	private final NamingStrategy jsonNamingStrategy = NamingStrategy.CAMELCASE_TO_UNDERSCORE;

	public static enum NamingStrategy {
		CAMELCASE_TO_UNDERSCORE
	}

	@SuppressWarnings({ "rawtypes" })
	public FieldBuilder(Class klass) {
		MAX_DEPTH = 5;
		processed = new HashSet<Class>();
		process(klass, new Stack<Method>(), null);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void process(Class klass, Stack<Method> stack, DataTable dataTable) {
		if (MAX_DEPTH < stack.size())
			return;
		Table table = (Table) klass.getAnnotation(Table.class);
		Alias tableAlias = (Alias) klass.getAnnotation(Alias.class);
		Sources sources = (Sources) klass.getAnnotation(Sources.class);
		if (dataTable == null) {
			String jsonName = tableAlias == null ? klass.getName() : tableAlias
					.value();
			String tableName = table == null ? klass.getSimpleName() : table
					.name();
			dataTable = new DataTable(null, tableName, null, jsonName, klass);
			this.dataTable = dataTable;
		}
		if (sources != null && table != null) {
			throw new RuntimeException(String.format(
					"Only one of the %s or %s should be present in %s",
					Table.class, Sources.class, klass));
		} else if (sources != null) {
			dataTable.updateAsSources(sources);
			if (sources.value() == null || sources.value().length == 0) {
				throw new RuntimeException(
						"source list should not be null or empty");
			}
			for (Source source : sources.value()) {
				logger.debug(
						"{}----> Source Name: {} Catalog: {}",
						new Object[] { stack.size(), source.table(),
								source.catalog() });
			}
		} else if (table != null) {
			dataTable.updateAsTable(table.catalog(), table.schema(),
					table.name());
			logger.debug("{}----> Table Name: {} Catalog: {}", new Object[] {
					stack.size(), table.name(), table.catalog() });
		} else {
			// Check that It only has derived fields and check if all
			// dependencies are met
		}
		/*
		 * for (Field field : klass.getDeclaredFields()) { Class type =
		 * field.getClass(); String extField = getFieldName(field.getName());
		 * String dbField = extField;// Default is same as field name
		 * LinkedHashSet<Field> keys = new LinkedHashSet<Field>(); for
		 * (Annotation annotation : field.getAnnotations()) { if (annotation
		 * instanceof Id) { keys.add(field); } if (annotation instanceof Column)
		 * { Column column = (Column) annotation; dbField = column.name(); } }
		 * boolean isTable = type.getAnnotation(Table.class) instanceof Table;
		 * logger.debug("{}      Field API: {} DB: isTable {} Type: {}", new
		 * Object[] { stack.size(), extField, dbField, isTable, type }); }
		 */

		for (Method method : klass.getDeclaredMethods()) {
			Class type = method.getReturnType();
			String extField = getFieldName(method.getName());
			String dbField = extField;// Default is same as field name
			String jsonName = getJsonName(stack, method);
			boolean isKey = false;
			boolean isTable = type.getAnnotation(Table.class) instanceof Table;
			HashMap<String, String> contextField = new HashMap<String, String>();
			String contextKey = null;
			Metric.Type metricType = null;
			Duraton duration = null;
			for (Annotation annotation : method.getAnnotations()) {
				if (annotation instanceof Id) {
					isKey = true;
				}
				if (annotation instanceof Column) {
					Column column = (Column) annotation;
					dbField = column.name();
				}
				if (annotation instanceof Metric) {
					Metric metric = (Metric) annotation;
					metricType = metric.value();
				}
				if (annotation instanceof Duraton) {
					Duraton dAnnotattion = (Duraton) annotation;
					duration = dAnnotattion;
				}
				if (annotation instanceof ColumnContext) {
					ColumnContext context = (ColumnContext) annotation;
					contextKey = context.context().getName();
					for (ColumnName columnName : context.value()) {
						contextField.put(columnName.context().getName(),
								columnName.value());
						if (!columnName.globalName().isEmpty()) {
							// Expose new field here ex:-
							// revenue_in_advertiser_currency
						}
					}

				}
			}
			DataField dataField;
			if (isTable) {
				dataField = new DataTable(dataTable, extField, dbField,
						jsonName, type);
			} else {
				dataField = new DataField(dataTable, extField, dbField,
						jsonName, type, metricType);
				dataField.setContext(contextKey, contextField);
			}
			dataTable.addField(dataField, isKey);
			ExtendedDataField extendedDataField = new ExtendedDataField(
					dataField, stack.size());
			addDataField(extendedDataField);
			if (!isTable && duration != null) {
				this.durationField = extendedDataField;
				this.duration = duration;
			}
			logger.debug(
					"{}      Field API: [{} = {}] DB: isTable [{}] Type: [{}] Key: [{}]]",
					new Object[] { stack.size(), extField, jsonName, dbField,
							isTable, type, isKey });
			stack.push(method);
			if (isTable)
				process(type, stack, (DataTable) dataField);
			stack.pop();
		}
	}

	private String getJsonName(Stack<Method> stack, Method method) {
		StringBuilder builder = new StringBuilder();
		for (Method m : stack) {
			builder.append(getJsonName(m)).append(".");
		}
		builder.append(getJsonName(method));
		return builder.toString();
	}

	private String getJsonName(Method method) {
		Alias alias = method.getAnnotation(Alias.class);
		if (alias != null)
			return alias.value();
		String name = getFieldName(method.getName());
		switch (jsonNamingStrategy) {
		case CAMELCASE_TO_UNDERSCORE:
			return camelcaseToUnderscore(name);
		default:
			return name;
		}

	}

	private static String camelcaseToUnderscore(String camelCase) {
		return camelCase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}

	private static String getFieldName(String method) {
		method = method.startsWith("get") ? method.substring(3) : method;
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for (char c : method.toCharArray()) {
			if (first)
				builder.append(String.valueOf(c).toLowerCase());
			else
				builder.append(c);
			first = false;
		}
		return builder.toString();
	}

	public String getApiName() {
		return dataTable.getJsonName();
	}

}
