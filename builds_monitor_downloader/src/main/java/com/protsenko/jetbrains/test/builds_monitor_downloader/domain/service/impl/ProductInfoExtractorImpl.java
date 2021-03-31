package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.ProductInfoExtractor;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ProductInfoExtractorImpl implements ProductInfoExtractor {

	private final int BUFFER_SIZE;

	public ProductInfoExtractorImpl(@Value("${com.builds-monitor-downloader.buffer-size}") int bufferSize) {
		BUFFER_SIZE = bufferSize;
	}

	@Override
	public String extractProductInfo(String filePath) throws IOException {
		FileInputStream in = new FileInputStream(filePath);
		GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(in);
		try (TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)) {
			TarArchiveEntry entry;
			while ((entry = (TarArchiveEntry) tarIn.getNextEntry()) != null) {
				if (entry.getName().contains("product-info.json")) {
					int count;
					byte[] data = new byte[BUFFER_SIZE];
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					try (BufferedOutputStream dest = new BufferedOutputStream(outputStream, BUFFER_SIZE)) {
						while ((count = tarIn.read(data, 0, BUFFER_SIZE)) != -1) {
							dest.write(data, 0, count);
						}
					}
					return outputStream.toString();
				}
			}
		}
		return null;
	}
}
