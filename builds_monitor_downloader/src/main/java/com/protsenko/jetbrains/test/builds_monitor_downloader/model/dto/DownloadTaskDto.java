package com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DownloadTaskDto {
	int id;
	String productCode;
	String buildFullNumber;
	String linuxRepoLink;
	int size;
	String checksumLink;
	String currentChecksum;
}
