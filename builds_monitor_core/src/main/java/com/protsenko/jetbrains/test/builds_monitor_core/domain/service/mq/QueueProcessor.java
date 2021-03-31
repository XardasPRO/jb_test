package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.mq;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import org.springframework.amqp.AmqpException;

public interface QueueProcessor {
	boolean sendDownloadTask(ProductBuildInfo build) throws AmqpException;
}
