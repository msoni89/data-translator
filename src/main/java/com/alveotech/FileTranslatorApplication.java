package com.alveotech;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alveotech.constants.ApplicationConstants;
import com.alveotech.service.TranslatorServiceImpl;
import com.alveotech.utils.ValidatorUtilis;

/**
 * FileTranslatorApplication main class
 * 
 * @author msoni89
 *
 */
public class FileTranslatorApplication {
	private static final Logger LOGGER = LogManager.getLogger(FileTranslatorApplication.class);
	private static final FileTranslatorApplication fileTranslatorApplication = new FileTranslatorApplication();

	public static void main(String[] args) {
		fileTranslatorApplication.doStart(args);
	}

	/**
	 * 
	 * @param args
	 */
	public void doStart(String[] args) {
		LOGGER.info("Inside File TranslatorApplication start() method, arguments are {}", Arrays.asList(args));
		for (String arg : args) {
			if (arg.equals("--help")) {
				LOGGER.info(ValidatorUtilis.helpHint());
				return;
			}
		}
		// parse arguments and return map
		Map<String, String> argumentMaps = ValidatorUtilis.parseArguments(args);
		// check for mandatory arguments
		Boolean isValid = ValidatorUtilis.validateMandatoryArguments(argumentMaps);
		if (!isValid) {
			LOGGER.info(ValidatorUtilis.helpHint());
			throw new RuntimeException("Missing mandatory options, please check --help command");
		}
		// validating resource paths
		Boolean isValidPath = ValidatorUtilis.validatePath(argumentMaps.values().stream().toArray(n -> new String[n]));
		if (!isValidPath) {
			throw new RuntimeException("Invalid file path provided, please check --help command");
		}
		// Initializing TranslatorServiceImpl object
		final TranslatorServiceImpl translatorService = new TranslatorServiceImpl(
				Paths.get(argumentMaps.get(ApplicationConstants.VENDOR_DATA)),
				Paths.get(argumentMaps.get(ApplicationConstants.COLUMN_MAPPING)),
				Paths.get(argumentMaps.get(ApplicationConstants.EXTRACT_ROW)),
				Paths.get(argumentMaps.get(ApplicationConstants.OUTPUT)));

		// translate file entry point
		translatorService.doTranslate();
		LOGGER.info("...Translation finished...");
	}

}
