package org.bitbucket.thinbus.junitjs;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

public class JSRunner extends Runner implements Filterable, Sortable  {

	private List<TestClass> tests;
	private final Class<?> cls;
	private Context context;

	public JSRunner(Class<?> cls) {
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
			List<TestCase> tests = testClass.testCases;
			Description desc = Description.createTestDescription(testClass.junitName(), testClass.junitName());
			suite.addChild(desc);
			for (TestCase test : tests) {
				Description methodDesc = Description.createTestDescription(testClass.junitName(), test.name);
				desc.addChild(methodDesc);
			}
		}
		return suite;
	}

	@Override
	public void run(RunNotifier notifier) {
		try {
			for (TestClass testClass : tests) {
				List<TestCase> tests = testClass.testCases;
				for (TestCase test : tests) {
					Description desc = Description.createTestDescription(testClass.junitName(), test.name);
					notifier.fireTestStarted(desc);
					try {
						test.testCase.run();
						notifier.fireTestFinished(desc);
					} catch (Exception | Error e) {
						notifier.fireTestFailure(new Failure(desc, bestException(e)));
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
		try (InputStream is = JSRunner.class.getResourceAsStream("/JUnitJSUtils.js")) {
			String utilsScript = new String(is.readAllBytes(), StandardCharsets.UTF_8);
			context.eval("js", utilsScript);
		}
	}

	public static class Loader {
		
		private final Context context;

		public Loader(Context context) {
			this.context = context;
		}
		
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
	
	private void setupJavaScriptEnvironment(Context context) {
		// Set up the load function for JavaScript files
		Value bindings = context.getBindings("js");
		bindings.putMember("Loader", new Loader(context));
		context.eval("js", "function load(filename) { Loader.load(filename); }");
	}
	
	@SuppressWarnings("unchecked")
	private List<TestCase> load(Context context, String name) throws IOException {
		setupJavaScriptEnvironment(context);
		try (InputStream s = JSRunner.class.getResourceAsStream("/" + name)) {
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
