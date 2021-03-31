package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DownloadTaskDto {
	int id;
	String productCode;
	String buildFullNumber;
	String linuxRepoLink;
	int size;
	String checksumLink;
	String currentChecksum;
}
