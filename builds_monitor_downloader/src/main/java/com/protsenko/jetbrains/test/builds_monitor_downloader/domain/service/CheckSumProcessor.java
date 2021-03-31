package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service;

import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface CheckSumProcessor {
	boolean isChecksumValid(DownloadTaskDto downloadTask, String filePath) throws IOException, NoSuchAlgorithmException;
}
