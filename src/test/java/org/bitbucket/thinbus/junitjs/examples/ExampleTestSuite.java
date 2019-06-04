package org.bitbucket.thinbus.junitjs.examples;

import org.bitbucket.thinbus.junitjs.JSRunner;
import org.bitbucket.thinbus.junitjs.Tests;
import org.junit.runner.RunWith;

/**
 * Note this test has deliberate failures so it is given a none standard name so that it is ingored by mvn surefire plugin!
 */
@Tests({
	"ExampleTestOne.js",
	"ExampleTestTwo.js",
	"TestFileUnderTest.js"
})
@RunWith(JSRunner.class)
public class ExampleTestSuite {
	
}
