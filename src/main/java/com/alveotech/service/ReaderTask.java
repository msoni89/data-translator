package com.alveotech.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alveotech.constants.ApplicationConstants;
import com.alveotech.utils.ValidatorUtilis;

public class ReaderTask implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(TranslatorServiceImpl.class);

	private final BlockingQueue<String> queue;
	private final BufferedReader bufferedReader;
	private final Map<Integer, Boolean> availableColumnIndex;
	private final Map<String, String> extractMap;

	@Override
	public void run() {
		try {
			String line = null;
			// loop for reading file line by line
			while ((line = bufferedReader.readLine()) != null) {
				if (ValidatorUtilis.isBlankString(line))
					continue;
				// Translating and filtering rows
				Optional<String> optional = translateLine(line, availableColumnIndex, extractMap);
				if (optional.isPresent())
					queue.put(optional.get());
			}
			queue.put("EOF");
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	private Optional<String> translateLine(String row, Map<Integer, Boolean> availableColumnIndex,
			Map<String, String> extractMap) {
		StringJoiner joiner = new StringJoiner(ApplicationConstants.TAB);
		String[] parts = row.split(ApplicationConstants.TAB);
		String idColumn = parts[0];
		// check translated row id is present into extract rows cache.
		if (extractMap.containsKey(idColumn)) {
			// check id translated value and append it into joiner
			joiner.add(extractMap.get(idColumn));
			for (int index = 1; index < parts.length; index++) {
				// checking column positioning index, if found means present else ignore.
				if (availableColumnIndex.containsKey(index)) {
					joiner.add(parts[index]);
				}
			}
			return Optional.of(joiner.toString());
		}
		return Optional.empty();
	}

	public ReaderTask(BlockingQueue<String> queue, BufferedReader bufferedReader,
			Map<Integer, Boolean> availableColumnIndex, Map<String, String> extractMap) {
		this.queue = queue;
		this.bufferedReader = bufferedReader;
		this.availableColumnIndex = availableColumnIndex;
		this.extractMap = extractMap;
	}
}