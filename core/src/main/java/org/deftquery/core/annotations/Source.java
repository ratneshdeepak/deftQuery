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
@Target(ElementType.TYPE)
public @interface Source {

	String catalog() default "";

	String schema() default "";

	String table();

	String parseAs() default "";

	int priority() default 100;

	int reliability() default 100;

	int sla() default 0;

}
