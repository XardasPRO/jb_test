package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

public interface SourceDataDownloader {
	byte[] getUpdatesXML();

	byte[] getReleasesByProjectJSON(String projectCode);

	String getChecksum(String checksumUrl);
}
