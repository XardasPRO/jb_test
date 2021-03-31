package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

import java.util.HashMap;
import java.util.List;

public interface ProductInfoProvider {
	String getProductInfo(String productCode, String fullBuildNumber);

	List<HashMap> getProductInfos(String productCode);
}
