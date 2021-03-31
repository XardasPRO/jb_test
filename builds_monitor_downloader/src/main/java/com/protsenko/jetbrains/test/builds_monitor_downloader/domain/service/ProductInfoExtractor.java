package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service;

import java.io.IOException;

public interface ProductInfoExtractor {
	String extractProductInfo(String filePath) throws IOException;
}
