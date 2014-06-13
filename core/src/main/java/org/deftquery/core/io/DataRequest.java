package org.deftquery.core.io;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 */
public class DataRequest implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 122144243808872411L;
	public static final int DEFAULT_PAGE_SIZE = 10;
	public static final int DEFAULT_START_INDEX = 0;

	protected LinkedHashSet<String> columns = new LinkedHashSet<String>();

	protected LinkedHashSet<Predicate> filters = new LinkedHashSet<Predicate>();

	protected LinkedHashSet<String> groupBy = new LinkedHashSet<String>();

	// protected List<ResultFilter> groupingFilters;

	protected LinkedHashSet<String> sortBy = new LinkedHashSet<String>();

	protected Integer pageSize = DEFAULT_PAGE_SIZE;

	protected Integer startIndex = DEFAULT_START_INDEX;

	protected HashMap<String, String> context = new HashMap<String, String>();

	protected String apiName;

	protected String reportId;

	protected TimePeriod duration;

	public DataRequest() {
		this(null);
	}

	public DataRequest(DataRequest reportingRequest) {
		super();
		setRequestParams(reportingRequest);
		addContextFilters();
	}

	protected void addContextFilters() {

	}

	public void setRequestParams(DataRequest reportingRequest) {
		if (reportingRequest == null)
			return;
		if (reportingRequest.columns != null)
			this.columns = reportingRequest.columns;
		if (reportingRequest.filters != null)
			this.filters = reportingRequest.filters;
		if (reportingRequest.groupBy != null)
			this.groupBy = reportingRequest.groupBy;
		if (reportingRequest.sortBy != null)
			this.sortBy = reportingRequest.sortBy;
		if (reportingRequest.pageSize != null)
			this.pageSize = reportingRequest.pageSize;
		if (reportingRequest.startIndex != null)
			this.startIndex = reportingRequest.startIndex;
		if (reportingRequest.reportId != null)
			this.reportId = reportingRequest.reportId;
	}

	public LinkedHashSet<String> getColumns() {
		return columns;
	}

	public LinkedHashSet<Predicate> getFilters() {
		return filters;
	}

	public LinkedHashSet<String> getGroupBy() {
		return groupBy;
	}

	public LinkedHashSet<String> getSortBy() {
		return sortBy;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getStartIndex() {
		return startIndex;
	}

	public HashMap<String, String> getContext() {
		return context;
	}

	public String getApiName() {
		return apiName;
	}

	public String getReportId() {
		return reportId;
	}

	public TimePeriod getDuration() {
		return duration;
	}

	public void setDuration(TimePeriod duration) {
		this.duration = duration;
	}

	public void setColumns(LinkedHashSet<String> columns) {
		this.columns = columns;
	}

	public DataRequest withColumns(String column, String... columns) {
		this.columns.add(column);
		if (columns != null)
			this.columns.addAll(Arrays.asList(columns));
		return this;
	}

	public void setFilters(LinkedHashSet<Predicate> filters) {
		this.filters = filters;
	}

	public DataRequest withFilters(Predicate predicate, Predicate... predicates) {
		this.filters.add(predicate);
		if (predicates != null)
			this.filters.addAll(Arrays.asList(predicates));
		return this;
	}

	public void setGroupBy(LinkedHashSet<String> groupBy) {
		this.groupBy = groupBy;
	}

	public DataRequest withGroupBys(String groupBy, String... groupBys) {
		this.groupBy.add(groupBy);
		if (groupBys != null)
			this.groupBy.addAll(Arrays.asList(groupBys));
		return this;
	}

	public void setSortBy(LinkedHashSet<String> sortBy) {
		this.sortBy = sortBy;
	}

	public DataRequest withSortBys(String sortBy, String... sortBys) {
		this.sortBy.add(sortBy);
		if (sortBys != null)
			this.sortBy.addAll(Arrays.asList(sortBys));
		return this;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public DataRequest withPageSize(Integer pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	public void setStartIndex(int startIndex) {
		this.startIndex = startIndex;
	}

	public DataRequest withStartIndex(int startIndex) {
		this.startIndex = startIndex;
		return this;
	}

	public void setContext(HashMap<String, String> context) {
		this.context = context;
	}

	public DataRequest withContext(String context, String value) {
		this.context.put(context, value);
		return this;
	}

	public void setApiName(String apiName) {
		this.apiName = apiName;
	}

	public DataRequest withApiName(String apiName) {
		this.apiName = apiName;
		return this;
	}

	public void setReportId(String reportId) {
		this.reportId = reportId;
	}

	@Override
	public String toString() {
		return "DataRequest [columns=" + columns + ", filters=" + filters
				+ ", groupBy=" + groupBy + ", sortBy=" + sortBy + ", pageSize="
				+ pageSize + ", startIndex=" + startIndex + ", context="
				+ context + ", apiName=" + apiName + ", reportId=" + reportId
				+ ", duration=" + duration + "]";
	}

}
