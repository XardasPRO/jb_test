package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ProductInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductInfoProviderImpl implements ProductInfoProvider {

	private final BuildsInfoDAO buildsInfoDAO;

	@Autowired
	public ProductInfoProviderImpl(BuildsInfoDAO buildsInfoDAO) {
		this.buildsInfoDAO = buildsInfoDAO;
	}

	@Override
	public String getProductInfo(String productCode, String fullBuildNumber) {
		ProductBuildInfo productBuildInfo = buildsInfoDAO.get(productCode, fullBuildNumber);
		if (productBuildInfo != null) {
			return productBuildInfo.getProductInfo();
		}
		return "{Not found}";
	}

	@Override
	public List<HashMap> getProductInfos(String productCode) {
		List<HashMap> result = buildsInfoDAO.getAllByProductCode(productCode).parallelStream()
				.map(productBuildInfo -> {
					HashMap hashMap = new HashMap();
					hashMap.put(productBuildInfo.getBuildFullNumber(), productBuildInfo.getProductInfo());
					return hashMap;
				})
				.collect(Collectors.toList());
		return result;
	}
}
