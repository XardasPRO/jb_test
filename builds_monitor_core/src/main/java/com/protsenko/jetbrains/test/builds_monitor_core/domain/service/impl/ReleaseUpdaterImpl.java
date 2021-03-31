package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.DownloadData;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.json.Release;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Product;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ReleaseUpdater;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.ReleasesJsonParser;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.SourceDataDownloader;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.UpdatesXMLParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReleaseUpdaterImpl implements ReleaseUpdater {

	private final SourceDataDownloader dataDownloader;
	private final UpdatesXMLParser xmlParser;
	private final ReleasesJsonParser jsonParser;
	private final BuildsInfoDAO buildsInfoDAO;

	@Autowired
	public ReleaseUpdaterImpl(SourceDataDownloader dataDownloader, UpdatesXMLParser xmlParser, ReleasesJsonParser jsonParser, BuildsInfoDAO buildsInfoDAO) {
		this.dataDownloader = dataDownloader;
		this.xmlParser = xmlParser;
		this.jsonParser = jsonParser;
		this.buildsInfoDAO = buildsInfoDAO;
	}

	@Override
	public void updateReleases(String osCode) {
		List<ProductBuildInfo> result = getProductsMap().entrySet()
				.parallelStream()
				.flatMap(stringListEntry -> makeBuildInfo(stringListEntry.getKey(), stringListEntry.getValue(), osCode).stream())
				.collect(Collectors.toList());

		saveBuilds(result);
	}

	@Override
	public void updateReleases(String productCode, String osCode) {
		Map<String, List<Product>> productReleases = getProductsMap();

		List<ProductBuildInfo> result = makeBuildInfo(productCode, productReleases.get(productCode), osCode);

		saveBuilds(result);
	}

	private List<Product> getProducts() {
		byte[] updatesXML = dataDownloader.getUpdatesXML();
		return xmlParser.parse(updatesXML).getProducts();
	}

	private List<Release> getReleases(String productCode) {
		byte[] releases = dataDownloader.getReleasesByProjectJSON(productCode);
		return jsonParser.parse(releases);
	}

	private Map<String, List<Product>> getProductsMap() {
		return getProducts().parallelStream()
				.collect(Collectors.groupingBy(Product::getCode));
	}

	private List<ProductBuildInfo> makeBuildInfo(String productCode, List<Product> products, String osCode) {
		Map<String, DownloadData> releases = getReleases(productCode).parallelStream()
				.filter(release -> release.getDownloads().get(osCode) != null)
				.collect(Collectors.toMap(Release::getBuild, release -> release.getDownloads().get(osCode)));

		return products.parallelStream()
				.flatMap(product -> product.getChannels().stream())
				.flatMap(channel -> channel.getBuilds().stream())
				.filter(build -> build.getReleaseDate() != null &&
						LocalDate.parse(build.getReleaseDate(), DateTimeFormatter.BASIC_ISO_DATE).isAfter(LocalDate.now().minusYears(1L)))
				.filter(build -> releases.containsKey(build.getFullNumber()))
				.map(build -> {
					DownloadData downloadData = releases.get(build.getFullNumber());
					String checksumLink = downloadData.getChecksumLink();
					return ProductBuildInfo.builder()
							.productCode(productCode)
							.buildFullNumber(build.getFullNumber())
							.buildDate(LocalDate.parse(build.getReleaseDate(), DateTimeFormatter.BASIC_ISO_DATE))
							.linuxRepoLink(downloadData.getLink())
							.size(downloadData.getSize())
							.checksumLink(checksumLink)
							.currentChecksum(dataDownloader.getChecksum(checksumLink).trim())
							.status(Status.NEW)
							.build();
				})
				.collect(Collectors.toList());
	}

	private void saveBuilds(List<ProductBuildInfo> builds) {
		builds.parallelStream()
				.forEach(build -> {
					ProductBuildInfo currentBuild = buildsInfoDAO.get(build.getProductCode(), build.getBuildFullNumber());
					if (currentBuild == null) {
						buildsInfoDAO.create(build);
					} else {
						if (!currentBuild.getLinuxRepoLink().equals(build.getLinuxRepoLink()) ||
								!currentBuild.getChecksumLink().equals(build.getChecksumLink()) ||
								!currentBuild.getCurrentChecksum().equals(build.getCurrentChecksum())) {
							buildsInfoDAO.update(build.withId(currentBuild.getId()));
						}
					}
				});
	}
}
