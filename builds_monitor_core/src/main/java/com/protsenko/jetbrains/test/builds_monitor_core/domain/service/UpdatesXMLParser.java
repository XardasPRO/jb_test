package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Updates;

public interface UpdatesXMLParser {
	Updates parse(byte[] xml);
}
