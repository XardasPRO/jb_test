package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.mq.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.mq.QueueProcessor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadResultDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadStatusDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;
import lombok.extern.java.Log;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Scope(scopeName = "prototype")
@Log
public class QueueProcessorImpl implements QueueProcessor {
	private final String sendExchangeName;
	private final String statusRoutingKey;
	private final String resultRoutingKey;
	private final String downloadTaskQueueName;

	private final RabbitTemplate rabbitTemplate;
	private final ObjectMapper objectMapper;

	@Autowired
	public QueueProcessorImpl(@Value("${com.builds-monitor.mq.tasks.exchange.name}") String sendExchangeName,
							  @Value("${com.builds-monitor.mq.status.routing-key}") String statusRoutingKey,
							  @Value("${com.builds-monitor.mq.result.routing-key}") String resultRoutingKey,
							  @Value("${com.builds-monitor.mq.download-tasks.queue.name}") String downloadTaskQueueName,
							  RabbitTemplate rabbitTemplate,
							  ObjectMapper objectMapper) {
		this.sendExchangeName = sendExchangeName;
		this.statusRoutingKey = statusRoutingKey;
		this.resultRoutingKey = resultRoutingKey;
		this.downloadTaskQueueName = downloadTaskQueueName;
		this.rabbitTemplate = rabbitTemplate;
		this.objectMapper = objectMapper;
	}

	@Override
	public DownloadTaskDto getDownloadTask() {
		try {
			Message message = rabbitTemplate.receive(downloadTaskQueueName);
			if (message != null) {
				log.info("Received download task: " + message);
				return objectMapper.readValue(message.getBody(), DownloadTaskDto.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void sendStatus(DownloadStatusDto status) {
		try {
			log.info("Send status: " + status);
			rabbitTemplate.convertAndSend(sendExchangeName, statusRoutingKey, objectMapper.writeValueAsString(status));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void sendResult(DownloadResultDto result) {
		try {
			log.info("Send result: " + result);
			rabbitTemplate.convertAndSend(sendExchangeName, resultRoutingKey, objectMapper.writeValueAsString(result));
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
}
