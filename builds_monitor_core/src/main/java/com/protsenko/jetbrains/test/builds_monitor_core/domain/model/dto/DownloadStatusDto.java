package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DownloadStatusDto {
	private int buildId;
	private int downloaded;
}
