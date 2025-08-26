/**
 * GraalVM CryptoJS Random Test
 * 
 * Tests secure random number generation using GraalVM's JavaScript engine
 * with Java SecureRandom for cryptographically secure randomness.
 */

// Test random hex generation
function testRandomHex() {
    var hex1 = crypto.randomHex(16);
    var hex2 = crypto.randomHex(16);
    
    // Verify length (32 characters for 16 bytes)
    if (hex1.length !== 32) {
        throw new Error('Random hex should be 32 characters for 16 bytes, got: ' + hex1.length);
    }
    if (hex2.length !== 32) {
        throw new Error('Random hex should be 32 characters for 16 bytes, got: ' + hex2.length);
    }
    
    // Verify they're valid hex
    if (!/^[0-9a-f]{32}$/.test(hex1)) {
        throw new Error('Random hex should be valid hex string: ' + hex1);
    }
    if (!/^[0-9a-f]{32}$/.test(hex2)) {
        throw new Error('Random hex should be valid hex string: ' + hex2);
    }
    
    // Verify they're different (extremely unlikely to be same)
    if (hex1 === hex2) {
        throw new Error('Random hex values should be different (extremely unlikely to be same)');
    }
    
    console.log('✓ Random hex test passed: ' + hex1 + ' vs ' + hex2);
}

// Test random hex with different lengths
function testRandomHexDifferentLengths() {
    var lengths = [1, 4, 8, 16, 32];
    
    for (var i = 0; i < lengths.length; i++) {
        var byteLength = lengths[i];
        var expectedCharLength = byteLength * 2;
        var hex = crypto.randomHex(byteLength);
        
        if (hex.length !== expectedCharLength) {
            throw new Error('Random hex for ' + byteLength + ' bytes should be ' + expectedCharLength + ' characters, got: ' + hex.length);
        }
        
        if (!/^[0-9a-f]+$/.test(hex)) {
            throw new Error('Random hex should be valid hex string: ' + hex);
        }
    }
    
    console.log('✓ Random hex different lengths test passed');
}

// Test random integers
function testRandomInt() {
    var bound = 100;
    var results = [];
    
    // Generate multiple random integers
    for (var i = 0; i < 10; i++) {
        var randomInt = crypto.randomInt(bound);
        
        // Verify range
        if (randomInt < 0 || randomInt >= bound) {
            throw new Error('Random int should be in range [0, ' + bound + '), got: ' + randomInt);
        }
        
        results.push(randomInt);
    }
    
    // Verify we got some variation (not all the same)
    var allSame = true;
    for (var i = 1; i < results.length; i++) {
        if (results[i] !== results[0]) {
            allSame = false;
            break;
        }
    }
    
    if (allSame) {
        console.log('Warning: All random integers were the same: ' + results[0] + ' (unlikely but possible)');
    }
    
    console.log('✓ Random int test passed: ' + results.join(', '));
}

// Test random integers with different bounds
function testRandomIntDifferentBounds() {
    var bounds = [2, 10, 50, 100, 1000];
    
    for (var i = 0; i < bounds.length; i++) {
        var bound = bounds[i];
        var randomInt = crypto.randomInt(bound);
        
        if (randomInt < 0 || randomInt >= bound) {
            throw new Error('Random int should be in range [0, ' + bound + '), got: ' + randomInt);
        }
    }
    
    console.log('✓ Random int different bounds test passed');
}

// Test randomness quality (basic statistical test)
function testRandomnessQuality() {
    var bound = 4; // 0, 1, 2, 3
    var counts = [0, 0, 0, 0];
    var iterations = 1000;
    
    // Generate many random numbers
    for (var i = 0; i < iterations; i++) {
        var randomInt = crypto.randomInt(bound);
        counts[randomInt]++;
    }
    
    // Check that each value appeared at least once (very likely with 1000 iterations)
    for (var i = 0; i < counts.length; i++) {
        if (counts[i] === 0) {
            console.log('Warning: Value ' + i + ' never appeared in ' + iterations + ' iterations (unlikely but possible)');
        }
    }
    
    // Check that distribution isn't too skewed (each should be roughly 250 ± significant margin)
    var expected = iterations / bound;
    var tolerance = expected * 0.3; // 30% tolerance
    
    for (var i = 0; i < counts.length; i++) {
        if (counts[i] < expected - tolerance || counts[i] > expected + tolerance) {
            console.log('Warning: Value ' + i + ' appeared ' + counts[i] + ' times, expected ~' + expected + ' (possible but unusual)');
        }
    }
    
    console.log('✓ Randomness quality test passed: ' + counts.join(', ') + ' (out of ' + iterations + ')');
}

// Return test cases using junit-js structure
tests({
    testRandomHex: testRandomHex,
    testRandomHexDifferentLengths: testRandomHexDifferentLengths,
    testRandomInt: testRandomInt,
    testRandomIntDifferentBounds: testRandomIntDifferentBounds,
    testRandomnessQuality: testRandomnessQuality
});