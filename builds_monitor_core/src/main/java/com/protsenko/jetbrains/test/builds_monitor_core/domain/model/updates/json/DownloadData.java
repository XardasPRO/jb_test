package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json;

import lombok.Data;

@Data
public class DownloadData {
	String link;
	int size;
	String checksumLink;
}
