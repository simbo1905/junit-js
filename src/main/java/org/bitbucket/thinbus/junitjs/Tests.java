package org.bitbucket.thinbus.junitjs;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Annotation to specify JavaScript test files to run.
 */
@Retention(RUNTIME)
public @interface Tests {
	/**
	 * Array of JavaScript test file names to execute.
	 * @return the test file names
	 */
	String[] value();
}
