package org.deftquery.core;

import java.util.HashMap;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
@SuppressWarnings("rawtypes")
public class ApiRegistry {

	private HashMap<String, FieldBuilder> hashMap = new HashMap<String, FieldBuilder>();

	public ApiRegistry(Class... apiClasses) {
		if (apiClasses != null)
			for (Class clazz : apiClasses) {
				push(clazz);
			}
	}

	protected void push(Class apiClass) {
		FieldBuilder builder = new FieldBuilder(apiClass);
		System.out.println("Registering new api: "+ builder.getApiName());
		hashMap.put(builder.getApiName(), builder);
	}

	public ApiRegistry(String... apiClasses) throws ClassNotFoundException {
		if (apiClasses != null)
			for (String className : apiClasses) {
				push(Class.forName(className));
			}
	}

	public FieldBuilder getFieldBuilder(String apiName) {
		return hashMap.get(apiName);
	}
}
