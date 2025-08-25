package org.bitbucket.thinbus.junitjs;

/**
 * Represents a single JavaScript test case.
 */
public class TestCase {

	/** The name of the test case. */
	public final String name;
	/** The runnable test case implementation. */
	public final Runnable testCase;

	/**
	 * Creates a new test case.
	 * @param name the name of the test case
	 * @param testCase the runnable test implementation
	 */
	public TestCase(String name, Runnable testCase) {
		this.name = name;
		this.testCase = testCase;
	}
}
