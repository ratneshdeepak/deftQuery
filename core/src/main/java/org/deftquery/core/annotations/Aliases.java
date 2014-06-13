package org.deftquery.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 *         It can be used to imitate the "alternative to" or "nicknamed from"
 *         behavior
 * 
 *         Example @Alias(name = "advertiser_name", source = "advertiser.name")
 * 
 *         Depth 0 means do not expose anything except what has been aliased
 *         Depth 1 means expose till 1st level and all that has been aliased so
 *         on...
 * 
 *         We can also use group regex here... Ex: @Alias(name = "av_(1)",
 *         source = "advertiser.(*)")
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.FIELD })
public @interface Aliases {

	Alias[] value() default {};

	int depth() default 0;

}
