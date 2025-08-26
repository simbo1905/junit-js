# JUnit-JS Repository Overview

## Project Description

JUnit-JS is a JUnit runner for JavaScript tests that enables running JavaScript test suites within Java environments using GraalVM. Originally created by Benji Weber and maintained by Simon Massey, this project has been upgraded to JUnit 5 with vintage mode support, allowing developers to write JavaScript tests and execute them through both JUnit 4 and modern JUnit 5 testing frameworks. The library uses GraalVM Polyglot for optimal JavaScript execution performance on Java 21+.

## File Structure

```
junit-js/
├── src/
│   ├── main/
│   │   ├── java/org/bitbucket/thinbus/junitjs/    # Core Java classes (JSRunner, Tests annotation, etc.)
│   │   └── resources/JUnitJSUtils.js              # JavaScript utilities loaded into test contexts
│   └── test/
│       ├── java/org/bitbucket/thinbus/junitjs/examples/  # Example test suites (JUnit 4 + JUnit 5)
│       └── resources/                             # Example JavaScript test files
├── pom.xml                                        # Maven build configuration with JUnit 5 + vintage support
├── README.md                                      # Comprehensive usage documentation
└── LICENSE.txt                                    # MIT license
```

## Running Tests and Development

**Build the project:**
```bash
mvn clean compile
```

**Run tests:**
```bash
mvn test
```

Note: The example test suite (`ExampleTestSuite`) contains intentionally failing tests and uses a non-standard naming convention to be ignored by Maven's surefire plugin. To run these examples, execute them manually in your IDE.

**Key Maven features:**
- **JUnit 5 Platform**: Automatic detection of both JUnit 4 (vintage) and JUnit 5 (jupiter) engines
- **Enhanced compiler warnings**: All warnings treated as errors for improved code quality
- **GraalVM Polyglot**: Optimized JavaScript execution on Java 21+

## Getting Started for New Developers

1. **Dependencies**: This is a Maven project requiring Java 21+ and Maven 3.6+
2. **JUnit 5 Support**: The project supports both JUnit 4 JavaScript tests (via vintage engine) and modern JUnit 5 Java tests side-by-side
3. **JavaScript Engine**: Uses GraalVM Polyglot for optimal JavaScript execution performance
4. **Test Structure**: 
   - **JavaScript tests**: Create Java test classes annotated with `@Tests` listing JavaScript files, then use `@RunWith(JSRunner.class)`
   - **JUnit 5 tests**: Use modern JUnit 5 annotations like `@Test`, `@DisplayName`, `@Nested` for Java tests
5. **JavaScript Testing**: Write tests using the `tests({})` function with JUnit assertions available via the global `assert` object
6. **Utilities**: The `JUnitJSUtils.js` provides additional JavaScript-specific assertions and stubbing capabilities

The project includes comprehensive examples in the test directory showing how to structure both JUnit 4 JavaScript test runners and modern JUnit 5 Java tests, demonstrating full compatibility between both testing approaches.End

## Software Engineering Practices

### Issue/Commit/PR Guidelines

**Issues** MUST only state "what" and "why":

- MUST outline which functionality is either needed or broken
- MUST NOT speculate or get excited or state the reasons or benefits or any other "sell the idea". 
- MUST NOT offer solution approaches, implementation details, or technical specifics in the body. Comments are discussions on the issue and comments and discusssions on a PR are the only appropriate place for "how" or "approach". 
- MUST focus on business requirements and user impact without justifying the need or impact just state as cold facts what is aimed at
- MUST NOT have detours; fresh issues/improvements must be raised as new Issues. A PR (below) may close many issues but we MUST NOT have scope/creep on any issue while.
- MUST NOT EVER be edited by you the agent. You many only Comment on issues asking for me to edit them to correct them based on the actual work done.
- MAY have comments that accurately reflect the work being done as the work is being done do not wait until you need to push a PR to raise to discuss how the implimentation work may require rescoping of the Issue.
- You MAY raise fresh minor Issues for small tidy-up work as you go. This must be named in the Commit(s) and PR(s) below. 

**Commits** MUST only state "what" was achieved and "how" to test:

- MUST name the issues/issue numbers being worked on
- MUST NOT repeat any content that is on the Issue
- MAY contain a link to the issue
- MUST give a clear indication if more commits will follow. 
- MUST say how to verify the changes work (test commands, expected number of successful test results, naming number of new tests, and their names)
- MAY ouytline some technical implementation details ONLY if they are suprising and not "obvious in hindsight" based on just reading the issue
- MUST NOT EVER Include Co-authored-by for AI assistance or any advertising whatsoever.

**Pull Requests** MUST only describe "what" was done not "why"/"how":

