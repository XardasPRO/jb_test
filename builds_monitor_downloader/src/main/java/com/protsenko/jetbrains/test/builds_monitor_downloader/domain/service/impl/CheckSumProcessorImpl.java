package com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_downloader.domain.service.CheckSumProcessor;
import com.protsenko.jetbrains.test.builds_monitor_downloader.model.dto.DownloadTaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class CheckSumProcessorImpl implements CheckSumProcessor {

	private final int BUFFER_SIZE;

	@Autowired
	public CheckSumProcessorImpl(@Value("${com.builds-monitor-downloader.buffer-size}") int bufferSize) {
		BUFFER_SIZE = bufferSize;
	}

	public boolean isChecksumValid(DownloadTaskDto downloadTask, String filePath) throws IOException, NoSuchAlgorithmException {
		String fileCheckSum = calculateFileCheckSumSHA256(filePath);
		String checksumByLink = getChecksumByLink(downloadTask.getChecksumLink());
		String currentChecksum = downloadTask.getCurrentChecksum().substring(0, downloadTask.getCurrentChecksum().indexOf(" "));

		return fileCheckSum.equals(checksumByLink) && fileCheckSum.equals(currentChecksum);
	}

	private String calculateFileCheckSumSHA256(String filePath) throws NoSuchAlgorithmException, IOException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		InputStream is = Files.newInputStream(Paths.get(filePath));
		DigestInputStream dis = new DigestInputStream(is, md);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int bytesRead = -1;
		byte[] buffer = new byte[BUFFER_SIZE];
		while ((bytesRead = dis.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		byte[] digest = md.digest();
		StringBuilder result = new StringBuilder();

		for (byte b : digest) {
			result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
		}

		out.close();
		dis.close();
		is.close();
		return result.toString();
	}

	private String getChecksumByLink(String checksumUrl) {
		byte[] bytes = loadFile(checksumUrl);
		if (bytes == null) {
			return null;
		}
		String checksum = new String(bytes).trim();
		return checksum.substring(0, checksum.indexOf(" "));
	}

	private byte[] loadFile(String fileUrl) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InputStream in = new URL(fileUrl).openStream();
			byte[] dataBuffer = new byte[BUFFER_SIZE];
			int bytesRead;
			while ((bytesRead = in.read(dataBuffer, 0, BUFFER_SIZE)) != -1) {
				out.write(dataBuffer, 0, bytesRead);
			}
			return out.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
