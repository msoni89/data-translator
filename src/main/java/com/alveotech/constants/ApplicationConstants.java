package com.alveotech.constants;

public class ApplicationConstants {

	public static final String OUTPUT = "--output";
	public static final String EXTRACT_ROW = "--extract-row";
	public static final String COLUMN_MAPPING = "--column-mapping";
	public static final String VENDOR_DATA = "--vendor-data";
	public static final String TAB = "\t";

	public final static String HELP_HINT_TEXT = "\n\nusage: java -jar file-translator-1.0-SNAPSHOT.jar [options] \n" + "\n"
			+ "Options:\n" + " --vendor-data=<relative file-path>					Vendor data file location\n"
			+ " --column-mapping=<relative file-path>					Column mapping config file location\n"
			+ " --extract-row=<relative file-path>					Extract rows config file location\n"
			+ " --output <arg>=<relative file-path>					Output file location\n";
}
