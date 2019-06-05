# JUnit-JS :: JUnit Runner for Javascript Tests using GraalVM or Nashorn

Copyright (c) 2000 - 2014 Benji Weber

Copyright (c) 2015, 2019 Simon Massey

This is a fork of https://github.com/benjiman/junit-js to get it up onto maven central.

Early versions were tested on JDK1.8 as the Nashorn is 10x faster than Rhino in testing the JavaScript cryptography of [thinbus-srp-js](https://bitbucket.org/simon_massey/thinbus-srp-js).

Nashorn is now deprecated and will be removed from the JDK. OpenJDK supports a faster polygot compiler called GraalVM that has a scripting runtime known as "graal.js". From release 1.1.0 the code now attempts to use graal.js, then falls back to Nashorn, then falls back to Rhino.

Note that the Graal scripting engine on OpenJDK 11 needs openjdk to be configured to use Graal as its compiler else it will be slower than Nashorn. I found this easy to do on the commandline but not easy to do in an IDE. See the following articles:

https://medium.com/graalvm/graalvms-javascript-engine-on-jdk11-with-high-performance-3e79f968a819

https://medium.com/graalvm/oracle-graalvm-announces-support-for-nashorn-migration-c04810d75c1f

Currently this project programmatically sets `-Dpolyglot.js.nashorn-compat=true` in a static initializer. That allows Graal to honour Nashorn code load methods that are now seen as insecure. That is probably fine in test code but not in production code.

When Nashorn is finally removed and GraalVM is more widely in use I may refactor the code so that it doesn't need `nashorn-compat` to be set.

## Maven Dependency

```
	<dependency>
		<groupId>org.bitbucket.thinbus</groupId>
		<artifactId>junit-js</artifactId>
		<version>1.1.0</version>
	</dependency>
```

## Using

See the example tests in this repo in `ExampleTestSuite` that deliberately have failing tests to show that tests can fail. So the build wont run them you have to run them manually (e.g., in your IDE). Better yet take a look at the sophisticated tests over at [thinbus-srp-js](https://bitbucket.org/simon_massey/thinbus-srp-js). A quick outline is to create an empty test suite with annotations of the javascript test files which are to be run:


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