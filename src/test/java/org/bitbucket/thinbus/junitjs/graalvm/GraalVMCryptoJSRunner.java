package org.bitbucket.thinbus.junitjs.graalvm;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.bitbucket.thinbus.junitjs.TestCase;
import org.bitbucket.thinbus.junitjs.TestClass;
import org.bitbucket.thinbus.junitjs.Tests;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.PolyglotException;
import org.graalvm.polyglot.Value;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Filterable;
import org.junit.runner.manipulation.NoTestsRemainException;
import org.junit.runner.manipulation.Sortable;
import org.junit.runner.manipulation.Sorter;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

/**
 * Custom JSRunner that provides crypto functionality to JavaScript tests
 * 
 * Based on the standard JSRunner but adds a crypto helper object to the
 * JavaScript context, enabling CryptoJS-like functionality using Java crypto classes.
 */
public class GraalVMCryptoJSRunner extends Runner implements Filterable, Sortable {

    private List<TestClass> tests;
    private final Class<?> cls;
    private Context context;

    /**
     * Helper class to provide crypto functionality to JavaScript
     */
    public static class CryptoHelper {
        private final java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        
        public String sha256(String text) {
            try {
                java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
                byte[] hashBytes = digest.digest(text.getBytes(java.nio.charset.StandardCharsets.UTF_8));
                
                StringBuilder hex = new StringBuilder();
                for (byte b : hashBytes) {
                    hex.append(String.format("%02x", b & 0xFF));
                }
                return hex.toString();
            } catch (Exception e) {
                throw new RuntimeException("SHA256 hashing failed", e);
            }
        }
        
        public String randomHex(int byteLength) {
            byte[] bytes = new byte[byteLength];
            secureRandom.nextBytes(bytes);
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b & 0xFF));
            }
            return hex.toString();
        }
        
        public int randomInt(int bound) {
            return secureRandom.nextInt(bound);
        }
    }

    /**
     * Utility class for loading JavaScript test files.
     */
    public static class Loader {
        
        private final Context context;

        /**
         * Creates a new Loader with the given JavaScript context.
         * @param context the GraalVM JavaScript context
         */
        public Loader(Context context) {
            this.context = context;
        }
        
        /**
         * Loads a JavaScript test file from the classpath.
         * @param filename the name of the JavaScript file to load
         */
        public void load(String filename) {
            try {
                Path filePath = Paths.get(filename);
                String script = Files.readString(filePath, StandardCharsets.UTF_8);
                context.eval("js", script);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public GraalVMCryptoJSRunner(Class<?> cls) {
        this.cls = cls;
        this.context = Context.newBuilder("js")
                .allowHostAccess(HostAccess.ALL)
                .allowHostClassLookup(s -> true)
                .build();
        List<String> testNames = asList(cls.getAnnotation(Tests.class).value());
        this.tests = findJSTests(testNames);
    }

    @Override
    public Description getDescription() {
        Description suite = Description.createSuiteDescription(cls);
        for (TestClass testClass : tests) {
            Description testClassDescription = Description.createSuiteDescription(testClass.name);
            for (TestCase testCase : testClass.testCases) {
                testClassDescription.addChild(Description.createTestDescription(cls, testCase.name));
            }
            suite.addChild(testClassDescription);
        }
        return suite;
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            for (TestClass testClass : tests) {
                for (TestCase testCase : testClass.testCases) {
                    Description description = Description.createTestDescription(cls, testCase.name);
                    notifier.fireTestStarted(description);
                    try {
                        testCase.testCase.run();
                        notifier.fireTestFinished(description);
                    } catch (Throwable e) {
                        notifier.fireTestFailure(new Failure(description, bestException(e)));
                    }
                }
            }
        } finally {
            // Close the context after all tests are done
            if (context != null) {
                context.close();
            }
        }
    }

    private List<TestClass> findJSTests(List<String> testNames) {
        try {
            loadTestUtilities(context);
            List<TestClass> testClasses = new ArrayList<TestClass>();
            for (String name : testNames) {
                testClasses.add(new TestClass(name, load(context, name)));
            }
            return testClasses;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void loadTestUtilities(Context context) throws IOException {
        try (InputStream is = GraalVMCryptoJSRunner.class.getResourceAsStream("/JUnitJSUtils.js")) {
            String utilsScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);
            context.eval("js", utilsScript);
        }
    }

    private void setupJavaScriptEnvironment(Context context) {
        // Set up the load function for JavaScript files
        Value bindings = context.getBindings("js");
        bindings.putMember("Loader", new Loader(context));
        context.eval("js", "function load(filename) { Loader.load(filename); }");
        
        // Add crypto helper to JavaScript context
        CryptoHelper crypto = new CryptoHelper();
        bindings.putMember("crypto", crypto);
    }

    @SuppressWarnings("unchecked")
    private List<TestCase> load(Context context, String name) throws IOException {
        setupJavaScriptEnvironment(context);
        try (InputStream s = GraalVMCryptoJSRunner.class.getResourceAsStream("/" + name)) {
            String script = new String(s.readAllBytes(), StandardCharsets.UTF_8);
            Value result = context.eval("js", script);
            return result.as(List.class);
        }
    }

    public void sort(Sorter sorter) {
        //
    }

    public void filter(Filter filter) throws NoTestsRemainException {
        //
    }

    private Throwable bestException(Throwable e) {
        if (graalvmException(e)) {
            PolyglotException pe = (PolyglotException) e;
            if (pe.isHostException()) {
                return pe.asHostException();
            }
        }
        return e;
    }

    private boolean graalvmException(Throwable e) {
        return e instanceof PolyglotException;
    }
}