package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.mq;


import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadResultDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadStatusDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;

public interface QueueProcessor {
	DownloadTaskDto getDownloadTask();
	void sendStatus(DownloadStatusDto status);
	void sendResult(DownloadResultDto result);
}
