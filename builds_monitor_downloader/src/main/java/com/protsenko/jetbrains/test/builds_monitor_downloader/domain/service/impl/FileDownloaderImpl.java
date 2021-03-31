package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.FileDownloader;
import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.mq.QueueProcessor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadStatusDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Log
public class FileDownloaderImpl implements FileDownloader {
	private final int BUFFER_SIZE;
	private final String filestorePath;
	private final QueueProcessor queueProcessor;

	@Autowired
	public FileDownloaderImpl(@Value("${com.builds-monitor-downloader.filestore-path}") String filestorePath,
							  @Value("${com.builds-monitor-downloader.buffer-size}") int bufferSize,
							  QueueProcessor queueProcessor) {
		this.filestorePath = filestorePath;
		this.BUFFER_SIZE = bufferSize;
		this.queueProcessor = queueProcessor;
	}

	@Override
	public String loadFile(DownloadTaskDto task) throws IOException {
		int loadedPercents = 0;
		URL url = new URL(task.getLinuxRepoLink());
		HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
		long fileLength = httpConnection.getContentLengthLong();

		String filePath = getFileName(task);
		File file = new File(filePath);
		if (!file.exists()) {
			file.createNewFile();
		}

		FileOutputStream outputStream = new FileOutputStream(file, true);

		long currentLength = file.length();
		InputStream inputStream = httpConnection.getInputStream();
		inputStream.skip(currentLength);
		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
			currentLength += bytesRead;
			loadedPercents = calculatePercent((int) (currentLength / (fileLength / 100)), loadedPercents, task, currentLength);
		}

		queueProcessor.sendStatus(new DownloadStatusDto(task.getId(), (int) currentLength));
		outputStream.close();
		inputStream.close();
		httpConnection.disconnect();
		return filePath;
	}

	private int calculatePercent(int currentPercent, int loadedPercents, DownloadTaskDto task, long currentLength) {
		if (currentPercent > loadedPercents) {
			if (currentPercent % 5 == 0) {
				log.info("Executor " + Thread.currentThread().getId() + " download bytes " + currentLength + " (" + currentPercent + "%)");
				queueProcessor.sendStatus(new DownloadStatusDto(task.getId(), (int) currentLength));
			}
			return currentPercent;
		}
		return loadedPercents;
	}

	@Override
	public void removeFile(String filePath) {
		File file = new File(filePath);
		if (file.exists()) {
			file.delete();
		}
	}

	private String getFileName(DownloadTaskDto task) {
		return filestorePath + task.getId() + task.getProductCode() + "_build_" + task.getBuildFullNumber() + "_linux.tar.gz";

	}
}
