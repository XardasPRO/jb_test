package com.protsenko.jetbrains.test.builds_monitor_core.db.mapper;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


@Mapper
public interface BuildsInfoMapper {
	void create(@Param("value") ProductBuildInfo value);

	ProductBuildInfo get(@Param("productCode") String productCode, @Param("buildFullNumber") String buildFullNumber);

	ProductBuildInfo getById(@Param("id") int id);

	List<ProductBuildInfo> getAllByProductCode(@Param("productCode") String productCode);

	List<ProductBuildInfo> getAll();

	List<ProductBuildInfo> getNewBuilds();

	void updateStatus(@Param("id") int id, @Param("status") Status status);

	void update(@Param("value") ProductBuildInfo value);

	void updateDownloaded(@Param("id") int id, @Param("bytesLoaded") int bytesLoaded, @Param("force") boolean force);

	List<ProductBuildInfo> getFreezedDownloads(@Param("freezeSeconds") int freezeSeconds);

	void updateProductInfo(@Param("id") int id, @Param("productInfo") String productInfo, @Param("checksum") String checksum);

}
