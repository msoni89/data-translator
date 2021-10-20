package com.alveotech.service;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WriterTask implements Runnable {
	private static final Logger LOGGER = LogManager.getLogger(TranslatorServiceImpl.class);

	private final BlockingQueue<String> queue;
	private final BufferedOutputStream bufferedOutputStream;

	@Override
	public void run() {
		try {
			String line;
			while ((line = queue.take()) != null) {
				// if line equal to EOF means file finished.
				if (line.equals("EOF")) {
					break;
				}
				line = line + "\n";
				bufferedOutputStream.write(line.getBytes());
				bufferedOutputStream.flush();
			}
		} catch (InterruptedException e) {
			LOGGER.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	public WriterTask(BlockingQueue<String> queue, BufferedOutputStream bufferedOutputStream) {
		this.queue = queue;
		this.bufferedOutputStream = bufferedOutputStream;
	}
}