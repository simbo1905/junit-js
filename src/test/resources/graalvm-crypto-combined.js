/**
 * GraalVM CryptoJS Combined Operations Test
 * 
 * Tests combined crypto operations simulating real-world usage patterns
 * like password hashing with salts, token generation, etc.
 */

// Test password hashing with salt
function testPasswordHashingWithSalt() {
    var password = 'mySecretPassword123';
    var salt = crypto.randomHex(16); // 16 bytes = 32 hex chars
    
    // Hash password with salt
    var saltedPassword = password + salt;
    var hashedPassword = crypto.sha256(saltedPassword);
    
    // Verify salt and hash properties
    if (salt.length !== 32) {
        throw new Error('Salt should be 32 characters, got: ' + salt.length);
    }
    if (hashedPassword.length !== 64) {
        throw new Error('Hash should be 64 characters, got: ' + hashedPassword.length);
    }
    
    // Test password verification
    var verifyPassword = 'mySecretPassword123';
    var verifySaltedPassword = verifyPassword + salt;
    var verifyHashedPassword = crypto.sha256(verifySaltedPassword);
    
    if (hashedPassword !== verifyHashedPassword) {
        throw new Error('Password verification should work with same salt');
    }
    
    // Test wrong password
    var wrongPassword = 'wrongPassword';
    var wrongSaltedPassword = wrongPassword + salt;
    var wrongHashedPassword = crypto.sha256(wrongSaltedPassword);
    
    if (hashedPassword === wrongHashedPassword) {
        throw new Error('Wrong password should not match');
    }
    
    console.log('✓ Password hashing with salt test passed');
}

// Test token generation
function testTokenGeneration() {
    var tokens = [];
    var tokenCount = 5;
    
    // Generate multiple tokens
    for (var i = 0; i < tokenCount; i++) {
        var randomData = crypto.randomHex(32); // 32 bytes = 64 hex chars
        var timestamp = Date.now().toString();
        var tokenData = randomData + timestamp;
        var token = crypto.sha256(tokenData);
        
        tokens.push(token);
        
        // Verify token properties
        if (token.length !== 64) {
            throw new Error('Token should be 64 characters, got: ' + token.length);
        }
        if (!/^[0-9a-f]{64}$/.test(token)) {
            throw new Error('Token should be valid hex string: ' + token);
        }
    }
    
    // Verify all tokens are unique
    for (var i = 0; i < tokens.length; i++) {
        for (var j = i + 1; j < tokens.length; j++) {
            if (tokens[i] === tokens[j]) {
                throw new Error('All tokens should be unique');
            }
        }
    }
    
    console.log('✓ Token generation test passed: generated ' + tokenCount + ' unique tokens');
}

// Test session ID generation
function testSessionIdGeneration() {
    var sessionIds = [];
    var sessionCount = 10;
    
    for (var i = 0; i < sessionCount; i++) {
        // Generate session ID using random data + user info + timestamp
        var randomPart = crypto.randomHex(16);
        var userId = 'user' + crypto.randomInt(1000);
        var timestamp = Date.now().toString();
        var sessionData = randomPart + userId + timestamp;
        var sessionId = crypto.sha256(sessionData);
        
        sessionIds.push(sessionId);
    }
    
    // Verify all session IDs are unique
    for (var i = 0; i < sessionIds.length; i++) {
        for (var j = i + 1; j < sessionIds.length; j++) {
            if (sessionIds[i] === sessionIds[j]) {
                throw new Error('All session IDs should be unique');
            }
        }
    }
    
    console.log('✓ Session ID generation test passed: generated ' + sessionCount + ' unique session IDs');
}

// Test API key generation
function testApiKeyGeneration() {
    var apiKeys = [];
    var keyCount = 3;
    
    for (var i = 0; i < keyCount; i++) {
        // Generate API key with prefix and random data
        var prefix = 'ak_';
        var randomData = crypto.randomHex(20); // 20 bytes = 40 hex chars
        var keyData = prefix + randomData;
        
        apiKeys.push(keyData);
        
        // Verify API key format
        if (!keyData.startsWith('ak_')) {
            throw new Error('API key should start with "ak_"');
        }
        if (keyData.length !== 43) { // 3 (prefix) + 40 (hex) = 43
            throw new Error('API key should be 43 characters, got: ' + keyData.length);
        }
    }
    
    // Verify all API keys are unique
    for (var i = 0; i < apiKeys.length; i++) {
        for (var j = i + 1; j < apiKeys.length; j++) {
            if (apiKeys[i] === apiKeys[j]) {
                throw new Error('All API keys should be unique');
            }
        }
    }
    
    console.log('✓ API key generation test passed: ' + apiKeys.join(', '));
}

// Test data integrity verification
function testDataIntegrityVerification() {
    var originalData = 'Important data that must not be tampered with';
    var checksum = crypto.sha256(originalData);
    
    // Verify original data
    var verifyChecksum = crypto.sha256(originalData);
    if (checksum !== verifyChecksum) {
        throw new Error('Data integrity verification should work for original data');
    }
    
    // Test with tampered data
    var tamperedData = 'Important data that must not be tampered with!'; // Added exclamation
    var tamperedChecksum = crypto.sha256(tamperedData);
    if (checksum === tamperedChecksum) {
        throw new Error('Data integrity verification should detect tampering');
    }
    
    console.log('✓ Data integrity verification test passed');
}

// Test challenge-response authentication simulation
function testChallengeResponseAuth() {
    var sharedSecret = 'shared_secret_key_123';
    
    // Server generates challenge
    var challenge = crypto.randomHex(16);
    
    // Client computes response
    var responseData = sharedSecret + challenge;
    var clientResponse = crypto.sha256(responseData);
    
    // Server verifies response
    var expectedResponse = crypto.sha256(sharedSecret + challenge);
    
    if (clientResponse !== expectedResponse) {
        throw new Error('Challenge-response authentication should work');
    }
    
    // Test with wrong secret
    var wrongSecret = 'wrong_secret_key_123';
    var wrongResponse = crypto.sha256(wrongSecret + challenge);
    
    if (clientResponse === wrongResponse) {
        throw new Error('Challenge-response should fail with wrong secret');
    }
    
    console.log('✓ Challenge-response authentication test passed');
}

// Return test cases using junit-js structure
tests({
    testPasswordHashingWithSalt: testPasswordHashingWithSalt,
    testTokenGeneration: testTokenGeneration,
    testSessionIdGeneration: testSessionIdGeneration,
    testApiKeyGeneration: testApiKeyGeneration,
    testDataIntegrityVerification: testDataIntegrityVerification,
    testChallengeResponseAuth: testChallengeResponseAuth
});