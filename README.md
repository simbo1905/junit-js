# JUnit-JS :: JUnit Runner for Javascript Tests using GraalVM Polyglot

Copyright (c) 2000 - 2014 Benji Weber  
Copyright (c) 2015, 2019, 2025 Simon Massey

This was originallly a fork of https://github.com/benjiman/junit-js to get it up onto maven central. Yet that was based on Rhino and Nashorn on Java 8. It has now been upgraded to Java 21 on GraalVM to that JavaScript or EMCAScript can be tested against Java. 

## Breaking Changes in 2.0.0

- **Java 21 minimum requirement** - leverages modern JVM features and GraalVM Polyglot API
- **Enhanced compiler warnings** - all warnings now treated as errors for improved code quality

## Maven Dependency

```xml
<dependency>
    <groupId>org.bitbucket.thinbus</groupId>
    <artifactId>junit-js</artifactId>
    <version>2.0.0</version>
</dependency>
```

**Requirements:**
- Java 21 or higher
- Maven 3.6+ or Gradle 7+

## Using

See the example tests in this repo in `ExampleTestSuite` that deliberately have failing tests to show that tests can fail. The build won't run them automatically - you have to run them manually (e.g., in your IDE). For more sophisticated examples, check out the tests at [thinbus-srp-js](https://bitbucket.org/simon_massey/thinbus-srp-js).

Create a test suite with annotations listing the JavaScript test files to run:

```java
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

In the JavaScript test files:

```javascript
// Load the scripts you want to test
load("src/main/webapp/js/amaze-balls.js");

tests({
    // org.junit.Assert is imported by default
    thisTestShouldPass: function() {
        console.log("One == One");
        assert.assertEquals("One", "One");
    },
    
    /*
    thisTestShouldFail: function() {
        console.log("Running a failing test");
        assert.fail();
    },
    */
    
    // Object equality works with no implicit conversions
    objectEquality: function() {
        var a = { foo: 'bar', bar: 'baz' };
        var b = a;
        assert.assertEquals(a, b);
    },
    
    // JavaScript-specific comparison methods
    javascriptComparison: function() {
        // No implicit conversion (uses '===')
        jsAssert.assertEqualNoCoercion(4, 4);
        // Works with JavaScript implicit conversion (uses '==')
        jsAssert.assertEqualCoercion("4", 4);
    }
});
```

## JavaScript Utilities

The file `JUnitJSUtils.js` is automatically loaded into each test script context, providing:

- `jsAssert.assertEqualNoCoercion()` - strict equality comparison using `===`
- `jsAssert.assertEqualCoercion()` - loose equality comparison using `==`  
- `newStub()` - creates mock functions that record method invocations and parameters

See `TestFileUnderTest.js` for examples of using the stubbing functionality.
