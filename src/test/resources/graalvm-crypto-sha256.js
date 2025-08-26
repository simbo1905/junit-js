/**
 * GraalVM CryptoJS SHA256 Test
 * 
 * Tests SHA256 hashing functionality using GraalVM's JavaScript engine
 * with Java crypto classes for enhanced security.
 */

// Return test cases using junit-js structure
tests({
    testBasicSHA256: function() {
        var hash = crypto.sha256('hello');
        
        // Verify hash length (64 characters for SHA256)
        if (hash.length !== 64) {
            throw new Error('SHA256 hash should be 64 characters, got: ' + hash.length);
        }
        
        // Verify it's valid hex
        if (!/^[0-9a-f]{64}$/.test(hash)) {
            throw new Error('SHA256 hash should be valid hex string');
        }
        
        console.log('✓ Basic SHA256 test passed: ' + hash);
    },

    testSHA256Consistency: function() {
        var hash1 = crypto.sha256('test');
        var hash2 = crypto.sha256('test');
        
        if (hash1 !== hash2) {
            throw new Error('Same input should produce same hash: ' + hash1 + ' vs ' + hash2);
        }
        
        console.log('✓ SHA256 consistency test passed');
    },

    testSHA256DifferentInputs: function() {
        var hash1 = crypto.sha256('hello');
        var hash2 = crypto.sha256('world');
        
        if (hash1 === hash2) {
            throw new Error('Different inputs should produce different hashes');
        }
        
        console.log('✓ SHA256 different inputs test passed');
    },

    testSHA256EmptyString: function() {
        var hash = crypto.sha256('');
        
        if (hash.length !== 64) {
            throw new Error('SHA256 hash of empty string should be 64 characters');
        }
        
        // Known SHA256 of empty string
        var expectedEmpty = 'e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855';
        if (hash !== expectedEmpty) {
            throw new Error('SHA256 of empty string should be: ' + expectedEmpty + ', got: ' + hash);
        }
        
        console.log('✓ SHA256 empty string test passed');
    },

    testSHA256KnownValues: function() {
        var testCases = [
            { input: 'hello', expected: '2cf24dba5fb0a30e26e83b2ac5b9e29e1b161e5c1fa7425e73043362938b9824' },
            { input: 'test', expected: '9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08' }
        ];
        
        for (var i = 0; i < testCases.length; i++) {
            var testCase = testCases[i];
            var hash = crypto.sha256(testCase.input);
            
            if (hash !== testCase.expected) {
                throw new Error('SHA256 of "' + testCase.input + '" should be: ' + testCase.expected + ', got: ' + hash);
            }
        }
        
        console.log('✓ SHA256 known values test passed');
    }
});