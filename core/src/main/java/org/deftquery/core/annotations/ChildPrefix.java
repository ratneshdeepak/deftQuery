package org.deftquery.core.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author Ratnesh Kumar Deepak <ratnesh.deepak@komli.com>
 * 
 *         Prefix which should be given to children entities
 * 
 *         Should also state the translation strategy for each field
 * 
 *         Should be used by the exposing class only not by the inner classes to
 *         avoid any confusion
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
// can use in method only.
public @interface ChildPrefix {

}
