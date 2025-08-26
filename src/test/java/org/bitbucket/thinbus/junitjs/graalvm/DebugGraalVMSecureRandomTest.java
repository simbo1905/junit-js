package org.bitbucket.thinbus.junitjs.graalvm;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * DEBUG TEST - Baby step 4: Secure random number generation from JavaScript
 * 
 * This test verifies that JavaScript can generate secure random numbers using Java crypto classes.
 * Following AGENTS.md guidance for baby steps debugging approach.
 */
public class DebugGraalVMSecureRandomTest {

    public static class RandomFunction {
        private final java.security.SecureRandom secureRandom = new java.security.SecureRandom();
        
        public byte[] randomBytes(int length) {
            byte[] bytes = new byte[length];
            secureRandom.nextBytes(bytes);
            return bytes;
        }
        
        public String randomHex(int byteLength) {
            byte[] bytes = randomBytes(byteLength);
            StringBuilder hex = new StringBuilder();
            for (byte b : bytes) {
                hex.append(String.format("%02x", b & 0xFF));
            }
            return hex.toString();
        }
        
        public int randomInt() {
            return secureRandom.nextInt();
        }
        
        public int randomIntBounded(int bound) {
            return secureRandom.nextInt(bound);
        }
    }

    @Test
    public void testSecureRandomGeneration() {
        System.out.println("DEBUG: Starting secure random generation test");
        
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                .allowHostClassLookup(className -> {
                    // Allow access to Java crypto and utility classes
                    return className.startsWith("java.security") ||
                           className.startsWith("javax.crypto") ||
                           className.equals("java.nio.charset.StandardCharsets") ||
                           className.equals("java.util.HexFormat") ||
                           className.equals("java.lang.String");
                })
                .build()) {
            
            System.out.println("DEBUG: GraalVM Context created with crypto access");
            
            // Provide random function to JavaScript
            RandomFunction randomFunction = new RandomFunction();
            context.getBindings("js").putMember("javaRandomFunction", randomFunction);
            
            // Test 1: Generate random hex string
            String jsCode1 = """
                javaRandomFunction.randomHex(16);
                """;
            
            Value result1 = context.eval("js", jsCode1);
            String randomHex = result1.asString();
            System.out.println("DEBUG: Random hex (16 bytes) = " + randomHex);
            
            // Verify it's a valid hex string of correct length (32 chars for 16 bytes)
            assertNotNull("Random hex should not be null", randomHex);
            assertEquals("Random hex should be 32 characters", 32, randomHex.length());
            assertTrue("Random hex should be valid hex", randomHex.matches("[0-9a-f]{32}"));
            
            // Test 2: Generate another random hex string and verify they're different
            Value result2 = context.eval("js", jsCode1);
            String randomHex2 = result2.asString();
            System.out.println("DEBUG: Random hex (16 bytes) #2 = " + randomHex2);
            assertNotEquals("Two random hex strings should be different", randomHex, randomHex2);
            
            // Test 3: Generate random integer
            String jsCode3 = """
                javaRandomFunction.randomInt();
                """;
            
            Value result3 = context.eval("js", jsCode3);
            int randomInt = result3.asInt();
            System.out.println("DEBUG: Random int = " + randomInt);
            
            // Test 4: Generate bounded random integer
            String jsCode4 = """
                javaRandomFunction.randomIntBounded(100);
                """;
            
            Value result4 = context.eval("js", jsCode4);
            int boundedInt = result4.asInt();
            System.out.println("DEBUG: Random int (0-99) = " + boundedInt);
            assertTrue("Bounded random should be >= 0", boundedInt >= 0);
            assertTrue("Bounded random should be < 100", boundedInt < 100);
            
            // Test 5: Generate multiple bounded randoms to verify they can be different
            Value result5 = context.eval("js", jsCode4);
            int boundedInt2 = result5.asInt();
            System.out.println("DEBUG: Random int (0-99) #2 = " + boundedInt2);
            assertTrue("Bounded random #2 should be >= 0", boundedInt2 >= 0);
            assertTrue("Bounded random #2 should be < 100", boundedInt2 < 100);
            
            // Test 6: Generate random bytes directly
            String jsCode6 = """
                javaRandomFunction.randomBytes(8);
                """;
            
            Value result6 = context.eval("js", jsCode6);
            assertTrue("Random bytes should be returned as array-like", result6.hasArrayElements());
            long arraySize = result6.getArraySize();
            assertEquals("Random bytes array should have 8 elements", 8, arraySize);
            
            System.out.println("DEBUG: Secure random functionality test completed successfully");
        } catch (Exception e) {
            System.err.println("DEBUG: Error in secure random test: " + e.getMessage());
            e.printStackTrace();
            fail("Secure random functionality test failed: " + e.getMessage());
        }
    }
}