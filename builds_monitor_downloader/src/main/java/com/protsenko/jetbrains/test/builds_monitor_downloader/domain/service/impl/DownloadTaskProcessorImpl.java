package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.DownloadTaskProcessor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.FileDownloader;
import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.ProductInfoExtractor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.mq.QueueProcessor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadResultDto;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

@Service
@Log
public class DownloadTaskProcessorImpl implements DownloadTaskProcessor {

	private final QueueProcessor queueProcessor;
	private final FileDownloader fileDownloader;
	private final CheckSumProcessorImpl checkSumProcessor;
	private final ProductInfoExtractor productInfoExtractor;

	@Autowired
	public DownloadTaskProcessorImpl(QueueProcessor queueProcessor,
									 FileDownloader fileDownloader,
									 CheckSumProcessorImpl checkSumProcessor,
									 ProductInfoExtractor productInfoExtractor) {
		this.fileDownloader = fileDownloader;
		this.queueProcessor = queueProcessor;
		this.checkSumProcessor = checkSumProcessor;
		this.productInfoExtractor = productInfoExtractor;
	}

	@Override
	public Runnable getJob() {
		return () -> {
			log.info("Start job " + Thread.currentThread().getId());
			DownloadTaskDto downloadTask = queueProcessor.getDownloadTask();
			while (true) {
				if (downloadTask != null) {
					log.info("Process task: " + downloadTask);
					DownloadResultDto resultDto = processTask(downloadTask);
					queueProcessor.sendResult(resultDto);
				} else {
					try {
						Thread.sleep(1000L);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				downloadTask = queueProcessor.getDownloadTask();
			}
		};
	}

	private DownloadResultDto processTask(DownloadTaskDto downloadTask) {
		String loadedFilePath = "";
		try {
			loadedFilePath = fileDownloader.loadFile(downloadTask);
			boolean isValid = checkSumProcessor.isChecksumValid(downloadTask, loadedFilePath);
			if (!isValid) {
				return makeResult(false, "InvalidChecksum", downloadTask, "");
			}
			String productInfo = productInfoExtractor.extractProductInfo(loadedFilePath);
			if (productInfo == null) {
				return makeResult(false, "Product info is not found.", downloadTask, "");
			}
			return makeResult(true, "", downloadTask, productInfo);
		} catch (IOException | NoSuchAlgorithmException e) {
			e.printStackTrace();
			return makeResult(false, e.toString(), downloadTask, "");
		} finally {
			fileDownloader.removeFile(loadedFilePath);
		}
	}


	private DownloadResultDto makeResult(boolean isSuccess, String errorInfo, DownloadTaskDto downloadTask, String productInfo) {
		return DownloadResultDto.builder()
				.buildId(downloadTask.getId())
				.checksum(downloadTask.getCurrentChecksum())
				.errorInfo(errorInfo)
				.isSuccess(isSuccess)
				.productInfo(productInfo)
				.build();
	}


}
