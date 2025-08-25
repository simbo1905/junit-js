package org.bitbucket.thinbus.junitjs;

import java.util.List;

/**
 * Represents a collection of JavaScript test cases from a single test file.
 */
public class TestClass {
	/** The list of test cases in this test class. */
	public final List<TestCase> testCases;
	/** The name of the test class. */
	public final String name;

	/**
	 * Creates a new test class.
	 * @param name the name of the test class
	 * @param testCases the list of test cases in this class
	 */
	public TestClass(String name, List<TestCase> testCases) {
		this.testCases = testCases;
		this.name = name;
	}

	/**
	 * Returns the JUnit-formatted name for this test class.
	 * @return the JUnit name format
	 */
    public String junitName() {
        return name.replaceAll("(.*)\\.(.*)","$2.$1");
    }
	
}
