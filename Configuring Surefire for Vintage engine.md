<img src="https://r2cdn.perplexity.ai/pplx-full-logo-primary-dark%402x.png" style="height:64px;margin-right:32px"/>

# Configuring Surefire for Vintage engine

Here's how to configure Maven Surefire to work with JUnit Vintage engine for running both JUnit 4 and JUnit 5 tests:

## Dependencies

Add both engines to your dependencies (not the plugin dependencies)[^1]:

```xml
<dependencies>
    <!-- JUnit 5 API -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 4 (for existing tests) -->
    <dependency>
        <groupId>junit</groupId>
        <artifactId>junit</artifactId>
        <version>4.13.2</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit 5 engine for new tests -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-engine</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
    
    <!-- JUnit Vintage engine for JUnit 4 tests -->
    <dependency>
        <groupId>org.junit.vintage</groupId>
        <artifactId>junit-vintage-engine</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>
```


## Surefire Plugin Configuration

For **Surefire 3.0.0-M5 and later**, the configuration is straightforward[^1]:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M7</version>
        </plugin>
    </plugins>
</build>
```

That's it! With Surefire 3.x, you **don't need to add engines to the plugin dependencies**[^1] - the plugin automatically detects them from your project dependencies.

## For Older Surefire Versions (2.22.x)

If you're using Surefire 2.22.x, you might need the engines in the plugin dependencies[^2]:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>2.22.2</version>
    <dependencies>
        <dependency>
            <groupId>org.junit.vintage</groupId>
            <artifactId>junit-vintage-engine</artifactId>
            <version>5.10.0</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter-engine</artifactId>
            <version>5.10.0</version>
        </dependency>
    </dependencies>
</plugin>
```


## Automatic Engine Detection

Modern Surefire versions (3.x) automatically detect which engines to use based on your test dependencies[^3]:

- **JUnit 4 tests** are detected when `junit:junit` is present and run via the Vintage engine
- **JUnit 5 tests** are detected when `junit-jupiter-api` is present and run via the Jupiter engine


## Your Use Case

For your GraalVM Java/JavaScript testing framework, this setup means:

- **Existing JUnit 4-based tests** with your custom annotations will continue working unchanged
- **New tests** can use JUnit 5 features while still using your custom JavaScript testing annotations
- **No configuration changes** needed for users - Surefire will automatically run both test types

The engines run completely independently, so your custom annotation-driven JavaScript testing will work seamlessly regardless of whether the underlying test class uses JUnit 4 or JUnit 5 patterns.
<span style="display:none">[^10][^4][^5][^6][^7][^8][^9]</span>

<div style="text-align: center">⁂</div>

[^1]: https://stackoverflow.com/questions/62552652/run-both-junit-4-and-junit5-with-maven-surefire-plugin-2020

[^2]: https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit-platform.html

[^3]: https://stackoverflow.com/questions/72697166/maven-surefire-plugin-executes-junit-4-tests-even-though-the-junit-vintage-engi/72697321

[^4]: https://maven.apache.org/surefire/maven-surefire-plugin/usage.html

[^5]: https://docs.junit.org/current/user-guide/

[^6]: https://maven.apache.org/surefire/maven-surefire-plugin/examples/junit.html

[^7]: https://community.developer.atlassian.com/t/problem-running-junit5-tests-with-maven-surefire/36792

[^8]: https://github.com/junit-team/junit5/issues/1425

[^9]: https://www.baeldung.com/maven-cant-find-junit-tests

[^10]: https://www.baeldung.com/junit-5-migration

