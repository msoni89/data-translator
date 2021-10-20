package com.alveotech.utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.alveotech.constants.ApplicationConstants;

public class ValidatorTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@Test
	public void testHelpArgument() {
		Assert.assertEquals(ApplicationConstants.HELP_HINT_TEXT, ValidatorUtilis.helpHint());
	}

	@Test
	public void testLookupArguments() {
		String[] arguments = new String[] { "--vendor-data=../target/vendor-data.txt" };
		Assert.assertTrue(ValidatorUtilis.parseArguments(arguments).containsKey("--vendor-data"));
	}

	@Test
	public void testLookupArguments_Empty() {
		String[] arguments = new String[] {};
		Assert.assertTrue(ValidatorUtilis.parseArguments(arguments).size() == 0);
	}

	@Test
	public void testValidateMandatory_ReturnTrue() {
		String[] arguments = new String[] { "--vendor-data=../target/vendor-data.txt",
				"--column-mapping=../target/vendor-data.txt", "--extract-row=../target/vendor-data.txt",
				"--output=../target/vendor-data.txt" };
		Map<String, String> parsed = ValidatorUtilis.parseArguments(arguments);
		Assert.assertTrue(ValidatorUtilis.validateMandatoryArguments(parsed));
	}

	@Test
	public void testValidateMandatory_ReturnFalse() {
		String[] arguments = new String[] { "--output=../target/vendor-data.txt" };
		Map<String, String> parsed = ValidatorUtilis.parseArguments(arguments);
		Assert.assertTrue(!ValidatorUtilis.validateMandatoryArguments(parsed));
	}

	@Test
	public void testIsBlankString_ReturnTrue() {
		Assert.assertTrue(!ValidatorUtilis.isBlankString("Test"));
	}

	@Test
	public void testIsBlankStringWithNull_ReturnFalse() {
		Assert.assertTrue(ValidatorUtilis.isBlankString(null));
	}

	@Test
	public void testIsBlankStringWithEmptyString_ReturnFalse() {
		Assert.assertTrue(ValidatorUtilis.isBlankString(" "));
	}

	@Test
	public void testValidatePathEmptyPath_ReturnFalse() {
		Assert.assertTrue(!ValidatorUtilis.validatePath(""));
	}

	@Test
	public void testValidatePathWrongPath_ReturnFalse() {
		Assert.assertTrue(!ValidatorUtilis.validatePath("/temp/dir/test.txt"));
	}

	@Test
	public void testValidatePath_ReturnTrue() throws IOException {
		File createdFile = folder.newFile("myfile.txt");
		Assert.assertTrue(ValidatorUtilis.validatePath(createdFile.getPath()));
	}
}
