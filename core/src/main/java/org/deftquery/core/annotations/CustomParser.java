package org.deftquery.core.annotations;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
public interface CustomParser {

	public String[] getNewColumns();// Ex: - start_timestamp, end_timestamp

	public void setNewColumnValues();// Ex: - from timestamp create
										// start_timestamp, end_timestamp

	public String[] getNewGroupByParams();// Ex: - introduces hourly, daily,
											// monthly grouping

	public String getQueryPredicate();// Should be used to prepare where clause

	public boolean isValidFilter();// Should be used to validate filters

}
