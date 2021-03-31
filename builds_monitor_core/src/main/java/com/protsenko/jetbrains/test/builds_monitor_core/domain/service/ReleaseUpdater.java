package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

public interface ReleaseUpdater {
	void updateReleases(String osCode);

	void updateReleases(String productCode, String osCode);
}
