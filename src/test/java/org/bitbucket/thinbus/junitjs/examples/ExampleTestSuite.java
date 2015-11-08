package org.bitbucket.thinbus.junitjs.examples;

import org.bitbucket.thinbus.junitjs.JSRunner;
import org.bitbucket.thinbus.junitjs.Tests;
import org.junit.runner.RunWith;

@Tests({
	"ExampleTestOne.js",
	"ExampleTestTwo.js",
	"TestFileUnderTest.js"
})
@RunWith(JSRunner.class)
public class ExampleTestSuite {
	
}
