package com.alveotech.service;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alveotech.constants.ApplicationConstants;
import com.alveotech.utils.ValidatorUtilis;

public class TranslatorServiceImpl {
	private static final Logger LOGGER = LogManager.getLogger(TranslatorServiceImpl.class);

	// LinkedBlockingQueue used for producer/ consumer message broker
	private final BlockingQueue<String> queue = new LinkedBlockingQueue<String>();

	// ConcurrentHashMap for maintaining cache columns translation mappings
	private final Map<String, String> columnsMapping = new ConcurrentHashMap<>();

	// ConcurrentHashMap for maintaining cache extracting rows and its IDs
	// translation mappings
	private final Map<String, String> extractRows = new ConcurrentHashMap<>();

	// File location paths passed as arguments
	private final Path vendorFilePath;
	private final Path outputFilePath;

	/**
	 * Constructor to initialize mandatory parameters
	 * 
	 * @param vendorFilePath
	 * @param columnMappingConfigFilePath
	 * @param extractRowsConfigFilePath
	 * @param outputFilePath
	 */
	public TranslatorServiceImpl(Path vendorFilePath, Path columnMappingConfigFilePath, Path extractRowsConfigFilePath,
			Path outputFilePath) {
		LOGGER.info("Updating passed path and loading config files into cache");
		this.vendorFilePath = vendorFilePath;
		this.outputFilePath = outputFilePath;
		LOGGER.info("loading columns mapping file into cache");
		System.out.println(columnMappingConfigFilePath);
		columnsMapping.putAll(readFile(columnMappingConfigFilePath));
		LOGGER.info("loading extract rows mapping file into cache");
		extractRows.putAll(readFile(extractRowsConfigFilePath));
		LOGGER.info("loading config file into cache finished");
	}

	/**
	 * This method is used to load column mapping and extract rows mapping files
	 * 
	 * @param path
	 * @return
	 */
	private Map<String, String> readFile(Path path) {
		LOGGER.info("Reading file {}", path.getFileName());
		try {
			// reading all lines of file and creating map based on separator
			return Files.readAllLines(path).stream().map(line -> line.split(ApplicationConstants.TAB))
					.filter((parts) -> parts.length != 1)
					.collect(Collectors.toMap(part -> String.valueOf(part[0]), part -> String.valueOf(part[1])));
		} catch (IOException e) {
			LOGGER.error("An exception occurred while opening the file.", e);
			throw new RuntimeException("An exception occurred while opening the file.");
		} catch (Exception e) {
			LOGGER.error("An exception occurred while opening the file.", e);
			throw new RuntimeException("An exception occurred while reading the file.");
		}
	}

	/**
	 * This method is for translating vendor data file and write into output file.
	 */
	public void doTranslate() {
		LOGGER.info("Inside translate() method");
		// check if configs cache map is not empty, else throw exception
		if (columnsMapping.isEmpty() || extractRows.isEmpty()) {
			throw new RuntimeException("Please provide valid mapping files!");
		}
		Integer cores = Runtime.getRuntime().availableProcessors();
		ExecutorService executorService = Executors.newFixedThreadPool(cores);
		LOGGER.info("Opening input/output file streams");
		try (BufferedReader br = new BufferedReader(new FileReader(vendorFilePath.toFile()))) {
			try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFilePath.toFile()))) {
				LOGGER.info("Reading first line for columns mapping");
				// read first line of file to extract columns and map it will mapperColumns
				String columnLine = br.readLine();
				LOGGER.info("Translating column");
				// translate column
				if (ValidatorUtilis.isBlankString(columnLine)) {
					LOGGER.error("No Column line found!");
					throw new RuntimeException("Vendor data file doesn't have column header!");
				}
				String translatedColumn = getTranslatedColumn(columnLine, columnsMapping);
				translatedColumn += "\n";
				LOGGER.info("Creating column positioning map");
				// create available column mapping index map
				Map<Integer, Boolean> columnIndex = getColumnsPositionIndexMap(columnLine, columnsMapping);
				// save mapped column into output file as first line
				bos.write(translatedColumn.getBytes());
				List<Future<String>> futures = new ArrayList<Future<String>>();
				LOGGER.info("Submitting task to reader and writer service");
				for (int i = 0; i < cores; i++) {
					futures.add(executorService.submit((Runnable) new ReaderTask(queue, br, columnIndex, extractRows),
							"DONE"));
					futures.add(executorService.submit((Runnable) new WriterTask(queue, bos), "DONE"));
				}
				LOGGER.info("Waiting for task or completion");
				for (Future<String> future : futures)
					future.get(); // get will block until the future is done
				LOGGER.info("Tasks completed without any error");
			}
		} catch (FileNotFoundException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException("An exception occurred file doesn't found!");
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException("An exception occurred while reading file!");
		} catch (InterruptedException | ExecutionException e) {
			LOGGER.error(e.getMessage(), e);
			throw new RuntimeException("An exception occurred while reading file using thread!");
		} finally {
			LOGGER.info("Executor stopped");
			executorService.shutdown();
			try {
				if (!executorService.awaitTermination(1000, TimeUnit.MILLISECONDS)) {
					executorService.shutdownNow();
				}
			} catch (InterruptedException e) {
				LOGGER.error(e.getMessage(), e);
				executorService.shutdownNow();
			}
		}
	}

	/**
	 * Returns the column index map.
	 * 
	 * @param columnLine
	 * @param columns
	 * @return Map<Integer, Boolean>
	 */
	private Map<Integer, Boolean> getColumnsPositionIndexMap(String columnLine, Map<String, String> columns) {
		LOGGER.info("Creating column index map");
		// Atomic integer using for indexing column positioning.
		AtomicInteger index = new AtomicInteger();
		// creating column positioning indexed map based on passed column line.
		return Arrays.asList(columnLine.split(ApplicationConstants.TAB)).stream()
				.filter((column) -> columns.containsKey(column))
				.collect(Collectors.toMap((c) -> index.getAndIncrement(), (c) -> columns.containsKey(c)));
	}

	/**
	 * Returns the translated column line in string format sperated by tab.
	 * 
	 * @param columnLine
	 * @param columns
	 * @return String
	 */
	private String getTranslatedColumn(String columnLine, Map<String, String> columns) {
		LOGGER.info("Translating column based on config file mapping");
		return Arrays.asList(columnLine.split(ApplicationConstants.TAB)).stream()
				.filter((column) -> columns.containsKey(column)).collect(Collectors.joining(ApplicationConstants.TAB));
	}

}
