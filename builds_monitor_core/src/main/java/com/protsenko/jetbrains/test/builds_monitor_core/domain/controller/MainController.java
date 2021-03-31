package com.protsenko.jetbrains.test.builds_monitor_core.domain.controller;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.DownloadTaskProcessor;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.MainPageConstructor;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ProductInfoProvider;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ReleaseUpdater;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
public class MainController {
	private final MainPageConstructor mainPageConstructor;
	private final ProductInfoProvider productInfoProvider;
	private final ReleaseUpdater releaseUpdater;
	private final DownloadTaskProcessor downloadTaskProcessor;

	@Autowired
	public MainController(MainPageConstructor mainPageConstructor,
						  ProductInfoProvider productInfoProvider,
						  ReleaseUpdater releaseUpdater,
						  DownloadTaskProcessor downloadTaskProcessor) {
		this.mainPageConstructor = mainPageConstructor;
		this.productInfoProvider = productInfoProvider;
		this.releaseUpdater = releaseUpdater;
		this.downloadTaskProcessor = downloadTaskProcessor;
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String getStatusPage() {
		return mainPageConstructor.createMainPageHtml();
	}

	@RequestMapping(value = "/status", method = RequestMethod.GET)
	@ResponseBody
	public List<ProductBuildInfo> getStatus() {
		return mainPageConstructor.getSourceData();
	}

	@RequestMapping("/{productCode}")
	public @ResponseBody
	List<HashMap> getProductsStatusByProductCode(@PathVariable(value = "productCode") String productCode) {
		return productInfoProvider.getProductInfos(productCode);
	}

	@RequestMapping("/{productCode}/{buildNumber}")
	public @ResponseBody
	String getProductsStatusByProductCode(@PathVariable(value = "productCode") String productCode,
										  @PathVariable(value = "buildNumber") String buildNumber) {
		return productInfoProvider.getProductInfo(productCode, buildNumber);
	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public String updateAll() {
		releaseUpdater.updateReleases("linux");
		downloadTaskProcessor.sendNewTasks();
		return "REFRESHING";
	}

	@RequestMapping(value = "/refresh/{productCode}", method = RequestMethod.GET)
	public String updateAll(@PathVariable(value = "productCode") String productCode) {
		releaseUpdater.updateReleases(productCode, "linux");
		downloadTaskProcessor.sendNewTasks();
		return "REFRESHING " + productCode;
	}


}
