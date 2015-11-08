# JUnit-JS :: JUnit Runner for Javascript Tests using Nashorn

Copyright (c) 2000 - 2014 Benji Weber

Copyright (c) 2015 Simon

This is a fork of https://github.com/benjiman/junit-js to get it up onto maven central. This version will only be tested on JDK1.8 as the Nashorn is 10x faster than Rhino in testing the Java cryptography of [thinbus-srp-js](https://bitbucket.org/simon_massey/thinbus-srp-js). 

Here is a picture of it in action running the Thinbus SRP crypogrophy tests: 

![Thinbus SRP JUnitJS](http://simon_massey.bitbucket.org/thinbus/junit-js.png "Thinbus SRP JUnitJS)

## Maven Dependency

```
	<dependency>
		<groupId>org.bitbucket.thinbus</groupId>
		<artifactId>junit-js</artifactId>
		<version>1.0.0</version>
	</dependency>
```

## Using

See the example tests in this repo or better yet take a look at the sophisticated tests over at [thinbus-srp-js](https://bitbucket.org/simon_massey/thinbus-srp-js). A quick outline is to create an empty test suite with annotations of the javascript test files which are to be run: 


```
#!java

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

```

In the javascript test files: 

```
#!javascript

// load the scripts you want to test
load("src/main/webapp/js/amaze-balls.js");

tests({
	// org.junit.Assert is imported by default
	thisTestShouldPass : function() {
		console.log("One == One");
		assert.assertEquals("One","One");
	},
	/*
	thisTestShouldFail : function() {
		console.log("Running a failing test");
		assert.fail();
	},
        */
	// this equals works as no implicit conversions involved 
	objectEquality : function() {
		var a = { foo: 'bar', bar: 'baz' };
		var b = a;
		assert.assertEquals(a, b);
	},
	// this methods use javascript '===' and '==' respectively
	javascriptComparison : function() {
		// no implicit conversion
		jsAssert.assertEqualNoCoercion(4, 4);
		// this works because of javascript implicit conversion
		jsAssert.assertEqualCoercion("4", 4);
	}
});
```

Note that the file "JUnitJSUtils.js" is loaded out of the classpath into each test script context. This defines a `jsAssert.assertEqualNoCoercion` and `jsAssert.assertEqualCoercion` shown above. There is also a `newStub()` function that creates function which records whatever methods you invoke up it and their parameters see `TestFileUnderTest.js`.