package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.mq.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto.DownloadResultDto;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto.DownloadStatusDto;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.dto.DownloadTaskDto;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.mq.QueueProcessor;
import lombok.extern.java.Log;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Log
public class QueueProcessorImpl implements QueueProcessor {
	private final String downloadTaskSendExchangeName;
	private final String downloadTaskRoutingKey;

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;
	private final BuildsInfoDAO buildsInfoDAO;

	@Autowired
	public QueueProcessorImpl(@Value("${com.builds-monitor.mq.download-tasks.exchange.name}") String downloadTaskSendExchangeName,
							  @Value("${com.builds-monitor.mq.download-tasks.routing-key}") String downloadTaskRoutingKey,
							  RabbitTemplate rabbitTemplate,
							  ObjectMapper objectMapper,
							  BuildsInfoDAO buildsInfoDAO) {
		this.downloadTaskSendExchangeName = downloadTaskSendExchangeName;
		this.downloadTaskRoutingKey = downloadTaskRoutingKey;
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
		this.buildsInfoDAO = buildsInfoDAO;
	}

	@Override
	public boolean sendDownloadTask(ProductBuildInfo build) {
		DownloadTaskDto downloadTask = DownloadTaskDto.builder()
				.id(build.getId())
				.productCode(build.getProductCode())
				.buildFullNumber(build.getBuildFullNumber())
				.linuxRepoLink(build.getLinuxRepoLink())
				.size(build.getSize())
				.currentChecksum(build.getCurrentChecksum())
				.checksumLink(build.getChecksumLink())
				.build();
		try {
			log.info("Sending download task: " + downloadTask);
			rabbitTemplate.convertAndSend(downloadTaskSendExchangeName, downloadTaskRoutingKey, objectMapper.writeValueAsString(downloadTask));
			return true;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return false;
	}


	@RabbitListener(queues = "${com.builds-monitor.mq.download-status.queue-name}")
	public void processStatusMessage(String content) {
		try {
			log.info("Received status message: " + content);
			DownloadStatusDto downloadStatus = objectMapper.readValue(content, DownloadStatusDto.class);
			buildsInfoDAO.updateDownloaded(downloadStatus.getBuildId(), downloadStatus.getDownloaded(), false);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@RabbitListener(queues = "${com.builds-monitor.mq.download-result.queue-name}")
	public void processResultMessage(String content) {
		try {
			log.info("Received result message: " + content);
			DownloadResultDto downloadStatus = objectMapper.readValue(content, DownloadResultDto.class);
			if (downloadStatus.isSuccess()) {
				buildsInfoDAO.updateProductInfo(downloadStatus.getBuildId(), downloadStatus.getProductInfo(), downloadStatus.getChecksum());
			} else {
				if (downloadStatus.getErrorInfo().contains("Product info is not found")) {
					buildsInfoDAO.updateProductInfo(downloadStatus.getBuildId(), objectMapper.writeValueAsString("Product info is not found"), downloadStatus.getChecksum());
				}
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
