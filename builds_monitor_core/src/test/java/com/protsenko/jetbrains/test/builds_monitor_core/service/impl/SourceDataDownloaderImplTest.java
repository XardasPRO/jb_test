package com.protsenko.jetbrains.test.builds_monitor_core.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl.SourceDataDownloaderImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class SourceDataDownloaderImplTest {

	@Test
	public void shouldDownloadUpdatesXML() {
		SourceDataDownloaderImpl testee = new SourceDataDownloaderImpl("https://www.jetbrains.com/updates/updates.xml", "");
		byte[] updatesXML = testee.getUpdatesXML();
		Assertions.assertNotNull(updatesXML);
	}

	@Test
	public void shouldDownloadReleasesJSON() {
		SourceDataDownloaderImpl testee = new SourceDataDownloaderImpl("", "https://data.services.jetbrains.com/products/releases?code=");
		byte[] releasesJSON = testee.getReleasesByProjectJSON("RM");
		Assertions.assertNotNull(releasesJSON);
	}

	@Test
	public void shouldDownloadChecksum() {
		SourceDataDownloaderImpl testee = new SourceDataDownloaderImpl("", "");
		String checksum = testee.getChecksum("https://download.jetbrains.com/go/goland-2020.3.4.tar.gz.sha256");
		Assertions.assertNotNull(checksum);
	}

}