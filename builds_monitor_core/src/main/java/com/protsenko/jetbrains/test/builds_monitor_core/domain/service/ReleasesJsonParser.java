package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.Release;

import java.util.List;

public interface ReleasesJsonParser {
	List<Release> parse(byte[] json);
}
