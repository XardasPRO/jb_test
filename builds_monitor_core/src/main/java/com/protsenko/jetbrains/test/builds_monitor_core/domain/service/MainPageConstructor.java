package com.protsenko.jetbrains.test.builds_monitor_core.domain.service;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;

import java.util.List;

public interface MainPageConstructor {
	String createMainPageHtml();

	List<ProductBuildInfo> getSourceData();
}
