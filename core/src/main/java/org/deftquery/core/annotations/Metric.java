package org.deftquery.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Metric {

	public enum Type {
		SUM, MIN, MAX, UNIQUE
	}

	Type value() default Type.SUM;

}
