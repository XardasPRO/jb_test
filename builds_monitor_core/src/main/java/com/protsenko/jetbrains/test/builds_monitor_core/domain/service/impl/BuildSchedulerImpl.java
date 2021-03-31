package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.BuildsScheduler;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.DownloadTaskProcessor;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ReleaseUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class BuildSchedulerImpl implements BuildsScheduler {

	private final ReleaseUpdater releaseUpdater;
	private final DownloadTaskProcessor downloadTaskProcessor;

	@Autowired
	public BuildSchedulerImpl(ReleaseUpdater releaseUpdater, DownloadTaskProcessor downloadTaskProcessor) {
		this.releaseUpdater = releaseUpdater;
		this.downloadTaskProcessor = downloadTaskProcessor;
	}

	@Override
	@Scheduled(cron = "${com.builds-monitor.update.cron}")
	public void updateBuilds() {
		releaseUpdater.updateReleases("linux");
		downloadTaskProcessor.sendNewTasks();
	}

	@Override
	@Scheduled(cron = "${com.builds-monitor.update-froze.cron}")
	public void restartFrozeDownloads() {
		downloadTaskProcessor.resendFrozeTasks();
	}
}
