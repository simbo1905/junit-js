package org.bitbucket.thinbus.junitjs.graalvm;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DEBUG TEST - Baby step 1: Basic GraalVM JavaScript execution
 * 
 * This is a minimal debug test to verify GraalVM Context can execute basic JavaScript.
 * Following AGENTS.md guidance for baby steps debugging approach.
 */
public class DebugGraalVMBasicTest {

    @Test
    public void testBasicJavaScriptExecution() {
        System.out.println("DEBUG: Starting basic GraalVM JavaScript execution test");
        
        try (Context context = Context.newBuilder("js").build()) {
            System.out.println("DEBUG: GraalVM Context created successfully");
            
            // Test 1: Simple arithmetic
            Value result = context.eval("js", "1 + 1");
            System.out.println("DEBUG: 1 + 1 = " + result.asInt());
            assertEquals("Basic arithmetic should work", 2, result.asInt());
            
            // Test 2: String operation
            Value stringResult = context.eval("js", "'Hello' + ' ' + 'World'");
            System.out.println("DEBUG: String concatenation = " + stringResult.asString());
            assertEquals("String concatenation should work", "Hello World", stringResult.asString());
            
            // Test 3: Simple function
            context.eval("js", "function testFunc() { return 'GraalVM works'; }");
            Value funcResult = context.eval("js", "testFunc()");
            System.out.println("DEBUG: Function result = " + funcResult.asString());
            assertEquals("Function execution should work", "GraalVM works", funcResult.asString());
            
            System.out.println("DEBUG: Basic GraalVM JavaScript execution test completed successfully");
        } catch (Exception e) {
            System.err.println("DEBUG: Error in basic GraalVM test: " + e.getMessage());
            e.printStackTrace();
            fail("Basic GraalVM JavaScript execution failed: " + e.getMessage());
        }
    }
}