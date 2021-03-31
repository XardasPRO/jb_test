package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data(staticConstructor = "of")
@NoArgsConstructor
public class DownloadResultDto {
	private int buildId;
	private boolean isSuccess;
	private String errorInfo;
	private String checksum;
	private String productInfo;
}
