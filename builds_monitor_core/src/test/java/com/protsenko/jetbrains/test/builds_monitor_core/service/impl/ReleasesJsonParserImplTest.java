package com.protsenko.jetbrains.test.builds_monitor_core.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.Release;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl.ReleasesJsonParserImpl;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl.SourceDataDownloaderImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class ReleasesJsonParserImplTest {

	@Test
	void shouldParseReleasesJson() {
		SourceDataDownloaderImpl dataDownloader = new SourceDataDownloaderImpl("", "https://data.services.jetbrains.com/products/releases?code=");
		byte[] releasesJSON = dataDownloader.getReleasesByProjectJSON("GO");

		ReleasesJsonParserImpl testee = new ReleasesJsonParserImpl();
		List<Release> parse = testee.parse(releasesJSON);
		Assertions.assertNotNull(parse);
		Assertions.assertTrue(parse.size() > 0);
	}
}