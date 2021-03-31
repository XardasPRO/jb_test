package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.db.dao.BuildsInfoDAO;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.ProductBuildInfo;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.product.Status;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.MainPageConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MainPageConstructorImpl implements MainPageConstructor {
	private final String header = "<!DOCTYPE html>\n" +
			"<html>\n" +
			"<head>\n" +
			"<style>\n" +
			"table {\n" +
			"  font-family: arial, sans-serif;\n" +
			"  border-collapse: collapse;\n" +
			"  width: 100%;\n" +
			"}\n" +
			"\n" +
			"td, th {\n" +
			"  border: 1px solid #dddddd;\n" +
			"  text-align: left;\n" +
			"  padding: 8px;\n" +
			"}\n" +
			"\n" +
			"tr:nth-child(even) {\n" +
			"  background-color: #dddddd;\n" +
			"}\n" +
			"</style>\n" +
			"</head>\n" +
			"<body>\n" +
			"\n" +
			"<h2>Products info</h2>\n" +
			"\n" +
			"<table>\n" +
			"  <tr>\n" +
			"    <th>Id</th>\n" +
			"    <th>Product Code</th>\n" +
			"    <th>Build full number</th>\n" +
			"    <th>Build date</th>\n" +
			"    <th>Status</th>\n" +
			"    <th>Last update date</th>\n" +
			"    <th>Product info</th>\n" +
			"  </tr>";

	private final String endOfPage = "</table>\n" +
			"\n" +
			"</body>\n" +
			"<h5>Created by Max Protsenko for JB</h5>\n" +
			"</html>\n";


	private final BuildsInfoDAO buildsInfoDAO;

	@Autowired
	public MainPageConstructorImpl(BuildsInfoDAO buildsInfoDAO) {
		this.buildsInfoDAO = buildsInfoDAO;
	}

	@Override
	public List<ProductBuildInfo> getSourceData() {
		return buildsInfoDAO.getAll();
	}

	@Override
	public String createMainPageHtml() {
		List<ProductBuildInfo> allBuild = buildsInfoDAO.getAll();
		StringBuilder sb = new StringBuilder();
		sb.append(header);

		for (ProductBuildInfo buildInfo : allBuild) {
			sb.append("<tr>\n");
			addToTable(String.valueOf(buildInfo.getId()), sb);
			addToTable(buildInfo.getProductCode(), sb);
			addToTable(buildInfo.getBuildFullNumber(), sb);
			addToTable(buildInfo.getBuildDate().toString(), sb);
			if (Status.DOWNLOADING.equals(buildInfo.getStatus())) {
				addToTable(buildInfo.getStatus().toString() + "(" + (buildInfo.getBuildBytesLoaded() / (buildInfo.getSize() / 100)) + "%)", sb);
			} else {
				addToTable(buildInfo.getStatus().toString(), sb);
			}
			addToTable(buildInfo.getLastUpdateTime().toString(), sb);
			addToTable(buildInfo.getProductInfo(), sb);
			sb.append("</tr>\n");
		}

		sb.append(endOfPage);
		return sb.toString();
	}

	private void addToTable(String value, StringBuilder sb) {
		sb.append("<th>\n");
		sb.append(value);
		sb.append("</th>\n");
	}
}
