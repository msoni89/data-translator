package com.alveotech;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

@RunWith(JUnit4.class)
public class FileTranslatorApplicationTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	@InjectMocks
	private FileTranslatorApplication fileTranslatorApplication = new FileTranslatorApplication();

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		init();
	}

	File vendorDataFileEmpty;
	File columnTranslateFileEmpty;
	File extractRowsFileEmpty;
	File outputFileEmpty;

	File vendorDataFileFilled;
	File columnTranslateFileFilled;
	File extractRowsFileFilled;

	private String[] emptyFilesArguments;
	private String[] missingMandatoryArgument;

	public void init() throws IOException {
		vendorDataFileEmpty = folder.newFile("vendor-data.txt");
		columnTranslateFileEmpty = folder.newFile("columns_translate.txt");
		extractRowsFileEmpty = folder.newFile("extract_rows.txt");
		outputFileEmpty = folder.newFile("output.txt");

		emptyFilesArguments = new String[] { "--vendor-data=" + vendorDataFileEmpty.getPath(),
				"--column-mapping=" + columnTranslateFileEmpty.getPath(),
				"--extract-row=" + extractRowsFileEmpty.getPath(), "--output=" + outputFileEmpty.getPath() };

		missingMandatoryArgument = new String[] { "--column-mapping=" + columnTranslateFileEmpty.getPath(),
				"--extract-row=" + extractRowsFileEmpty.getPath(), "--output=" + outputFileEmpty.getPath() };
	}

	private void writeToFiles(String lines, File tmpFile) throws IOException {
		try (FileWriter writer = new FileWriter(tmpFile)) {
			writer.write(lines);
			writer.close();
		} finally {

		}
	}

	@Test
	public void testDoStart_DoNothing() {
		String[] arguments = new String[] { "--help", };
		fileTranslatorApplication.doStart(arguments);
	}

	@Test
	public void shouldThrowRuntimeException_testDoStart_WithWrongFilePath() {
		String[] arguments = new String[] { "--vendor-data=../target/vendor-data.txt",
				"--column-mapping=../target/vendor-data.txt", "--extract-row=../target/vendor-data.txt",
				"--output=../target/vendor-data.txt" };
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileTranslatorApplication.doStart(arguments);
		});
		assertEquals("Invalid file path provided, please check --help command", exception.getMessage());
	}

	@Test
	public void shouldThrowRuntimeException_testDoStart_WithEmptyFilePath() throws Exception {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileTranslatorApplication.doStart(emptyFilesArguments);
		});
		assertEquals("Please provide valid mapping files!", exception.getMessage());
	}

	@Test
	public void shouldThrowRuntimeException_testDoStart_WithMissingMandatoryParameters() throws Exception {
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileTranslatorApplication.doStart(missingMandatoryArgument);
		});
		assertEquals("Missing mandatory options, please check --help command", exception.getMessage());
	}

	@Test
	public void shouldThrowRuntimeException_testDoStart_WithEmptyVendorDataParameters() throws Exception {
		folder.delete();
		folder.create();

		vendorDataFileFilled = folder.newFile("vendor-data.txt");
		columnTranslateFileFilled = folder.newFile("columns_translate.txt");
		extractRowsFileFilled = folder.newFile("extract_rows.txt");
		outputFileEmpty = folder.newFile("output.txt");

		writeToFiles("\n", vendorDataFileFilled);
		writeToFiles("COL0	OURID\n" + "COL1	OURCOL1\n" + "COL3	OURCOL3\n", columnTranslateFileFilled);
		writeToFiles("ID2	OURID2", extractRowsFileFilled);
		String[] filledFileArgument = new String[] { "--vendor-data=" + vendorDataFileFilled.getPath(),
				"--column-mapping=" + columnTranslateFileFilled.getPath(),
				"--extract-row=" + extractRowsFileFilled.getPath(), "--output=" + outputFileEmpty.getPath() };

		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileTranslatorApplication.doStart(filledFileArgument);
		});
		assertEquals("Vendor data file doesn't have column header!", exception.getMessage());
	}

	@Test
	public void testDoStart_WithParameters() throws Exception {
		folder.delete();
		folder.create();

		vendorDataFileFilled = folder.newFile("vendor-data.txt");
		columnTranslateFileFilled = folder.newFile("columns_translate.txt");
		extractRowsFileFilled = folder.newFile("extract_rows.txt");
		outputFileEmpty = folder.newFile("output.txt");

		writeToFiles("COL0	COL1	COL2	COL3\n" + "ID1	VAL11	VAL12	VAL13\n" + "ID2	VAL21	VAL22	VAL23\n"
				+ "ID3	VAL31	VAL32	VAL33\n" + "ID4	VAL41	VAL42	VAL43\n" + "ID5	VAL51	VAL52	VAL53\n",
				vendorDataFileFilled);
		writeToFiles("COL0	OURID\n" + "COL1	OURCOL1\n" + "COL3	OURCOL3\n", columnTranslateFileFilled);
		writeToFiles("ID2	OURID2", extractRowsFileFilled);
		String[] filledFileArgument = new String[] { "--vendor-data=" + vendorDataFileFilled.getPath(),
				"--column-mapping=" + columnTranslateFileFilled.getPath(),
				"--extract-row=" + extractRowsFileFilled.getPath(), "--output=" + outputFileEmpty.getPath() };

		fileTranslatorApplication.doStart(filledFileArgument);
		List<String> translatedValues = Files.readAllLines(Paths.get(outputFileEmpty.getPath()));
		assertTrue(translatedValues.containsAll(Arrays.asList("COL0	COL1	COL3", "OURID2	VAL21	VAL22")));
	}

	@Test
	public void testDoStart_WithExtraBlankLineIntoVendorDataParameters() throws Exception {
		folder.delete();
		folder.create();

		vendorDataFileFilled = folder.newFile("vendor-data.txt");
		columnTranslateFileFilled = folder.newFile("columns_translate.txt");
		extractRowsFileFilled = folder.newFile("extract_rows.txt");
		outputFileEmpty = folder.newFile("output.txt");

		writeToFiles("COL0	COL1	COL2	COL3\n" + "ID1	VAL11	VAL12	VAL13\n\n\n\n\n\n\n"
				+ "ID2	VAL21	VAL22	VAL23\n" + "ID3	VAL31	VAL32	VAL33\n" + "ID4	VAL41	VAL42	VAL43\n"
				+ "ID5	VAL51	VAL52	VAL53\n", vendorDataFileFilled);
		writeToFiles("COL0	OURID\n" + "COL1	OURCOL1\n" + "COL3	OURCOL3\n", columnTranslateFileFilled);
		writeToFiles("ID2	OURID2", extractRowsFileFilled);
		String[] filledFileArgument = new String[] { "--vendor-data=" + vendorDataFileFilled.getPath(),
				"--column-mapping=" + columnTranslateFileFilled.getPath(),
				"--extract-row=" + extractRowsFileFilled.getPath(), "--output=" + outputFileEmpty.getPath() };
		fileTranslatorApplication.doStart(filledFileArgument);
		List<String> translatedValues = Files.readAllLines(Paths.get(outputFileEmpty.getPath()));
		assertTrue(translatedValues.containsAll(Arrays.asList("COL0	COL1	COL3", "OURID2	VAL21	VAL22")));
	}

	@Test
	public void testDoStart_WithEmptyColumnMappingConfigFile() throws Exception {
		folder.delete();
		folder.create();

		vendorDataFileFilled = folder.newFile("vendor-data.txt");
		columnTranslateFileFilled = folder.newFile("columns_translate.txt");
		extractRowsFileFilled = folder.newFile("extract_rows.txt");
		outputFileEmpty = folder.newFile("output.txt");

		writeToFiles("COL0	COL1	COL2	COL3\n" + "ID1	VAL11	VAL12	VAL13\n" + "ID2	VAL21	VAL22	VAL23\n"
				+ "ID3	VAL31	VAL32	VAL33\n" + "ID4	VAL41	VAL42	VAL43\n" + "ID5	VAL51	VAL52	VAL53\n",
				vendorDataFileFilled);
		writeToFiles("", columnTranslateFileFilled);
		writeToFiles("ID2	OURID2", extractRowsFileFilled);
		String[] filledFileArgument = new String[] { "--vendor-data=" + vendorDataFileFilled.getPath(),
				"--column-mapping=" + columnTranslateFileFilled.getPath(),
				"--extract-row=" + extractRowsFileFilled.getPath(), "--output=" + outputFileEmpty.getPath() };
		RuntimeException exception = assertThrows(RuntimeException.class, () -> {
			fileTranslatorApplication.doStart(filledFileArgument);
		});
		assertEquals("Please provide valid mapping files!", exception.getMessage());

	}

}
