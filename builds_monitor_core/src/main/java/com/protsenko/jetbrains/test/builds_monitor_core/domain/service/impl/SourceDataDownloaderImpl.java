package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.SourceDataDownloader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

@Service
public class SourceDataDownloaderImpl implements SourceDataDownloader {
	private final String updatesUrl;
	private final String releasesUrl;

	public SourceDataDownloaderImpl(
			@Value("${com.builds-monitor.url.updates}") String updatesUrl,
			@Value("${com.builds-monitor.url.releases}") String releasesUrl
	) {
		this.updatesUrl = updatesUrl;
		this.releasesUrl = releasesUrl;
	}

	@Override
	public byte[] getUpdatesXML() {
		return loadFile(updatesUrl);
	}

	@Override
	public byte[] getReleasesByProjectJSON(String projectCode) {
		return loadFile(releasesUrl + projectCode);
	}

	@Override
	public String getChecksum(String checksumUrl) {
		byte[] bytes = loadFile(checksumUrl);
		if (bytes == null) {
			return null;
		}
		return new String(bytes);
	}

	private byte[] loadFile(String fileUrl) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = new URL(fileUrl).openStream();
			byte[] dataBuffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
				out.write(dataBuffer, 0, bytesRead);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