- MUST only be created if all tests pass locally.
- MUST name and link to the Issue(s) being closed.
- MUST close the issues on merge as uses the correct GitHub closes issue syntax
- MUST NOT include any edits or work that is not linked to a Issue as you MAY raise fresh minor Issues for small tidy-up work.
- MUST not be made if there are any diviation between the Issue and the PR rather you MAY raise a comments on the issue for them to be updated to properly match the PR and you MAY raise small tidy-up issues (e.g. fixing )
- MUST NOT report any success as it isn't possible to report that the checks run the PR will pass when creating the PR.
- MUST list out what additional tests in total have been added.
- MUST have a CI action that runs the new tests, plus all tests, and also checks that the total count of tests reported in the PR were seen to have been actually run.
- MUST NOT Highlight important implementation decisions those should have been raised and discussed as comments on the Issue
- MUST be changed to status Draft if the PR checks fail
  
CRITICAL you MUST NOT make a PR just because your TASKS.md say to make a PR. The only valid reason to make a PR is that it would close the Issue(s) named in the PR which must be issues named in the commit.

**CRITICAL: Zero Broken Tests Policy**

- MUST NOT create a PR with failing tests
- MUST be changed to status Draft if the PR checks fail
- The CI MUST verify exact test counts to prevent silent failures
- Any test failures MUST be fixed before marking PR as ready for review it must be in Draft state before it is fixed. 


**NEVER modify existing quality code when debugging tests!** Instead:

1. **Preserve Original Code**: Keep the original working code intact (e.g., don't change loop counts from 8 to 1)
2. **Create Separate Debug Tests**: Build new minimal test files from scratch to isolate issues
3. **Manual Baby Steps**: Add one line at a time with console.log, timing, and sleep statements (never sleep for long times on local code use single or low double digit seconds)
4. **Line-by-Line Logging**: Debug the manual way with detailed logging at each step
5. **Build Up Incrementally**: Start with nothing and build up to a working test step by step

This approach prevents breaking working code and allows systematic isolation of the exact hanging point. The original author spent significant effort creating quality software - respect that by debugging properly without shortcuts.

**SOFTWARE ENGINEERING PATTERN (CRITICAL):**

When debugging complex issues, follow this pattern religiously:

1. **Baby steps first** - get the core functionality working step by step in debug files
2. **Keep everything in sync** - the moment you see an error in baby steps, immediately fix it in BOTH baby steps AND all real test files
3. **Iterate until baby works** - only when baby steps are completely solid do you run the full test suite
4. **Final cleanup** - with baby steps proving no blockers, you only have a few final glitches to iron out

This prevents wasting time on broken approaches and ensures systematic progress. Also agents crash, forget, and get distracted, so the systematic methodology ensures work gets done systematically. Don't run full suites until baby steps prove the approach works.


**Critical Timeout Debug Script** (save as `debug_test.sh`):

```bash
#!/bin/bash
# Debug script with timeout to prevent hanging tests from blocking development
mvn test -Dtest="TestDebugSHA256" > test-debug-step.log 2>&1 &
TEST_PID=$!
echo "Testing debug step, PID: $TEST_PID"

# Wait 20 seconds (tests normally take 10-15s)
for i in {1..20}; do
    if ! kill -0 "$TEST_PID" 2>/dev/null; then
        echo "Debug step completed in $i seconds"
        wait "$TEST_PID"
        echo "Exit code: $?"
        break
    fi
    sleep 1
done

if kill -0 "$TEST_PID" 2>/dev/null; then
    echo "Debug step hanging after 20 seconds, killing..."
    kill -TERM "$TEST_PID" 2>/dev/null || true
    sleep 2
    kill -KILL "$TEST_PID" 2>/dev/null || true
fi

echo "Debug output:"
grep "DEBUG:" test-debug-step.log | tail -3
```

This script prevents infinite hangs and provides immediate feedback on where tests are failing.

## Code Style and Conventions

- **Java**:
  - MUST use Standard Java conventions
  - MUST use JUnit4 for unit-js Java Script Tests 
  - MUST use JUnit5 for any standalone fresh tests of Java
  - MUST Comprehensive JavaDoc comments using the new `/// markdown docs` format when writing/editing javadoc
  - SHOULD Immutable objects where possible

- **JavaScript**:
  - You MUST not edit upstream JavaScript files only testing shims and test should be in this repo.
  - Follows standard JavaScript conventions
  - MUST NOT modfiy or minify or change the versions downlaoded from thinbus-srp@2.0.2  
  - MUST Avoid modifying global scope
  - MUST only have new new JavaScript files in this repo to setup Java<->JavaScript interop
  - MUST use the junit-js latest version for testing and follow its testing patterns. 
