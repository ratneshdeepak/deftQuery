package org.deftquery.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
// can use in class only.
// Also means fact table
public @interface Sources {

	@SuppressWarnings("rawtypes")
	Class parser();

	String defaultCatalog() default "";

	String defaultSchema() default "";

	Source[] value();

}
