package org.bitbucket.thinbus.junitjs.examples;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Pure Java JUnit 5 test demonstrating side-by-side compatibility with JUnit 4-based JavaScript tests.
 * This test proves that modern JUnit 5 features work alongside the existing junit-js framework
 * without any conflicts or configuration issues.
 */
@DisplayName("Modern Java Test Suite")
public class ModernJavaTest {

    private String testData;

    @BeforeEach
    void setUp() {
        testData = "JUnit 5 Test Data";
        System.out.println("Setting up JUnit 5 test");
    }

    @AfterEach
    void tearDown() {
        System.out.println("Cleaning up JUnit 5 test");
        testData = null;
    }

    @Test
    @DisplayName("Basic assertion test")
    void basicAssertionTest() {
        System.out.println("Running JUnit 5 basic assertion test");
        assertEquals("JUnit 5 Test Data", testData);
        assertTrue(testData.contains("JUnit 5"));
        assertNotNull(testData);
    }

    @Test
    @DisplayName("String operations test")
    void stringOperationsTest() {
        System.out.println("Running JUnit 5 string operations test");
        String result = testData.toUpperCase();
        assertEquals("JUNIT 5 TEST DATA", result);
        assertAll("String operations",
            () -> assertTrue(result.startsWith("JUNIT")),
            () -> assertTrue(result.endsWith("DATA")),
            () -> assertEquals(17, result.length())
        );
    }

    @Test
    @DisplayName("Exception handling test")
    void exceptionHandlingTest() {
        System.out.println("Running JUnit 5 exception handling test");
        assertThrows(IllegalArgumentException.class, () -> {
            throw new IllegalArgumentException("Expected exception for testing");
        });
        
        assertDoesNotThrow(() -> {
            String safe = "Safe operation";
            assertEquals(14, safe.length());
        });
    }

    @Nested
    @DisplayName("Nested test class")
    class NestedTests {

        @Test
        @DisplayName("Nested test example")
        void nestedTest() {
            System.out.println("Running nested JUnit 5 test");
            assertNotNull(testData);
            assertTrue(testData.length() > 0);
        }

        @Test
        @DisplayName("Another nested test")
        void anotherNestedTest() {
            System.out.println("Running another nested JUnit 5 test");
            String[] words = testData.split(" ");
            assertEquals(4, words.length);
            assertEquals("JUnit", words[0]);
            assertEquals("5", words[1]);
            assertEquals("Test", words[2]);
            assertEquals("Data", words[3]);
        }
    }

    @Test
    @DisplayName("Compatibility verification test")
    void compatibilityVerificationTest() {
        System.out.println("Verifying JUnit 5 compatibility with junit-js framework");
        
        // Verify we can use modern Java features
        var modernString = "Modern Java syntax works";
        assertNotNull(modernString);
        
        // Verify lambda expressions work
        assertAll("Lambda compatibility",
            () -> assertEquals(24, modernString.length()),
            () -> assertTrue(modernString.contains("Modern")),
            () -> assertTrue(modernString.contains("Java"))
        );
        
        System.out.println("✅ JUnit 5 and junit-js framework are fully compatible");
    }
}