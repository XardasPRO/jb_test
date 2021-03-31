package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

public interface BuildsScheduler {
	void updateBuilds();

	void restartFrozeDownloads();
}
