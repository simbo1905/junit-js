package org.bitbucket.thinbus.junitjs.graalvm;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DEBUG TEST - Baby step 2: Java crypto interop from JavaScript
 * 
 * This test verifies that JavaScript can access Java crypto classes through GraalVM Polyglot.
 * Following AGENTS.md guidance for baby steps debugging approach.
 */
public class DebugGraalVMCryptoInteropTest {

    @Test
    public void testJavaCryptoInterop() {
        System.out.println("DEBUG: Starting Java crypto interop test");
        
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                .allowHostClassLookup(className -> {
                    // Allow access to Java crypto classes
                    return className.startsWith("java.security") ||
                           className.startsWith("javax.crypto") ||
                           className.equals("java.nio.charset.StandardCharsets");
                })
                .build()) {
            
            System.out.println("DEBUG: GraalVM Context created with crypto access");
            
            // Test 1: Access MessageDigest class
            Value result = context.eval("js", 
                "var MessageDigest = Java.type('java.security.MessageDigest'); " +
                "MessageDigest !== null && MessageDigest !== undefined");
            System.out.println("DEBUG: MessageDigest accessible = " + result.asBoolean());
            assertTrue("MessageDigest should be accessible", result.asBoolean());
            
            // Test 2: Create MessageDigest instance
            Value digestResult = context.eval("js", 
                "var MessageDigest = Java.type('java.security.MessageDigest'); " +
                "var digest = MessageDigest.getInstance('SHA-256'); " +
                "digest !== null");
            System.out.println("DEBUG: SHA-256 MessageDigest created = " + digestResult.asBoolean());
            assertTrue("SHA-256 MessageDigest should be created", digestResult.asBoolean());
            
            // Test 3: Access SecureRandom class
            Value randomResult = context.eval("js", 
                "var SecureRandom = Java.type('java.security.SecureRandom'); " +
                "var random = new SecureRandom(); " +
                "random !== null");
            System.out.println("DEBUG: SecureRandom created = " + randomResult.asBoolean());
            assertTrue("SecureRandom should be created", randomResult.asBoolean());
            
            // Test 4: Test basic crypto operation - get algorithm name
            Value algorithmResult = context.eval("js", 
                "var MessageDigest = Java.type('java.security.MessageDigest'); " +
                "var digest = MessageDigest.getInstance('SHA-256'); " +
                "digest.getAlgorithm()");
            System.out.println("DEBUG: Algorithm name = " + algorithmResult.asString());
            assertEquals("Algorithm should be SHA-256", "SHA-256", algorithmResult.asString());
            
            System.out.println("DEBUG: Java crypto interop test completed successfully");
        } catch (Exception e) {
            System.err.println("DEBUG: Error in crypto interop test: " + e.getMessage());
            e.printStackTrace();
            fail("Java crypto interop test failed: " + e.getMessage());
        }
    }
}