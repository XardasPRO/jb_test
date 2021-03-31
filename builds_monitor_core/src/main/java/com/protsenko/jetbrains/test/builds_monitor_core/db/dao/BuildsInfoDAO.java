package com.protsenko.jetbrains.test.builds_monitor_core.db.dao;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;

import java.util.List;


public interface BuildsInfoDAO {
	void create(ProductBuildInfo value);

	ProductBuildInfo get(String productCode, String buildFullNumber);

	List<ProductBuildInfo> getAll();

	List<ProductBuildInfo> getAllByProductCode(String productCode);

	void update(ProductBuildInfo value);

	List<ProductBuildInfo> getNewBuilds();

	void updateStatus(int id, Status status);

	void updateDownloaded(int id, int bytesLoaded, boolean force);

	List<ProductBuildInfo> getFrozeDownloads();

	void updateProductInfo(int buildId, String productInfo, String checksum);

}
