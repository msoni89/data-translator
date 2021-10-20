package com.alveotech.utils;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alveotech.constants.ApplicationConstants;

public class ValidatorUtilis {

	private static final Logger LOGGER = LogManager.getLogger(ValidatorUtilis.class);

	/**
	 * Return Boolean flag.
	 * 
	 * @param args
	 * @return
	 */
	public static Boolean validateMandatoryArguments(Map<String, String> args) {
		LOGGER.info("Inside validateMandatoryArguments, parsed arguments");
		return args.keySet().stream().collect(Collectors.toList()).equals(List.of(ApplicationConstants.VENDOR_DATA,
				ApplicationConstants.COLUMN_MAPPING, ApplicationConstants.EXTRACT_ROW, ApplicationConstants.OUTPUT));
	}

	/**
	 * Return map of arguments passed
	 * 
	 * @param args
	 * @return Map
	 */
	public static Map<String, String> parseArguments(String[] args) {
		LOGGER.info("Inside ValidatorUtils parseArguments() method");
		return Arrays.stream(args).map(i -> i.split("=")).filter((parts) -> parts.length != 1)
				.collect(Collectors.toMap(a -> a[0], a -> a[1]));
	}

	/**
	 * Return hint string on console.
	 * 
	 * @return hint string
	 */
	public static String helpHint() {
		return ApplicationConstants.HELP_HINT_TEXT;
	}

	/**
	 * Returns true if boolean string is null or empty, else false.
	 * 
	 * @param string
	 * @return Boolean
	 */
	public static Boolean isBlankString(String string) {
		return string == null || string.isBlank();
	}

	/**
	 * Returns true if passed path are valid file locations, else false.
	 * 
	 * @param filePaths
	 * @return Boolean
	 */
	public static Boolean validatePath(String... filePaths) {
		LOGGER.info("Inside validatePath, provided filePaths");
		if (filePaths == null)
			return false;
		return Arrays.asList(filePaths).stream().map((path) -> {
			if (ValidatorUtilis.isBlankString(path) || !Files.exists(Paths.get(path))) {
				return false;
			}
			return true;
		}).noneMatch(it -> it == false);
	}
}
