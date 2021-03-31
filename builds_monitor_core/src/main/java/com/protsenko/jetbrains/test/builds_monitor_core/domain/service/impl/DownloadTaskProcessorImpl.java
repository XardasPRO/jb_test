package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.DownloadTaskProcessor;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.mq.QueueProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DownloadTaskProcessorImpl implements DownloadTaskProcessor {

	private final BuildsInfoDAO buildsInfoDAO;
	private final QueueProcessor queueProcessor;

	@Autowired
	public DownloadTaskProcessorImpl(BuildsInfoDAO buildsInfoDAO, QueueProcessor queueProcessor) {
		this.buildsInfoDAO = buildsInfoDAO;
		this.queueProcessor = queueProcessor;
	}

	@Override
	public void sendNewTasks() {
		List<ProductBuildInfo> newBuilds = buildsInfoDAO.getNewBuilds();
		sendTasks(newBuilds);
	}

	@Override
	public void sendTasks(List<ProductBuildInfo> builds) {
		for (ProductBuildInfo build : builds) {
			if (queueProcessor.sendDownloadTask(build)) {
				buildsInfoDAO.updateStatus(build.getId(), Status.QUEUED);
			}
		}
	}

	@Override
	public void resendFrozeTasks() {
		List<ProductBuildInfo> frozeDownloads = buildsInfoDAO.getFrozeDownloads();
		frozeDownloads.parallelStream().forEach(productBuildInfo -> {
			buildsInfoDAO.updateDownloaded(productBuildInfo.getId(), 0, true);
			buildsInfoDAO.updateStatus(productBuildInfo.getId(), Status.QUEUED);
			queueProcessor.sendDownloadTask(productBuildInfo);
		});
	}
}
