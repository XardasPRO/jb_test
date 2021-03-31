package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.DownloadTaskProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class DownloadTaskExecutorService {
	private final int threadsCount;

	private ExecutorService executorService;
	private final DownloadTaskProcessor fileDownloader;

	@Autowired
	public DownloadTaskExecutorService(@Value("${com.builds-monitor-downloader.threads-count}") int threadsCount, DownloadTaskProcessor fileDownloader) {
		this.threadsCount = threadsCount;
		this.fileDownloader = fileDownloader;
	}

	@PostConstruct
	public void init() {
		executorService = Executors.newFixedThreadPool(threadsCount);
		for (int i = 0; i < threadsCount; i++) {
			createTask();
		}
	}

	@PreDestroy
	public void cleanup() {
		executorService.shutdown();
	}

	private void createTask() {
		executorService.submit(fileDownloader.getJob());
	}
}
