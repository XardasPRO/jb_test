package com.protsenko.jetbrains.test.builds_monitor_core.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Updates;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl.SourceDataDownloaderImpl;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl.UpdatesXMLParserImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UpdatesXMLParserImplTest {

	@Test
	void shouldParseUpdatesXML() {
		UpdatesXMLParserImpl testee = new UpdatesXMLParserImpl();

		SourceDataDownloaderImpl sourceDataDownloader = new SourceDataDownloaderImpl("https://www.jetbrains.com/updates/updates.xml", "");
		byte[] updatesXML = sourceDataDownloader.getUpdatesXML();

		Updates parse = testee.parse(updatesXML);
		Assertions.assertTrue(parse.getProducts().size() > 0);
	}

}