load("src/test/resources/TestUtils.js");
load("src/main/java/org/bitbucket/thinbus/junitjs/examples/FileUnderTest.js");

var stub = newStub();
underTest.collaborator = stub;

tests({
	returnsFour_ShouldReturnFour: function() {
		jsAssert.assertIntegerEquals(4, underTest.returnsFour());
	},

	/*
	doesSomethingImportant_ThisTestShouldFail: function() {
		underTest.doesSomethingImportant();
		
		stub.assertCalled({
			name: 'importantFunction',
			args: ['wrong', 'args']
		});
	},
        */
	doesSomethingImportant_ShouldDoSomethingImportant: function() {
		underTest.doesSomethingImportant();
		
		stub.assertCalled({
			name: 'importantFunction',
			args: ['hello', 'world']
		});
	}
});
