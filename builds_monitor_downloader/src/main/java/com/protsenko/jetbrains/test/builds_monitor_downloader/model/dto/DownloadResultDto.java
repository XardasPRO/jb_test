package com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadResultDto {
	private int buildId;
	private boolean isSuccess;
	private String errorInfo;
	private String checksum;
	private String productInfo;
}
