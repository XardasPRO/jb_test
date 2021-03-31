package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json;

import lombok.Data;

import java.util.Map;

@Data
public class Release {
	String date;
	String type;
	String version;
	String build;
	Map<String, DownloadData> downloads;
}
