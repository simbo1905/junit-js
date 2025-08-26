package org.bitbucket.thinbus.junitjs.graalvm;

import org.bitbucket.thinbus.junitjs.JSRunner;
import org.bitbucket.thinbus.junitjs.Tests;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.junit.runner.RunWith;

/**
 * GraalVM+NodeJS Polyglot Support for Enhanced JavaScript Testing
 * 
 * This test class demonstrates CryptoJS-like functionality using GraalVM's JavaScript engine
 * with Java crypto classes for SHA256 hashing and secure random number generation.
 * 
 * Following junit-js patterns with @Tests annotation for test discovery.
 */
@Tests({
    "graalvm-crypto-sha256.js",
    "graalvm-crypto-random.js",
    "graalvm-crypto-combined.js"
})
@RunWith(GraalVMCryptoJSRunner.class)
public class GraalVMNodeJSCryptoJSTest {

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
     * Test SHA256 hashing functionality using GraalVM JavaScript engine
     */
    public void testSHA256Hashing() {
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                .build()) {
            
            // Provide crypto helper to JavaScript
            CryptoHelper crypto = new CryptoHelper();
            context.getBindings("js").putMember("crypto", crypto);
            
            // Test SHA256 hashing
            String jsCode = """
                var hash1 = crypto.sha256('hello');
                var hash2 = crypto.sha256('world');
                var hash3 = crypto.sha256('hello'); // Should match hash1
                
                // Return results for verification
                ({
                    hash1: hash1,
                    hash2: hash2,
                    hash3: hash3,
                    hash1Length: hash1.length,
                    hash2Length: hash2.length,
                    hash1EqualsHash3: hash1 === hash3,
                    hash1EqualsHash2: hash1 === hash2
                });
                """;
            
            Value result = context.eval("js", jsCode);
            
            // Verify results
            String hash1 = result.getMember("hash1").asString();
            String hash2 = result.getMember("hash2").asString();
            String hash3 = result.getMember("hash3").asString();
            
            // All hashes should be 64 characters (32 bytes in hex)
            if (result.getMember("hash1Length").asInt() != 64) {
                throw new AssertionError("SHA256 hash should be 64 characters, got: " + result.getMember("hash1Length").asInt());
            }
            if (result.getMember("hash2Length").asInt() != 64) {
                throw new AssertionError("SHA256 hash should be 64 characters, got: " + result.getMember("hash2Length").asInt());
            }
            
            // Same input should produce same hash
            if (!result.getMember("hash1EqualsHash3").asBoolean()) {
                throw new AssertionError("Same input should produce same hash: " + hash1 + " vs " + hash3);
            }
            
            // Different inputs should produce different hashes
            if (result.getMember("hash1EqualsHash2").asBoolean()) {
                throw new AssertionError("Different inputs should produce different hashes: " + hash1 + " vs " + hash2);
            }
            
            System.out.println("✓ SHA256 hashing test passed");
        } catch (Exception e) {
            throw new RuntimeException("SHA256 hashing test failed", e);
        }
    }

    /**
     * Test secure random number generation using GraalVM JavaScript engine
     */
    public void testSecureRandomGeneration() {
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                .build()) {
            
            // Provide crypto helper to JavaScript
            CryptoHelper crypto = new CryptoHelper();
            context.getBindings("js").putMember("crypto", crypto);
            
            // Test random number generation
            String jsCode = """
                var hex1 = crypto.randomHex(16);
                var hex2 = crypto.randomHex(16);
                var int1 = crypto.randomInt(100);
                var int2 = crypto.randomInt(100);
                
                // Return results for verification
                ({
                    hex1: hex1,
                    hex2: hex2,
                    int1: int1,
                    int2: int2,
                    hex1Length: hex1.length,
                    hex2Length: hex2.length,
                    hex1EqualsHex2: hex1 === hex2,
                    int1InRange: int1 >= 0 && int1 < 100,
                    int2InRange: int2 >= 0 && int2 < 100
                });
                """;
            
            Value result = context.eval("js", jsCode);
            
            // Verify hex strings are correct length (32 chars for 16 bytes)
            if (result.getMember("hex1Length").asInt() != 32) {
                throw new AssertionError("Random hex should be 32 characters, got: " + result.getMember("hex1Length").asInt());
            }
            if (result.getMember("hex2Length").asInt() != 32) {
                throw new AssertionError("Random hex should be 32 characters, got: " + result.getMember("hex2Length").asInt());
            }
            
            // Random values should be different (extremely unlikely to be same)
            if (result.getMember("hex1EqualsHex2").asBoolean()) {
                throw new AssertionError("Random hex values should be different (extremely unlikely to be same)");
            }
            
            // Bounded integers should be in range
            if (!result.getMember("int1InRange").asBoolean()) {
                throw new AssertionError("Random int should be in range [0, 100), got: " + result.getMember("int1").asInt());
            }
            if (!result.getMember("int2InRange").asBoolean()) {
                throw new AssertionError("Random int should be in range [0, 100), got: " + result.getMember("int2").asInt());
            }
            
            System.out.println("✓ Secure random generation test passed");
        } catch (Exception e) {
            throw new RuntimeException("Secure random generation test failed", e);
        }
    }

    /**
     * Test combined crypto operations simulating CryptoJS usage patterns
     */
    public void testCombinedCryptoOperations() {
        try (Context context = Context.newBuilder("js")
                .allowHostAccess(org.graalvm.polyglot.HostAccess.ALL)
                .build()) {
            
            // Provide crypto helper to JavaScript
            CryptoHelper crypto = new CryptoHelper();
            context.getBindings("js").putMember("crypto", crypto);
            
            // Test combined operations like a real crypto library would use
            String jsCode = """
                // Simulate generating a salt and hashing a password
                var salt = crypto.randomHex(16);
                var password = 'mySecretPassword';
                var saltedPassword = password + salt;
                var hashedPassword = crypto.sha256(saltedPassword);
                
                // Simulate verifying the password
                var verifyPassword = 'mySecretPassword';
                var verifySaltedPassword = verifyPassword + salt;
                var verifyHashedPassword = crypto.sha256(verifySaltedPassword);
                
                // Return results for verification
                ({
                    salt: salt,
                    hashedPassword: hashedPassword,
                    verifyHashedPassword: verifyHashedPassword,
                    passwordsMatch: hashedPassword === verifyHashedPassword,
                    saltLength: salt.length,
                    hashLength: hashedPassword.length
                });
                """;
            
            Value result = context.eval("js", jsCode);
            
            // Verify salt and hash lengths
            if (result.getMember("saltLength").asInt() != 32) {
                throw new AssertionError("Salt should be 32 characters, got: " + result.getMember("saltLength").asInt());
            }
            if (result.getMember("hashLength").asInt() != 64) {
                throw new AssertionError("Hash should be 64 characters, got: " + result.getMember("hashLength").asInt());
            }
            
            // Password verification should work
            if (!result.getMember("passwordsMatch").asBoolean()) {
                throw new AssertionError("Password verification should work with same salt");
            }
            
            System.out.println("✓ Combined crypto operations test passed");
        } catch (Exception e) {
            throw new RuntimeException("Combined crypto operations test failed", e);
        }
    }
}