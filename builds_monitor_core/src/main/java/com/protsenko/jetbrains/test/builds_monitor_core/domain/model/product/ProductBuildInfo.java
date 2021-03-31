package com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product;

import lombok.Builder;
import lombok.Value;
import lombok.With;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
@With
public class ProductBuildInfo {
	int id;
	String productCode;
	String buildFullNumber;
	String linuxRepoLink;
	int size;
	String checksumLink;
	String currentChecksum;
	LocalDate buildDate;
	Status status;
	int buildBytesLoaded;
	LocalDateTime lastUpdateTime;
	String productInfo;
}
