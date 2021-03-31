package com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DownloadStatusDto {
	private int buildId;
	private int downloaded;
}
