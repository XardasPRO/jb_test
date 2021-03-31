package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service;

import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;

import java.io.File;
import java.io.IOException;

public interface FileDownloader {
	String loadFile(DownloadTaskDto task) throws IOException;
	void removeFile(String filePath);
}
