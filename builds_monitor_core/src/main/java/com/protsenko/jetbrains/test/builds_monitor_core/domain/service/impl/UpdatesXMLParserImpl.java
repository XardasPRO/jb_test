package com.protsenko.jetbrains.test.builds_monitor_core.domain.service.impl;

import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Build;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Channel;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Product;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.model.updates.xml.Updates;
import com.protsenko.jetbrains.test.builds_monitor_core.domain.service.UpdatesXMLParser;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class UpdatesXMLParserImpl implements UpdatesXMLParser {

	@Override
	public Updates parse(byte[] xml) {
		List<Product> products = new ArrayList<>();
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document document = documentBuilder.parse(new ByteArrayInputStream(xml));
			Node root = document.getDocumentElement();

			NodeList childNodes = root.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode.getNodeType() != Node.TEXT_NODE && "product".equals(childNode.getNodeName().toLowerCase(Locale.ROOT))) {
					products.add(parseProduct(childNode));
				}
			}
		} catch (IOException | ParserConfigurationException | SAXException e) {
			e.printStackTrace();
		}

		return Updates.of(products);
	}

	private Product parseProduct(Node productNode) {
		ArrayList<Channel> channels = new ArrayList<>();
		String code = null;

		NodeList childNodes = productNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.TEXT_NODE && "channel".equals(childNode.getNodeName().toLowerCase(Locale.ROOT))) {
				channels.add(parseChannel(childNode));
			}
			if (childNode.getNodeType() == Node.ELEMENT_NODE && "code".equals(childNode.getNodeName().toLowerCase(Locale.ROOT))) {
				code = childNode.getTextContent();
			}
		}

		return Product.of(getAttribute(productNode.getAttributes(), "name"), code, channels);
	}

	private Channel parseChannel(Node channelNode) {
		NamedNodeMap attributes = channelNode.getAttributes();

		ArrayList<Build> builds = new ArrayList<>();

		NodeList childNodes = channelNode.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node childNode = childNodes.item(i);
			if (childNode.getNodeType() != Node.TEXT_NODE && "build".equals(childNode.getNodeName().toLowerCase(Locale.ROOT))) {
				builds.add(pareBuild(childNode));
			}
		}

		return Channel.builder()
				.id(getAttribute(attributes, "id"))
				.name(getAttribute(attributes, "name"))
				.status(getAttribute(attributes, "status"))
				.url(getAttribute(attributes, "url"))
				.feedback(getAttribute(attributes, "feedback"))
				.majorVersion(getAttribute(attributes, "majorVersion"))
				.licensing(getAttribute(attributes, "licensing"))
				.builds(builds)
				.build();
	}

	private Build pareBuild(Node buildNode) {
		NamedNodeMap attributes = buildNode.getAttributes();

		return Build.builder()
				.number(getAttribute(attributes, "number"))
				.version(getAttribute(attributes, "version"))
				.fullNumber(getAttribute(attributes, "fullNumber"))
				.releaseDate(getAttribute(attributes, "releaseDate"))
				.build();
	}

	private String getAttribute(NamedNodeMap attributes, String attributeName) {
		if (attributes != null) {
			Node namedItem = attributes.getNamedItem(attributeName);
			if (namedItem != null && namedItem.getNodeType() == Node.ATTRIBUTE_NODE) {
				return namedItem.getTextContent();
			}
		}
		return null;
	}
}
