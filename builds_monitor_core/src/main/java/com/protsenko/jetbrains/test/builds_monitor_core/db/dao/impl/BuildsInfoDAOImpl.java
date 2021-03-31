package com.protsenko.jetbrains.test.builds_monitor_core.db.dao.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.db.mapper.BuildsInfoMapper;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BuildsInfoDAOImpl implements BuildsInfoDAO {

	private final int freezeLimit;

	private final BuildsInfoMapper buildsInfoMapper;

	@Autowired
	public BuildsInfoDAOImpl(@Value("${com.builds-monitor.freeze-limit.seconds}") int freezeLimit,
							 BuildsInfoMapper buildsInfoMapper) {
		this.freezeLimit = freezeLimit;
		this.buildsInfoMapper = buildsInfoMapper;
	}

	@Override
	public void create(ProductBuildInfo value) {
		buildsInfoMapper.create(value);
	}

	@Override
	public ProductBuildInfo get(String productCode, String buildFullNumber) {
		return buildsInfoMapper.get(productCode, buildFullNumber);
	}

	@Override
	public List<ProductBuildInfo> getAll() {
		return buildsInfoMapper.getAll();
	}

	@Override
	public List<ProductBuildInfo> getAllByProductCode(String productCode) {
		return buildsInfoMapper.getAllByProductCode(productCode);
	}

	@Override
	public void update(ProductBuildInfo value) {
		buildsInfoMapper.update(value);
	}

	@Override
	public List<ProductBuildInfo> getNewBuilds() {
		return buildsInfoMapper.getNewBuilds();
	}

	@Override
	public void updateStatus(int id, Status status) {
		buildsInfoMapper.updateStatus(id, status);
	}

	@Override
	public void updateDownloaded(int id, int bytesLoaded, boolean force) {
		ProductBuildInfo productBuildInfo = buildsInfoMapper.getById(id);
		if (productBuildInfo != null && Status.QUEUED.equals(productBuildInfo.getStatus())) {
			buildsInfoMapper.updateStatus(id, Status.DOWNLOADING);
		}
		buildsInfoMapper.updateDownloaded(id, bytesLoaded, force);
	}

	@Override
	public List<ProductBuildInfo> getFrozeDownloads() {
		return buildsInfoMapper.getFreezedDownloads(freezeLimit);
	}

	@Override
	public void updateProductInfo(int buildId, String productInfo, String checksum) {
		ProductBuildInfo productBuildInfo = buildsInfoMapper.getById(buildId);
		if (productBuildInfo != null && Status.DOWNLOADING.equals(productBuildInfo.getStatus())) {
			buildsInfoMapper.updateProductInfo(buildId, productInfo, checksum);
		}
	}
}
